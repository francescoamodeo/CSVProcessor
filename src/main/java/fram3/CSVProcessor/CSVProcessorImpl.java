package fram3.CSVProcessor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.cli.ParseException;

public class CSVProcessorImpl implements CSVProcessor{

    private String inputFileName;
    private String outputFileName;
    List<String[]> data;

    CSVProcessorImpl(String inputFileName, String outputFileName) throws ParseException {

        try (FileReader fileReader = new FileReader(inputFileName);
             BufferedReader bufferedReader = new BufferedReader(fileReader);
             CSVReader csvReader = new CSVReaderBuilder(bufferedReader).build() ) {

            this.inputFileName = inputFileName;
            this.outputFileName = outputFileName;

            this.data = csvReader.readAll();

        }catch (FileNotFoundException e){
            throw new ParseException("Invalid input file name");
        }catch (IOException e){
            System.out.println("Error while reading csv file: "+ e.getMessage());
            System.exit(1);
        } catch (CsvException e) {
            System.out.println("User-defined validator failed");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sortBy(String[] properties) throws IllegalArgumentException {

        String[] header = data.getFirst();
        List<Integer> columnIdxs = new ArrayList<>();
        for (String property : properties){
            int i = 0;
            while (i < header.length && !(header[i].equals(property))) i++;

            if (i == header.length) throw new IllegalArgumentException("Property not found");
            else columnIdxs.add(i);
        }

        List<String[]> rows = data.subList(1, data.size());

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
    }

    @Override
    public void filter(FilterCondition condition) {
    }

    public void writeChanges() {
        try (FileWriter fileWriter = new FileWriter(outputFileName);
             BufferedWriter bufferedReader = new BufferedWriter(fileWriter);
             CSVWriter csvWriter = new CSVWriter(bufferedReader)) {

            csvWriter.writeAll(data);

        } catch (IOException e){
            System.out.println("Error while writing csv file: "+ e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void notarize() {
    }

}
