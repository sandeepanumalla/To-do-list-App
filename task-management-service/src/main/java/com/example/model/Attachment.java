package com.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String mediaType;

    @Lob
    @Column(length = 1048576) // Example: set the length to 1 MB (1048576 bytes)
    private byte[] data;

    @Column(nullable = true)
    private String filePath;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;


    @ManyToOne( fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id")
    private User uploadedBy;
}
