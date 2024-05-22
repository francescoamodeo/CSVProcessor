package fram3.CSVProcessor;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterCondition {
    private final String FILTER_PATTERN = "(\\w+)\\s*(=|!=|>|<|>=|<=)\\s*('.*'|\\d+)";
    private final String LOGICAL_OPERATOR_PATTERN = "\\s*(AND|OR)\\s*";

    private static class Filter{
        String property;
        String operator;
        String value;

        Filter(String property, String operator, String value){
            this.property = property;
            this.operator = operator;
            this.value = value;
        }
    }

    ArrayList<Filter> filters;
    ArrayList<String> logicalOperators;

    public FilterCondition(String conditionStr){

        this.filters = new ArrayList<>();
        this.logicalOperators = new ArrayList<>();
        parseCondition(conditionStr);
    }

    private void parseCondition(String conditionStr){

        Pattern filterPattern = Pattern.compile(FILTER_PATTERN);
        Pattern logicalOperatorPattern = Pattern.compile(LOGICAL_OPERATOR_PATTERN);
        Matcher filterMatcher = filterPattern.matcher(conditionStr);
        Matcher logicalOperatorMatcher = logicalOperatorPattern.matcher(conditionStr);

        while(filterMatcher.find()) {
            String property = filterMatcher.group(1);
            String operator = filterMatcher.group(2);
            String value = filterMatcher.group(3);
            if (value.startsWith("'") && value.endsWith("'"))
                value = value.substring(1, value.length() - 1);

            this.filters.add(new Filter(property, operator, value));
        }

        while (logicalOperatorMatcher.find())
            this.logicalOperators.add(logicalOperatorMatcher.group(1));
    }

    public boolean evalFilterCondition(String[] header, String[] row){

        boolean filterEval = evalFilter(this.filters.getFirst(), header, row);
        for (int i = 1; i < this.filters.size(); i++) {
            String logicalOperator = this.logicalOperators.get(i-1);
            Filter nextFilter = this.filters.get(i);

            switch (logicalOperator) {
                case "AND" -> {
                    if (!filterEval) return false;
                    filterEval = evalFilter(nextFilter, header, row);
                }
                case "OR" -> {
                    if (filterEval) return true;
                    filterEval = evalFilter(nextFilter, header, row);
                }
                default -> {
                    return false; //TODO
                }
            }
        }
        return filterEval;
    }

    private boolean evalFilter(Filter filter, String[] header, String[] row) {

        int i = 0, columnIdx;
        while (i < header.length && !(header[i].equalsIgnoreCase(filter.property))) i++;

        if (i == header.length) return false; //TODO
        else columnIdx = i;

        String cellValue = row[columnIdx];
        return switch (filter.operator) {
            case "=" -> cellValue.equals(filter.value);
            case "!=" -> !cellValue.equals(filter.value);
            case ">" -> compareCellValues(cellValue, filter.value) > 0;
            case "<" -> compareCellValues(cellValue, filter.value) < 0;
            case ">=" -> compareCellValues(cellValue, filter.value) >= 0;
            case "<=" -> compareCellValues(cellValue, filter.value) <= 0;
            default -> false;
        };
    }

    private int compareCellValues(String cellValue, String value){
        try {
            Double num1 = Double.valueOf(cellValue);
            Double num2 = Double.valueOf(value);
            return num1.compareTo(num2);
        } catch (NumberFormatException e) {
            return cellValue.compareTo(value);
        }
    }

}
