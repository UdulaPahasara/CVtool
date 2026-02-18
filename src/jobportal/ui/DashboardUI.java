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

    // Color Palette
    private final Color PRIMARY_COLOR = new Color(52, 152, 219); // Blue
    private final Color SECONDARY_COLOR = new Color(44, 62, 80); // Dark Blue
    private final Color BG_COLOR = new Color(244, 247, 246); // Light Grey
    private final Color TEXT_COLOR = new Color(52, 73, 94); // Dark Grey

    // Font
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public DashboardUI() {
        setTitle("Online Job Portal Resume Ranking System");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main Container
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Header
        mainPanel.add(createHeader(), BorderLayout.NORTH);

        // Center Content (Form + Output)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        centerPanel.add(createFormPanel());
        centerPanel.add(createOutputPanel());

        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titleLabel = new JLabel("Candidate Ranking System");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout(0, 15));
        formPanel.setOpaque(false);

        JPanel fieldsPanel = new JPanel(new GridLayout(6, 1, 0, 10));
        fieldsPanel.setOpaque(false);

        fieldsPanel.add(createLabeledField("Candidate ID:", idField = new JTextField()));
        fieldsPanel.add(createLabeledField("Name:", nameField = new JTextField()));

        // Skills Section
        JPanel skillsContainer = new JPanel(new BorderLayout(5, 5));
        skillsContainer.setOpaque(false);
        JLabel skillsLabel = new JLabel("Skills:");
        skillsLabel.setFont(LABEL_FONT);
        skillsLabel.setForeground(TEXT_COLOR);
        skillsContainer.add(skillsLabel, BorderLayout.NORTH);

        JPanel skillsInputPanel = new JPanel(new BorderLayout(5, 0));
        skillsInputPanel.setOpaque(false);

        String[] skills = { "Java", "SQL", "Python", "C++", "HTML/CSS", "JavaScript" };
        JComboBox<String> skillsBox = new JComboBox<>(skills);
        skillsBox.setFont(INPUT_FONT);
        skillsBox.setBackground(Color.WHITE);

        JButton addSkillBtn = createStyledButton("+", PRIMARY_COLOR);
        addSkillBtn.setPreferredSize(new Dimension(45, 30));

        skillsInputPanel.add(skillsBox, BorderLayout.CENTER);
        skillsInputPanel.add(addSkillBtn, BorderLayout.EAST);

        skillsContainer.add(skillsInputPanel, BorderLayout.CENTER);
        skillsField = new JTextField();
        skillsField.setFont(INPUT_FONT);
        skillsField.setEditable(false); // Make it read-only manual entry
        skillsContainer.add(skillsField, BorderLayout.SOUTH);

        fieldsPanel.add(skillsContainer);

        // Add Skill Action
        addSkillBtn.addActionListener(e -> {
            String current = skillsField.getText();
            String selected = (String) skillsBox.getSelectedItem();
            if (current.isEmpty()) {
                skillsField.setText(selected);
            } else {
                skillsField.setText(current + ", " + selected);
            }
        });

        fieldsPanel.add(createLabeledField("Experience (Years):", expField = new JTextField()));
        fieldsPanel.add(createLabeledField("Education:", eduField = new JTextField()));

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);

        JButton addBtn = createStyledButton("Add Candidate", PRIMARY_COLOR);
        JButton topBtn = createStyledButton("Show Best Candidate", SECONDARY_COLOR);

        addBtn.addActionListener(e -> addCandidate());
        topBtn.addActionListener(e -> showTop());

        buttonPanel.add(addBtn);
        buttonPanel.add(topBtn);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Wrapper for alignment
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(formPanel, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel createLabeledField(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);

        textField.setFont(INPUT_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel label = new JLabel("Results Log");
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        panel.add(label, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void addCandidate() {
        try {
            Candidate c = new Candidate(
                    idField.getText(),
                    nameField.getText(),
                    skillsField.getText(),
                    Integer.parseInt(expField.getText()),
                    eduField.getText());

            heap.insert(c);
            outputArea.append("✅ Inserted: " + c.getName() + "\n   Score: " + c.getTotalScore() + "\n\n");

            // Clear fields (optional)
            // idField.setText(""); nameField.setText(""); ...

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid details!", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTop() {
        Candidate top = heap.getTop();
        if (top != null) {
            outputArea.append("🏆 TOP CANDIDATE:\n   Name: " + top.getName() +
                    "\n   Score: " + top.getTotalScore() + "\n   Role: " + top.getJobRole() + "\n\n");
        } else {
            outputArea.append("⚠️ Heap is empty. No candidates.\n\n");
        }
    }
}
