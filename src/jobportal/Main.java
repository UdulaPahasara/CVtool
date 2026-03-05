package jobportal;

import jobportal.ui.DashboardUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new DashboardUI().setVisible(true));



    }
}