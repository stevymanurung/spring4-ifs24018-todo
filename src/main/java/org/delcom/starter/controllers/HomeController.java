package org.delcom.starter.controllers;

import org.springframework.web.bind.annotation.*;
import java.util.Base64;

@RestController
public class HomeController {

    @GetMapping("/")
    public String hello() {
        return "Hay Abdullah, selamat datang di pengembangan aplikasi dengan Spring Boot!";
    }

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    // INFORMASI NIM
    @GetMapping("/informasiNim/{nim}")
    public String informasiNim(@PathVariable String nim) {
        nim = nim.trim();
        StringBuilder sb = new StringBuilder();

        if (nim.length() < 8) {
            return "NIM tidak sesuai format!";
        }

        String awal = nim.substring(0, 3);
        String thn = nim.substring(3, 5);
        String urut = nim.substring(5);

        String jurusan;
        switch (awal) {
            case "11S": jurusan = "Sarjana Informatika"; break;
            case "12S": jurusan = "Sarjana Sistem Informasi"; break;
            case "14S": jurusan = "Sarjana Teknik Elektro"; break;
            case "21S": jurusan = "Sarjana Manajemen Rekayasa"; break;
            case "22S": jurusan = "Sarjana Teknik Metalurgi"; break;
            case "31S": jurusan = "Sarjana Teknik Bioproses"; break;
            case "114": jurusan = "Diploma 4 Teknologi Rekayasa Perangkat Lunak"; break;
            case "113": jurusan = "Diploma 3 Teknologi Informasi"; break;
            case "133": jurusan = "Diploma 3 Teknologi Komputer"; break;
            default: jurusan = null;
        }

        if (jurusan == null) {
            sb.append("Prefix ").append(awal).append(" tidak dikenali");
        } else {
            int tahunMasuk = Integer.parseInt("20" + thn);
            int no = Integer.parseInt(urut);

            sb.append("Informasi NIM ").append(nim).append(":")
              .append(" >> Program Studi: ").append(jurusan)
              .append(" >> Angkatan: ").append(tahunMasuk)
              .append(" >> Urutan: ").append(no);
        }

        return sb.toString();
    }

    // PEROLEHAN NILAI
    @GetMapping("/perolehanNilai/{strBase64}")
    public String perolehanNilai(@PathVariable String strBase64) {
        String[] kategori = {"Partisipatif", "Tugas", "Kuis", "Proyek", "UTS", "UAS"};
        double[] bobot = {0.10, 0.15, 0.10, 0.15, 0.20, 0.30};
        double[] skor = new double[kategori.length];

        try {
            String decoded = new String(Base64.getDecoder().decode(strBase64));
            String[] lines = decoded.split("\\R");

            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    for (int i = 0; i < kategori.length; i++) {
                        if (kategori[i].equals(parts[0].trim())) {
                            try {
                                skor[i] = Double.parseDouble(parts[1].trim());
                            } catch (NumberFormatException ignored) {}
                            break;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        double nilaiAkhir = 0.0;
        StringBuilder sb = new StringBuilder("Perolehan Nilai: ");
        for (int i = 0; i < kategori.length; i++) {
            double nilaiTerbobot = skor[i] * bobot[i];
            nilaiAkhir += nilaiTerbobot;
            sb.append(String.format(">> %s: %.0f/100 (%.2f/%.0f) ", kategori[i], skor[i], nilaiTerbobot, bobot[i]*100));
        }

        String grade;
        if (nilaiAkhir >= 85) grade = "A";
        else if (nilaiAkhir >= 70) grade = "B";
        else if (nilaiAkhir >= 55) grade = "C";
        else if (nilaiAkhir >= 40) grade = "D";
        else grade = "E";

        sb.append(String.format(">> Nilai Akhir: %.2f >> Grade: %s", nilaiAkhir, grade));
        return sb.toString();
    }

    @GetMapping("/perbedaanL/{strBase64}")
    public String perbedaanL(@PathVariable String strBase64) {
        try {
            String input = new String(Base64.getDecoder().decode(strBase64));
            String[] parts = input.trim().split("\\|");
            if (parts.length != 3) return "Error: Format input tidak valid. Harusnya 'angka1|angka2|angka3'.";

            int a = Integer.parseInt(parts[0]);
            int b = Integer.parseInt(parts[1]);
            int c = Integer.parseInt(parts[2]);
            int perbedaan = Math.abs(a - b);

            return String.format("Nilai L: %d Nilai Kebalikan L: %d Nilai Tengah: %d Perbedaan: %d Dominan: %d",
                    a, b, c, perbedaan, c);
        } catch (NumberFormatException e) {
            return "Error: Input bukan angka yang valid.";
        } catch (IllegalArgumentException e) {
            return "Error: Format Base64 tidak valid.";
        }
    }

    @GetMapping("/palingTer/{strBase64}")
    public String palingTer(@PathVariable String strBase64) {
        try {
            String input = new String(Base64.getDecoder().decode(strBase64));
            String[] lines = input.split("\\R");

            int[] angka = new int[lines.length];
            int count = 0;
            int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;

            for (String line : lines) {
                try {
                    int val = Integer.parseInt(line.trim());
                    angka[count++] = val;
                    if (val > max) max = val;
                    if (val < min) min = val;
                } catch (NumberFormatException ignored) {}
            }

            if (count == 0) return "Tidak ada angka yang valid ditemukan.";

            int terbanyakVal = 0, terbanyakCount = 0;
            int tersedikitVal = 0, tersedikitCount = Integer.MAX_VALUE;
            int terendahCount = 0;

            for (int i = 0; i < count; i++) {
                int n = angka[i], freq = 0;
                boolean seen = false;
                for (int j = 0; j < i; j++) if (angka[j] == n) seen = true;
                if (seen) continue;

                for (int j = 0; j < count; j++) if (angka[j] == n) freq++;

                if (freq > terbanyakCount) { terbanyakCount = freq; terbanyakVal = n; }
                if (freq < tersedikitCount) { tersedikitCount = freq; tersedikitVal = n; }
            }

            for (int i = 0; i < count; i++) if (angka[i] == min) terendahCount++;

            long jumlahTertinggi = (long) max * terbanyakCount;
            long jumlahTerendah = (long) min * terendahCount;

            return String.format("Tertinggi: %d Terendah: %d Terbanyak: %d (%dx) Tersedikit: %d (%dx) Jumlah Tertinggi: %d * %d = %d Jumlah Terendah: %d * %d = %d",
                    max, min, terbanyakVal, terbanyakCount, tersedikitVal, tersedikitCount,
                    max, terbanyakCount, jumlahTertinggi, min, terendahCount, jumlahTerendah);

        } catch (IllegalArgumentException e) {
            return "Error: Format Base64 tidak valid.";
        }
    }
}
