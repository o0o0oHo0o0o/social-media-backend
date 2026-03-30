package com.example.SocialMedia.serviceImpl;

import com.example.SocialMedia.dto.UserProfileDto;
import com.example.SocialMedia.dto.response.ShortUserResponse;
import com.example.SocialMedia.exception.BusinessException;
import com.example.SocialMedia.mapper.UserMapper;
import com.example.SocialMedia.model.coredata_model.User;
import com.example.SocialMedia.repository.UserRepository;
import com.example.SocialMedia.service.IMinioService;
import com.example.SocialMedia.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.SocialMedia.constant.OtpChannel;
import com.example.SocialMedia.constant.UsernameConstants;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final SecureRandom random = new SecureRandom();

    private final IMinioService minioService;

    private static final java.util.List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");


    @Override
    public String generateUniqueUsername(String identifier, OtpChannel channel) {
        String baseUsername = "Debater_";
        String adj = UsernameConstants.ADJECTIVES[random.nextInt(UsernameConstants.ADJECTIVES.length)];
        String noun = UsernameConstants.NOUNS[random.nextInt(UsernameConstants.NOUNS.length)];
        String funUsername = baseUsername + noun + adj;
        if (userRepository.findByUserName(funUsername).isEmpty()) {
            return funUsername;
        }
        String hash = Integer
                .toHexString(identifier.hashCode())
                .replace("-", "");
        if (hash.length() > 4) hash = hash.substring(0, 4);
        String fallback = funUsername + hash;
        int counter = 1;
        while (userRepository.findByUserName(fallback).isPresent()) {
            fallback = funUsername + hash + counter++;
        }
        return fallback;
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<ShortUserResponse> getUserByKeyword(String keyword, Pageable pageable){
        Page<User> users = userRepository.findByUserNameContainingIgnoreCase(keyword, pageable);
        return users.getContent().stream().map((user)->
                new ShortUserResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getUsername(),
                        user.getProfilePictureURL(),
                        user.getCreatedLocalDateTime())).toList();
    }
    @Override
    @Transactional
    // Quan trọng: Đảm bảo DB và MinIO đồng bộ, nếu DB fail thì không upload MinIO (hoặc ngược lại tùy thiết kế)
    public String updateAvatar(String username, MultipartFile file) {
        // 1. Tìm User hiện tại
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Không tìm thấy user"));

        // 2. Validate File
        if (file == null || file.isEmpty()) {
            throw new BusinessException("FILE_EMPTY", "Vui lòng chọn một file ảnh");
        }

        // Kiểm tra định dạng file (Content Type)
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException("INVALID_FILE_TYPE", "Chỉ cho phép upload ảnh (jpg, png, gif, webp)");
        }

        // (Tùy chọn) Kiểm tra kích thước file, ví dụ max 5MB
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException("FILE_TOO_LARGE", "Kích thước ảnh tối đa là 5MB");
        }

        // 3. Xử lý file cũ (Lấy tên file cũ để xóa sau)
        String oldFileByMinIO = user.getProfilePictureURL();

        try {
            // 4. Upload file mới lên MinIO (Không cần truyền bucketName)
            // Vì hàm của anh trả về FileUploadResponse, ta hứng object đó
            var uploadResponse = minioService.uploadFile(file);

            // Lấy tên file gốc (ví dụ: abc-123.jpg) để lưu vào DB
            String newFileName = uploadResponse.getFileName();
            // Lấy link xem ảnh trực tiếp (Presigned URL)
            String newAvatarUrl = uploadResponse.getMediaUrl();

            // 5. Cập nhật Database (Lưu tên file thô vào DB là chuẩn nhất)
            user.setProfilePictureURL(newFileName);
            userRepository.save(user);

            // 6. Xóa file cũ trên MinIO
            if (oldFileByMinIO != null && !oldFileByMinIO.isBlank() && !oldFileByMinIO.startsWith("http")) {
                try {
                    // Truyền đúng 1 tham số là tên file cũ
                    minioService.deleteFile(oldFileByMinIO);
                } catch (Exception e) {
                    System.err.println("Lỗi xóa file cũ trên MinIO: " + e.getMessage());
                }
            }

            // 7. Trả về presigned URL mới để Frontend hiển thị ngay lập tức
            return newAvatarUrl;

        } catch (Exception e) {
            throw new BusinessException("UPLOAD_FAILED", "Có lỗi xảy ra khi upload ảnh lên server");
        }

    }
    public UserProfileDto getUserProfile(String username) {
        User user = userRepository.findByUserName(username).orElseThrow();
        return userMapper.toUserProfileDto(user);
    }
}