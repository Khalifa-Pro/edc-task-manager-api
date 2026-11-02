package com.test.edc.taskmanager.task_manager_api.dto;

import com.test.edc.taskmanager.task_manager_api.entity.TaskPriority;
import com.test.edc.taskmanager.task_manager_api.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Le titre est obligatoire")
        @Size(min = 3, max = 120, message = "Le titre doit contenir de 3 à 120 caractères")
        String title,

        @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
        String description,

        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate
) {}
