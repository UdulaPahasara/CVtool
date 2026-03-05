package jobportal.service;

import jobportal.heap.MaxHeap;
import jobportal.model.Candidate;
import jobportal.model.RecruitmentSummary;

import java.util.ArrayList;
import java.util.List;

public class RecruitmentService {

    private MaxHeap heap = new MaxHeap();

    public void addCandidate(Candidate c) {
        heap.insert(c);
    }

    public Candidate viewTopCandidate() {
        return heap.getTop();
    }

    public Candidate hireTopCandidate() {
        return heap.removeTop();
    }

    public List<Candidate> getRankedList() {
        List<Candidate> ranked = new ArrayList<>();
        MaxHeap temp = new MaxHeap();

        for (Candidate c : heap.getAllCandidates()) {
            temp.insert(c);
        }

        while (temp.size() > 0) {
            ranked.add(temp.removeTop());
        }

        return ranked;
    }

    public Candidate searchById(String id) {
        if (id == null)
            return null;
        String target = id.trim();

        for (Candidate c : heap.getAllCandidates()) {
            if (c.getId().equalsIgnoreCase(target)) {
                return c;
            }
        }
        return null;
    }

    public List<Candidate> filterBySkill(String skill) {
        List<Candidate> results = new ArrayList<>();
        if (skill == null || skill.trim().isEmpty())
            return results;

        String s = skill.trim().toLowerCase();

        for (Candidate c : heap.getAllCandidates()) {
            if (c.getSkills().toLowerCase().contains(s)) {
                results.add(c);
            }
        }
        return results;
    }


    public Candidate deleteById(String id) {
        return heap.removeById(id);
    }


    // Gives IDs like C001, C002, ...
    public String suggestNextId() {
        int max = 0;

        for (Candidate c : heap.getAllCandidates()) {
            String id = c.getId();
            if (id == null)
                continue;

            // Extract digits from any id format, e.g. C001, EMP-12, 7
            String digits = id.replaceAll("[^0-9]", "");
            if (!digits.isEmpty()) {
                try {
                    int n = Integer.parseInt(digits);
                    if (n > max)
                        max = n;
                } catch (Exception ignored) {
                }
            }
        }

        int next = max + 1;
        return String.format("C%03d", next);
    }

    //  Suggest Next ID based on a given string
    public String suggestNextId(String baseId) {
        if (baseId == null || baseId.isEmpty())
            return "C001";

        String digits = baseId.replaceAll("[^0-9]", "");
        String prefix = baseId.replaceAll("[0-9]", "");

        if (digits.isEmpty()) {
            return baseId + "1";
        }

        try {
            int n = Integer.parseInt(digits) + 1;
            // Try to maintain padding if the original had leading zeros and a fixed length
            String formatPattern = "%0" + digits.length() + "d";
            return prefix + String.format(formatPattern, n);
        } catch (Exception e) {
            return baseId + "1";
        }
    }

    // Summary
    public RecruitmentSummary getSummary() {
        RecruitmentSummary summary = new RecruitmentSummary();

        ArrayList<Candidate> list = heap.getAllCandidates();
        summary.totalCandidates = list.size();

        if (list.isEmpty())
            return summary;

        int totalScore = 0;

        summary.highestCandidate = list.get(0);
        summary.lowestCandidate = list.get(0);

        for (Candidate c : list) {
            totalScore += c.getTotalScore();

            if (c.getTotalScore() > summary.highestCandidate.getTotalScore())
                summary.highestCandidate = c;
            if (c.getTotalScore() < summary.lowestCandidate.getTotalScore())
                summary.lowestCandidate = c;

            if (c.getEducation().equalsIgnoreCase("PhD"))
                summary.phdCount++;
            else if (c.getEducation().equalsIgnoreCase("Masters"))
                summary.mastersCount++;
            else if (c.getEducation().equalsIgnoreCase("Degree"))
                summary.degreeCount++;
            else
                summary.othersCount++;
        }

        summary.averageScore = totalScore / (double) summary.totalCandidates;
        return summary;
    }

    public int size() {
        return heap.size();
    }
}