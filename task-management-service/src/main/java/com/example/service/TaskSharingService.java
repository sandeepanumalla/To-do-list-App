package com.example.service;

import com.example.request.TaskShareRequest;
import com.example.request.TaskUnShareRequest;

public interface TaskSharingService {

    public void share(TaskShareRequest taskShareRequest);

    void unShare(TaskUnShareRequest taskUnShareRequest);
}
