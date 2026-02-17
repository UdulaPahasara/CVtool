package jobportal.heap;

import jobportal.model.Candidate;
import java.util.ArrayList;

public class MaxHeap {

    private ArrayList<Candidate> heap = new ArrayList<>();

    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i) { return 2 * i + 1; }
    private int right(int i) { return 2 * i + 2; }

    // Insert Candidate
    public void insert(Candidate candidate) {
        heap.add(candidate);
        int i = heap.size() - 1;

        while (i != 0 && heap.get(parent(i)).getScore() < heap.get(i).getScore()) {
            swap(i, parent(i));
            i = parent(i);
        }
    }

    // Get Top Candidate (O(1))
    public Candidate getTop() {
        if (heap.isEmpty()) return null;
        return heap.get(0);
    }

    // Remove Top Candidate
    public Candidate removeTop() {
        if (heap.isEmpty()) return null;

        Candidate root = heap.get(0);
        heap.set(0, heap.get(heap.size() - 1));
        heap.remove(heap.size() - 1);

        heapify(0);
        return root;
    }

    private void heapify(int i) {
        int largest = i;
        int left = left(i);
        int right = right(i);

        if (left < heap.size() && heap.get(left).getScore() > heap.get(largest).getScore())
            largest = left;

        if (right < heap.size() && heap.get(right).getScore() > heap.get(largest).getScore())
            largest = right;

        if (largest != i) {
            swap(i, largest);
            heapify(largest);
        }
    }

    private void swap(int i, int j) {
        Candidate temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public ArrayList<Candidate> getAllCandidates() {
        return heap;
    }
}
