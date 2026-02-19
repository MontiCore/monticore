/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.generator;

import de.monticore.cd4code.CD4CodeMill;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.nio.file.Path;

public class Generator {

  public static void main(String[] args) {
    // Define CLI options
    Options options = new Options();
    options.addOption(Option.builder("o")
        .longOpt("output")
        .hasArg()
        .argName("path")
        .desc("Output directory for generated tuples")
        .required()
        .build());
    options.addOption(Option.builder("s")
        .longOpt("size")
        .hasArg()
        .argName("N")
        .desc("Maximum tuple size to generate")
        .required()
        .build());

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd;
    try {
      cmd = parser.parse(options, args);
    }
    catch (ParseException e) {
      System.err.println("Error parsing command-line arguments: " + e.getMessage());
      formatter.printHelp("java -jar tuple-generator.jar", options);
      System.exit(1);
      return;
    }

    // Retrieve option values
    Path outputDir = Path.of(cmd.getOptionValue("output"));
    int n;
    try {
      n = Integer.parseInt(cmd.getOptionValue("size"));
    }
    catch (NumberFormatException e) {
      System.err.println("The --size option must be an integer.");
      System.exit(1);
      return;
    }

    // Initialize and run generator
    CD4CodeMill.init();
    new TupleGenerator(outputDir.toFile()).buildClasses(n);
    new ActionGenerator(outputDir.toFile()).buildClasses(n);
    new FunctionGenerator(outputDir.toFile()).buildClasses(n);
  }

}
