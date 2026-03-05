package jobportal.ui;

import jobportal.model.Candidate;
import jobportal.model.RecruitmentSummary;
import jobportal.service.RecruitmentService;
import jobportal.util.PDFParserUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
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

    // ✅ New: Preview/Confirm Add
    private Candidate pendingCandidate;
    private JTextArea previewArea;
    private JButton confirmAddBtn;

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
            if (cardKey.equals("ADD")) resetAddCardDefaults();
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
    // CARD 1: ADD CANDIDATE (Centered + Suggest ID + Preview + Confirm)
    // ------------------------------------------------------------
    private JPanel createAddCandidateCard() {
        JPanel card = createCardPanel();
        card.add(createCardTitle("Add Candidate (PDF Upload)"), BorderLayout.NORTH);

        // Wrapper centers the whole form in the card
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Consistent sizes (DON'T change box sizes)
        Dimension fieldSize = new Dimension(320, 34);
        Dimension btnSize = new Dimension(160, 38);

        // Row 0: Candidate ID label
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formGrid.add(makeLabel("Candidate ID:"), gbc);

        // Row 1: Candidate ID field + Suggest button
        addIdField = new JTextField();
        addIdField.setText(service.suggestNextId());
        setFieldSize(addIdField, fieldSize);

        JButton btnSuggestId = createPrimaryButton("Suggest ID");
        btnSuggestId.setPreferredSize(new Dimension(130, 34));
        btnSuggestId.addActionListener(e -> addIdField.setText(service.suggestNextId()));

        JPanel idRow = new JPanel(new BorderLayout(10, 0));
        idRow.setOpaque(false);
        idRow.add(addIdField, BorderLayout.CENTER);
        idRow.add(btnSuggestId, BorderLayout.EAST);

        gbc.gridy = 1;
        formGrid.add(idRow, gbc);

        // Row 2: Upload CV label
        gbc.gridy = 2;
        formGrid.add(makeLabel("Upload CV (PDF):"), gbc);

        // Row 3: Upload field
        addCvField = new JTextField();
        addCvField.setEditable(false);
        setFieldSize(addCvField, fieldSize);
        gbc.gridy = 3;
        formGrid.add(addCvField, gbc);

        // Row 4: Browse + Parse&Preview
        JButton browseBtn = createPrimaryButton("Browse PDF...");
        browseBtn.setPreferredSize(btnSize);
        browseBtn.addActionListener(e -> choosePdfFile());

        JButton previewBtn = createPrimaryButton("Parse & Preview");
        previewBtn.setPreferredSize(btnSize);
        previewBtn.addActionListener(e -> parseAndPreview());

        JPanel btnRow1 = new JPanel(new GridLayout(1, 2, 12, 0));
        btnRow1.setOpaque(false);
        btnRow1.add(browseBtn);
        btnRow1.add(previewBtn);

        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formGrid.add(btnRow1, gbc);

        // Row 5: Confirm Add (disabled until preview ok)
        confirmAddBtn = createPrimaryButton("Confirm Add");
        confirmAddBtn.setPreferredSize(new Dimension(330, 40));
        confirmAddBtn.setEnabled(false);
        confirmAddBtn.addActionListener(e -> confirmAddCandidate());

        gbc.gridy = 5;
        formGrid.add(confirmAddBtn, gbc);

        // Row 6: Preview area
        previewArea = new JTextArea(7, 30);
        styleTextArea(previewArea);
        previewArea.setText("Preview will appear here after parsing the PDF.");

        JScrollPane previewScroll = new JScrollPane(previewArea);
        previewScroll.setPreferredSize(new Dimension(460, 160));

        gbc.gridy = 6;
        formGrid.add(previewScroll, gbc);

        // Center it
        centerWrapper.add(formGrid, new GridBagConstraints());
        card.add(centerWrapper, BorderLayout.CENTER);
        return card;
    }

    private void resetAddCardDefaults() {
        if (addIdField != null && addIdField.getText().trim().isEmpty()) {
            addIdField.setText(service.suggestNextId());
        }
        if (previewArea != null && (previewArea.getText() == null || previewArea.getText().trim().isEmpty())) {
            previewArea.setText("Preview will appear here after parsing the PDF.");
        }
    }

    private void choosePdfFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Documents", "pdf"));
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            addCvField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    // ✅ Parse first and show preview
    private void parseAndPreview() {
        try {
            String cid = addIdField.getText().trim();
            String path = addCvField.getText().trim();

            if (cid.isEmpty() || path.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter Candidate ID and select a PDF CV.");
                return;
            }

            if (service.searchById(cid) != null) {
                JOptionPane.showMessageDialog(this, "Candidate ID already exists. Use a unique ID.");
                return;
            }

            pendingCandidate = PDFParserUtil.extractCandidateInfo(path, cid);

            previewArea.setText(
                    "PREVIEW CANDIDATE\n\n" +
                            "ID: " + pendingCandidate.getId() + "\n" +
                            "Name: " + pendingCandidate.getName() + "\n" +
                            "Role: " + pendingCandidate.getJobRole() + "\n" +
                            "Education: " + pendingCandidate.getEducation() + "\n" +
                            "Skills: " + pendingCandidate.getSkills() + "\n" +
                            "Experience Score: " + pendingCandidate.getExperienceScore() + "\n" +
                            "Total Score: " + pendingCandidate.getTotalScore()
            );

            confirmAddBtn.setEnabled(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            pendingCandidate = null;
            confirmAddBtn.setEnabled(false);
            previewArea.setText("Error parsing CV: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error parsing CV: " + ex.getMessage());
        }
    }

    // ✅ Add only after confirm
    private void confirmAddCandidate() {
        if (pendingCandidate == null) {
            JOptionPane.showMessageDialog(this, "No candidate preview found. Please Parse & Preview first.");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Confirm adding candidate?\n\n" + pendingCandidate.toString(),
                "Confirm Add",
                JOptionPane.YES_NO_OPTION
        );

        if (ok == JOptionPane.YES_OPTION) {
            service.addCandidate(pendingCandidate);

            JOptionPane.showMessageDialog(this,
                    "Candidate Added!\nName: " + pendingCandidate.getName() +
                            "\nScore: " + pendingCandidate.getTotalScore());

            pendingCandidate = null;
            confirmAddBtn.setEnabled(false);

            addIdField.setText(service.suggestNextId());
            addCvField.setText("");
            previewArea.setText("Preview will appear here after parsing the PDF.");
        }
    }

    // (Keep old method if you still call it anywhere; otherwise optional)
    private void addCandidateFromPdf() {
        // You can still keep this if another place calls it,
        // but your Add Card now uses Parse & Preview + Confirm Add.
        parseAndPreview();
        // Do not auto-confirm here.
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

    // CARD 3: RANKED LIST + Export CSV
    private JPanel createRankedCard() {
        JPanel card = createCardPanel();
        card.add(createCardTitle("Ranked List (True Sorted)"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);

        JButton exportBtn = createPrimaryButton("Export CSV");
        exportBtn.addActionListener(e -> exportRankedCsv());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topBar.setOpaque(false);
        topBar.add(exportBtn);

        content.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Rank", "ID", "Name", "Role", "Score"};
        rankedModel = new DefaultTableModel(cols, 0);
        rankedTable = new JTable(rankedModel);
        styleTable(rankedTable);

        content.add(new JScrollPane(rankedTable), BorderLayout.CENTER);

        card.add(content, BorderLayout.CENTER);
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

    private void exportRankedCsv() {
        List<Candidate> ranked = service.getRankedList();
        if (ranked.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No candidates to export.");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Ranked List as CSV");
        chooser.setSelectedFile(new File("ranked_candidates.csv"));

        int res = chooser.showSaveDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();

        try (PrintWriter out = new PrintWriter(file, "UTF-8")) {
            out.println("Rank,ID,Name,Role,Skills,Education,ExperienceScore,TotalScore");

            int rank = 1;
            for (Candidate c : ranked) {
                out.println(
                        rank++ + "," +
                                csv(c.getId()) + "," +
                                csv(c.getName()) + "," +
                                csv(c.getJobRole()) + "," +
                                csv(c.getSkills()) + "," +
                                csv(c.getEducation()) + "," +
                                c.getExperienceScore() + "," +
                                c.getTotalScore()
                );
            }

            JOptionPane.showMessageDialog(this, "Exported successfully:\n" + file.getAbsolutePath());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }

    private String csv(String s) {
        if (s == null) return "";
        String v = s.replace("\"", "\"\"");
        if (v.contains(",") || v.contains("\n") || v.contains("\r")) {
            return "\"" + v + "\"";
        }
        return v;
    }

    // CARD 4: SEARCH + Delete by ID
    private JPanel createSearchCard() {
        JPanel card = createCardPanel();
        card.add(createCardTitle("Search Candidate by ID"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);

        searchIdField = new JTextField();
        top.add(createMiniField("Enter Candidate ID:", searchIdField), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);

        JButton searchBtn = createPrimaryButton("Search");
        searchBtn.addActionListener(e -> doSearchById());
        btns.add(searchBtn);

        JButton deleteBtn = createPrimaryButton("Delete");
        deleteBtn.addActionListener(e -> doDeleteById());
        btns.add(deleteBtn);

        top.add(btns, BorderLayout.EAST);

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

    private void doDeleteById() {
        String id = searchIdField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Candidate ID to delete.");
            return;
        }

        Candidate c = service.searchById(id);
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Candidate not found for ID: " + id);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete?\n\n" + c.toString(),
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (ok == JOptionPane.YES_OPTION) {
            Candidate removed = service.deleteById(id);
            if (removed != null) {
                searchResultArea.setText("Deleted: " + removed.toString());
                refreshRanked();
                refreshSummary();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }
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

    // Helper label for consistent style
    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT);
        l.setForeground(TEXT_COLOR);
        return l;
    }

    // Keep field sizes consistent (DON'T change sizes)
    private void setFieldSize(JTextField field, Dimension size) {
        field.setPreferredSize(size);
        field.setMinimumSize(size);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, size.height));
        field.setFont(LABEL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
    }
}