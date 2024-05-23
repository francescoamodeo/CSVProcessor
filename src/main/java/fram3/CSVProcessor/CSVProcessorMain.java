package fram3.CSVProcessor;

import org.apache.commons.cli.*;

public class CSVProcessorMain {

    public static void main(String[] args) {

        //define options
        Options options = new Options();

        Option sortBy = Option.builder("s")
                .longOpt("sort-by")
                .argName("property")
                .hasArgs()
                .desc("Sort csv values by property").build();

        Option filter = Option.builder("f")
                .longOpt("filter")
                .argName("condition")
                .hasArg()
                .desc("filter csv values by condition").build();

        Option notarize = Option.builder("n")
                .longOpt("notarize")
                .desc("notarize csv file to check integrity and file creation date").build();

        options.addOption(sortBy);
        options.addOption(filter);
        options.addOption(notarize);

        //define parser
        CommandLine cmd;
        CommandLineParser parser = new DefaultParser();
        HelpFormatter helper = new HelpFormatter();

        try {
            cmd = parser.parse(options, args);

            //getting filenames that are not seen as options
            String[] remaining = cmd.getArgs();
            if (remaining.length < 2){
                System.out.println("Missing input or output filename");
                helper.printHelp("CSVProcessor <inputFileName> <outputFileName>", options);
                System.exit(0);
            }

            //passing input and output filename
            CSVProcessor processor = new CSVProcessorImpl(remaining[0], remaining[1]);

            if (cmd.hasOption(sortBy)) {
                String[] sortProperties = cmd.getOptionValues(sortBy);
                processor.sortBy(sortProperties);
            }
            if (cmd.hasOption(filter)) {
                FilterCondition filterCondition = new FilterCondition(cmd.getOptionValue(filter));
                processor.filter(filterCondition);
            }

            if (cmd.getOptions().length == 0){
                System.out.println("No options passed. CSV File unchanged.");
                System.exit(0);
            } else {
                processor.writeChanges();
            }

            if (cmd.hasOption(notarize)) {
                processor.notarize();
            }

        } catch (ParseException e){
            System.out.println(e.getMessage());
            helper.printHelp("CSVProcessor <inputFileName> <outputFileName>", options);
        }
    }
}
