package com.diary_server.service;

import com.diary_server.dto.DiaryResponse;
import com.diary_server.dto.DiarySaveRequest;
import com.diary_server.dto.Message;
import com.diary_server.dto.UserDto;
import com.diary_server.model.Diary;
import com.diary_server.model.User;
import com.diary_server.repository.DiaryRepository;
import com.diary_server.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    @Value("${openai.data.filename}")
    private String dataFileName;

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final OpenAiClient openAiClient;
    private List<Map<String, String>> promptTemplates;

    @PostConstruct
    public void init() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource(dataFileName);
        try (InputStream inputStream = resource.getInputStream()) {
            this.promptTemplates = objectMapper.readValue(inputStream, new TypeReference<>() {});
        }
    }

    public Long saveDiary(DiarySaveRequest diarySaveRequest, UserDto userDto) {
        // todo content 데이터 검증 추가
        // gpt 요청 형식 생성, 대화컨셉 및 일기내용 추가
        List<Message> messages = createGptRequest(diarySaveRequest, userDto.getUsername());
        // gpt 응답 생성
        String comment = openAiClient.generateComment(messages);
        User user = userRepository.findById(userDto.getId()).orElseThrow(() -> new IllegalStateException("Id에 해당하는 유저가 존재하지 않습니다."));
        return diaryRepository.save(diarySaveRequest.toEntity(user, comment)).getId();
    }

    public List<DiaryResponse> getAllDiaryListByUserId(UserDto userDto) {
        List<Diary> diaryList = diaryRepository.findAllByUserId(userDto.getId());
        return diaryList.stream()
                .map(DiaryResponse::fromEntity)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // gpt 답변의 컨셉 데이터를 Message 타입으로 변환
    public List<Message> createGptRequest(DiarySaveRequest diarySaveRequest, String userName) {
        List<Message> messages = promptTemplates.stream()
                .map(template -> new Message(template.get("role"), template.get("content")
                        .replace("userName", userName)
                        .replace("weather", diarySaveRequest.getWeather())))
                .collect(Collectors.toCollection(ArrayList::new));
        messages.add(new Message("user", diarySaveRequest.getContent()));
        return messages;
    }

}
