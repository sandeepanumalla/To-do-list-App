package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.List;

import java.util.Collection;
import java.util.Set;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@Table(name = "user")
public class User implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String password;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner"
//            , cascade = CascadeType.REMOVE
            , orphanRemoval = true
    )
    private List<Task> ownTasks;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "sharedWithUsers", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
//    @JoinTable(
//            name = "shared_tasks",
//            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "userId"),
//            inverseJoinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id")
//    )
    private List<Task> sharedTasks;


    @OneToOne(mappedBy = "userId")
    private Profile profile;


    @ManyToMany(mappedBy = "assignedToUsers")
    private Set<Task> assignedTo;

    @OneToMany(mappedBy = "user")
    private List<UserNotificationPreferences> notificationPreferences;

    @OneToMany(mappedBy = "user")
    private List<UserNotification> userNotifications;

//    @OneToMany(mappedBy = "user", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    private List<MyDayTask> myDayTaskList;

    @ManyToMany(mappedBy = "myDayTasks", fetch = FetchType.EAGER
//            , cascade = CascadeType.ALL
    )
    private List<Task> myDayTasksList;


//    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
//    @JoinColumn(name = "signup_type_id", nullable = false)
//    private SignupType signupType;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
