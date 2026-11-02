package com.test.edc.taskmanager.task_manager_api.controller;

import com.test.edc.taskmanager.task_manager_api.dto.TaskRequest;
import com.test.edc.taskmanager.task_manager_api.entity.Task;
import com.test.edc.taskmanager.task_manager_api.entity.TaskStatus;
import com.test.edc.taskmanager.task_manager_api.service.implementation.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<Task> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TaskStatus status) {
        return service.findAll(search, status);
    }

    @GetMapping("/{id}")
    public Task findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@Valid @RequestBody TaskRequest request) {
        System.out.println("TITLE: "+request.title());
        System.out.println("DESCRIPTION: "+request.description());
        System.out.println("STATUS: "+request.status());
        System.out.println("PRIORITY: "+request.priority());
        System.out.println("DUE_DATE: "+request.dueDate());

        return service.create(request);
    }

    @PutMapping("/{id}")
    public Task update(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
