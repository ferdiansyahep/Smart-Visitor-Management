package com.example.vigigate.controller;

import com.example.vigigate.model.Visitor;
import com.example.vigigate.repository.VisitorRepository;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/visitors")
public class VisitorController {

    @Autowired private VisitorRepository repo;

    // API Key kamu
    private final String API_KEY = "AIzaSyA67_C_i3E5BQVXmrMswdmixFUIQTzzSHY";

    @PostMapping
    public Visitor register(@RequestBody Visitor visitor) {
        String score = "GREEN"; // Default fallback

        try {
            // 1. Inisialisasi SDK Resmi Gemini
            Client client = Client.builder().apiKey(API_KEY).build();

            // 2. Buat Prompt
            String prompt = "Sebagai sistem keamanan, analisis tujuan tamu ini: '" + visitor.getPurpose() + "'. " +
                    "Jika ada indikasi kejahatan/bahaya jawab RED. " +
                    "Jika mencurigakan jawab YELLOW. Jika aman jawab GREEN. " +
                    "Jawab hanya dengan SATU KATA.";

            // 3. Panggil AI dengan satu baris kode!
            GenerateContentResponse response = client.models.generateContent(
                    "gemini-3.5-flash",
                    prompt,
                    null
            );

            // 4. Ambil teks jawaban dengan sangat mudah
            if (response.text() != null) {
                String aiText = response.text().toUpperCase();
                System.out.println("DEBUG - Jawaban Resmi SDK Gemini: " + aiText);

                if (aiText.contains("RED")) score = "RED";
                else if (aiText.contains("YELLOW")) score = "YELLOW";
                else score = "GREEN";
            }
        } catch (Exception e) {
            System.out.println("DEBUG - Error SDK: " + e.getMessage());
            score = "GREEN";
        }

        visitor.setRiskScore(score);
        return repo.save(visitor);
    }

    @GetMapping
    public List<Visitor> getAll() {
        return repo.findAll();
    }
}