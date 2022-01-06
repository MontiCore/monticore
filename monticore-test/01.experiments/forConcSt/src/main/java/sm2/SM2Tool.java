/* (c) https://github.com/MontiCore/monticore */
package sm2;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import sm2._ast.ASTAutomaton;
import sm2._cocos.SM2CoCoChecker;
import sm2._parser.SM2Parser;
import sm2._symboltable.*;
import sm2._visitor.SM2Traverser;
import sm2.cocos.AtLeastOneInitialState;
import sm2.cocos.SM2CoCos;
import sm2.cocos.StateNameStartsWithCapitalLetter;
import sm2.cocos.TransitionSourceExists;

import java.io.IOException;
import java.util.Optional;

/**
 * Main class for the SM2 DSL tool.
 *
 */
public class SM2Tool extends SM2ToolTOP{
  
  /**
   * Use the single argument for specifying the single input sm2 file.
   *
   * @param args
   */
  public static void main(String[] args) {
  
    // use normal logging (no DEBUG, TRACE)
    Log.ensureInitalization();

    Log.info("SM2 DSL Tool", "SM2Tool");
    Log.info("------------------", "SM2Tool");
    SM2Tool sm2Tool = new SM2Tool();
    sm2Tool.run(args);

  }

  @Override
  public void run(String[] args) {
    SM2Mill.init();
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

      if (cmd.hasOption("i")) {
        String model = cmd.getOptionValue("i");
        final ASTAutomaton ast = parse(model);
        Log.info(model + " parsed successfully!", "SM2Tool");
        ISM2GlobalScope globalScope = SM2Mill.globalScope();
        globalScope.setFileExt("aut");
        globalScope.setSymbolPath(new MCPath());
        ISM2ArtifactScope symbolTable = createSymbolTable(ast);
        // can be used for resolving things in the model
        Optional<StateSymbol> aSymbol = symbolTable.resolveState("Ping");
        if (aSymbol.isPresent()) {
          Log.info("Resolved state symbol \"Ping\"; FQN = " + aSymbol.get().toString(),
              "SM2Tool");
        }
        runDefaultCoCos(ast);
        runAdditionalCoCos(ast);
        // analyze the model with a visitor
        CountStates cs = new CountStates();
        SM2Traverser traverser = SM2Mill.traverser();
        traverser.add4SM2(cs);
        ast.accept(traverser);
        Log.info("The model contains " + cs.getCount() + " states.", "SM2Tool");
        prettyPrint(ast,"");

      }

    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xEE744 Could not process SM2Tool parameters: " + e.getMessage());
    }
  }


  /**
   * Run the default context conditions {@link AtLeastOneInitialState},
   * {@link TransitionSourceExists}, and
   * {@link StateNameStartsWithCapitalLetter}.
   *
   * @param ast
   */
  @Override
  public  void runDefaultCoCos(ASTAutomaton ast) {
    new SM2CoCos().getCheckerForAllCoCos().checkAll(ast);
  }

  @Override
  public void runAdditionalCoCos(ASTAutomaton ast){
    SM2CoCoChecker customCoCos = new SM2CoCoChecker();
    customCoCos.addCoCo(new StateNameStartsWithCapitalLetter());
    customCoCos.checkAll(ast);
  }

  @Override
  public void prettyPrint(ASTAutomaton ast, String file){
    PrettyPrinter pp = new PrettyPrinter();
    SM2Traverser traverser2 = SM2Mill.traverser();
    traverser2.add4SM2(pp);
    traverser2.setSM2Handler(pp);
    ast.accept(traverser2);
    Log.info("Pretty printing the parsed sm2 into console:", "SM2Tool");
    Log.println(pp.getResult());

  }
  
}
