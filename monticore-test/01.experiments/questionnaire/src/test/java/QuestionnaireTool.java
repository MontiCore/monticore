/* (c) https://github.com/MontiCore/monticore */
import java.util.*;
import questionnaire.*;
import questionnaire._ast.*;
import questionnaire._parser.*;
import questionnaire._visitor.*;

import java.io.IOException;
import org.antlr.v4.runtime.RecognitionException;

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
      Log.error("0xEE749 Please specify only one single path to the input model.");
      return;
    }
    Log.info("Questionnaire DSL Tool", "QuestionnaireTool");
    Log.info("------------------", "QuestionnaireTool");
    String model = args[0];

    // parse the model and create the AST representation
    final ASTQDefinition ast = parse(model);
    Log.info(model + " parsed successfully!", "QuestionnaireTool");

    // run the pretty printing:
    QuestionnaireVisitor2 qv = new QuestionnairePrettyPrinter();
    QuestionnaireTraverser t1 = QuestionnaireMill.traverser();
    t1.add4Questionnaire(qv);
    ast.accept(t1);
    Log.info("------------------", "QuestionnaireTool");

 // run the detailed tree pretty printing:
    QuestionnaireTraverser t2 = QuestionnaireMill.traverser();
    QuestionnaireVisitor2 vis = new QuestionnaireTreePrinter();
    QuestionnaireHandler han = new QuestionnaireTreeHandler();
    t2.add4Questionnaire(vis);
    t2.setQuestionnaireHandler(han);
    ast.accept(t2);
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
      Log.error("0xEE849 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE649 Failed to parse " + model, e);
    }
    return null;
  }

}