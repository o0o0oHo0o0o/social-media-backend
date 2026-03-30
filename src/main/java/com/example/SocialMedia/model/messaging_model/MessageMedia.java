package com.example.SocialMedia.model.messaging_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MessageMedia", schema = "Messaging")
@Getter
@Setter
public class MessageMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MediaID")
    private int MediaID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MessageID")
    private Messages message;

    @Column(name = "MediaName")
    private String MediaName;

    @Column(name = "MediaType")
    private String MediaType;

    @Column(name = "FileName")
    private String FileName;

    @Column(name = "FileSize")
    private Integer FileSize;

    @Column(name = "ThumbnailName")
    private String ThumbnailName;
}
