package app;
import java.text.BreakIterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultHighlighter;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author SYARIF
 */
public class FrmPenghitungKata extends javax.swing.JFrame {

    /**
     * Creates new form FrmPenghitungKata
     */
    public FrmPenghitungKata() {
        initComponents();

        setTitle("Aplikasi Penghitung Kata");
        setLocationRelativeTo(null);

        // ❌ DIHAPUS: DocumentListener (agar tidak real-time)
        // txtInput.getDocument().addDocumentListener(new DocumentListener() {
        //     @Override public void insertUpdate(DocumentEvent e) { hitungDanTampil(); }
        //     @Override public void removeUpdate(DocumentEvent e) { hitungDanTampil(); }
        //     @Override public void changedUpdate(DocumentEvent e) { hitungDanTampil(); }
        // });

        // ✅ Hitung hanya saat tombol ditekan
        btnHitung.addActionListener(e -> hitungDanTampil());

        btnBersihkan.addActionListener(e -> {
            txtInput.setText("");
            bersihkanHighlight();
            tampilHasil(0,0,0,0,0,0);
            kataTarget = "";
        });

        btnCari.addActionListener(e -> aksiCariKata());
        btnSimpan.addActionListener(e -> aksiSimpan());

        // ❌ DIHAPUS: jangan hitung otomatis saat start
        // hitungDanTampil();

        // ✅ Tampilkan nilai awal 0
        tampilHasil(0,0,0,0,0,0);
    }

    private final DefaultHighlighter.DefaultHighlightPainter highlightPainter =
        new DefaultHighlighter.DefaultHighlightPainter(
            UIManager.getColor("TextField.selectionBackground")
        );

    private String kataTarget = "";

    private void hitungDanTampil() {
        String teks = txtInput.getText();
        int jKata = hitungKataUnicode(teks);
        int jKar = teks.length();
        int jKarNoSpace = teks.replaceAll("\\s+", "").length();
        int jKalimat = hitungKalimat(teks);
        int jParagraf = hitungParagraf(teks);
        int jMuncul = (kataTarget == null || kataTarget.isBlank()) ? 0 : hitungKemunculanKata(teks, kataTarget);
        tampilHasil(jKata, jKar, jKarNoSpace, jKalimat, jParagraf, jMuncul);
    }

    private int hitungKataUnicode(String teks) {
        BreakIterator it = BreakIterator.getWordInstance(new Locale("id", "ID"));
        it.setText(teks);
        int count = 0, start = it.first();
        for (int end = it.next(); end != BreakIterator.DONE; start = end, end = it.next()) {
            String token = teks.substring(start, end).trim();
            if (!token.isEmpty() && Character.isLetterOrDigit(token.codePointAt(0))) count++;
        }
        return count;
    }

    private int hitungKalimat(String teks) {
        String t = teks.trim();
        if (t.isEmpty()) return 0;
        String[] parts = t.split("(?<=[.!?])\\s+");
        int c = 0; for (String p : parts) if (!p.trim().isEmpty()) c++; return c;
    }

    private int hitungParagraf(String teks) {
        String t = teks.trim();
        if (t.isEmpty()) return 0;
        String[] parts = t.split("(\\r?\\n){2,}");
        int c = 0; for (String p : parts) if (!p.trim().isEmpty()) c++; return c;
    }

    private void aksiCariKata() {
        String in = JOptionPane.showInputDialog(this, "Masukkan kata yang dicari:", kataTarget);
        if (in == null) return; // Cancel
        kataTarget = in.trim();
        bersihkanHighlight();
        int found = 0;
        if (!kataTarget.isEmpty()) found = highlightSemuaKemunculan(txtInput, kataTarget);

        // ❌ DIHAPUS: jangan memicu hitung otomatis di sini
        // hitungDanTampil();

        // Info ke user: tekan Hitung untuk memperbarui ringkasan
        JOptionPane.showMessageDialog(this,
            "Ditemukan: " + found + " kemunculan.\n" +
            "Tekan tombol 'Hitung' untuk memperbarui angka ringkasan.");
    }

    private int hitungKemunculanKata(String teks, String target) {
        Matcher m = Pattern.compile(Pattern.quote(target),
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(teks);
        int c = 0; while (m.find()) c++; return c;
    }

    private int highlightSemuaKemunculan(JTextArea area, String target) {
        String teks = area.getText();
        Matcher m = Pattern.compile(Pattern.quote(target),
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(teks);
        int c = 0;
        while (m.find()) {
            try { area.getHighlighter().addHighlight(m.start(), m.end(), highlightPainter); c++; }
            catch (Exception ignored) {}
        }
        return c;
    }

    private void bersihkanHighlight() {
        txtInput.getHighlighter().removeAllHighlights();
    }

    private void aksiSimpan() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan Teks & Hasil");
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".txt")) {
                f = new java.io.File(f.getAbsolutePath() + ".txt");
            }
            try (java.io.PrintWriter pw = new java.io.PrintWriter(
                    f, java.nio.charset.StandardCharsets.UTF_8)) {
                pw.println("=== TEKS ===");
                pw.println(txtInput.getText());
                pw.println();
                pw.println("=== HASIL ===");
                pw.printf("Kata: %s%n", lblKata.getText());
                pw.printf("Karakter (dengan spasi): %s%n", lblKarakter.getText());
                pw.printf("Karakter (tanpa spasi): %s%n", lblKarakterTanpaSpasi.getText());
                pw.printf("Kalimat: %s%n", lblKalimat.getText());
                pw.printf("Paragraf: %s%n", lblParagraf.getText());
                pw.printf("Kemunculan \"%s\": %s%n", kataTarget, lblKemunculanKata.getText());
                JOptionPane.showMessageDialog(this, "Berhasil disimpan: " + f.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void tampilHasil(int kata, int kar, int karNoSpace, int kalimat, int paragraf, int muncul) {
        lblKata.setText(String.valueOf(kata));
        lblKarakter.setText(String.valueOf(kar));
        lblKarakterTanpaSpasi.setText(String.valueOf(karNoSpace));
        lblKalimat.setText(String.valueOf(kalimat));
        lblParagraf.setText(String.valueOf(paragraf));
        lblKemunculanKata.setText(String.valueOf(muncul));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        center = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtInput = new javax.swing.JTextArea();
        south = new javax.swing.JPanel();
        lblKata = new javax.swing.JLabel();
        lblKarakter = new javax.swing.JLabel();
        lblKarakterTanpaSpasi = new javax.swing.JLabel();
        lblKalimat = new javax.swing.JLabel();
        lblParagraf = new javax.swing.JLabel();
        lblKemunculanKata = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        north = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        easth = new javax.swing.JPanel();
        btnHitung = new javax.swing.JButton();
        btnBersihkan = new javax.swing.JButton();
        btnCari = new javax.swing.JButton();
        btnSimpan = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Aplikasi Penghitung Kata");

        txtInput.setColumns(20);
        txtInput.setLineWrap(true);
        txtInput.setRows(5);
        txtInput.setWrapStyleWord(true);
        jScrollPane1.setViewportView(txtInput);

        javax.swing.GroupLayout centerLayout = new javax.swing.GroupLayout(center);
        center.setLayout(centerLayout);
        centerLayout.setHorizontalGroup(
            centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
        );
        centerLayout.setVerticalGroup(
            centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
        );

        getContentPane().add(center, java.awt.BorderLayout.CENTER);

        jLabel2.setText("Kata                                 :");

        jLabel3.setText("Karakter (dengan spasi) :");

        jLabel4.setText("Karakter (tanpa spasi)    :");

        jLabel5.setText("Kalimat                            :");

        jLabel6.setText("Paragraf                  :");

        jLabel7.setText("Kemunculan kata    :");

        javax.swing.GroupLayout southLayout = new javax.swing.GroupLayout(south);
        south.setLayout(southLayout);
        southLayout.setHorizontalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(southLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(southLayout.createSequentialGroup()
                        .addGroup(southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblKarakter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblKarakterTanpaSpasi, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblKata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(southLayout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblParagraf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(southLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblKemunculanKata, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(southLayout.createSequentialGroup()
                        .addComponent(lblKalimat, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 105, Short.MAX_VALUE)))
                .addContainerGap())
        );
        southLayout.setVerticalGroup(
            southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(southLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKata)
                    .addComponent(lblParagraf)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKarakter)
                    .addComponent(lblKemunculanKata)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKarakterTanpaSpasi)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(southLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblKalimat)
                    .addComponent(jLabel5))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        getContentPane().add(south, java.awt.BorderLayout.SOUTH);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Aplikasi Penghitung Kata");

        javax.swing.GroupLayout northLayout = new javax.swing.GroupLayout(north);
        north.setLayout(northLayout);
        northLayout.setHorizontalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addGap(118, 118, 118)
                .addComponent(jLabel1)
                .addContainerGap(159, Short.MAX_VALUE))
        );
        northLayout.setVerticalGroup(
            northLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(northLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        getContentPane().add(north, java.awt.BorderLayout.NORTH);

        btnHitung.setText("Hitung");

        btnBersihkan.setText("Bersih");

        btnCari.setText("Cari");

        btnSimpan.setText("Simpan");

        javax.swing.GroupLayout easthLayout = new javax.swing.GroupLayout(easth);
        easth.setLayout(easthLayout);
        easthLayout.setHorizontalGroup(
            easthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(easthLayout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(easthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnHitung, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBersihkan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCari, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSimpan, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        easthLayout.setVerticalGroup(
            easthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(easthLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnHitung)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBersihkan)
                .addGap(10, 10, 10)
                .addComponent(btnSimpan)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 152, Short.MAX_VALUE)
                .addComponent(btnCari)
                .addContainerGap())
        );

        getContentPane().add(easth, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmPenghitungKata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmPenghitungKata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmPenghitungKata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmPenghitungKata.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmPenghitungKata().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBersihkan;
    private javax.swing.JButton btnCari;
    private javax.swing.JButton btnHitung;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JPanel center;
    private javax.swing.JPanel easth;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblKalimat;
    private javax.swing.JLabel lblKarakter;
    private javax.swing.JLabel lblKarakterTanpaSpasi;
    private javax.swing.JLabel lblKata;
    private javax.swing.JLabel lblKemunculanKata;
    private javax.swing.JLabel lblParagraf;
    private javax.swing.JPanel north;
    private javax.swing.JPanel south;
    private javax.swing.JTextArea txtInput;
    // End of variables declaration//GEN-END:variables
}
