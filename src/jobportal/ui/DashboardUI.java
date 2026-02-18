package jobportal.ui;

import jobportal.heap.MaxHeap;
import jobportal.model.Candidate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DashboardUI extends JFrame {

    private JTextField idField, nameField, skillsField, expField, eduField;
    private JTextArea outputArea;

    private MaxHeap heap = new MaxHeap();

    public DashboardUI() {
        setTitle("Online Job Portal Resume Ranking System");
        setSize(600,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // Form Panel
        JPanel form = new JPanel(new GridLayout(6,2,10,10));

        form.add(new JLabel("Candidate ID:"));
        idField = new JTextField();
        form.add(idField);

        form.add(new JLabel("Name:"));
        nameField = new JTextField();
        form.add(nameField);

        form.add(new JLabel("Skills:"));
        skillsField = new JTextField();
        form.add(skillsField);

        form.add(new JLabel("Experience (Years):"));
        expField = new JTextField();
        form.add(expField);

        form.add(new JLabel("Education:"));
        eduField = new JTextField();
        form.add(eduField);

        JButton addBtn = new JButton("Add Candidate");
        form.add(addBtn);

        JButton topBtn = new JButton("Show Top Candidate");
        form.add(topBtn);

        add(form, BorderLayout.NORTH);

        // Output Area
        outputArea = new JTextArea();
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Button Actions
        addBtn.addActionListener((ActionEvent e) -> addCandidate());
        topBtn.addActionListener((ActionEvent e) -> showTop());
    }

    private void addCandidate() {
        try {
            Candidate c = new Candidate(
                    idField.getText(),
                    nameField.getText(),
                    skillsField.getText(),
                    Integer.parseInt(expField.getText()),
                    eduField.getText()
            );

            heap.insert(c);
            outputArea.append("Inserted: " + c.getName() + " | Score: " + c.getScore() + "\n");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid Input!");
        }
    }

    private void showTop() {
        Candidate top = heap.getTop();
        if (top != null) {
            outputArea.append("\nTOP Candidate: " + top.getName() +
                    " | Score: " + top.getScore() + "\n");
        } else {
            outputArea.append("Heap is empty\n");
        }
    }
}
