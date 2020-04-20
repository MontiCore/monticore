/* (c) https://github.com/MontiCore/monticore */
import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import de.se_rwth.commons.logging.Log;
import hierinvautomata._parser.HierInvAutomataParser;
import invautomata._ast.ASTAutomaton;


/**
 * Main class for the Automata DSL tool.
 *
 */
public class HierInvAutomataTool {
  
  /**
   * Use the single argument for specifying the single input automata file.
   * 
   * @param args
   */
  public static void main(String[] args) throws IOException {

    // use normal logging (no DEBUG, TRACE)
    Log.init();

    // Retrieve the model name
    if (args.length != 1) {
      Log.error("0xEE742 Please specify only one single path to the input model.");
      return;
    }
    Log.info("HierInvAutomata DSL Tool", "HierIAT");
    Log.info("------------------", "HierIAT");
    String model = args[0];
    
    
    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", "HierIAT");

    // Example, how to use a monolithic visitor on the model
    Log.info("=== 2: HierInvAutomatonCheapVisit =============", "HierIAT");
   HierInvAutomataCheapVisit acv1 = new HierInvAutomataCheapVisit();
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
      HierInvAutomataParser parser = new HierInvAutomataParser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model);
      
      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("0xEE842 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE642 Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
  
}
