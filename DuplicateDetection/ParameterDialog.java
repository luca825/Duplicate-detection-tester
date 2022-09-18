package DuplicateDetection;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ParameterDialog {
    private JRadioButton snmButton;
    private JRadioButton sbButton;
    private JRadioButton lshButton;
    private JRadioButton levenshteinButton;
    private JRadioButton jaroButton;
    private JRadioButton optimalSimButton;
    private JTextField attributeLengthsText;
    private JLabel attributLengthsLabel;
    private JLabel attributesLabel;
    public JPanel mainPanel;
    private JLabel bandsLabel;
    private JLabel hashFunctionsLabel;
    private JLabel shinglingLabel;
    private JSlider bandsSlider;
    private JSlider hashFunctionSlider;
    private JSlider shinglingSlider;
    private JRadioButton multiPassButton;
    private JSlider multipassSlider;
    private JLabel mutlipassLabel;
    private JButton runButton;
    private JButton exitButton;
    private JLabel resultLabel;
    private JTextArea resultTextArea;
    private JLabel pathLabel;
    private JTextField pathTextField;
    private JButton loadDataButton;
    private JLabel gsPathLabel;
    private JButton loadGSButton;
    private JLabel algorithmsLabel;
    private JLabel measuresLabel;
    private JLabel loadingSuccDataLabel;
    private JLabel loadingSuccGSLabel;
    private JList attributsJList;
    private JRadioButton useKeysButton;
    private JSlider thresholdSlider;
    private JLabel thresholdLabel;
    private JLabel ws_o_Label;
    private JSlider ws_o_Slider;
    private JRadioButton compareKeysButton;
    private JButton restartButton;
    private JRadioButton naiveButton;
    private JRadioButton jaroWinklerButton;
    private JSlider phiSlider;
    private JLabel phiLabel;
    private JScrollPane scrollpane;

    private int count = 0;
    private ArrayList<Duplicate> comp1 = new ArrayList<>();
    private ArrayList<Duplicate> comp2 = new ArrayList<>();


    private boolean multiPassSliderVisible = false;
    private int algo;
    private int measure;
    private boolean algoChosen = false;
    private boolean measureChosen = false;
    private boolean dataLoaded = false;
    private boolean gsLoaded = false;


    private HashMap<String, Integer> attributesToIndices;
    private ArrayList<String[]> goldStandard;
    private ArrayList<String[]> data;
    private ArrayList<Integer> indicesInOrder = new ArrayList<>();




    public ParameterDialog() {
        // --------------- Data loading listeners ---------------
        loadDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                data = loadTSV();
                if(data != null) {
                    ArrayList<String> attributList = new ArrayList<>();
                    int idIndex = -1;
                    int i = 0;
                    for(String attribut : data.get(0)) {
                        attribut = attribut.replace("\"", "");
                        if(!attribut.equalsIgnoreCase("id")) {
                            attributList.add(attribut);
                        } else {
                            idIndex = i;
                        }
                        i++;
                    }
                    if(idIndex == -1) {
                        loadingSuccDataLabel.setText("Could not find an ID column -> Data is not allowed");
                    } else {
                        attributesToIndices = new HashMap<>();
                        for(int j = 0; j < attributList.size(); j++) {
                            if(j < idIndex) attributesToIndices.put(attributList.get(j), j);
                            else attributesToIndices.put(attributList.get(j), j + 1);
                        }
                        loadingSuccDataLabel.setText("loading successfull!");
                        attributsJList.setListData(attributList.toArray());
                        if (goldStandard != null) {
                            enable_disable_AlgosAndMeasures(true);
                        }
                    }
                } else {
                    loadingSuccDataLabel.setText("File is empty");
                }
                loadingSuccDataLabel.setVisible(true);
            }
        });
        loadGSButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goldStandard = loadTSV();
                if(goldStandard != null) {
                    loadingSuccGSLabel.setVisible(true);
                    if(data != null) {
                        enable_disable_AlgosAndMeasures(true);
                    }
                }
            }
        });
        // --------------- Data loading listeners END ---------------

        // --------------- Parameter component listeners ---------------
        snmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch_Parameters(true);
                algoChosen = true;
                checkConditionsForOk();
                algo = 0;
                ws_o_Label.setText("Init-window-size:");
                attributeLengthsText.setEnabled(true);
                attributLengthsLabel.setEnabled(true);
            }
        });
        sbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch_Parameters(true);
                algoChosen = true;
                checkConditionsForOk();
                algo = 1;
                ws_o_Label.setText("Overlap:");
                phiSlider.setVisible(false);
                phiLabel.setVisible(false);
                attributeLengthsText.setEnabled(true);
                attributLengthsLabel.setEnabled(true);
            }
        });
        lshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch_Parameters(false);
                algoChosen = true;
                checkConditionsForOk();
                algo = 2;
                if(useKeysButton.isSelected()) {
                    attributeLengthsText.setEnabled(true);
                    attributLengthsLabel.setEnabled(true);
                } else {
                    attributeLengthsText.setEnabled(false);
                    attributLengthsLabel.setEnabled(false);
                }
            }
        });
        naiveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch_Parameters(false);
                algoChosen = true;
                checkConditionsForOk();
                algo = 3;
                bandsLabel.setVisible(false);
                hashFunctionsLabel.setVisible(false);
                shinglingLabel.setVisible(false);
                bandsSlider.setVisible(false);
                hashFunctionSlider.setVisible(false);
                shinglingSlider.setVisible(false);
                useKeysButton.setText("Use keys for comparison");
                if(useKeysButton.isSelected()) {
                    attributeLengthsText.setEnabled(true);
                    attributLengthsLabel.setEnabled(true);
                } else {
                    attributeLengthsText.setEnabled(false);
                    attributLengthsLabel.setEnabled(false);
                }
            }
        });
        multiPassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!multiPassSliderVisible) {
                    multipassSlider.setEnabled(true);
                    mutlipassLabel.setEnabled(true);
                    multiPassSliderVisible = true;
                }
                else {
                    multipassSlider.setEnabled(false);
                    mutlipassLabel.setEnabled(false);
                    multiPassSliderVisible = false;
                }
            }
        });
        levenshteinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                measureChosen = true;
                checkConditionsForOk();
                measure = 0;
                thresholdLabel.setEnabled(true);
                thresholdSlider.setEnabled(true);
            }
        });
        jaroButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                measureChosen = true;
                checkConditionsForOk();
                measure = 1;
                thresholdLabel.setEnabled(true);
                thresholdSlider.setEnabled(true);
            }
        });
        jaroWinklerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                measureChosen = true;
                checkConditionsForOk();
                measure = 2;
                thresholdLabel.setEnabled(true);
                thresholdSlider.setEnabled(true);
            }
        });
        optimalSimButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                measureChosen = true;
                checkConditionsForOk();
                measure = 3;
                thresholdLabel.setEnabled(false);
                thresholdSlider.setEnabled(false);
            }
        });
        attributsJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                List<String> selectedAttributes = attributsJList.getSelectedValuesList();

                // Get the order of the selected attributes, first loop adds newly selected elements. Second loop deletes newly deleted elements
                for(String s : selectedAttributes) {
                    int index = -1;
                    index = attributesToIndices.get(s);
                    if(!indicesInOrder.contains(index)) {
                        indicesInOrder.add(index);
                    }
                }
                List<Integer> selectedIndices = selectedAttributes.stream().map(p -> attributesToIndices.get(p)).toList();
                int index = -1;
                for(int i : indicesInOrder) {
                    if(!selectedIndices.contains(i)) {
                        index = i;
                    }
                }
                if(index >= 0)  {
                    int i = index;
                    indicesInOrder.removeIf(p -> p == i);
                }
            }
        });
        useKeysButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(useKeysButton.isSelected()) {
                    attributLengthsLabel.setEnabled(true);
                    attributeLengthsText.setEnabled(true);
                } else {
                    attributLengthsLabel.setEnabled(false);
                    attributeLengthsText.setEnabled(false);
                }
            }
        });
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartButton.setEnabled(false);
                enable_disable_LoadButtons(true);
                enable_disable_AlgosAndMeasures(true);
                snmButton.setSelected(false);
                sbButton.setSelected(false);
                lshButton.setSelected(false);
                levenshteinButton.setSelected(false);
                jaroButton.setSelected(false);
                optimalSimButton.setSelected(false);
                enable_disable_Configuration(true);
                runButton.setEnabled(true);
                if(!multiPassButton.isSelected()) {
                    multipassSlider.setEnabled(false);
                }
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        bandsSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
            }
        });
        hashFunctionSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                bandsSlider.setMaximum(hashFunctionSlider.getValue());
            }
        });
        // --------------- Parameter component listeners END ---------------

        // --------------- Runner listener ---------------
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runButton.setEnabled(false);
                enable_disable_Configuration(false);

                double thresholdSim = Double.valueOf(thresholdSlider.getValue()) / Double.valueOf(100);
                List<Duplicate> gsDuplicates = Preprocessor.prepareGoldStandard(goldStandard);
                // dont count header
                Statistics stat = new Statistics(data.size()-1);
                // parse Strings of key attributes to their respective indices
                Integer[] keyIndices = getKeyAttributeIndices();

                // SortingBased-Algorithms
                if(algo == 0 || algo == 1) {
                    // Get parameter values
                    int multiPassAmount = 1;
                    if(multiPassButton.isSelected()) multiPassAmount = multipassSlider.getValue();
                    boolean compareKeys = compareKeysButton.isSelected();
                    int ws_o = ws_o_Slider.getValue();

                    // parse String with key attribute lengths in integer values
                    Integer[] keyAttributLengths = getKeyAttributSizes();

                    int steps = 100;
                    HashMap<Thread, SortingBasedAlgorithmThread> threads = new HashMap<>();
                    for(int i = 0; i < multiPassAmount; i++) {
                        SortingBasedAlgorithmThread algorithm = null;
                        Integer[] keyIndicesOrder = null;
                        if(i == 0) {
                            keyIndicesOrder = keyIndices;
                        }
                        else {
                            int sortingCriteriaIndex = ThreadLocalRandom.current().nextInt(0, keyIndices.length);
                            keyIndicesOrder = Preprocessor.createKeyPermutation(keyIndices, sortingCriteriaIndex);
                        }

                        if(algo == 0) {
                            double phi = Double.valueOf(phiSlider.getValue()) / Double.valueOf(100);
                            algorithm = new SortedNeighberhood(data, ws_o, steps, measure, thresholdSim, phi, compareKeys, keyIndicesOrder, keyAttributLengths, gsDuplicates);
                        } else {
                            algorithm = new SortedBlocks(data, ws_o, steps, measure,thresholdSim ,false, keyIndicesOrder, keyAttributLengths, gsDuplicates);
                        }
                        Thread thread = new Thread(algorithm);
                        threads.put(thread, algorithm);
                        thread.start();
                    }

                    boolean sectionFinished = false;
                    while(!sectionFinished) {
                        sectionFinished = true;
                        for (SortingBasedAlgorithmThread algo : threads.values()) {
                            if (!algo.getSectionFinished()) sectionFinished = false;
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }

                    // Comment this part in if bad performing threads should be shut down
//                    int avg = 0;
//                    for (SortingBasedAlgorithmThread thread : threads.values()) avg += thread.getDuplicatesForSection();
//                    avg = avg / threads.size();
//                    for (SortingBasedAlgorithmThread thread : threads.values()) {
//                      if(thread.getDuplicatesForSection()/avg < 0.5) {
//                          thread.setStopFlag(true);
//                          System.out.println("STOPPED");
//                      }
//                    }

                    for (Thread thread : threads.keySet()) {
                        if(thread.isAlive()) {
                            waitForThreadToFinish(thread);
                        }
                    }
                    // create statistics text
                    ArrayList<Duplicate> combined = new ArrayList<>();
                    int r = 1;
                    String result = "";
                    for (SortingBasedAlgorithmThread thread : threads.values()) {
                        ArrayList<Duplicate> foundDuplicates = thread.getDuplicates();
                        if(multiPassAmount > 1) combined = Preprocessor.combine(combined, foundDuplicates);

                        String resultOfRun = "----------- Results for run " + r + "  ------------\n";
                        resultOfRun += thread.algo() + "\n" + thread.result() + "\n";
                        stat.compareResults(foundDuplicates, gsDuplicates);
                        resultOfRun += createStatString(stat);
                        result += resultOfRun + "\n\n";
                        r++;
                    }
                    if(multiPassAmount > 1) {
                        stat.compareResults(combined, gsDuplicates);
                        result += "\n\n";
                        result += "----------- Results multipass ------------\n";
                        result += createStatString(stat);
                    }
                    showResults(result);
                // LSH-algorithms
                } else if(algo == 2) {
                    int b = bandsSlider.getValue();
                    int N = hashFunctionSlider.getValue();
                    int k = shinglingSlider.getValue();
                    boolean useKeys = useKeysButton.isSelected();

                    AlgorithmThread algorithm = null;
                    Integer[] keyAttributLengths = null;
                    if(useKeys) keyAttributLengths = getKeyAttributSizes();
                    algorithm = new LSH(data, measure, thresholdSim, k, N, b, keyIndices, keyAttributLengths, gsDuplicates);

                    Thread thread = new Thread(algorithm);
                    thread.start();

                    if(thread.isAlive()) {
                        waitForThreadToFinish(thread);
                    }

                    ArrayList<Duplicate> foundDuplicates = algorithm.getDuplicates();

                    String result = "----------- Results ------------\n";
                    result += algorithm.algo() + "\n" + algorithm.result() + "\n";
                    result += "Jaccard-Similarity-Threshold: " + ((LSH) algorithm).getJaccardSimThreshold() + "\n";
                    stat.compareResults(foundDuplicates, gsDuplicates);
                    result += createStatString(stat);
                    showResults(result);
                //Naive approach
                } else {
                    boolean useKeys = useKeysButton.isSelected();

                    AlgorithmThread algorithm = null;
                    Integer[] keyAttributLengths = null;
                    if(useKeys) keyAttributLengths = getKeyAttributSizes();
                    algorithm = new Naive(data, measure, thresholdSim, keyIndices, keyAttributLengths, gsDuplicates);

                    Thread thread = new Thread(algorithm);
                    thread.start();

                    if(thread.isAlive()) {
                        waitForThreadToFinish(thread);
                    }

                    ArrayList<Duplicate> foundDuplicates = algorithm.getDuplicates();

                    String result = "----------- Results ------------\n";
                    result += algorithm.algo() + "\n" + algorithm.result() + "\n";
                    stat.compareResults(foundDuplicates, gsDuplicates);
                    result += createStatString(stat);
                    showResults(result);
                }
                restartButton.setVisible(true);
                restartButton.setEnabled(true);
            }
        });
        // --------------- Runner listener END ---------------
    }

    /**
     * Checks if an algorithm and a measure is chosen or not
     */
    private void checkConditionsForOk() {
        if(algoChosen && measureChosen) runButton.setEnabled(true);

    }

    /**
     * Creates the result-text with the stats of the run
     * @param stat Statistics object
     * @return the result-text as a string
     */
    private String createStatString(Statistics stat) {
        String result = "";
        result += "ReductionRatio: " + stat.reductionRatio() + "\n";
        result += "F-Score: " + stat.fScore() + "\n";
        result += "Precision: " + stat.precision() + "\n";
        result += "Recall: " + stat.recall() + "\n";
        return result;
    }

    /**
     * Enable/disables the the algorithm and measure components, for the algorithm, in the GUI for a specific algorithm
     * @param enable If true the components are enabled, if false disabled
     */
    private void enable_disable_AlgosAndMeasures(boolean enable) {
        algorithmsLabel.setEnabled(enable);
        measuresLabel.setEnabled(enable);
        snmButton.setEnabled(enable);
        sbButton.setEnabled(enable);
        lshButton.setEnabled(enable);
        naiveButton.setEnabled(enable);
        levenshteinButton.setEnabled(enable);
        jaroButton.setEnabled(enable);
        jaroWinklerButton.setEnabled(enable);
        optimalSimButton.setEnabled(enable);
        thresholdLabel.setEnabled(enable);
        thresholdSlider.setEnabled(enable);
    }

    /**
     * Enables/disables the file loading buttons and labels
     * @param enable If true the components are enabled, if false disabled
     */
    private void enable_disable_LoadButtons(boolean enable) {
        loadDataButton.setEnabled(enable);
        loadGSButton.setEnabled(enable);
        loadingSuccDataLabel.setEnabled(enable);
        loadingSuccGSLabel.setEnabled(enable);
        pathLabel.setEnabled(enable);
        gsPathLabel.setEnabled(enable);
    }

    /**
     * Enable/disables the parameter/configuration components, for the algorithm, in the GUI for a specific algorithm
     * @param enable If true the components are enabled, if false disabled
     */
    private void enable_disable_Configuration(boolean enable) {
        enable_disable_AlgosAndMeasures(enable);
        attributsJList.setEnabled(enable);
        attributeLengthsText.setEnabled(enable);
        attributLengthsLabel.setEnabled(enable);
        attributesLabel.setEnabled(enable);
        multiPassButton.setEnabled(enable);
        multipassSlider.setEnabled(enable);
        mutlipassLabel.setEnabled(enable);
        phiLabel.setEnabled(enable);
        phiSlider.setEnabled(enable);
        compareKeysButton.setEnabled(enable);
        bandsLabel.setEnabled(enable);
        hashFunctionsLabel.setEnabled(enable);
        shinglingLabel.setEnabled(enable);
        bandsSlider.setEnabled(enable);
        hashFunctionSlider.setEnabled(enable);
        shinglingSlider.setEnabled(enable);
        useKeysButton.setEnabled(enable);
        loadDataButton.setEnabled(enable);
        loadGSButton.setEnabled(enable);
        loadingSuccDataLabel.setEnabled(enable);
        loadingSuccGSLabel.setEnabled(enable);
        pathLabel.setEnabled(enable);
        gsPathLabel.setEnabled(enable);
        ws_o_Label.setEnabled(enable);
        ws_o_Slider.setEnabled(enable);
    }

    /**
     * Sets either the parameter components of SNM and SB or LSH as visible
     * @param sortingAlgos If true, parameter components of SNM and SB are shown, if false the components of LSH
     */
    private void switch_Parameters(boolean sortingAlgos) {
        multiPassButton.setVisible(sortingAlgos);
        multipassSlider.setVisible(sortingAlgos);
        mutlipassLabel.setVisible(sortingAlgos);
        compareKeysButton.setVisible(sortingAlgos);
        ws_o_Label.setVisible(sortingAlgos);
        ws_o_Slider.setVisible(sortingAlgos);
        phiLabel.setVisible(sortingAlgos);
        phiSlider.setVisible(sortingAlgos);

        bandsLabel.setVisible(!sortingAlgos);
        hashFunctionsLabel.setVisible(!sortingAlgos);
        shinglingLabel.setVisible(!sortingAlgos);
        bandsSlider.setVisible(!sortingAlgos);
        hashFunctionSlider.setVisible(!sortingAlgos);
        shinglingSlider.setVisible(!sortingAlgos);
        useKeysButton.setVisible(!sortingAlgos);

        attributsJList.setVisible(true);
        attributeLengthsText.setVisible(true);
        attributLengthsLabel.setVisible(true);
        attributesLabel.setVisible(true);

        if(!sortingAlgos) {
            attributLengthsLabel.setEnabled(false);
            attributeLengthsText.setEnabled(false);
        }
    }

    /**
     * Show the result-area and the results in the GUI
     * @param result A String with the results
     */
    private void showResults(String result) {
        resultLabel.setVisible(true);
        resultTextArea.setVisible(true);
        scrollpane.setVisible(true);
        resultTextArea.setText(result);
    }

    /**
     * Program will wait, till the given thread is finished
     * @param thread The thread, to wait for
     */
    private void waitForThreadToFinish(Thread thread) {
        try {
            thread.join();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    /**
     * load the TSV file
     * @return The TSV file, with a list of the records, where the values of a record are given as an String array
     */
    private ArrayList<String[]> loadTSV() {
        JFileChooser chooser = new JFileChooser();
        int ret = chooser.showOpenDialog(null);

        if(ret == JFileChooser.APPROVE_OPTION) {
            return Preprocessor.readTSV(chooser.getSelectedFile().getPath());
        }
        return null;
    }

    /**
     * Get the indices of the chosen attributes. If no attribute is chosen, on default all attributes are used
     * @return an integer array with the selected indices of the attributes
     */
    private Integer[] getKeyAttributeIndices() {
        Integer[] keyIndices = null;
        if(attributsJList.isSelectionEmpty()) {
            int maxIndex = attributsJList.getMaxSelectionIndex();
            keyIndices = new Integer[maxIndex];
            for(int i = 0; i < maxIndex; i++) {
                keyIndices[i] = i;
            }
        } else {
            // parse Strings of key attributes to their respective indices
            keyIndices = new Integer[indicesInOrder.size()];
            for (int i = 0; i < indicesInOrder.size(); i++) {
                keyIndices[i] = indicesInOrder.get(i);
            }
        }
        return keyIndices;
    }

    /**
     * Get the sizes for the chosen attributes, for the key. If the text is empty, chose default value 3 for all attributes.
     * @return an integer array with the sizes
     */
    private Integer[] getKeyAttributSizes() {
        String keyAttributLengthText = attributeLengthsText.getText();
        Integer[] keyAttributLengths = null;
        // If no entry was made for attribute length, use standard length of 3
        if(keyAttributLengthText.isEmpty()) {
            keyAttributLengths = new Integer[] {3};
        } else {
            String[] tmp = keyAttributLengthText.trim().split(",");
            keyAttributLengths = new Integer[tmp.length];
            for(int i = 0; i < tmp.length; i++) {
                keyAttributLengths[i] = Integer.parseInt(tmp[i].trim());
            }
        }
        return keyAttributLengths;
    }

    public static void main(String[] args) throws InterruptedException {
        JFrame parameterForm = new JFrame("Duplicate Detection Tester");
        parameterForm.setContentPane(new ParameterDialog().mainPanel);
        parameterForm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        parameterForm.pack();
        parameterForm.setExtendedState(JFrame.MAXIMIZED_BOTH);
        parameterForm.setVisible(true);
    }
}
