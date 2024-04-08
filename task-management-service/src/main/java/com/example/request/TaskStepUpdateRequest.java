package com.example.request;


import lombok.Getter;
import lombok.Setter;
import lombok.Value;

import java.io.Serializable;

@Value
@Setter
@Getter
public class TaskStepUpdateRequest implements Serializable {

    String name;

    Integer sequence;
}
