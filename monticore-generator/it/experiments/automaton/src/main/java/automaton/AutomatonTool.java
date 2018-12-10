/* (c) Monticore license: https://github.com/MontiCore/monticore */
import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import automaton._ast.ASTAutomaton;
import automaton._cocos.AutomatonCoCoChecker;
import automaton._parser.AutomatonParser;
import automaton._symboltable.AutomatonLanguage;
import automaton._symboltable.AutomatonSymbolTableCreator;
import automaton._symboltable.StateSymbol;
import automaton.cocos.AtLeastOneInitialAndFinalState;
import automaton.cocos.StateNameStartsWithCapitalLetter;
import automaton.cocos.TransitionSourceExists;
import automaton.prettyprint.PrettyPrinter;
import automaton.visitors.CountStates;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

/**
 * Main class for the Automaton DSL tool.
 *
 */
public class AutomatonTool {
  
  /**
   * Use the single argument for specifying the single input automaton file.
   * 
   * @param args
   */
  public static void main(String[] args) {

    // use normal logging (no DEBUG, TRACE)
    Log.init();

    // Retrieve the model name
    if (args.length != 1) {
      Log.error("Please specify only one single path to the input model.");
      return;
    }
    Log.info("Automaton DSL Tool", AutomatonTool.class.getName());
    Log.info("------------------", AutomatonTool.class.getName());
    String model = args[0];
    
    // setup the language infrastructure
    AutomatonLanguage lang = new AutomatonLanguage();
    
    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", AutomatonTool.class.getName());
    
    // setup the symbol table
    Scope modelTopScope = createSymbolTable(lang, ast);
    
    // can be used for resolving names in the model
    Optional<Symbol> aSymbol =
    	    modelTopScope.resolve("Ping", StateSymbol.KIND);
    if (aSymbol.isPresent()) {
      Log.info("Resolved state symbol \"Ping\"; FQN = "
      	       + aSymbol.get().toString(),
          AutomatonTool.class.getName());
    } else {
      Log.info("This automaton does not contain a state called \"Ping\";",
          AutomatonTool.class.getName());
    }
    
    // setup context condition insfrastructure
    AutomatonCoCoChecker checker = new AutomatonCoCoChecker();

    // add a custom set of context conditions
    checker.addCoCo(new StateNameStartsWithCapitalLetter());
    checker.addCoCo(new AtLeastOneInitialAndFinalState());
    checker.addCoCo(new TransitionSourceExists());

    // check the CoCos
    checker.checkAll(ast);
    
    // Now we know the model is well-formed 
    
    // analyze the model with a visitor
    CountStates cs = new CountStates();
    cs.handle(ast);
    Log.info("The model contains " + cs.getCount() + " states.", AutomatonTool.class.getName());
    
    // execute a pretty printer
    PrettyPrinter pp = new PrettyPrinter();
    pp.handle(ast);
    Log.info("Pretty printing the parsed automaton into console:", AutomatonTool.class.getName());
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
      AutomatonParser parser = new AutomatonParser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model);
      
      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
  
  /**
   * Create the symbol table from the parsed AST.
   * 
   * @param lang
   * @param ast
   * @return
   */
  public static Scope createSymbolTable(AutomatonLanguage lang, ASTAutomaton ast) {
    ResolvingConfiguration rc = new ResolvingConfiguration();
    rc.addDefaultFilters(lang.getResolvingFilters());
    
    GlobalScope globalScope = new GlobalScope(new ModelPath(), lang, rc);
    
    Optional<AutomatonSymbolTableCreator> symbolTable = lang.getSymbolTableCreator(
        rc, globalScope);
    return symbolTable.get().createFromAST(ast);
  }
  
}
