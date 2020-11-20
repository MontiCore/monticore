/* (c) https://github.com/MontiCore/monticore */
package automata6;

import java.io.IOException;
import java.util.*;

import automata5.Automata5Mill;
import automata5.Automata5PrettyPrinter;
import automata5._visitor.Automata5Traverser;
import org.antlr.v4.runtime.RecognitionException;

import automata5._ast.*;
import automata6._ast.*;
import automata6._parser.*;
import automata6._visitor.*;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.*;
import de.se_rwth.commons.logging.*;

/**
 * Main class for the Automata DSL tool.
 *
 */
public class Automata6Tool {
  
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
      Log.error("0xEE746 Please specify only one single path to the input model.");
      return;
    }
    Log.info("Automata6 DSL Tool", "Automata6Tool");
    Log.info("------------------", "Automata6Tool");
    String model = args[0];
    
    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", "Automata6Tool");
    
    // execute a pretty printer
    Automata6PrettyPrinter pp = new Automata6PrettyPrinter();
    Automata5PrettyPrinter pp5 = new Automata5PrettyPrinter();
    Automata6Traverser traverser = Automata6Mill.traverser();
    traverser.setAutomata6Visitor(pp);
    traverser.setAutomata5Visitor(pp5);
    ast.accept(traverser);
    Log.info("Pretty printing the parsed automaton into console:", "Automata6Tool");
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
      Automata6Parser parser = new Automata6Parser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model);
      
      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("0xEE846 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE646 Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
  
}
