package com.example.vigigate.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String nik;
    private String purpose;
    private String riskScore;
    private LocalDateTime checkInTime = LocalDateTime.now();
}