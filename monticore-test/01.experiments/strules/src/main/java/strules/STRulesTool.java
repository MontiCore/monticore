/* (c) https://github.com/MontiCore/monticore */
package strules;

import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import strules.STRulesMill;
import strules._ast.ASTSTRules;
import strules._parser.STRulesParser;
import strules._symboltable.ISTRulesArtifactScope;
import strules._symboltable.STRulesScopesGenitorDelegator;

import java.io.IOException;
import java.util.Optional;

/**
 * Small tool for creating symbol tables of STRules models
 * <p>
 * and also includes a main function
 */
public class STRulesTool extends STRulesToolTOP {

  /**
   * Use the single argument for specifying the single input strule file.
   *
   * @param args
   */
  public static void main(String[] args) {
    STRulesTool tool = new STRulesTool();
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

      Log.info("STRules DSL Tool", "STRulesTool");
      Log.info("------------------", "STRulesTool");

      if (cmd.hasOption("i")) {
        String model = cmd.getOptionValue("i");
        // parse the model and create the AST representation
        ASTSTRules ast = parse(model);
        Log.info(model + " parsed successfully!", "STRulesTool");

        ISTRulesArtifactScope symTab = createSymbolTable(ast);
        if (null== symTab){
          Log.error("0xFE349 Symbol table of Model could not be created.");
        }

        Log.info("------------------", "STRulesTool");
      }else{
        printHelp(options);
      }
    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xFE749 Could not process STRulesTool parameters: " + e.getMessage());
    }
  }

}
