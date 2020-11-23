/* (c) https://github.com/MontiCore/monticore */

import java.io.IOException;
import java.util.*;

import automata15.Automata15Mill;
import automata15._visitor.Automata15Traverser;
import automata16.Automata16Mill;
import org.antlr.v4.runtime.RecognitionException;

import automata15._ast.*;
import automata16._ast.*;
import automata16._parser.*;
import automata16._visitor.*;
import de.se_rwth.commons.logging.*;

/**
 * Main class for the Automaton DSL tool.
 *
 */
public class Automata16Tool {
  
  /**
   * Use the single argument for specifying the single input automaton file.
   * 
   * @param args
   */
  public static void main(String[] args) {

    // use normal logging (no DEBUG, TRACE)
    Log.ensureInitalization();

    // Retrieve the model name
    if (args.length != 1) {
      Log.error("0xEE752 Please specify only one single path to the input model.");
      return;
    }
    Log.info("Automaton16 DSL Tool", "Automata16Tool");
    Log.info("------------------", "Automata16Tool");
    String model = args[0];
    
    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", "Automata16Tool");
    
    // execute a pretty printer
    Automata16PrettyPrinter pp = new Automata16PrettyPrinter();
    Automata16Traverser traverser = Automata16Mill.traverser();
    traverser.addAutomata16Visitor(pp);
    Automata15PrettyPrinter pre = new Automata15PrettyPrinter();
    traverser.addAutomata15Visitor(pre);
    ast.accept(traverser);
    Log.info("Pretty printing the parsed automaton into console:", "Automata16Tool");
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
      Automata16Parser parser = new Automata16Parser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model);
      
      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("0xEE852 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE652 Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
  
}
