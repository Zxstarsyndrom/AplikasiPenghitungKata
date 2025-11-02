# Aplikasi Penghitung Kata (Java Swing)

Aplikasi desktop untuk menganalisis teks: menghitung **kata**, **karakter (dengan & tanpa spasi)**, **kalimat**, **paragraf**, serta **kemunculan kata** yang dicari. Dibuat dengan **Java Swing (JFrame Form)**.  
**Mode hitung manual**: angka **baru diperbarui saat tombol _Hitung_ ditekan** (bukan real-time).
<img width="573" height="573" alt="image" src="https://github.com/user-attachments/assets/340b4631-8220-4ced-be48-030a86aedb65" />

> Paket utama: `app` · IDE: NetBeans (Ant Project) · JDK 17+

---

## ✨ Fitur
- Input teks melalui **JTextArea** (dibungkus **JScrollPane**, line wrap aktif).
- **Hitung manual** lewat tombol **Hitung** (memenuhi kebutuhan tugas: ActionListener).
- **Cari & sorot kata** (case-insensitive) + menampilkan jumlah kemunculan (angka di Ringkasan diperbarui setelah menekan **Hitung**).
- **Bersihkan**: hapus teks & semua highlight.
- **Simpan ke .txt (UTF-8)**: menyimpan **TEKS** dan **HASIL** (ringkasan metrik).
- Panel **Ringkasan** (SOUTH) menampilkan:
  - Kata, Karakter (spasi), Karakter (tanpa spasi), Kalimat, Paragraf, Kemunculan kata.

---

## 🧱 Tata Letak (BorderLayout)
- **NORTH**: Judul aplikasi
- **CENTER**: `JTextArea` (dalam `JScrollPane`)
- **EAST**: Panel tombol vertikal — **Hitung**, **Bersihkan**, **Cari…**, **Simpan**
- **SOUTH**: Panel **Ringkasan** (`GridLayout 6×2`) → kiri judul statis, kanan label dinamis:
  - `lblKata`, `lblKarakter`, `lblKarakterTanpaSpasi`, `lblKalimat`, `lblParagraf`, `lblKemunculanKata`

---

## 📁 Struktur Proyek
AplikasiPenghitungKata/
├─ src/
│ └─ app/
│ ├─ FrmPenghitungKata.java # JFrame Form + logika
│ └─ Main.java # launcher
├─ nbproject/ # konfigurasi NetBeans (Ant)
├─ build/ # hasil build (otomatis)
├─ dist/ # JAR setelah Clean & Build
└─ README.md
