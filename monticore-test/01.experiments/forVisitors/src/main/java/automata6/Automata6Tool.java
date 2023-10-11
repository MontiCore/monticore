/* (c) https://github.com/MontiCore/monticore */
package automata6;

import automata5.Automata5PrettyPrinter;
import automata5.Automata5Tool;
import automata5._ast.ASTAutomaton;
import automata6._visitor.Automata6Traverser;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main class for the Automata DSL tool.
 */
public class Automata6Tool extends Automata6ToolTOP {

  /**
   * Use the single argument for specifying the single input automata file.
   *
   * @param args
   */
  public static void main(String[] args) {
    // use normal logging (no DEBUG, TRACE)
    Log.ensureInitialization();

    Log.info("Automata6 DSL Tool", "Automata6Tool");
    Log.info("------------------", "Automata6Tool");
    Automata6Tool automata6Tool = new Automata6Tool();
    automata6Tool.run(args);
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
        final ASTAutomaton ast = parse(model);
        Log.info(model + " parsed successfully!", "Automata6Tool");
        prettyPrint(ast, "");
      }

    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xEE746 Could not process Automata6Tool parameters: " + e.getMessage());
    }
  }

  @Override
  public void prettyPrint(ASTAutomaton ast, String file) {
    // execute a pretty printer
    Automata6PrettyPrinter pp = new Automata6PrettyPrinter();
    Automata5PrettyPrinter pp5 = new Automata5PrettyPrinter();
    Automata6Traverser traverser = Automata6Mill.traverser();
    traverser.add4Automata6(pp);
    traverser.add4Automata5(pp5);
    ast.accept(traverser);
    Log.info("Pretty printing the parsed automaton into console:", "Automata6Tool");
    System.out.println(pp.getResult());
  }
}
