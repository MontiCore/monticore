/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._parser.AutomataParser;
import automata._visitor.AutomataTraverser;
import automata.prettyprint.PrettyPrinter;
import automata.visitors.CountStates;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
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
  
    // use normal logging (no DEBUG, TRACE)
    Log.ensureInitialization();
    new AutomataTool().run(args);
  }
  
  /**
   * Runs the tooling with given arguments
   * @param args
   */
  public void run(String[] args) {
    AutomataMill.init();
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

        // setup the symbol table: deliberately omitted here

        // check the CoCos: deliberately omitted here

        // analyze the model with a visitor
        CountStates cs = new CountStates();
        AutomataTraverser traverser = AutomataMill.traverser();
        traverser.add4Automata(cs);
        ast.accept(traverser);
        Log.info("Automaton has " + cs.getCount() + " states.", "AutomataTool");
        prettyPrint(ast, "");
      }else{
        printHelp(options);
      }
    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xEE751 Could not process AutomataTool parameters: " + e.getMessage());
    }






  }

  @Override
  public void prettyPrint(ASTAutomaton ast, String file){
    // execute a pretty printer
    PrettyPrinter pp = new PrettyPrinter();
    AutomataTraverser traverser2 = AutomataMill.traverser();
    traverser2.add4Automata(pp);
    traverser2.setAutomataHandler(pp);
    ast.accept(traverser2);
    Log.info("Pretty printing the parsed automaton into console:", "AutomataTool");
    // print the result
    Log.println(pp.getResult());
  }
  
}
