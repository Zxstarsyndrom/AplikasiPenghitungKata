package app;

public class main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            FrmPenghitungKata f = new FrmPenghitungKata();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
