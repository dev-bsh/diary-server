package com.diary_server.controller;

import com.diary_server.dto.JwtResponse;
import com.diary_server.dto.UserDto;
import com.diary_server.model.User;
import com.diary_server.service.AuthService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.results.internal.ResolvedSqlSelection;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login/{provider}")
    public void login(HttpServletResponse response, @PathVariable String provider) throws IOException {
        String uri = "/oauth2/authorization/" + provider;
        response.sendRedirect(uri);
    }

    @GetMapping("/refresh")
    public ResponseEntity<JwtResponse> token(@AuthenticationPrincipal UserDto userDto) {
        JwtResponse newToken = authService.getNewToken(userDto);
        return ResponseEntity.ok(newToken);
    }

}
