package jobportal.ui;

import jobportal.model.Candidate;
import jobportal.model.RecruitmentSummary;
import jobportal.service.RecruitmentService;
import jobportal.util.PDFParserUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardUI extends JFrame {

    private final RecruitmentService service = new RecruitmentService();

    // Colors
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color SECONDARY_COLOR = new Color(44, 62, 80);
    private final Color BG_COLOR = new Color(244, 247, 246);
    private final Color TEXT_COLOR = new Color(52, 73, 94);
    private final Color CARD_BG = Color.WHITE;

    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BTN_FONT = new Font("Segoe UI", Font.BOLD, 14);

    // Card layout for right side
    private CardLayout cardLayout;
    private JPanel rightCards;

    // Common components
    private JTextField addIdField, addCvField;

    private JTable rankedTable;
    private DefaultTableModel rankedModel;

    private JTextField searchIdField;
    private JTextArea searchResultArea;

    private JComboBox<String> filterSkillBox;
    private JTable filterTable;
    private DefaultTableModel filterModel;

    private JTextArea topArea;
    private JTextArea hireArea;

    private JTextArea summaryArea;

    public DashboardUI() {
        setTitle("Job Portal Resume Ranking System");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_COLOR);
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createBody(), BorderLayout.CENTER);

        // Default view
        showCard("ADD");
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("Candidate Ranking System");
        title.setForeground(Color.WHITE);
        title.setFont(HEADER_FONT);

        header.add(title, BorderLayout.WEST);
        return header;
    }

    private JPanel createBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG_COLOR);

        body.add(createSidebar(), BorderLayout.WEST);
        body.add(createRightCards(), BorderLayout.CENTER);

        return body;
    }

    // LEFT SIDEBAR
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SECONDARY_COLOR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));

        sidebar.add(createSidebarButton("Add Candidate", "ADD"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        sidebar.add(createSidebarButton("View Top Candidate", "TOP"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        sidebar.add(createSidebarButton("Show Ranked List", "RANKED"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        sidebar.add(createSidebarButton("Search by ID", "SEARCH"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        sidebar.add(createSidebarButton("Filter by Skill", "FILTER"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        sidebar.add(createSidebarButton("Hire", "HIRE"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        sidebar.add(createSidebarButton("Recruitment Summary", "SUMMARY"));

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton createSidebarButton(String text, String cardKey) {
        JButton btn = new JButton(text);
        btn.setFont(BTN_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(52, 73, 94));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        btn.addActionListener(e -> {
            showCard(cardKey);

            // Refresh views when clicked
            if (cardKey.equals("TOP")) refreshTop();
            if (cardKey.equals("RANKED")) refreshRanked();
            if (cardKey.equals("SUMMARY")) refreshSummary();
        });

        return btn;
    }

    // RIGHT SIDE CARDS
    private JPanel createRightCards() {
        cardLayout = new CardLayout();
        rightCards = new JPanel(cardLayout);
        rightCards.setBackground(BG_COLOR);
        rightCards.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        rightCards.add(createAddCandidateCard(), "ADD");
        rightCards.add(createTopCard(), "TOP");
        rightCards.add(createRankedCard(), "RANKED");
        rightCards.add(createSearchCard(), "SEARCH");
        rightCards.add(createFilterCard(), "FILTER");
        rightCards.add(createHireCard(), "HIRE");
        rightCards.add(createSummaryCard(), "SUMMARY");

        return rightCards;
    }

    private void showCard(String key) {
        cardLayout.show(rightCards, key);
    }

    // ------------------------------------------------------------
    // CARD 1: ADD CANDIDATE (PDF UPLOAD)  ✅ FIXED SMALL INPUT BOXES
    // ------------------------------------------------------------
    private JPanel createAddCandidateCard() {
        JPanel card = createCardPanel();

        JLabel title = createCardTitle("Add Candidate (PDF Upload)");
        card.add(title, BorderLayout.NORTH);

        JPanel formWrapper = new JPanel();
        formWrapper.setOpaque(false);
        formWrapper.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); // ✅ prevents stretching

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setPreferredSize(new Dimension(420, 260)); // ✅ controls form width

        addIdField = new JTextField();
        form.add(createFieldBlock("Candidate ID:", addIdField));

        addCvField = new JTextField();
        addCvField.setEditable(false);
        form.add(createFieldBlock("Upload CV (PDF):", addCvField));

        JButton browse = createPrimaryButton("Browse PDF...");
        browse.setAlignmentX(Component.LEFT_ALIGNMENT);
        browse.addActionListener(e -> choosePdfFile());
        form.add(browse);

        form.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton addBtn = createPrimaryButton("Add Candidate");
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addBtn.addActionListener(e -> addCandidateFromPdf());
        form.add(addBtn);

        formWrapper.add(form);
        card.add(formWrapper, BorderLayout.CENTER);

        return card;
    }

    private void choosePdfFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Documents", "pdf"));
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            addCvField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void addCandidateFromPdf() {
        try {
            String cid = addIdField.getText().trim();
            String path = addCvField.getText().trim();

            if (cid.isEmpty() || path.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Candidate ID and select a PDF CV.");
                return;
            }

            // Prevent duplicate IDs
            if (service.searchById(cid) != null) {
                JOptionPane.showMessageDialog(this, "Candidate ID already exists. Use a unique ID.");
                return;
            }

            Candidate c = PDFParserUtil.extractCandidateInfo(path, cid);
            service.addCandidate(c);

            JOptionPane.showMessageDialog(this,
                    "Candidate Added!\nName: " + c.getName() + "\nScore: " + c.getTotalScore());

            addIdField.setText("");
            addCvField.setText("");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error parsing CV: " + ex.getMessage());
        }
    }

    // CARD 2: TOP CANDIDATE
    private JPanel createTopCard() {
        JPanel card = createCardPanel();
        card.add(createCardTitle("Top Candidate"), BorderLayout.NORTH);

        topArea = new JTextArea();
        styleTextArea(topArea);
        card.add(new JScrollPane(topArea), BorderLayout.CENTER);
        return card;
    }

    private void refreshTop() {
        Candidate top = service.viewTopCandidate();
        if (top == null) {
            topArea.setText("No candidates available.");
            return;
        }

        topArea.setText(
                "TOP CANDIDATE\n\n" +
                        "ID: " + top.getId() + "\n" +
                        "Name: " + top.getName() + "\n" +
                        "Role: " + top.getJobRole() + "\n" +
                        "Education: " + top.getEducation() + "\n" +
                        "Skills: " + top.getSkills() + "\n" +
                        "Experience Score: " + top.getExperienceScore() + "\n" +
                        "Total Score: " + top.getTotalScore()
        );
    }

    // CARD 3: RANKED LIST
    private JPanel createRankedCard() {
        JPanel card = createCardPanel();
        card.add(createCardTitle("Ranked List (True Sorted)"), BorderLayout.NORTH);

        String[] cols = {"Rank", "ID", "Name", "Role", "Score"};
        rankedModel = new DefaultTableModel(cols, 0);
        rankedTable = new JTable(rankedModel);
        styleTable(rankedTable);

        card.add(new JScrollPane(rankedTable), BorderLayout.CENTER);
        return card;
    }

    private void refreshRanked() {
        rankedModel.setRowCount(0);

        List<Candidate> ranked = service.getRankedList();
        if (ranked.isEmpty()) {
            rankedModel.addRow(new Object[]{"-", "-", "No candidates", "-", "-"});
            return;
        }

        int rank = 1;
        for (Candidate c : ranked) {
            rankedModel.addRow(new Object[]{
                    rank++,
                    c.getId(),
                    c.getName(),
                    c.getJobRole(),
                    c.getTotalScore()
            });
        }
    }

    // CARD 4: SEARCH
    private JPanel createSearchCard() {
        JPanel card = createCardPanel();
        card.add(createCardTitle("Search Candidate by ID"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);

        searchIdField = new JTextField();
        top.add(createMiniField("Enter Candidate ID:", searchIdField), BorderLayout.CENTER);

        JButton searchBtn = createPrimaryButton("Search");
        searchBtn.addActionListener(e -> doSearchById());
        top.add(searchBtn, BorderLayout.EAST);

        searchResultArea = new JTextArea();
        styleTextArea(searchResultArea);

        content.add(top, BorderLayout.NORTH);
        content.add(new JScrollPane(searchResultArea), BorderLayout.CENTER);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private void doSearchById() {
        String id = searchIdField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Candidate ID to search.");
            return;
        }

        Candidate c = service.searchById(id);
        if (c == null) {
            searchResultArea.setText("Candidate not found for ID: " + id);
        } else {
            searchResultArea.setText(
                    "CANDIDATE FOUND\n\n" +
                            "ID: " + c.getId() + "\n" +
                            "Name: " + c.getName() + "\n" +
                            "Role: " + c.getJobRole() + "\n" +
                            "Education: " + c.getEducation() + "\n" +
                            "Skills: " + c.getSkills() + "\n" +
                            "Experience Score: " + c.getExperienceScore() + "\n" +
                            "Total Score: " + c.getTotalScore()
            );
        }
    }

    // CARD 5: FILTER
    private JPanel createFilterCard() {
        JPanel card = createCardPanel();
        card.add(createCardTitle("Filter Candidates by Skill"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);

        String[] skills = {"Java", "Python", "SQL", "C#", "C", "C++", "JavaScript", "HTML/CSS", "Spring-Boot", "React"};
        filterSkillBox = new JComboBox<>(skills);

        top.add(createMiniField("Select Skill:", filterSkillBox), BorderLayout.CENTER);

        JButton filterBtn = createPrimaryButton("Filter");
        filterBtn.addActionListener(e -> refreshFilter());
        top.add(filterBtn, BorderLayout.EAST);

        String[] cols = {"ID", "Name", "Role", "Skills", "Score"};
        filterModel = new DefaultTableModel(cols, 0);
        filterTable = new JTable(filterModel);
        styleTable(filterTable);

        content.add(top, BorderLayout.NORTH);
        content.add(new JScrollPane(filterTable), BorderLayout.CENTER);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private void refreshFilter() {
        filterModel.setRowCount(0);

        String skill = (String) filterSkillBox.getSelectedItem();
        List<Candidate> list = service.filterBySkill(skill);

        if (list.isEmpty()) {
            filterModel.addRow(new Object[]{"-", "No matches", "-", skill, "-"});
            return;
        }

        for (Candidate c : list) {
            filterModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getJobRole(),
                    c.getSkills(),
                    c.getTotalScore()
            });
        }
    }

    // CARD 6: HIRE
    private JPanel createHireCard() {
        JPanel card = createCardPanel();
        card.add(createCardTitle("Hire Top Candidate"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);

        JButton hireBtn = createPrimaryButton("Hire Top Candidate");
        hireBtn.addActionListener(e -> doHire());

        hireArea = new JTextArea();
        styleTextArea(hireArea);

        content.add(hireBtn, BorderLayout.NORTH);
        content.add(new JScrollPane(hireArea), BorderLayout.CENTER);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private void doHire() {
        Candidate hired = service.hireTopCandidate();
        if (hired == null) {
            hireArea.setText("No candidates to hire (heap is empty).");
            return;
        }

        hireArea.setText(
                "HIRED TOP CANDIDATE\n\n" +
                        "ID: " + hired.getId() + "\n" +
                        "Name: " + hired.getName() + "\n" +
                        "Role: " + hired.getJobRole() + "\n" +
                        "Total Score: " + hired.getTotalScore() + "\n\n" +
                        "Remaining Candidates: " + service.size()
        );
    }

    // CARD 7: SUMMARY
    private JPanel createSummaryCard() {
        JPanel card = createCardPanel();
        card.add(createCardTitle("Recruitment Summary"), BorderLayout.NORTH);

        summaryArea = new JTextArea();
        styleTextArea(summaryArea);
        card.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        return card;
    }

    private void refreshSummary() {
        RecruitmentSummary s = service.getSummary();

        if (s.totalCandidates == 0) {
            summaryArea.setText("No data available for summary.");
            return;
        }

        summaryArea.setText(
                "RECRUITMENT SUMMARY\n\n" +
                        "Total Candidates: " + s.totalCandidates + "\n" +
                        "Average Score: " + String.format("%.2f", s.averageScore) + "\n\n" +
                        "Highest Candidate: " + s.highestCandidate.getName() + " (Score: " + s.highestCandidate.getTotalScore() + ")\n" +
                        "Lowest Candidate: " + s.lowestCandidate.getName() + " (Score: " + s.lowestCandidate.getTotalScore() + ")\n\n" +
                        "Education Distribution:\n" +
                        "  PhD: " + s.phdCount + "\n" +
                        "  Masters: " + s.mastersCount + "\n" +
                        "  Degree: " + s.degreeCount + "\n" +
                        "  Others: " + s.othersCount + "\n"
        );
    }

    // ---------------- UI HELPERS ----------------

    private JPanel createCardPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return p;
    }

    private JLabel createCardTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    // ✅ SMALLER FIELD BLOCK (prevents stretching)
    private JPanel createFieldBlock(String label, JTextField field) {
        JPanel block = new JPanel();
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        block.setOpaque(false);
        block.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(LABEL_FONT);
        l.setForeground(TEXT_COLOR);

        field.setFont(LABEL_FONT);

        // ✅ control width and height
        field.setPreferredSize(new Dimension(250, 32));
        field.setMaximumSize(new Dimension(350, 32));
        field.setMinimumSize(new Dimension(200, 32));

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        block.add(l);
        block.add(Box.createRigidArea(new Dimension(0, 5)));
        block.add(field);
        block.add(Box.createRigidArea(new Dimension(0, 15)));

        return block;
    }

    private JPanel createMiniField(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(LABEL_FONT);
        l.setForeground(TEXT_COLOR);

        comp.setFont(LABEL_FONT);

        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BTN_FONT);
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        return btn;
    }

    private void styleTextArea(JTextArea area) {
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setForeground(TEXT_COLOR);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
    }
}