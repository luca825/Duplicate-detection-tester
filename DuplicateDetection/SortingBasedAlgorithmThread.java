package DuplicateDetection;

import java.util.ArrayList;

public interface SortingBasedAlgorithmThread extends AlgorithmThread {

    public int getDuplicatesForSection();

    public boolean getSectionFinished();

    public void setStopFlag(boolean stopFlag);

    public boolean getStopFlag();

    public int getSteps();

    public void setSteps(int steps);

}
