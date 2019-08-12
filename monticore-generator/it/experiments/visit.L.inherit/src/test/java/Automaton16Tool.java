package automaton16;

import java.io.IOException;
import java.util.*;

import org.antlr.v4.runtime.RecognitionException;

import automaton15._ast.*;
import automaton16._ast.*;
import automaton16._parser.*;
import automaton16._visitor.*;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.*;
import de.se_rwth.commons.logging.*;

/**
 * Main class for the Automaton DSL tool.
 *
 */
public class Automaton16Tool {
  
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
      Log.error("Please specify only one single path to the input model.");
      return;
    }
    Log.info("Automaton16 DSL Tool", Automaton16Tool.class.getName());
    Log.info("------------------", Automaton16Tool.class.getName());
    String model = args[0];
    
    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", Automaton16Tool.class.getName());
    
    // execute a pretty printer
    Automaton16PrettyPrinter pp = new Automaton16PrettyPrinter();
    pp.handle(ast);
    Log.info("Pretty printing the parsed automaton into console:", Automaton16Tool.class.getName());
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
      Automaton16Parser parser = new Automaton16Parser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model);
      
      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
  
}
