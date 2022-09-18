package DuplicateDetection;

import java.lang.reflect.Array;
import java.util.*;

public class SortedNeighberhood extends SortingBasedAlgorithm {
    private int initWindowSize;
    private double phi;

    /**
     * Constructor
     * @param data List of header and records. A record is given as an array string, which contains the attribute values
     * @param initWindowSize initial size of the windows
     * @param steps Number of records/keys, before an evaluation of the run is made
     * @param measure Similarity-measure which is used. 0 - Levenshtein, 1 - Jaro, 2 - optimal similarity-measure
     * @param thresholdSim Threshold of similarity for two records, to be considered an duplicate
     * @param compareKeys True if keys should be compared, false when records should be compared
     * @param attributes Attributes are used to build the key
     * @param keyPartSizes Sizes of parts of the attributes used in the keys
     * @param goldStandard List of duplicate pairs in the gold standard
     */
    public SortedNeighberhood(ArrayList<String[]> data, int initWindowSize, int steps, int measure, double thresholdSim, double phi, boolean compareKeys,
                              Integer[] attributes, Integer[] keyPartSizes, List<Duplicate> goldStandard) {
        super(data, steps, measure, thresholdSim, compareKeys, attributes, keyPartSizes, goldStandard);
        this.initWindowSize = initWindowSize;
        this.phi = phi;
    }

    @Override
    public void run() {
        HashMap<RecordIdentifier, Integer> unsortedIndices = new HashMap<>();
        if(!this.compareKeys) {
            for (int i = 0; i < this.keys.size(); i++) {
                unsortedIndices.put(this.keys.get(i), i);
            }
        }
        Collections.sort(this.keys);
        ArrayList<RecordIdentifier> window = new ArrayList<>();
        int windowSize = initWindowSize;
        // populate window with ordered keys, use unsortedIndices to find unordered index of equivalent record if needed
        for(int i = 0; i < windowSize; i++) window.add(this.keys.get(i));
        int recordsCount = this.keys.size();
        int duplicateCountTmp, comparisionCountTmp;
        comparisionCountTmp = duplicateCountTmp = 0;
        this.duplicates = new ArrayList<>();

        if (this.initWindowSize > 1) {
            for(int i = 0; i < recordsCount; i++) {
                if(this.stopFlag) break;
                int j = 1;
                while(j < window.size()) {
                    RecordIdentifier a = null;
                    RecordIdentifier b = null;
                    if(this.compareKeys) {
                        a = window.get(0);
                        b = window.get(j);
                    } else {
                        a = new RecordIdentifier(this.records.get(unsortedIndices.get(window.get(0))), window.get(0).getId());
                        b = new RecordIdentifier(this.records.get(unsortedIndices.get(window.get(j))), window.get(j).getId());
                    }
                    if (dm.similarity(a, b) >= thresholdSim) {
                        duplicates.add(new Duplicate(window.get(0).getId(), window.get(j).getId()));
                        duplicateCountTmp++;
                        this.duplicateCount++;
                    }
                    comparisionCountTmp++;
                    this.comparisonCount++;
                    if (j == window.size() - 1 && i + j + 1 < recordsCount && Double.valueOf(duplicateCountTmp)/Double.valueOf(comparisionCountTmp) > phi) {
                        window.add(this.keys.get(i + j + 1));
                    }
                    j++;
                }
                j--;
                if(!window.isEmpty()) window.remove(0);
                if(window.size() < this.initWindowSize && i + j + 1 < recordsCount) {
                    window.add(this.keys.get(i + j + 1));
                } else {
                    while(window.size() > this.initWindowSize) {
                        window.remove(window.size() - 1);
                    }
                }
                duplicateCountTmp = comparisionCountTmp = 0;
                if(i >= this.steps) {
                    this.duplicatesInSection = this.duplicateCount;
                    this.sectionFinished = true;
                }
            }
        } else {
            System.out.println("Window has to be greater than 1");
        }
    }

    @Override
    public String algo() {
        return "SortedNeighberhood";
    }

    @Override
    public String result() {
        return "SNM found " + this.duplicateCount + " duplicates, with " +  this.comparisonCount + " comparisons";
    }
}
