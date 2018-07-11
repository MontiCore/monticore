/* (c) Monticore license: https://github.com/MontiCore/monticore */
package sm2;

import java.io.IOException;
import java.util.Optional;

import sm2._ast.ASTAutomaton;
import sm2._parser.SM2Parser;
import sm2._symboltable.SM2Language;
import sm2._symboltable.SM2SymbolTableCreator;
import sm2._symboltable.StateSymbol;
import sm2.PrettyPrinter;
import sm2.CountStates;
import sm2._cocos.SM2CoCoChecker;
import sm2.cocos.AtLeastOneInitialState;
import sm2.cocos.SM2CoCos;
import sm2.cocos.StateNameStartsWithCapitalLetter;
import sm2.cocos.TransitionSourceExists;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

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
    Log.init();
    
    if (args.length != 1) {
      Log.error("Please specify only one single path to the input model.");
      return;
    }
    Log.info("SM2 DSL Tool", SM2Tool.class.getName());
    Log.info("------------------", SM2Tool.class.getName());
    String model = args[0];
    
    // setup the language infrastructure
    final SM2Language lang = new SM2Language();
    
    // parse the model and create the AST representation
    final ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", SM2Tool.class.getName());
    
    // setup the symbol table
    Scope modelTopScope = createSymbolTable(lang, ast);
    // can be used for resolving things in the model
    Optional<Symbol> aSymbol = modelTopScope.resolve("Ping", StateSymbol.KIND);
    if (aSymbol.isPresent()) {
      Log.info("Resolved state symbol \"Ping\"; FQN = " + aSymbol.get().toString(),
          SM2Tool.class.getName());
    }
    
    // execute default context conditions
    runDefaultCoCos(ast);
   
    // execute a custom set of context conditions
    SM2CoCoChecker customCoCos = new SM2CoCoChecker();
    customCoCos.addCoCo(new StateNameStartsWithCapitalLetter());
    customCoCos.checkAll(ast);

    // analyze the model with a visitor
    CountStates cs = new CountStates();
    cs.handle(ast);
    Log.info("The model contains " + cs.getCount() + " states.", SM2Tool.class.getName());
    
    // execute a pretty printer
    PrettyPrinter pp = new PrettyPrinter();
    pp.handle(ast);
    Log.info("Pretty printing the parsed sm2 into console:", SM2Tool.class.getName());
    System.out.println(pp.getResult());
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
      Log.error("Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("Failed to parse " + model, e);
    }
    return null;
  }
  
  /**
   * Create the symbol table from the parsed AST.
   * 
   * @param lang
   * @param ast
   * @return
   */
  public static Scope createSymbolTable(SM2Language lang, ASTAutomaton ast) {
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(lang.getResolvingFilters());
    
    GlobalScope globalScope = new GlobalScope(new ModelPath(), lang, resolvingConfiguration);
    
    Optional<SM2SymbolTableCreator> symbolTable = lang.getSymbolTableCreator(
        resolvingConfiguration, globalScope);
    return symbolTable.get().createFromAST(ast);
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
