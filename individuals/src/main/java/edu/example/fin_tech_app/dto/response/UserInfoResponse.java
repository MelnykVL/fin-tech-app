package edu.example.fin_tech_app.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record UserInfoResponse(String id, String email, List<String> roles, LocalDateTime createdAt) { }
