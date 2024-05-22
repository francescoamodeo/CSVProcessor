package fram3.CSVProcessor;

public interface CSVProcessor {

    void sortBy(String[] properties);
    void filter(FilterCondition condition);
    void writeChanges();
    void notarize();

}
