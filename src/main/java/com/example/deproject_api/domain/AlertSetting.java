package com.example.deproject_api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AlertSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean enabled;

    @OneToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email", unique = true)
    private User user;
}
