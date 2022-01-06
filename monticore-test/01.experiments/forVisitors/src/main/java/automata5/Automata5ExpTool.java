/* (c) https://github.com/MontiCore/monticore */
package automata5;

import java.io.IOException;
import java.util.*;

import org.antlr.v4.runtime.RecognitionException;

import automata5._ast.*;
import automata5._cocos.*;
import automata5._parser.*;
import automata5._symboltable.*;
import automata5._visitor.*;
import de.monticore.symboltable.*;
import de.se_rwth.commons.logging.*;

/**
 * Main class for the Automata DSL tool.
 *
 */
public class Automata5ExpTool {
  
  /**
   * Use the single argument for specifying the single input automata file.
   * 
   * @param args
   */
  public static void main(String[] args) {

    // use normal logging (no DEBUG, TRACE)
    Log.ensureInitalization();

    // Retrieve the model name
    if (args.length != 1) {
      Log.error("0xEE745 Please specify only one single path to the input model.");
      return;
    }
    Log.info("Automata5 DSL Tool", "Automata5Tool");
    Log.info("------------------", "Automata5Tool");
    String model = args[0];
    
    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", "Automata5Tool");
    
    // execute a pretty printer
    Automata5PrettyPrinter pp = new Automata5PrettyPrinter();
    Automata5Traverser traverser = Automata5Mill.traverser();
    traverser.add4Automata5(pp);
    ast.accept(traverser);
    Log.info("Pretty printing the parsed automaton into console:", "Automata5Tool");
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
      Automata5Parser parser = new Automata5Parser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model);
      
      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("0xEE845 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE645 Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
  
}
