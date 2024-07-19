package com.diary_server.dto;

import com.diary_server.model.Diary;
import com.diary_server.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DiarySaveRequest {
    private String content;
    private String weather;

    @Builder
    public DiarySaveRequest(String content, String weather) {
        this.content = content;
        this.weather = weather;
    }

    public Diary toEntity(User user, String comment) {
        return  Diary.builder()
                .user(user)
                .comment(comment)
                .content(content)
                .weather(weather)
                .build();
    }
}

