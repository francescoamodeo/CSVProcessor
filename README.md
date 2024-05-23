# CSVProcessor

Processor for .csv files. 
Provides functionalities to sort and filter data using multiple properties and logical conditions.
Provides file notarization to create and store a timestamp using [@OpenTimestamps](https://github.com/opentimestamps/java-opentimestamps) java library for future verification of file integrity and data creation

The project uses Maven for build and create the target. 
To create the .jar file execute in the repository root: 
```
mvn package
```

To execute the application as example: 
```
java -jar target/CSVProcessor-1.0.jar path/to/input.csv /path/to/output.csv --sort-by Age --filter "Property > Value AND Property='Value'" --notarize
```

To clean target folder and build files:
```
mvn clean
```
