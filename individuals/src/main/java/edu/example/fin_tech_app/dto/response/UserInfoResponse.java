package edu.example.fin_tech_app.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;

public record UserInfoResponse(@JsonAlias("sub") String id, @JsonProperty("email") String email,
                               @JsonProperty("roles") List<String> roles,
                               @JsonProperty("created_at") @JsonFormat(shape = JsonFormat.Shape.STRING,
                                       timezone = "UTC") Timestamp createdAt) {
}
