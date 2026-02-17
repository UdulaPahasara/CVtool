import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Online Job Portal Ranking System");
        frame.setSize(600,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JLabel label = new JLabel("Welcome to Resume Ranking System", SwingConstants.CENTER);
        frame.add(label);

        frame.setVisible(true);


    }
}