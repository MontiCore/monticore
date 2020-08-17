/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._cocos.*;
import automata._parser.AutomataParser;
import automata._symboltable.*;
import automata.prettyprint.PrettyPrinter;
import automata.visitors.CountStates;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
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
    Log.ensureInitalization();
    new AutomataTool().run(args);
  }
  
  /**
   * Runs the tooling with given arguments
   * @param args
   */
  public void run(String[] args) {
    
    // Retrieve the model name
    // (this is only a very reduced CLI)
    if (args.length != 1) {
      Log.error("0xEE7400 Please specify 1. the path to the input model");
      return;
    }
    Log.info("Automaton DSL Tool", "AutomataTool");
    Log.info("------------------", "AutomataTool");
    String model = args[0];

    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", "AutomataTool");

    // setup the symbol table: deliberately omitted here
    
    // check the CoCos: deliberately omitted here
    
    // analyze the model with a visitor
    CountStates cs = new CountStates();
    cs.handle(ast);
    Log.info("The model contains " + cs.getCount() + " states.", "AutomataTool");

    // execute a pretty printer
    PrettyPrinter pp = new PrettyPrinter();
    pp.handle(ast);
    Log.info("Pretty printing the parsed automaton into console:", "AutomataTool");
    // print the result
    Log.println(pp.getResult());
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
      Log.error("0xEE840 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE640 Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
  
}
