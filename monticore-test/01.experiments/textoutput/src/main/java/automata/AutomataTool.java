/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._parser.AutomataParser;
import automata._symboltable.*;
import automata._visitor.AutomataTraverser;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Main class for the Automaton DSL tool.
 *
 */
public class AutomataTool extends AutomataToolTOP {

  /**
   * Use the single argument for specifying the single input automaton file.
   *
   * @param args
   */
  public static void main(String[] args) {
    AutomataTool tool = new AutomataTool();
    tool.run(args);
  }

  public void run(String[] args){
    // use normal logging (no DEBUG, TRACE)
    AutomataMill.init();
    Log.ensureInitialization();

    Options options = initOptions();

    try {
      //create CLI Parser and parse input options from commandline
      CommandLineParser cliparser = new org.apache.commons.cli.DefaultParser();
      CommandLine cmd = cliparser.parse(options, args);

      //help: when --help
      if (cmd.hasOption("h")) {
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

      Log.info("Automata DSL Tool", "AutomataTool");
      Log.info("------------------", "AutomataTool");

      if (cmd.hasOption("i")) {
        String model = cmd.getOptionValue("i");

        // parse the model and create the AST representation
        ASTAutomaton ast = parse(model);
        Log.info(model + " parsed successfully!", "AutomataTool");

        // execute a pretty printer
        TextPrinter pp = new TextPrinter();
        AutomataTraverser traverser = AutomataMill.traverser();
        traverser.add4Automata(pp);
        traverser.setAutomataHandler(pp);
        ast.accept(traverser);
        Log.info("Printing the parsed automaton into textual form:", "AutomataTool");
        Log.println(pp.getResult());
      }else{
        printHelp(options);
      }
    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xFE754 Could not process AutomataTool parameters: " + e.getMessage());
    }
  }

}
