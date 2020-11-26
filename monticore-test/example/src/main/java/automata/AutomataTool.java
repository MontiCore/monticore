/* (c) https://github.com/MontiCore/monticore */
package automata;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

import automata._symboltable.*;
import org.antlr.v4.runtime.RecognitionException;

import automata._ast.ASTAutomaton;
import automata._cocos.AutomataCoCoChecker;
import automata._parser.AutomataParser;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;

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
    
    // setup the deser infrastructure
    final AutomataScopeDeSer deser = new AutomataScopeDeSer();

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
    String qualifiedModelName = model.replace("src/main/resources", "");
    qualifiedModelName = qualifiedModelName.replace("src/test/resources", "");
    String outputFileName = Paths.get(model).getFileName()+"sym";
    String packagePath = Paths.get(qualifiedModelName).getParent().toString();
    String storagePath = Paths.get("target/symbols", packagePath,
        outputFileName).toString();
    deser.store(modelTopScope,storagePath);
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
    IAutomataGlobalScope globalScope = AutomataMill.globalScope();
    globalScope.setModelPath(new ModelPath());
    globalScope.setFileExt("aut");

    AutomataSymbolTableCreator symbolTable = AutomataMill
        .automataSymbolTableCreator();
    symbolTable.putOnStack(globalScope);
  
    return symbolTable.createFromAST(ast);
  }
  
  
}
