package com.example.vigigateproject.controller;

import com.example.vigigateproject.model.Visitor;
import com.example.vigigateproject.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api/visitors")
@CrossOrigin(origins = "*")
public class VisitorController {

    @Autowired
    private VisitorService service;

    // List untuk menampung semua konektor browser yang sedang membuka halaman dashboard
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @PostMapping("/register")
    public Visitor register(@RequestBody Visitor visitor) {
        Visitor savedVisitor = service.registerVisitor(visitor);

        // KIRIM DATA REAL-TIME: Kirim data tamu yang baru disimpan ke semua browser yang terhubung
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(savedVisitor, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);

        return savedVisitor;
    }

    @GetMapping("/logs")
    public List<Visitor> getLogs() {
        return service.getAllVisitorsToday();
    }

    @GetMapping("/ai-summary")
    public String getAiSummary() {
        return service.generateAiSummary();
    }

    // ==========================================
    // ENDPOINT BARU UNTUK KONEKSI REAL-TIME (SSE)
    // ==========================================
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamVisitors() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Koneksi tanpa timeout
        this.emitters.add(emitter);

        // Jika koneksi selesai atau error, hapus dari list penyiaran
        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));
        emitter.onError((e) -> this.emitters.remove(emitter));

        return emitter;
    }
}