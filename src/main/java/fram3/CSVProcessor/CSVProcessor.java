package fram3.CSVProcessor;

public interface CSVProcessor {
    /**
     * sort csv data using the properties specified one by one
     * @param properties properties used to sort
     * @throws IllegalArgumentException if one of the properties is not found
     */
    void sortBy(String[] properties) throws IllegalArgumentException;

    /**
     * filter csv data using a logical condition formed by AND and OR operators and Filter expressions
     * @param condition logical condition of filters
     * @throws IllegalArgumentException if one filter is malformed
     */
    void filter(FilterCondition condition) throws IllegalArgumentException;

    /**
     * notarize the output file of CSVProcessor timestamping with OpenTimestamps
     */
    void notarize();

    /**
     * notarize the output file of CSVProcessor timestamping with OpenTimestamps
     * @param fileName file name of the file to be notarized
     */
    void notarize(String fileName);

    /**
     * read new csv data
     * @param inputFileName file name used read to read csv data
     * @throws IllegalArgumentException if file is not found
     */
    void readData(String inputFileName) throws IllegalArgumentException;

    /**
     * write new csv data
     * @param outputFileName file name used to write csv data
     */
    void writeData(String outputFileName);

    /**
     * write partial changes made with CSVProcessor
     */
    void writeChanges();

}
