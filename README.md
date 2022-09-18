# Duplicate-detection-tester

Read me

To run the duplicate detection tester, run the class „ParameterDialog.java“.

Test configuration:

1. Loading data-file and gold-standard file:
 Click load and chose the file.  Both the data file and the gold-standard file should be in TSV format and contain headers with an „id“ column.


2. Algorithm and similarity-measure:
 Chose an algorithm and a similarity-measure to proceed. The slider allows to chose an threshold for the similarity measure. If two records are compared with a similarity-measure, the threshold has to be higher or equal than the treshold, for being seen as a duplicate. For the option „optimal-similarity-threshold“ no threshold is needed. If two compared records are a duplicate, the measure will recognize it on the basis of the gold-standard.

3. Algorithm configuration

SNM/Sorted-Blocks configuration:
 
„Multipass“: 
If SNM/Sorted-Blocks should be run mutliple times with different keys/blocking-criteria, than „Mutlipass“ can be selected. The slider allows to chose the number of runs. If „Multipass“ is not selected, only one run will be done.

„Init-Window-Size“/“Overlap“:
Allows to chose the initial window size for SNM, or respectively the overlap for Sorted-Blocks.

„Attributes“: 
Chose the attributes, which should be used to build the keys. The order in which the attributes are selected is the order of the attribut parts in the key. The first selected attribut will then also determine the ordering of SNM and acta s blocking criterion for SB.
If no attribute is chosen, the default will be to use all attributes.
If „Multipass“ is used, the first run is done according to the order of the selected attributes. For the other runs a random order is chosen out of the selected attributes.

„Attribute lengths“:
The lengths for the attribute parts can be entered here, seperated by comma. Example:
3, 5, 3, 4
The order is the order of the chosen attributes in the list of „Attributes“. This means the first value will be used for the first selected attribute.
It is also possible to use only one entry, or less then the chosen attributes. In this case the last value is used for the rest of the chosen attributes. If not value is entered the default value of three is chosen.

„DCS-Threshold“:
For SNM a DCS threshold can be selected. If (found duplicates in window/comparisons in window) is greater than the threshold, the window size will be increased for the current record.

„Use Keys for comparison“:
If selected the keys will be compared as strings in the similarity-measure. If not selected, the concatenated attribute values of the record will be compared.


LSH configuration:
 
„Bands b“:
Number of bands/parts the signature is split in. Maximum value is the value of „# hash-functions N“.

„# hash-functions N“:
Number of hash-functions, which are used to create the signature.

„k-shingling“:
Length of the shingles.

„Use Keys for signature creation“:
If selected, the signatures are created out of keys, insted of records. 

„Attributes“:
Chose the attributes, from which the signatures should be created. If „Use Keys for signature“ is selected, these attributes are used for the keys.

„Attribute lengths“:
If „Use Keys for signature“ is selected, then the lengths of the key parts can be chosen (see „SNM/Sorted-Blocks-configuration“ under „Attribute lengths“).


Naive algo configuration:
 
„Use Keys for comparison“:
If selected the keys will be compared as strings in the similarity-measure. If not selected, the concatenated attribute values of the record will be compared.

„Attributes“:
Chose the attributes, from which the signatures should be created. If „Use Keys for comparison“ is selected, these attributes are used for the keys.

„Attribute lengths“:
If „Use Keys for comparison“ is selected, then the lengths of the key parts can be chosen (see „SNM/Sorted-Blocks-configuration“ under „Attribute lengths“).



4. Run – Restart – Exit:
„Run“: Run the test, after the configuration is chosen. The results of the run will be shown in the GUI.
„Restart“: Allows new runs and changes in the configuration.
„Exit“: End program.
