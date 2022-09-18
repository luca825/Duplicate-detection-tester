package DuplicateDetection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Naive implements AlgorithmThread {
    private ArrayList<RecordIdentifier> records;
    private ArrayList<Duplicate> duplicates;
    private int comparisonCount;
    private int duplicateCount;
    private int measure;
    private double thresholdSim;
    private DistanceMeasures dm;
    private List<Duplicate> goldStandard;


    public Naive(ArrayList<String[]> data, int measure, double thresholdSim,
                              Integer[] attributes, Integer[] keyPartSizes, List<Duplicate> goldStandard) {
        this.measure = measure;
        this.thresholdSim = thresholdSim;
        this.records = Preprocessor.createRecordIdentifiers(data, attributes, keyPartSizes);
        dm = new DistanceMeasures(measure, goldStandard);
        this.goldStandard = goldStandard;
    }

    @Override
    public void run() {
        duplicates = new ArrayList<>();
        for(int i = 0; i < records.size(); i++) {
            for (int j = i + 1; j < records.size(); j++) {
                RecordIdentifier a = records.get(i);;
                RecordIdentifier b = records.get(j);
                if (dm.similarity(a, b) >= thresholdSim) {
                    duplicates.add(new Duplicate(records.get(i).getId(), records.get(j).getId()));
                    duplicateCount++;
                }
                comparisonCount++;
            }
        }
    }

    @Override
    public ArrayList<Duplicate> getDuplicates() {
        return duplicates;
    }

    @Override
    public int getDuplicateCount() {
        return duplicateCount;
    }

    @Override
    public int getComparisonCount() {
        return comparisonCount;
    }

    @Override
    public String algo() {
        return "Naive algo";
    }

    @Override
    public String result() {
        return "The naive algo found " + this.duplicateCount + " duplicates, with " +  this.comparisonCount + " comparisons";
    }
}
