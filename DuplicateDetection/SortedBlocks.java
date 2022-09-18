package DuplicateDetection;

import java.sql.SQLOutput;
import java.util.*;

public class SortedBlocks extends SortingBasedAlgorithm {
    // Maybe the data itself (since in the data array their is no write, but only readable access) the data array can only
    // be stored once in a central place

    private int overlap;

    /**
     * Constructor
     * @param data List of header and records. A record is given as an array string, which contains the attribute values
     * @param overlap Overlap between two blocks, in which records/keys are compared
     * @param steps Number of records/keys, before an evaluation of the run is made
     * @param measure Similarity-measure which is used. 0 - Levenshtein, 1 - Jaro, 2 - optimal similarity-measure
     * @param thresholdSim Threshold of similarity for two records, to be considered an duplicate
     * @param compareKeys True if keys should be compared, false when records should be compared
     * @param attributes Attributes are used to build the key
     * @param keyPartSizes Sizes of parts of the attributes used in the keys
     * @param goldStandard List of duplicate pairs in the gold standard
     */
    public SortedBlocks(ArrayList<String[]> data, int overlap, int steps, int measure, double thresholdSim,
                        boolean compareKeys, Integer[] attributes, Integer[] keyPartSizes, List<Duplicate> goldStandard) {
        super(data, steps, measure, thresholdSim, compareKeys, attributes, keyPartSizes, goldStandard);
        this.overlap = overlap;
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
        ArrayList<RecordIdentifier> comparisonRecords = new ArrayList<>();
        int windowNr = overlap + 1;
        this.duplicates = new ArrayList<>();

        int i = 0;
        while (i < this.keys.size()) {
            if(this.stopFlag) break;
            RecordIdentifier record = this.keys.get(i);
            if(i > 0 && !record.getKey().startsWith(this.keys.get(i - 1).getKey())) {
                while (comparisonRecords.size() > overlap) {
                    comparisonRecords.remove(0);
                }
                windowNr = 1;
            } else if(windowNr <= overlap) {
                comparisonRecords.remove(0);
                windowNr++;
            }
            for(int j = 0; j < comparisonRecords.size(); j++) {
                RecordIdentifier a = null;
                RecordIdentifier b = null;
                if(this.compareKeys) {
                    a = record;
                    b = comparisonRecords.get(j);
                } else {
                    a = new RecordIdentifier(this.records.get(unsortedIndices.get(record)), record.getId());
                    b = new RecordIdentifier(this.records.get(unsortedIndices.get(comparisonRecords.get(j))), comparisonRecords.get(j).getId());
                }
                if (dm.similarity(a, b) >= thresholdSim) {
                    duplicates.add(new Duplicate(record.getId(), comparisonRecords.get(j).getId()));
                    this.duplicateCount++;
                }
                this.comparisonCount++;
            }
            comparisonRecords.add(record);
            i++;
            if(i >= this.steps) {
                this.duplicatesInSection = this.duplicateCount;
                this.sectionFinished = true;
            }
        }
    }

    @Override
    public String algo() {
        return "SortedBlocks";
    }

    @Override
    public String result() {
        return "SB found " + this.duplicateCount + " duplicates, with " +  this.comparisonCount + " comparisons";
    }
}
