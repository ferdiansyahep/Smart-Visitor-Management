package com.example.vigigateproject.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nama;
    private String nik;
    private String tujuan;
    private String fotoUrl;
    private LocalDateTime waktuMasuk;
    private String statusRisiko;

    public Visitor() {}

    // Getter dan Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }
    public String getTujuan() { return tujuan; }
    public void setTujuan(String tujuan) { this.tujuan = tujuan; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public LocalDateTime getWaktuMasuk() { return waktuMasuk; }
    public void setWaktuMasuk(LocalDateTime waktuMasuk) { this.waktuMasuk = waktuMasuk; }
    public String getStatusRisiko() { return statusRisiko; }
    public void setStatusRisiko(String statusRisiko) { this.statusRisiko = statusRisiko; }
}