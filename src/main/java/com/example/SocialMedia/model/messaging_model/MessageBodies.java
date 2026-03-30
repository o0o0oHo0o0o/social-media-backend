package com.example.SocialMedia.model.messaging_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MessageBodies", schema = "Messaging")
@Getter
@Setter
public class MessageBodies
{
    @Id
    @Column(name = "MessageID")
    private Long messageID;

    @Lob
    @Column(name = "Content", columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "MessageID")
    private Messages message;
}
