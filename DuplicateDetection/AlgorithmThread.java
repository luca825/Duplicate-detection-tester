package DuplicateDetection;

import java.util.ArrayList;

public interface AlgorithmThread extends Runnable {

    public ArrayList<Duplicate> getDuplicates();

    public int getDuplicateCount();

    public int getComparisonCount();

    public String algo();

    public String result();
}
