package jobportal.service;

import jobportal.heap.MaxHeap;
import jobportal.model.Candidate;

import java.util.ArrayList;

public class RecruitmentService {

    private MaxHeap heap = new MaxHeap();

    public void addCandidate(Candidate c) {
        heap.insert(c);
    }

    public void showTopCandidate() {
        Candidate top = heap.getTop();
        if (top == null)
            System.out.println("No candidates available.");
        else
            System.out.println("Top Candidate: " + top);
    }

    public void showAllRanked() {

        MaxHeap tempHeap = new MaxHeap();

        for (Candidate c : heap.getAllCandidates())
            tempHeap.insert(c);

        while (tempHeap.size() > 0) {
            System.out.println(tempHeap.removeTop());
        }
    }

    public void recruitmentSummary() {

        ArrayList<Candidate> list = heap.getAllCandidates();

        if (list.isEmpty()) {
            System.out.println("No data available.");
            return;
        }

        int total = list.size();
        int totalExp = 0;
        int masters = 0, degree = 0, others = 0;
        int highest = Integer.MIN_VALUE;
        int lowest = Integer.MAX_VALUE;

        for (Candidate c : list) {

            totalExp += c.getExperience();

            if (c.getEducation().equalsIgnoreCase("Masters"))
                masters++;
            else if (c.getEducation().equalsIgnoreCase("Degree"))
                degree++;
            else
                others++;

            highest = Math.max(highest, c.getTotalScore());
            lowest = Math.min(lowest, c.getTotalScore());
        }

        System.out.println("----- Recruitment Summary -----");
        System.out.println("Total Candidates: " + total);
        System.out.println("Average Experience: " + (totalExp / (double) total));
        System.out.println("Highest Score: " + highest);
        System.out.println("Lowest Score: " + lowest);
        System.out.println("Masters: " + masters);
        System.out.println("Degree: " + degree);
        System.out.println("Others: " + others);
        System.out.println("--------------------------------");
    }
}
