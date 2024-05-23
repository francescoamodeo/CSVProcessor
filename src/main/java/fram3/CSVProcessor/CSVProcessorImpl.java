package fram3.CSVProcessor;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.eternitywall.ots.DetachedTimestampFile;
import com.eternitywall.ots.OpenTimestamps;
import com.eternitywall.ots.Timestamp;
import com.eternitywall.ots.op.OpSHA256;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

public class CSVProcessorImpl implements CSVProcessor{

    private String inputFileName;
    private String outputFileName;
    List<String[]> data;

    public CSVProcessorImpl(String inputFileName, String outputFileName) throws IllegalArgumentException {

        readData(inputFileName);
        this.outputFileName = outputFileName;
    }

    @Override
    public void sortBy(String[] properties) throws IllegalArgumentException {

        String[] header = data.getFirst();
        List<Integer> columnIdxs = new ArrayList<>();
        //check if every property is defined in csv
        for (String property : properties){
            int i = 0;
            while (i < header.length && !(header[i].equalsIgnoreCase(property))) i++;

            if (i == header.length) throw new IllegalArgumentException("Property not found");
            else columnIdxs.add(i);
        }

        List<String[]> rows = data.subList(1, data.size());

        //sort first by the first property, if it finds equals then sort by next property
        rows.sort((row1, row2) -> {
            int i = 0, result;
            while (i < columnIdxs.size()) {
                try {
                    Double num1 = Double.valueOf(row1[columnIdxs.get(i)]);
                    Double num2 = Double.valueOf(row2[columnIdxs.get(i)]);
                    result = num1.compareTo(num2);
                } catch (NumberFormatException e) {
                    result = row1[columnIdxs.get(i)].compareTo(row2[columnIdxs.get(i)]);
                }
                if(result != 0) return result;
                else i++;
            }
            return 0;
        });
        System.out.println("Data successfully sorted by: "+ Arrays.toString(properties));
    }

    @Override
    public void filter(FilterCondition condition) throws IllegalArgumentException {

        String[] header = data.getFirst();

        List<String[]> filteredData = new ArrayList<>();
        filteredData.add(header);
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);

            //evaluate the logical condition of filters row by row
            if (condition.evalFilterCondition(header, row)) {
                filteredData.add(row);
            }
        }
        data = filteredData;
        System.out.println("Data filtered using condition: "+ condition.getConditionStr());
    }

    @Override
    public void notarize(String fileName) {

        File outputFile = new File(fileName);
        File timeStampedFile = new File(fileName + ".ots");

        System.out.println("Notarizing " + fileName + ".ots ...");
        try (FileOutputStream fos = new FileOutputStream(timeStampedFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(oos)){

            DetachedTimestampFile detachedTimestampFile = DetachedTimestampFile.from(new OpSHA256(), outputFile);
            Timestamp timestamp = OpenTimestamps.stamp(detachedTimestampFile);

            //serialization timestamp object
            bufferedOutputStream.write(timestamp.serialize());
            System.out.println("The timestamp proof '"+fileName+".ots' has been created!");

        }catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void notarize() {
        notarize(this.outputFileName);
    }

    public void readData(String inputFileName) throws IllegalArgumentException{
        try (FileReader fileReader = new FileReader(inputFileName);
             BufferedReader bufferedReader = new BufferedReader(fileReader);
             CSVReader csvReader = new CSVReaderBuilder(bufferedReader).build() ) {

            this.data = csvReader.readAll();

            this.inputFileName = inputFileName;

        }catch (FileNotFoundException e){
            throw new IllegalArgumentException("Invalid input file name");
        }catch (IOException e){
            System.out.println("Error while reading csv file: "+ e.getMessage());
            System.exit(1);
        } catch (CsvException e) {
            System.out.println("OpenCSV lib: User-defined validator failed");
            System.exit(1);
        }
    }

    public void writeData(String outputFileName){
        try (FileWriter fileWriter = new FileWriter(outputFileName);
             BufferedWriter bufferedReader = new BufferedWriter(fileWriter);
             CSVWriter csvWriter = new CSVWriter(bufferedReader)) {

            csvWriter.writeAll(data);

            this.outputFileName = outputFileName;

        } catch (IOException e){
            System.out.println("Error while writing csv file: "+ e.getMessage());
            System.exit(1);
        }
    }

    public void writeChanges() {
        writeData(this.outputFileName);
    }

}
