package com.example.vigigateproject.service;

// Perbaikan Import: Menggunakan path com.example.vigigateproject yang benar
import com.example.vigigateproject.model.Visitor;
import com.example.vigigateproject.repository.VisitorRepository;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class VisitorService {

    @Autowired
    private VisitorRepository repository;

    @Value("${gemini.api.key}")
    private String apiKey;

    public Visitor registerVisitor(Visitor visitor) {
        visitor.setWaktuMasuk(LocalDateTime.now());
        String score = calculateRiskScore(visitor.getNik(), visitor.getWaktuMasuk());
        visitor.setStatusRisiko(score);
        return repository.save(visitor);
    }

    public List<Visitor> getAllVisitorsToday() {
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
        return repository.findByWaktuMasukAfter(startOfToday);
    }

    private String calculateRiskScore(String nik, LocalDateTime waktu) {
        LocalTime jam = waktu.toLocalTime();

        // Logika Aturan Jam Malam (22:00 - 04:00) -> RED
        if (jam.isAfter(LocalTime.of(22, 0)) || jam.isBefore(LocalTime.of(4, 0))) {
            return "RED";
        }

        // Logika Frekuensi Kunjungan Hari Ini >= 3 Kali -> YELLOW
        LocalDateTime startOfToday = LocalDateTime.now().with(LocalTime.MIN);
        long hitungKunjungan = repository.countByNikAndWaktuMasukAfter(nik, startOfToday);
        if (hitungKunjungan >= 3) {
            return "YELLOW";
        }

        return "GREEN";
    }

    public String generateAiSummary() {
        List<Visitor> listHariIni = getAllVisitorsToday();

        if (listHariIni.isEmpty()) {
            return "[Gemini AI]: Belum ada aktivitas kunjungan terdeteksi hari ini.";
        }

        StringBuilder dataMentahTamu = new StringBuilder();
        dataMentahTamu.append("Data Kunjungan Tamu Hari Ini:\n");
        for (Visitor v : listHariIni) {
            dataMentahTamu.append(String.format("- Nama: %s, Tujuan: %s, Jam Masuk: %s, Status Risiko: %s\n",
                    v.getNama(), v.getTujuan(), v.getWaktuMasuk().toLocalTime(), v.getStatusRisiko()));
        }

        String prompt = "Kamu adalah sistem AI keamanan pintar bernama Vigi-Gate. " +
                "Tugasmu adalah menganalisis data log kunjungan berikut dan membuat ringkasan singkat (maksimal 3 kalimat) " +
                "mengenai situasi keamanan hari ini, serta memberikan rekomendasi pengetatan jika ada status RED atau YELLOW.\n\n" +
                dataMentahTamu.toString();

        try {
            Client ai = Client.builder().apiKey(apiKey).build();

            // Perbaikan 1: Hapus tanda kurung pada 'models'
            GenerateContentResponse response = ai.models.generateContent(
                    "gemini-2.5-flash", // atau ganti ke "gemini-3.5-flash" sesuai dokumentasi terbaru jika error
                    prompt,
                    null
            );

            // Perbaikan 2: Gunakan text() bukan getText()
            return "[Gemini AI Insights]: " + response.text();

        } catch (Exception e) {
            return "[Sistem Keamanan]: Gagal terhubung ke Gemini API. " + e.getMessage();
        }
    }
}