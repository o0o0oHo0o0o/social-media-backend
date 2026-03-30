package com.example.SocialMedia.model.coredata_model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reports", schema = "CoreData")
@Setter
@Getter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportID")
    private int reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReporterID", nullable = false)
    private User reportUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReportedPostID")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ReportedCommentID")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "ReportedUserID")
    private User reportedUser;

    @Column(name = "Reason", nullable = false)
    private String reason;

    @Column(name = "ReportStatus")
    private String reportStatus;

    @Column(name = "ReportedAt", nullable = false)
    private LocalDateTime reportedLocalDateTime;
}
