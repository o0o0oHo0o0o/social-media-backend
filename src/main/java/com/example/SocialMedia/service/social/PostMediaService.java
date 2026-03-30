package com.example.SocialMedia.service.social;

import com.example.SocialMedia.model.coredata_model.Post;
import com.example.SocialMedia.model.coredata_model.PostMedia;
import org.springframework.data.domain.Pageable;

import java.util.List;

// KHÔNG ĐỂ @Service Ở ĐÂY
public interface PostMediaService {
    // Trả về Entity để PostService xử lý logic MinIO
    List<PostMedia> findByPost(Post post, Pageable pageable);

    // Nhận metadata thay vì MultipartFile (vì file đã upload ở lớp trên rồi)
    void createPostMedia(Post post, String fileName, String mediaType);

    void deletePostMedia(int id);
}