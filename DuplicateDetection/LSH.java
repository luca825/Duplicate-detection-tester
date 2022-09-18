package DuplicateDetection;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


public class LSH implements AlgorithmThread {
    private ArrayList<String[]> data;
    private ArrayList<RecordIdentifier> records;
    private ArrayList<Duplicate> duplicates;
    private int comparisonCount;
    private List<Duplicate> goldStandard;
    private int measure;
    private double thresholdSim;
    private int b;
    private int k;
    private int N;
    private DistanceMeasures dm;
    private double t;
    // Set of all shingles found in the data set
    private HashSet<String> shinglesSet;
    // Not used currently
    private LinkedList<HashMap<Integer, String>> shingleSets;

    private HashMap<String, Integer> M;
    private ArrayList<ArrayList<Integer>> C;

    /**
     * Constructor
     * @param data List of data, where a column is given as a String array
     * @param measure Similarity-measure which is used. 0 - Levenshtein, 1 - Jaro, 2 - Jaro-Winkler, 3 - optimal similarity-measure
     * @param thresholdSim Threshold of similarity for two records, to be considered an duplicate
     * @param b Number of bands
     * @param k Shingle size
     * @param N Number of hash-functions for minhashing
     * @param attributes Attributes are used to build the key
     * @param keyPartSizes Sizes of parts of the attributes used in the keys
     * @param goldStandard List of duplicate pairs in the gold standard
     */
    public LSH(ArrayList<String[]> data, int measure, double thresholdSim, int k, int N, int b,
               Integer[] attributes, Integer[] keyPartSizes, List<Duplicate> goldStandard) {
        this.records = Preprocessor.createRecordIdentifiers(data, attributes, keyPartSizes);
        this.data = data;
        this.measure = measure;
        this.thresholdSim = thresholdSim;
        this.b = b;
        this.k = k;
        this.N = N;
        this.goldStandard = goldStandard;
        this.dm = new DistanceMeasures(measure, goldStandard);
        this.duplicates = new ArrayList<Duplicate>();
        this.comparisonCount = 0;
        this.t = 0.0;
    }

    private ArrayList<HashSet<String>> k_shingling() {
        // shingleSets are the sets of all shingles found for every record
        ArrayList<HashSet<String>> shinglesSets = new ArrayList<>();
        M = new HashMap<>();
        int l = 0;
        C = new ArrayList<>();
        for(int i = 0; i < records.size(); i++) {
            C.add(new ArrayList<>());
            String record = records.get(i).getKey();
            shinglesSets.add(new HashSet<String>());
            for(int j = 0; j < record.length(); j++) {
                if(j < record.length() - (k-1)) {
                    String shingle = record.substring(j, j+k).toLowerCase();
                    shinglesSets.get(i).add(shingle);
                    if(!M.containsKey(shingle)) {
                        M.put(shingle, l);
                        l++;
                    }
                    C.get(i).add(M.get(shingle));
                } else {
                    break;
                }
            }
        }
        return shinglesSets;
    }

    private int[][] signatures(HashFunction<Object>[] hashFunctions) {
        int n = C.size();
        int N = hashFunctions.length;

        // Signature-matrix has n rows (number of records) and N columns, where N is the number of used hash-functions
        int[][] signatureMatrix = new int[n][N];
        // Initialize signature-matrix with inf in every entry
        for(int i = 0; i < n; i++) {
            for (int j = 0; j < N; j++) {
                signatureMatrix[i][j] = Integer.MAX_VALUE;
            }
        }
        // Create minhash-signatures, with permuting non-zero entries via the hash function. Then take the first
        // occasion of a non zero entry in the permutation
        for(int i = 0; i < n; i++) {
            for (int j = 0; j < C.get(i).size(); j++) {
                for(int k = 0; k < N; k++) {
                    signatureMatrix[i][k] = Math.min(signatureMatrix[i][k], hashFunctions[k].hash(C.get(i).get(j)));
                }
            }
        }
        return signatureMatrix;
    }

    /**
     * Produce a signatures s_i for strings via Minhash. Minhashing implementation is based on the algorithm described in
     * "Mining massive datasets" from A. Rajaraman and J. Ullman
     * @return The signature-matrix for the records
     */
    public int[][] minHashing() {
        // Shingle data
        ArrayList<HashSet<String>> shinglesSets = k_shingling();
        // Generate the independent hash functions
        HashFunction<Object>[] hashFunctions = generateRandomHashfunctions(N, M.size());
        // Create signatures from the characteristicMatrix
        int[][] signatureMatrix = signatures(hashFunctions);
        return signatureMatrix;
    }

    /**
     * Locality sensitive hashing via minhashing and band technique. See 'Mining of Massive Datasets' from Leskovec, Rajaraman, Ullman
     */
    public void run() {
        HashFunction hashFunction = null;
        hashFunction = generateRandomHashfunction();
        // Minhash data to signatures
        int[][] signatureMatrix = minHashing();

        // b is the number of bands; r is the number of columns of the signature at a time, which will be used to hash to the buckets
        int r = signatureMatrix[0].length / b;
        int n = signatureMatrix.length;
        int m = signatureMatrix[0].length;
        System.out.println("M " + m);
        int r_beginn = 0;
        int r_end = r;
        // Choose t low to avoid false negatives, choose t high to get more speed
        t = Math.pow(1.0 / Double.valueOf(b), 1.0 / Double.valueOf(r));

        // hashes safes the hashes for a record, which are produced, with hashing the r_beginn to r_end columns of the signature of this record
        HashMap<Integer, ArrayList<Integer>> hashes = new HashMap<>();
        LinkedList<HashMap<Integer, ArrayList<Integer>>> buckets = new LinkedList<>();
        // buckets has b entries in the list, which represents the bands. The bands hold the hash-buckets (HashMap),
        // where the calculated hash of the signature part r_beginn to r_end hashes to the bucket.
        // The bucket holds all elements, which where hashed to this bucket (ArrayList)

        // Go through bands, and then through all record-signatures. For each band, take r_beginn to r_end from the signature
        // and hash it to the buckets of the band
        for (int i = 0; i < b; i++) {
            buckets.add(new HashMap<Integer, ArrayList<Integer>>());
            for (int j = 0; j < n; j++) {
                int[] s_r = Arrays.copyOfRange(signatureMatrix[j], r_beginn, r_end + 1);;
                int hash = 0;
                hash = hashFunction.hash(Arrays.hashCode(s_r));
                if (buckets.get(i).containsKey(hash)) {
                    buckets.get(i).get(hash).add(j);
                } else {
                    buckets.get(i).put(hash, new ArrayList<Integer>());
                    buckets.get(i).get(hash).add(j);
                }
                // Save hash for later
                if(hashes.containsKey(j)) {
                    hashes.get(j).add(hash);
                } else {
                    hashes.put(j, new ArrayList<Integer>());
                    hashes.get(j).add(hash);
                }
            }
            // Move indexes to the next band
            r_beginn = r_end + 1;
            if (r_end + r < m) r_end += r;
            else r_end = m - 1;
        }

        LinkedList<HashSet<Integer>> duplicateCandidates = new LinkedList<>();

        // Go through all records and then through all bands. For each record r_i and band check in which bucket r_i is
        // remove r_i out of the bucket and put the other elements in the bucket in the candidateSet for r_i.
        // All possible candidates for that band of r_i are then in candidateSet, so deleting r_i from the bucket,
        // will avoid having the same candidate-pairs in multiple candidateSets
        for (int i = 0; i < records.size(); i++) {
            duplicateCandidates.add(new HashSet<Integer>());
            for (int j = 0; j < buckets.size(); j++) {
                HashMap<Integer, ArrayList<Integer>> bandBuckets = buckets.get(j);
                ArrayList<Integer> bucket = bandBuckets.get(hashes.get(i).get(j));
                // Do with predicate, because object is an int, remove will remove the object on index i, not object i
                //bucket.remove(i);
                int row = i;
                bucket.removeIf(p -> p == row);
                // Not sure if this is even necessary
                bandBuckets.put(hashes.get(i).get(j), bucket);
                duplicateCandidates.get(i).addAll(bucket);
            }
        }
        //System.out.println("DuplicateCandidates: " + duplicateCandidates.size());
        duplicates = checkDuplicateCandidates(duplicateCandidates);
    }

    private ArrayList<Duplicate> checkDuplicateCandidates(LinkedList<HashSet<Integer>> duplicateCandidates) {
        int idAttributeIndex = Preprocessor.getIdAttributeIndex(data.get(0));
        ArrayList<Duplicate> foundDuplicates = new ArrayList<>();

        for(int i = 0; i < duplicateCandidates.size(); i++) {
            HashSet<Integer> candidateSet = duplicateCandidates.get(i);
            RecordIdentifier r1 = new RecordIdentifier(records.get(i).getKey(), data.get(i+1)[idAttributeIndex]);
            for(int candidate : candidateSet) {
                RecordIdentifier r2 = new RecordIdentifier(records.get(candidate).getKey(), data.get(candidate+1)[idAttributeIndex]);
                comparisonCount++;
                if (dm.similarity(r1, r2) >= thresholdSim) {
                    foundDuplicates.add(new Duplicate(r1.getId(), r2.getId()));
                }
            }
        }

        return foundDuplicates;
    }

    /**
     * Generate an array of N random and independent hash-functions, which hash each to m buckets
     * @param N Number of hash-functions, which will be created
     * @param m Number of buckets each hash function hashes
     * @return list of hash functions
     */
    private HashFunction<Object>[] generateRandomHashfunctions(int N, int m) {
        // pic random integers a and b (a should not be a multiple of p)
        // N is a self picked parameter, which is the number of Hashfunctions => number of buckets
        // Pick a prime number (random) bigger than N
        HashFunction<Object>[] hashFunctions = new HashFunction[N];
        Random generator=new Random();

        // o should be the row index
        for(int i = 0; i < N; i++) {
            hashFunctions[i] = generateRandomHashfunction(m);
        }
        return hashFunctions;
    }

    /**
     * Generate a Murmur-Hash hash function with random seed and a fixed number of hash buckets
     * @param m Number of buckets
     * @return Hash function
     */
    private HashFunction generateRandomHashfunction(int m) {
        Random generator=new Random();
        int r = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
        return (x -> Math.abs(MurmurHash.hash32(x.toString(), r)) % m);
    }

    /**
     * Generate a Murmur-Hash hash function with random seed
     * @return Hash function
     */
    private HashFunction generateRandomHashfunction() {
        Random generator=new Random();
        int r = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
        return (x -> Math.abs(MurmurHash.hash32(x.toString(), r)));
    }

    // Old version, use MurmurHash instead
    /**
     * Generate a random hash-functions of the from: h(x) = ((a * x + b) % p) % N)
     * a - random int < p and a % p != 0
     * b - random int < p
     * p - a random prime number with p > N
     * N number of buckets, to which the hash-function will hash
     * The type of hash function is recommended by Johannes Zschache in lecture slides (Uni Leipzig) about LSH
     * @param m Number of buckets
     * @return
     */
    private HashFunction generateRandomHashfunction_oldVersion(int m) {
        Random generator=new Random();
        int c = ThreadLocalRandom.current().nextInt(m+1, m*100);
        int p = findNextBiggestPrime(c);
        int a = ThreadLocalRandom.current().nextInt(1, p-1);
        int b = ThreadLocalRandom.current().nextInt(1, p-1);

        //System.out.println("((" + a + "*x+" + b + ")" + "%" + p + ")%" + m);
        return (x -> ((a * x.hashCode() + b) % p) % m);
    }

    /**
     * Print a 2D-array to console
     * @param matrix
     */
    private void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.println(Arrays.toString(matrix[i]));
        }
    }

    /**
     * Get next biggest prime number for a given number n
     * @param n
     * @return The next biggest prime number
     */
    private int findNextBiggestPrime(int n) {
        int i = n;
        while(true) {
            i++;
            if (i == 2 || i == 3) break;
            if (i <= 1 || i % 2 == 0 || i % 3 == 0) continue;
            for (int j = 5; j * j <= i; j += 6) {
                if (i % j == 0 || i % (j + 2) == 0) continue;
            }
            break;
        }
        return i;
    }

    @Override
    public ArrayList<Duplicate> getDuplicates() {
        return duplicates;
    }

    @Override
    public int getDuplicateCount() {
        return duplicates.size();
    }

    @Override
    public int getComparisonCount() {
        return comparisonCount;
    }

    @Override
    public String algo() {
        return "LSH (Minhash)";
    }

    @Override
    public String result() {
        return "LSH found " + getDuplicateCount() + " duplicates, with " +  this.comparisonCount + " comparisons";
    }

    public double getJaccardSimThreshold() {
        return t;
    }
}
