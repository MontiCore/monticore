/* (c) https://github.com/MontiCore/monticore */
package automata;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import automata._symboltable.*;
import de.monticore.utils.Names;
import org.antlr.v4.runtime.RecognitionException;

import automata._ast.ASTAutomaton;
import automata._cocos.AutomataCoCoChecker;
import automata._parser.AutomataParser;
import de.se_rwth.commons.logging.Log;

import static de.se_rwth.commons.Names.getPathFromPackage;

/**
 * Main class for the Automaton DSL tool.
 *
 */
public class AutomataTool {

  /**
   * Use the single argument for specifying the single input automata file.
   *
   * @param args
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      Log.error("Please specify only one single path to the input model.");
      return;
    }
    String model = args[0];

    // setup the serialization infrastructure
    final AutomataSymbols2Json s2j = new AutomataSymbols2Json();

    // parse the model and create the AST representation
    final ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", AutomataTool.class.getName());

    // setup the symbol table
    IAutomataArtifactScope modelTopScope = createSymbolTable(ast);

    // can be used for resolving things in the model
    Optional<StateSymbol> aSymbol = modelTopScope.resolveState("Ping");
    if (aSymbol.isPresent()) {
      Log.info("Resolved state symbol \"Ping\"; FQN = " + aSymbol.get().toString(),
          AutomataTool.class.getName());
    }

    // execute a custom set of context conditions
    AutomataCoCoChecker customCoCos = new AutomataCoCoChecker();
    customCoCos.checkAll(ast);

    // store artifact scope
    String symFile = "target/symbols/"+ getPathFromPackage(modelTopScope.getFullName()) + ".autsym";
    s2j.store(modelTopScope, symFile);
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
   * @param ast
   * @return
   */
  public static IAutomataArtifactScope createSymbolTable(ASTAutomaton ast) {
    return AutomataMill.scopesGenitorDelegator().createFromAST(ast);
  }


}
