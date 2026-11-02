package com.test.edc.taskmanager.task_manager_api.service.interfaces;

import com.test.edc.taskmanager.task_manager_api.dto.TaskRequest;
import com.test.edc.taskmanager.task_manager_api.entity.Task;
import com.test.edc.taskmanager.task_manager_api.entity.TaskStatus;

import java.util.List;

public interface ITaskService {
    List<Task> findAll(String search, TaskStatus status);
    Task findById(Long id);
    Task create(TaskRequest request);
    Task update(Long id, TaskRequest request);
    void delete(Long id);

}
