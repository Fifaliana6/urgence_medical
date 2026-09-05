// dto/ExamRequest.java
package com.hopital.urgences.dto;

import jakarta.validation.constraints.NotBlank;

public record ExamRequest(@NotBlank String type) {}