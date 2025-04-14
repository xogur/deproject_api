// src/main/java/com/example/deproject_api/domain/ColorTrend.java
package com.example.deproject_api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "color_trend")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColorTrend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long id; // 혹시 id 없으면 생략 가능

    private int r;
    private int g;
    private int b;

    @Column(name = "image_url")
    private String imageUrl;
}