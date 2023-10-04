/* (c) https://github.com/MontiCore/monticore */
package automata5;

import automata5._ast.ASTAutomaton;
import automata5._visitor.Automata5Traverser;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main class for the Automata DSL tool.
 */
public class Automata5Tool extends Automata5ToolTOP {

  /**
   * Use the single argument for specifying the single input automata file.
   *
   * @param args
   */
  public static void main(String[] args) {
    // use normal logging (no DEBUG, TRACE)
    Log.ensureInitialization();

    Log.info("Automata5 DSL Tool", "Automata5Tool");
    Log.info("------------------", "Automata5Tool");
    Automata5Tool automata5Tool = new Automata5Tool();
    automata5Tool.run(args);
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
        Log.info(model + " parsed successfully!", "Automata5Tool");
        prettyPrint(ast, "");
      }

    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xEE745 Could not process Automata5Tool parameters: " + e.getMessage());
    }
  }

  @Override
  public void prettyPrint(ASTAutomaton ast, String file) {
    Automata5PrettyPrinter pp = new Automata5PrettyPrinter();
    Automata5Traverser traverser = Automata5Mill.traverser();
    traverser.add4Automata5(pp);
    ast.accept(traverser);
    Log.info("Pretty printing the parsed automaton into console:", "Automata5Tool");
    System.out.println(pp.getResult());
  }
}
