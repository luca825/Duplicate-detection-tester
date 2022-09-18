package DuplicateDetection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Preprocessor {

    /**
     * Read an TSV-file
     * @param path Path as string to the TSV-file
     * @return List of rows in the TSV file, with tabulator seperated values as string array
     */
    public static ArrayList<String[]> readTSV(String path) {
        ArrayList<String[]> Data = new ArrayList<>();
        try (BufferedReader tsvReader = new BufferedReader(new FileReader(new File(path)))) {
            String line = "";
            while ((line = tsvReader.readLine()) != null) {
                String[] lineItems = line.split("\t");
                Data.add(lineItems);
            }
        } catch (Exception e) {
            System.out.println("Problem occoured, while loading the data");
        }
        return Data;
    }

    /**
     * Create RecordIdentifiers for a list of records. The RecordIdentifier contains the key (signature) of the record,
     * the tokens of the key and the record id
     * @param records List of records, as string arrays with attribute values
     * @param attributes Array of attribut indices, which should be used for the key
     * @param keyPartSizes Length of the attribut parts used for the key
     * @return List of RecordIdentifiers
     */
    public static ArrayList<RecordIdentifier> createRecordIdentifiers(ArrayList<String[]> records, Integer[] attributes, Integer[] keyPartSizes) {
        int idAttribute = getIdAttributeIndex(records.get(0));
        int k;
        ArrayList<RecordIdentifier> recIds = new ArrayList<>();
        if(keyPartSizes == null) k = 0;
        else k = keyPartSizes.length - 1;
        for(int i = 1; i < records.size(); i++) {
            String key = "";
            int j = 0;
            ArrayList<String> keyTokens = new ArrayList<>();
            int h = 0;
            for(int attribut : attributes) {
                h++;
                String attributValue = records.get(i)[attribut].replaceAll("\"", "").toLowerCase();
                int p;
                if(keyPartSizes == null) {
                    p = attributValue.length();
                    if(h < attributes.length) attributValue += " ";
                }
                else {
                    p = keyPartSizes[j];
                    if (attributValue.length() > p) attributValue = attributValue.substring(0, p);
                }
                key += attributValue;
                keyTokens.add(attributValue);
                // if less size parameters for attribut splitting are given, the attributes begin again at the beginning
                if(j < k) j++;
                else j = 0;
            }
            recIds.add(new RecordIdentifier(key, records.get(i)[idAttribute], keyTokens));
        }
        return recIds;
    }

    /**
     * Get the index of the "id"-attribut
     * @param header Header of the TSV-Record-file
     * @return
     */
    public static int getIdAttributeIndex(String[] header) {
        int idAttribute = -1;
        for(int i = 0; i < header.length; i++) {
            if(header[i].replace("\"", "").equalsIgnoreCase("id")) {
                idAttribute = i;
                break;
            }
        }

        if(idAttribute == -1) {
            // Make an exception here
            System.out.println("Could not find the id column in header");
            return -1;
        }

        return idAttribute;
    }

    /**
     * Creates an permutation of an integer array of indices, with the value on index "sortingCriteriaIndex" as first element
     * @param a Integer array
     * @param sortingCriteriaIndex This index will be the first place in the permutation
     * @return permutated array
     */
    public static Integer[] createKeyPermutation(Integer[] a, int sortingCriteriaIndex) {
        List<Integer> l = new LinkedList<Integer>(Arrays.asList(a));
        Integer sortingCriteria = l.get(sortingCriteriaIndex);
        l.remove(sortingCriteriaIndex);
        Collections.shuffle(l);
        l.add(0, sortingCriteria);
        return l.toArray(a);
    }

    /**
     * Combine two list of duplciates
     * @param l1 First list
     * @param l2 Second list
     * @return
     */
    public static ArrayList<Duplicate> combine(ArrayList<Duplicate> l1, ArrayList<Duplicate> l2) {
        Set<Duplicate> set = new HashSet<Duplicate>();

        set.addAll(l1);
        set.addAll(l2);

        return new ArrayList<Duplicate>(set);
    }

    /**
     * Builds string out of records.
     * @param records Record-file as list for each pair and an string array with the id's of the records
     * @param indices Indices of the attributes, which should be used to build the string
     * @return List of records as strings
     */
    public static ArrayList<String> stringifyRecords(ArrayList<String[]> records, List<Integer> indices) {
        ArrayList<String> joinedRecords = new ArrayList<>();
        for(int i = 1; i < records.size(); i++) {
            String recordWithoutId = "";
            for(int j = 0; j < records.get(i).length; j++) {
                if(indices.contains(j)) recordWithoutId += records.get(i)[j];
            }
            joinedRecords.add(recordWithoutId);
        }
        return joinedRecords;
    }

    /**
     * Remove header of and build duplicates given in the gold standard
     * @param goldStandard Gold-standard file as list for each pair and an string array with the id's of the records
     * @return List of duplicate pairs
     */
    public static List<Duplicate> prepareGoldStandard(ArrayList<String[]> goldStandard) {
        List<Duplicate> duplicates = new LinkedList<>();
        for(int i = 1; i < goldStandard.size(); i++) {
            String[] pair = goldStandard.get(i);
            duplicates.add(new Duplicate(pair[0], pair[1]));
        }
        return duplicates;
    }
}
