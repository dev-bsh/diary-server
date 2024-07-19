package com.diary_server.dto;

import com.diary_server.model.Diary;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class DiaryResponse {

    private Long id;
    private LocalDateTime createDate;
    private String weather;
    private String content;
    private String comment;

    @Builder
    public DiaryResponse(Long id, LocalDateTime createDate, String weather, String content, String comment) {
        this.id = id;
        this.createDate = createDate;
        this.weather = weather;
        this.content = content;
        this.comment = comment;
    }

    public static DiaryResponse fromEntity(Diary diary) {
        return DiaryResponse.builder()
                .id(diary.getId())
                .createDate(diary.getCreatedDate())
                .weather(diary.getWeather())
                .content(diary.getContent())
                .comment(diary.getComment())
                .build();
    }
}
