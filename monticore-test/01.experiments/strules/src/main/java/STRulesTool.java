/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import strules.STRulesMill;
import strules._ast.ASTSTRules;
import strules._parser.STRulesParser;
import strules._symboltable.ISTRulesArtifactScope;
import strules._symboltable.STRulesScopesGenitorDelegator;
import strules._symboltable.STRulesSymbolTableCreatorDelegator;

import java.io.IOException;
import java.util.Optional;

/**
 * Small tool for creating symbol tables of STRules models
 * <p>
 * and also includes a main function
 */
public class STRulesTool {

  /**
   * Use the single argument for specifying the single input strule file.
   *
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      Log.error("0xFE749 Please specify only one single path to the input model.");
      return;
    }
    Log.info("ColoredGraph DSL Tool", "STRulesTool");
    Log.info("------------------", "STRulesTool");
    String model = args[0];

    // parse the model and create the AST representation
    ASTSTRules ast = parse(model);
    Log.info(model + " parsed successfully!", "ColoredGraphTool");

    // instantiate symbol table:
    STRulesScopesGenitorDelegator stc = STRulesMill
        .scopesGenitorDelegator();
    ISTRulesArtifactScope symTab = stc.createFromAST(ast);
    if (null== symTab){
      Log.error("0xFE349 Symbol table of Model could not be parsed.");
    }

    Log.info("------------------", "ColoredGraphTool");

  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static ASTSTRules parse(String model) {
    try {
      STRulesParser parser = new STRulesParser();
      Optional<ASTSTRules> optAST = parser.parse(model);

      if (!parser.hasErrors() && optAST.isPresent()) {
        return optAST.get();
      }
      Log.error("0xFE849 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xFE649 Failed to parse " + model, e);
    }
    return null;
  }

}
