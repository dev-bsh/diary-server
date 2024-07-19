package com.diary_server.controller;

import com.diary_server.dto.DiaryResponse;
import com.diary_server.dto.DiarySaveRequest;
import com.diary_server.dto.UserDto;
import com.diary_server.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping("")
    public ResponseEntity<Long> saveDiary(@AuthenticationPrincipal UserDto userDto, @RequestBody DiarySaveRequest diarySaveRequest) {
        return ResponseEntity.ok(diaryService.saveDiary(diarySaveRequest, userDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<DiaryResponse>>  getAllDiaryByUser(@AuthenticationPrincipal UserDto userDto) {
        return ResponseEntity.ok(diaryService.getAllDiaryListByUserId(userDto));
    }

}
