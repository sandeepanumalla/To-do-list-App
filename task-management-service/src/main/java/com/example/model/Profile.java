package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
public class Profile {

    @Id
    long profileId;

    @OneToOne
    private User userId;

    private String bio;

    private long tasksCompleted;

    private long tasksInProgress;

    private double successRate;

    private boolean isPersonalNotificationEnabled;

    private boolean isPrivateNotificationEnabled;
}
