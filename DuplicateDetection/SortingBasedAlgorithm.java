package DuplicateDetection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SortingBasedAlgorithm implements SortingBasedAlgorithmThread {
    protected ArrayList<RecordIdentifier> keys;
    protected ArrayList<String> records;
    protected ArrayList<Duplicate> duplicates;
    protected int steps;
    protected int duplicateCount;
    protected int comparisonCount;
    protected boolean sectionFinished;
    protected int duplicatesInSection;
    protected boolean stopFlag;
    protected int measure;
    protected boolean compareKeys;
    protected double thresholdSim;
    protected DistanceMeasures dm;


    /**
     * Constructor
     */
    private SortingBasedAlgorithm() {
        this.duplicateCount = 0;
        this.comparisonCount = 0;
        this.sectionFinished = false;
        this.duplicatesInSection = 0;
        this.stopFlag = false;
    }

    /**
     * Constructor
     * @param data List of header and records. A record is given as an array string, which contains the attribute values
     * @param steps Number of records/keys, before an evaluation of the run is made
     * @param measure Similarity-measure which is used. 0 - Levenshtein, 1 - Jaro, 2 - Jaro-Winkler, 3 - optimal similarity-measure
     * @param thresholdSim Threshold of similarity for two records, to be considered an duplicate
     * @param compareKeys True if keys should be compared, false when records should be compared
     * @param attributes Attributes are used to build the key
     * @param keyPartSizes Sizes of parts of the attributes used in the keys
     * @param goldStandard List of duplicate pairs in the gold standard
     */
    public SortingBasedAlgorithm(ArrayList<String[]> data, int steps, int measure, double thresholdSim, boolean compareKeys,
                              Integer[] attributes, Integer[] keyPartSizes, List<Duplicate> goldStandard) {
        this();
        this.steps = steps;
        this.measure = measure;
        this.thresholdSim = thresholdSim;
        this.compareKeys = compareKeys;
        this.keys = Preprocessor.createRecordIdentifiers(data, attributes, keyPartSizes);
        if (!compareKeys) {
            records = Preprocessor.stringifyRecords(data, Arrays.stream(attributes).toList());
        }
        dm = new DistanceMeasures(measure, goldStandard);
    }



    public ArrayList<Duplicate> getDuplicates() {
        return this.duplicates;
    }

    public int getSteps() {
        return this.steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getDuplicateCount() {
        return this.duplicateCount;
    }

    public int getDuplicatesForSection() {
        return this.duplicatesInSection;
    }

    public boolean getSectionFinished() {
        return this.sectionFinished;
    }

    public void setStopFlag(boolean stopFlag) {
        this.stopFlag = stopFlag;
    }

    public boolean getStopFlag() {
        return this.stopFlag;
    }

    public int getComparisonCount() {
        return comparisonCount;
    }
}
