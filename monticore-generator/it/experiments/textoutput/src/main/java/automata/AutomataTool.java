/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._parser.AutomataParser;
import automata._symboltable.*;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Main class for the Automaton DSL tool.
 *
 */
public class AutomataTool {

  /**
   * Use the single argument for specifying the single input automaton file.
   *
   * @param args
   */
  public static void main(String[] args) {

    // use normal logging (no DEBUG, TRACE)
    Log.init();

    // Retrieve the model name
    if (args.length != 1) {
      Log.error("0xEE754 Please specify only one single path to the input model.");
      return;
    }
    Log.info("Automaton DSL Tool", AutomataTool.class.getName());
    Log.info("------------------", AutomataTool.class.getName());
    String model = args[0];

    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", AutomataTool.class.getName());

    // execute a pretty printer
    TextPrinter pp = new TextPrinter();
    pp.handle(ast);
    Log.info("Printing the parsed automaton into textual form:", AutomataTool.class.getName());
    System.out.println(pp.getResult());

  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static ASTAutomaton parse(String model) {
    try {
      AutomataParser parser = new AutomataParser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model);

      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("0xEE854 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE654 Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }

}
