/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._cocos.AutomataCoCoChecker;
import automata._parser.AutomataParser;
import automata._symboltable.*;
import automata._symboltable.serialization.AutomataScopeDeSer;
import automata.cocos.AtLeastOneInitialAndFinalState;
import automata.cocos.StateNameStartsWithCapitalLetter;
import automata.cocos.TransitionSourceExists;
import automata.prettyprint.PrettyPrinter;
import automata.visitors.CountStates;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Main class for the Automaton DSL tool.
 *
 */
public class AutomataTool {

  public static final Path DEFAULT_SYMBOL_LOCATION = Paths.get("target");

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
      Log.error("0xEE7400 Please specify only one single path to the input model.");
      return;
    }
    Log.info("Automaton DSL Tool", AutomataTool.class.getName());
    Log.info("------------------", AutomataTool.class.getName());
    String model = args[0];

    // setup the language infrastructure
    AutomataLanguage lang = new AutomataLanguage();

    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", AutomataTool.class.getName());

    // setup the symbol table
    AutomataArtifactScope modelTopScope =
            createSymbolTable(lang, ast);

    // can be used for resolving names in the model
    Optional<StateSymbol> aSymbol =
            modelTopScope.resolveState("Ping");
    if (aSymbol.isPresent()) {
      Log.info("Resolved state symbol \"Ping\"; FQN = "
               + aSymbol.get().toString(),
          AutomataTool.class.getName());
    } else {
      Log.info("This automaton does not contain a state called \"Ping\";",
          AutomataTool.class.getName());
    }

    // setup context condition insfrastructure
    AutomataCoCoChecker checker = new AutomataCoCoChecker();

    // add a custom set of context conditions
    checker.addCoCo(new StateNameStartsWithCapitalLetter());
    checker.addCoCo(new AtLeastOneInitialAndFinalState());
    checker.addCoCo(new TransitionSourceExists());

    // check the CoCos
    checker.checkAll(ast);

    // Now we know the model is well-formed

    // store artifact scope and its symbols
    AutomataScopeDeSer deser = new AutomataScopeDeSer();
    deser.setSymbolFileExtension("autsym");
    deser.store(modelTopScope, DEFAULT_SYMBOL_LOCATION);

    // analyze the model with a visitor
    CountStates cs = new CountStates();
    cs.handle(ast);
    Log.info("The model contains " + cs.getCount() + " states.", AutomataTool.class.getName());

    // execute a pretty printer
    PrettyPrinter pp = new PrettyPrinter();
    pp.handle(ast);
    Log.info("Pretty printing the parsed automaton into console:", AutomataTool.class.getName());
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
      AutomataParser parser = new AutomataParser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model);

      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("0xEE840 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE640 Failed to parse " + model, e);
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
  public static AutomataArtifactScope createSymbolTable(AutomataLanguage lang, ASTAutomaton ast) {

    AutomataGlobalScope globalScope = new AutomataGlobalScope(new ModelPath(), lang);

    AutomataSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(
         globalScope);
    return symbolTable.createFromAST(ast);
  }

}
