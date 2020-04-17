/* (c) https://github.com/MontiCore/monticore */
package grammarforast15;

import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import grammarforast15._ast.ASTS;
import grammarforast15._parser.GrammarForAST15Parser;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;

/**
 * Main class for the grammarforast15 DSL tool.
 *
 */
public class GrammarForAST15Tool {
  
  /**
   * Use the single argument for specifying the single input grammarforast15 file.
   * 
   * @param args
   */
  public static void main(String[] args) {

    // use normal logging (no DEBUG, TRACE)
    Log.init();
    
    if (args.length != 1) {
      Log.error("0xEE743 Please specify only one single path to the input model.");
      return;
    }
    Log.info("GrammarForAST15 DSL Tool", GrammarForAST15Tool.class.getName());
    Log.info("------------------", GrammarForAST15Tool.class.getName());
    String model = args[0];
    
    // parse the model and create the AST representation
    final ASTS ast = parse(model);
    Log.info(model + " parsed successfully!", GrammarForAST15Tool.class.getName());
    
  }
  
  /**
   * Parse the model contained in the specified file.
   * 
   * @param model - file to parse
   * @return
   */
  public static ASTS parse(String model) {
    try {
      GrammarForAST15Parser parser = new GrammarForAST15Parser() ;
      Optional<ASTS> optGrammarForAST15 = parser.parse(model);
      
      if (!parser.hasErrors() && optGrammarForAST15.isPresent()) {
        return optGrammarForAST15.get();
      }
      Log.error("0xEE843 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE643 Failed to parse " + model, e);
    }
    return null;
  }
  
}
