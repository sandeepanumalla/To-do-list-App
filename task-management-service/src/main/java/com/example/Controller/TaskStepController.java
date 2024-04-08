package com.example.Controller;

import com.example.model.Step;
import com.example.model.User;
import com.example.request.TaskStepRequest;
import com.example.request.TaskStepUpdateRequest;
import com.example.response.TaskResponse;
import com.example.response.TaskStepResponse;
import com.example.service.TaskStepService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<?> createStep(@PathVariable Long taskId, @RequestBody TaskStepRequest stepRequest) {
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

    @PutMapping("/steps/{stepId}")
    public ResponseEntity<Step> updateStep(@PathVariable Long taskId, @PathVariable("stepId") Long stepId,
                                           @RequestBody TaskStepUpdateRequest stepRequest, HttpServletRequest request) {
        // Fetching user details from request
        // Assuming userService.getUserIdByToken(request) returns the user details
        // Replace this with actual method to fetch user details
         User user = userService.getUserIdByToken(request);

        // Passing user details to updateStep method
        // Step updatedStep = taskStepService.updateStep(taskId, stepId, stepRequest, user);
        // Alternatively, you can modify the service method to directly accept the user details
        Step updatedStep = taskStepService.updateStep(taskId, stepId, stepRequest, user);
        return ResponseEntity.ok(updatedStep);
    }

    @DeleteMapping("/{taskId}/steps/{stepId}")
    public ResponseEntity<Void> deleteStep(@PathVariable Long taskId, @PathVariable("stepId") Long stepId, HttpServletRequest request) {
        // Fetching user details from request
        // Assuming userService.getUserIdByToken(request) returns the user details
        // Replace this with actual method to fetch user details
         User user = userService.getUserIdByToken(request);

        // Passing user details to deleteStep method
        // taskStepService.deleteStep(taskId, stepId, user);
        // Alternatively, you can modify the service method to directly accept the user details
        taskStepService.deleteStep(taskId, stepId, user);
        return ResponseEntity.noContent().build();
    }
}
