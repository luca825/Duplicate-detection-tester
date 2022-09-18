package DuplicateDetection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Statistics {
    private int correctlyClassified;
    private int falseNegatives;
    private int falsePositives;
    private int trueDuplicatePairs;
    private int identifiedDuplicates;
    private int allPairs;

    public Statistics(int recordCount) {
        this.correctlyClassified = 0;
        this.falseNegatives = 0;
        this.falsePositives = 0;
        this.trueDuplicatePairs = 0;
        this.identifiedDuplicates = 0;
        this.allPairs = recordCount;
    }


    /**
     * Compare the list of found duplicates with the duplicates in the gold-standard.
     * @param result List found duplictaes
     * @param goldStandard List of gold standard duplicates
     */
    public void compareResults(List<Duplicate> result, List<Duplicate> goldStandard) {
        // False negatives
        List<Duplicate> falseNegative = goldStandard.stream()
                .filter(element -> !result.contains(element))
                .collect(Collectors.toList());

        List<Duplicate> falsePositive = result.stream()
                .filter(element -> !goldStandard.contains(element))
                .collect(Collectors.toList());

        // Correct results
        List<Duplicate> correct = result.stream()
                .distinct()
                .filter(goldStandard::contains)
                .collect(Collectors.toList());

        System.out.println("Found " + correct.size() + " correct classifications \n" +
                falseNegative.size() + " false negatives \n" +
                falsePositive.size() + " false positives");

        this.correctlyClassified = correct.size();
        this.falseNegatives = falseNegative.size();
        this.falsePositives = falsePositive.size();
        this.trueDuplicatePairs = goldStandard.size();
        this.identifiedDuplicates = result.size();


//        System.out.println("False positives:");
//        falsePositive.forEach((Duplicate p) -> System.out.println(p.toString()));
//        System.out.println("False negatives:");
//        falseNegative.forEach((Duplicate p) -> System.out.println(p.toString()));

    }

    public double reductionRatio() {
        return Double.valueOf(this.identifiedDuplicates)/Double.valueOf(this.allPairs);
    }

    /**
     * Calculate fScore, the harmonic mean, between precision and recall, given as:
     * (2 * precision * recall) / (precision + recall)
     * @return value between 0 and 1
     */
    public double fScore() {
        return (2 * this.precision() * this.recall())/(this.precision() + this.recall());
    }

    /**
     * Calculate precision, given as: Correctly classified duplicate pairs / All duplicate pairs found from the algorithm
     * @return value between 0 and 1
     */
    public double precision() {
        return Double.valueOf(this.correctlyClassified)/Double.valueOf(this.identifiedDuplicates);
    }

    /**
     * Calculate recall, given as: Correctly classified duplicate pairs / All duplicate pairs in the gold standard
     * @return value between 0 and 1
     */
    public double recall() {
        return Double.valueOf(this.correctlyClassified)/Double.valueOf(this.trueDuplicatePairs);
    }
}
