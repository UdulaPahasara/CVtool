package jobportal.heap;

import jobportal.model.Candidate;

public interface Heap {

    void insert(Candidate candidate);

    Candidate getTop();

    Candidate removeTop();

    int size();

}
