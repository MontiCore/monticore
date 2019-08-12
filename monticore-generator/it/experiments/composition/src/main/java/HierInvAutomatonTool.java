/* (c) https://github.com/MontiCore/monticore */
import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import de.se_rwth.commons.logging.Log;
import hierinvautomaton._parser.HierInvAutomatonParser;
import invautomaton._ast.ASTAutomaton;


/**
 * Main class for the Automaton DSL tool.
 *
 */
public class HierInvAutomatonTool {
  
  /**
   * Use the single argument for specifying the single input automaton file.
   * 
   * @param args
   */
  public static void main(String[] args) throws IOException {

    // use normal logging (no DEBUG, TRACE)
    Log.init();

    // Retrieve the model name
    if (args.length != 1) {
      Log.error("Please specify only one single path to the input model.");
      return;
    }
    Log.info("HierInvAutomaton DSL Tool", "HierIAT");
    Log.info("------------------", "HierIAT");
    String model = args[0];
    
    
    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", "HierIAT");

    // Example, how to use a monolithic visitor on the model
    Log.info("=== 2: HierInvAutomatonCheapVisit =============", "HierIAT");
   HierInvAutomatonCheapVisit acv1 =
   					new HierInvAutomatonCheapVisit();
   ast.accept(acv1);
   // acv1.setVerbosity();
   // Log.info("=== 3: HierInvAutomatonCheapVisit.verbose() ===", "HierIAT");
   // ast.accept(acv1);

  }
  
  /**
   * Parse the model contained in the specified file.
   * 
   * @param model - file to parse
   * @return
   */
  public static ASTAutomaton parse(String model) {
    try {
      HierInvAutomatonParser parser = new HierInvAutomatonParser() ;
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
