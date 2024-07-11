package com.example.Controller;

import com.example.model.Step;
import com.example.model.TaskRecurrence;
import com.example.model.User;
import com.example.request.TaskStepRequest;
import com.example.request.TaskStepUpdateRequest;
import com.example.response.TaskStepResponse;
import com.example.service.TaskStepService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskStepController {

    private final TaskStepService taskStepService;
    private final UserService userService;

    public TaskStepController(TaskStepService taskStepService, UserService userService) {
        this.taskStepService = taskStepService;
        this.userService = userService;
    }

    @PostMapping("/{taskId}/steps")
    public ResponseEntity<?> createStep(@PathVariable Long taskId, @Valid @RequestBody TaskStepRequest stepRequest) {
        TaskStepResponse createdStep = taskStepService.createStep(taskId, stepRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStep);
    }

    @GetMapping("/{taskId}/steps")
    public ResponseEntity<List<Step>> getAllSteps(@PathVariable("taskId") Long taskId) {
        List<Step> steps = taskStepService.getAllSteps(taskId);
        return ResponseEntity.ok(steps);
    }

    @GetMapping("/steps/{stepId}")
    public ResponseEntity<Step> getStep(@PathVariable Long taskId, @PathVariable("stepId") Long stepId) {
        Step step = taskStepService.getStep(taskId, stepId);
        return ResponseEntity.ok(step);
    }

    @PutMapping("{taskId}/steps/{stepId}")
    public ResponseEntity<?> updateStep(@PathVariable Long taskId, @PathVariable("stepId") Long stepId,
                                           @RequestBody @Valid TaskStepUpdateRequest stepRequest, HttpServletRequest request) {
         User user = userService.getUserIdByToken(request);
        TaskStepResponse updatedStep = taskStepService.updateStep(taskId, stepId, stepRequest, user);
        return ResponseEntity.ok(updatedStep);
    }

    @DeleteMapping("/{taskId}/steps/{stepId}")
    public ResponseEntity<Void> deleteStep(@PathVariable Long taskId, @PathVariable("stepId") Long stepId, HttpServletRequest request) {
         User user = userService.getUserIdByToken(request);
        taskStepService.deleteStep(taskId, stepId, user);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/")
    public ResponseEntity<?> createTaskRecurrence() {

        return ResponseEntity.status(HttpStatus.CREATED).body("Task added to My Day successfully.");
    }


}
