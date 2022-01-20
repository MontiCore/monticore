package questionnaire;/* (c) https://github.com/MontiCore/monticore */
import java.util.*;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
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
public class QuestionnaireTool extends QuestionnaireToolTOP {

  /**
   * Use the single argument for specifying the single input questionnaire file.
   * 
   * @param args
   */
  public static void main(String[] args) {
    QuestionnaireTool tool = new QuestionnaireTool();
    tool.run(args);
  }

  public void run(String[] args){
    init();
    Options options = initOptions();

    try {
      //create CLI Parser and parse input options from commandline
      CommandLineParser cliparser = new org.apache.commons.cli.DefaultParser();
      CommandLine cmd = cliparser.parse(options, args);

      //help: when --help
      if (cmd.hasOption("h")) {
        printHelp(options);
        //do not continue, when help is printed.
        return;
      }
      //version: when --version
      else if (cmd.hasOption("v")) {
        printVersion();
        //do not continue when help is printed
        return;
      }

      Log.info("Questionnaire DSL Tool", "QuestionnaireTool");
      Log.info("------------------", "QuestionnaireTool");

      if (cmd.hasOption("i")) {
        String model = cmd.getOptionValue("i");
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
      }else{
        printHelp(options);
      }
    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xEE750 Could not process QuestionnaireTool parameters: " + e.getMessage());
    }
  }
  
}
