/* (c) https://github.com/MontiCore/monticore */
package grammarforast15;

import de.se_rwth.commons.logging.Log;
import grammarforast15._ast.ASTS;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main class for the grammarforast15 DSL tool.
 */
public class GrammarForAST15Tool extends GrammarForAST15ToolTOP {

  /**
   * Use the single argument for specifying the single input grammarforast15 file.
   *
   * @param args
   */
  public static void main(String[] args) {
    // use normal logging (no DEBUG, TRACE)
    Log.ensureInitalization();

    Log.info("GrammarForAST15 DSL Tool", "GrammarForAST15Tool");
    Log.info("------------------", "GrammarForAST15Tool");

    GrammarForAST15Tool tool = new GrammarForAST15Tool();
    tool.run(args);

  }

  @Override
  public void run(String[] args) {
    init();
    Options options = initOptions();
    try {
      //create CLI Parser and parse input options from commandline
      CommandLineParser cliparser = new org.apache.commons.cli.DefaultParser();
      CommandLine cmd = cliparser.parse(options, args);

      //help: when --help
      if (cmd.hasOption("h") || !cmd.hasOption("i")) {
        printHelp(options);
        //do not continue, when help is printed.
        return;
      }
      //version: when --version
      else if (cmd.hasOption("v")) {
        printVersion();
        //do not continue when help is printed
        return;
      }

      if (cmd.hasOption("i")) {
        String model = cmd.getOptionValue("i");
        final ASTS ast = parse(model);
        Log.info(model + " parsed successfully!", "GrammarForAST15Tool");
      }

    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xEE743 Could not process GrammarForAST15Tool parameters: " + e.getMessage());
    }
  }
}
