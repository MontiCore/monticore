/* (c) Monticore license: https://github.com/MontiCore/monticore */
import java.util.*;
import questionnaire.*;
import questionnaire._ast.*;
import questionnaire._parser.*;
import questionnaire._visitor.*;
import de.monticore.ast.ASTNode;

import java.io.IOException;
import org.antlr.v4.runtime.RecognitionException;
import de.monticore.io.paths.ModelPath;

import de.se_rwth.commons.logging.Log;

/**
 * Small pretty printer for questionnaires
 *
 * and also includes a main function
 */
public class QuestionnaireTool {

  /**
   * Use the single argument for specifying the single input questionnaire file.
   * 
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      Log.error("Please specify only one single path to the input model.");
      return;
    }
    Log.info("Questionnaire DSL Tool", QuestionnaireTool.class.getName());
    Log.info("------------------", QuestionnaireTool.class.getName());
    String model = args[0];
    
    // parse the model and create the AST representation
    final ASTQDefinition ast = parse(model);
    Log.info(model + " parsed successfully!", QuestionnaireTool.class.getName());

    // run the pretty printing:
    QuestionnaireVisitor qv = new QuestionnairePrettyPrinter(); 
    ast.accept(qv);

    Log.info("------------------", QuestionnaireTool.class.getName());

    // run the detailed tree pretty printing:
    qv = new QuestionnaireTreePrinter(); 
    ast.accept(qv);
  }
  
  /**
   * Parse the model contained in the specified file.
   * 
   * @param model - file to parse
   * @return
   */
  public static ASTQDefinition parse(String model) {
    try {
      QuestionnaireParser parser = new QuestionnaireParser() ;
      Optional<ASTQDefinition> optQuestionnaire = parser.parse(model);
      
      if (!parser.hasErrors() && optQuestionnaire.isPresent()) {
        return optQuestionnaire.get();
      }
      Log.error("Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("Failed to parse " + model, e);
    }
    return null;
  }
  
}
