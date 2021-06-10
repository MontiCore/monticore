/* (c) https://github.com/MontiCore/monticore */
package sm2;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
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
public class SM2Tool {
  
  /**
   * Use the single argument for specifying the single input sm2 file.
   *
   * @param args
   */
  public static void main(String[] args) {
  
    // use normal logging (no DEBUG, TRACE)
    Log.ensureInitalization();
    
    if (args.length != 1) {
      Log.error("0xEE744 Please specify only one single path to the input model.");
      return;
    }
    Log.info("SM2 DSL Tool", "SM2Tool");
    Log.info("------------------", "SM2Tool");
    String model = args[0];
    
    // parse the model and create the AST representation
    final ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", "SM2Tool");
    
    // setup the symbol table
    ISM2ArtifactScope modelTopScope = createSymbolTable(ast);
    // can be used for resolving things in the model
    Optional<StateSymbol> aSymbol = modelTopScope.resolveState("Ping");
    if (aSymbol.isPresent()) {
      Log.info("Resolved state symbol \"Ping\"; FQN = " + aSymbol.get().toString(),
          "SM2Tool");
    }
    
    // execute default context conditions
    runDefaultCoCos(ast);
   
    // execute a custom set of context conditions
    SM2CoCoChecker customCoCos = new SM2CoCoChecker();
    customCoCos.addCoCo(new StateNameStartsWithCapitalLetter());
    customCoCos.checkAll(ast);

    // analyze the model with a visitor
    CountStates cs = new CountStates();
    SM2Traverser traverser = SM2Mill.traverser();
    traverser.add4SM2(cs);
    ast.accept(traverser);
    Log.info("The model contains " + cs.getCount() + " states.", "SM2Tool");
    
    // execute a pretty printer
    PrettyPrinter pp = new PrettyPrinter();
    SM2Traverser traverser2 = SM2Mill.traverser();
    traverser2.add4SM2(pp);
    traverser2.setSM2Handler(pp);
    ast.accept(traverser2);
    Log.info("Pretty printing the parsed sm2 into console:", "SM2Tool");
    Log.println(pp.getResult());

  }
  
  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public static ASTAutomaton parse(String model) {
    try {
      SM2Parser parser = new SM2Parser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model);
      
      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("0xEE844 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE644 Failed to parse " + model, e);
    }
    return null;
  }
  
  /**
   * Create the symbol table from the parsed AST.
   *
   * @param ast
   * @return
   */
  public static ISM2ArtifactScope createSymbolTable(ASTAutomaton ast) {
    ISM2GlobalScope globalScope = SM2Mill.globalScope();
    globalScope.setFileExt("aut");
    globalScope.setSymbolPath(new MCPath());

    SM2ScopesGenitorDelegator symbolTable = SM2Mill
        .scopesGenitorDelegator();


    return symbolTable.createFromAST(ast);
  }
  
  /**
   * Run the default context conditions {@link AtLeastOneInitialState},
   * {@link TransitionSourceExists}, and
   * {@link StateNameStartsWithCapitalLetter}.
   *
   * @param ast
   */
  public static void runDefaultCoCos(ASTAutomaton ast) {
    new SM2CoCos().getCheckerForAllCoCos().checkAll(ast);
  }
  
}
