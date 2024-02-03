package com.example.request;

import com.example.model.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

        @NotEmpty(message = "please provide category name")
        private String categoryName;

        private User categoryOwner;

        private long taskId;



}
