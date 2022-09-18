package DuplicateDetection;

import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class DistanceMeasures {
    private List<Duplicate> goldStandard;
    private int measure;

    public DistanceMeasures(int measure) {
        this.measure = measure;
    }

    /**
     * Constructor
     * Measure:
     * 0 - Levenshtein
     * 1 - Jaro
     * 2 - Jaro-Winkler
     * 3 - Optimal similarity-measure
     * @param measure similarity-measure to compare records
     * @param goldStandard List of duplicates in gold standard
     */
    public DistanceMeasures(int measure, List<Duplicate> goldStandard) {
        this.goldStandard = goldStandard;
        this.measure = measure;
    }

    /**
     * Levenshtein-distance as similarity measure, for comparing two strings.
     * @param x First string
     * @param y Second string
     * @return a similarity score between 0.0 and 1
     */
    static double Levenshtein(String x, String y) {
        int[][] table = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    table[i][j] = j;
                } else if (j == 0) {
                    table[i][j] = i;
                } else {
                    table[i][j] = Math.min(
                            Math.min(table[i - 1][j - 1] + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                                    table[i - 1][j] + 1),
                            table[i][j - 1] + 1);
                }
            }
        }
        return 1 - (Double.valueOf(table[x.length()][y.length()]) / Double.valueOf(Math.max(x.length(),y.length())));
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    /**
     * Jaro-similarity for comparing two strings
     * @param s1 First string
     * @param s2 Second string
     * @return
     */
    public static double jaro(String s1, String s2) {
        if (s1 == s2) return 1.0;

        int len1 = s1.length();
        int len2 = s2.length();

        // Maximum distance for matching characters
        int max_dist = (int) (Math.floor(Math.max(len1, len2) / 2) - 1);

        int match = 0;

        int hash_s1[] = new int[s1.length()];
        int hash_s2[] = new int[s2.length()];

        for (int i = 0; i < len1; i++) {
            for (int j = Math.max(0, i - max_dist); j < Math.min(len2, i + max_dist + 1); j++) {
                if (s1.charAt(i) == s2.charAt(j) && hash_s2[j] == 0) {
                    hash_s1[i] = 1;
                    hash_s2[j] = 1;
                    match++;
                    break;
                }
            }
        }

        if (match == 0) return 0.0;
        double t = 0;
        int point = 0;

        for (int i = 0; i < len1; i++) {
            if (hash_s1[i] == 1) {
                while (hash_s2[point] == 0) point++;
                if (s1.charAt(i) != s2.charAt(point++))
                    t++;
            }
        }

        t /= 2;

        return (((double) match) / ((double) len1)
                + ((double) match) / ((double) len2)
                + ((double) match - t) / ((double) match))
                / 3.0;
    }


    /**
     * Jarp-Winkler similarity for comparing two strings
     * @param s1 First string
     * @param s2 Secon string
     * @return
     */
    public static double jaro_Winkler(String s1, String s2) {
        double jaro_dist = jaro(s1, s2);
        if (jaro_dist > 0.7) {
            int prefix = 0;
            for (int i = 0; i < Math.min(s1.length(), s2.length()); i++) {
                // If the characters match
                if (s1.charAt(i) == s2.charAt(i)) prefix++;
                else break;
            }

            // Maximum of 4 characters are allowed in prefix
            prefix = Math.min(4, prefix);

            jaro_dist += 0.1 * prefix * (1 - jaro_dist);
        }
        return jaro_dist;
    }

    /**
     * Calculate similarity of two records, with a given sim-measure.
     * Measure = 0 => Levenshtein-Distance adapted as sim-measure
     * Measure = 1 => Jaro
     * Measure = 2 => Jaro-Winkler
     * Measure = 2 => Optimal-sim-measure. Is 1 if a and b are a duplicate in the gold-standard, else 0
     * @param a First RecordIdentifier
     * @param b Second RecordIdentifier
     * @return Similarity between the two records
     */
    public double similarity(RecordIdentifier a, RecordIdentifier b) {
        return switch (measure) {
            case 0 -> Levenshtein(a.getKey(), b.getKey());
            case 1 -> jaro(a.getKey(), b.getKey());
            case 2 -> jaro_Winkler(a.getKey(), b.getKey());
            case 3 -> checkGoldStandard(a, b);
            default -> 0;
        };
    }

    /**
     * Checks if two records build an duplicate pair in the gold standard
     * @param a First RecordIdentifier
     * @param b Second RecordIdentifier
     * @return
     */
    private double checkGoldStandard(RecordIdentifier a, RecordIdentifier b) {
        Duplicate d = new Duplicate(a.getId(), b.getId());
        if(contains(this.goldStandard, d)) {
            return 1.0;
        }
        return 0.0;
    }

    /**
     * Check if a list of duplicates contains a given duplicate
     * @param tc List of duplicates
     * @param d duplicate
     * @return
     */
    private static boolean contains(List<Duplicate> tc, Duplicate d) {
        for (int i = 0; i < tc.size(); i++) {
            if (tc.get(i).equals(d)) return true;
        }
        return false;
    }

}
