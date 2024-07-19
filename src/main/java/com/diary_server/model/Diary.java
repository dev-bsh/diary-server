package com.diary_server.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "diary", indexes = {
        @Index(name = "idx_name_id", columnList = "user_id")
})
public class Diary extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String weather;

    @Column(nullable = false)
    private String content;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Diary(Long id, String content, String weather, String comment, User user) {
        this.id = id;
        this.content = content;
        this.weather = weather;
        this.comment = comment;
        this.user = user;
    }

}
