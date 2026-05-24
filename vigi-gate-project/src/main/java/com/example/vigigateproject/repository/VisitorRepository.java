package com.example.vigigateproject.repository;

import com.example.vigigateproject.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    long countByNikAndWaktuMasukAfter(String nik, LocalDateTime startOfDay);
    List<Visitor> findByWaktuMasukAfter(LocalDateTime startOfDay);
}