package jobportal.ui;

import jobportal.heap.MaxHeap;
import jobportal.model.Candidate;
import jobportal.util.PDFParserUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;

public class DashboardUI extends JFrame {

    private JTextField idField, cvField;
    private JPanel outputPanel;
    private MaxHeap heap = new MaxHeap();
    private JTable table;
    private JTextArea summaryArea;
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

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        fieldsPanel.add(createLabeledField("Candidate ID:", idField = new JTextField()));
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Upload CV Section
        JPanel cvContainer = new JPanel(new BorderLayout(5, 5));
        cvContainer.setOpaque(false);

        JLabel cvLabel = new JLabel("Upload CV (PDF):");
        cvLabel.setFont(LABEL_FONT);
        cvLabel.setForeground(TEXT_COLOR);
        cvContainer.add(cvLabel, BorderLayout.NORTH);

        JPanel cvInputPanel = new JPanel(new BorderLayout(5, 0));
        cvInputPanel.setOpaque(false);

        cvField = new JTextField();
        cvField.setFont(INPUT_FONT);
        cvField.setEditable(false);
        cvField.setBackground(Color.WHITE);
        cvField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton browseBtn = createStyledButton("Browse...", PRIMARY_COLOR);
        browseBtn.setPreferredSize(new Dimension(100, 30));
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Documents", "pdf");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                cvField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        cvInputPanel.add(cvField, BorderLayout.CENTER);
        cvInputPanel.add(browseBtn, BorderLayout.EAST);
        cvContainer.add(cvInputPanel, BorderLayout.CENTER);

        fieldsPanel.add(cvContainer);
        fieldsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        // Remove old manual input containers

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setOpaque(false);

        JButton addBtn = createStyledButton("Add Candidate", PRIMARY_COLOR);
        JButton topBtn = createStyledButton("Show Best Candidate", SECONDARY_COLOR);
        JButton listBtn = createStyledButton("Show Ranked List", PRIMARY_COLOR);
        JButton summaryBtn = createStyledButton("View Summary", SECONDARY_COLOR);

        addBtn.addActionListener(e -> addCandidate());
        topBtn.addActionListener(e -> showTop());
        listBtn.addActionListener(e -> showRankedList());
        summaryBtn.addActionListener(e -> showSummary());

        buttonPanel.add(addBtn);
        buttonPanel.add(topBtn);
        buttonPanel.add(listBtn);
        buttonPanel.add(summaryBtn);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Wrapper for alignment
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(formPanel, BorderLayout.NORTH);
        return wrapper;
    }

    private void showRankedList() {
        outputPanel.removeAll();

        if (heap.size() == 0) {
            JLabel empty = new JLabel("No candidates available.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            outputPanel.add(empty, BorderLayout.CENTER);
        } else {
            String[] columns = { "Rank", "Name", "Role", "Skills", "Score" };
            Object[][] data = new Object[heap.size()][5];

            MaxHeap tempHeap = new MaxHeap();
            for (Candidate c : heap.getAllCandidates()) {
                tempHeap.insert(c);
            }

            int i = 0;
            while (tempHeap.size() > 0) {
                Candidate c = tempHeap.removeTop();
                data[i][0] = i + 1;
                data[i][1] = c.getName();
                data[i][2] = c.getJobRole();
                data[i][3] = c.getSkills();
                data[i][4] = c.getTotalScore();
                i++;
            }

            JTable table = new JTable(data, columns);
            table.setFillsViewportHeight(true);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            table.setRowHeight(25);
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            table.getTableHeader().setBackground(PRIMARY_COLOR);
            table.getTableHeader().setForeground(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(table);
            outputPanel.add(scrollPane, BorderLayout.CENTER);
        }

        outputPanel.revalidate();
        outputPanel.repaint();
    }

    private void showSummary() {
        outputPanel.removeAll();

        if (heap.size() == 0) {
            JLabel empty = new JLabel("No data for summary.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            outputPanel.add(empty, BorderLayout.CENTER);
        } else {
            int total = heap.size();
            int totalScore = 0;

            for (Candidate c : heap.getAllCandidates()) {
                totalScore += c.getTotalScore();
            }

            double avg = (double) totalScore / total;

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel header = new JLabel("📊 RECRUITMENT SUMMARY");
            header.setFont(new Font("Segoe UI", Font.BOLD, 18));
            header.setForeground(PRIMARY_COLOR);
            panel.add(header);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));

            panel.add(createInfoLabel("Total Candidates: " + total));
            panel.add(createInfoLabel("Average Score: " + avg));

            outputPanel.add(panel, BorderLayout.CENTER);
        }

        outputPanel.revalidate();
        outputPanel.repaint();
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

    private JPanel createOutputPanel() { // CHANGE HERE
        outputPanel = new JPanel(new BorderLayout(0, 10));
        outputPanel.setOpaque(false);

        JLabel label = new JLabel("Results Log");
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        outputPanel.add(label, BorderLayout.NORTH);

        summaryArea = new JTextArea();
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        summaryArea.setEditable(false);
        summaryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        outputPanel.add(scrollPane, BorderLayout.CENTER);
        return outputPanel;
    }

    private void addCandidate() {
        try {
            String cvPath = cvField.getText();
            String cid = idField.getText();

            if (cvPath == null || cvPath.isEmpty() || cid == null || cid.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide Candidate ID and upload a CV.");
                return;
            }

            Candidate c = PDFParserUtil.extractCandidateInfo(cvPath, cid);

            heap.insert(c);

            // ---------------- MODERN VISUALIZATION ----------------
            outputPanel.removeAll();

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel header = new JLabel(" Candidate Added Successfully");
            header.setFont(new Font("Segoe UI", Font.BOLD, 16));
            header.setForeground(PRIMARY_COLOR);
            panel.add(header);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));

            panel.add(createInfoLabel("Name: " + c.getName()));
            panel.add(createInfoLabel("Role: " + c.getJobRole()));
            panel.add(createInfoLabel("Skills: " + c.getSkills()));
            panel.add(createInfoLabel("Experience (Parsed): " + c.getExperience()));
            panel.add(createInfoLabel("Education: " + c.getEducation()));
            panel.add(createInfoLabel("Total Score: " + c.getTotalScore()));

            outputPanel.add(panel, BorderLayout.CENTER);
            outputPanel.revalidate();
            outputPanel.repaint();
            // ---------------- END MODERN VISUALIZATION ----------------

            // Clear fields after adding
            idField.setText("");
            if (cvField != null)
                cvField.setText("");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error parsing CV: " + ex.getMessage());
        }
    }

    private void showTop() {
        outputPanel.removeAll();

        Candidate top = heap.getTop();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        if (top != null) {
            JLabel header = new JLabel("🏆 TOP CANDIDATE");
            header.setFont(new Font("Segoe UI", Font.BOLD, 18));
            header.setForeground(PRIMARY_COLOR);
            panel.add(header);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));

            panel.add(createInfoLabel("Name: " + top.getName()));
            panel.add(createInfoLabel("Role: " + top.getJobRole()));
            panel.add(createInfoLabel("Skills: " + top.getSkills()));
            panel.add(createInfoLabel("Score: " + top.getTotalScore()));
        } else {
            JLabel empty = new JLabel("No candidates available.");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            panel.add(empty);
        }

        outputPanel.add(panel, BorderLayout.CENTER);
        outputPanel.revalidate();
        outputPanel.repaint();
    }

    // Helper method for styled info labels
    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(TEXT_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        return label;
    }
}
