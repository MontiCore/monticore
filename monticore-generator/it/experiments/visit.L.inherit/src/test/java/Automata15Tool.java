/* (c) https://github.com/MontiCore/monticore */

import java.io.IOException;
import java.util.*;

import org.antlr.v4.runtime.RecognitionException;

import automata15._ast.*;
import automata15._cocos.*;
import automata15._parser.*;
import automata15._symboltable.*;
import automata15._visitor.*;
import de.se_rwth.commons.logging.*;

/**
 * Main class for the Automata DSL tool.
 *
 */
public class Automata15Tool {
  
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
      Log.error("0xEE750 Please specify only one single path to the input model.");
      return;
    }
    Log.info("Automata15 DSL Tool", Automata15Tool.class.getName());
    Log.info("------------------", Automata15Tool.class.getName());
    String model = args[0];
    
    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", Automata15Tool.class.getName());
    
    // execute a pretty printer
    Automata15PrettyPrinter pp = new Automata15PrettyPrinter();
    pp.handle(ast);
    Log.info("Pretty printing the parsed automaton into console:", Automata15Tool.class.getName());
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
      Automata15Parser parser = new Automata15Parser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model);
      
      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("0xEE850 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE650 Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
  
}
