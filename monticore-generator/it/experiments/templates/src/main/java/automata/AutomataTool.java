/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._cocos.AutomataCoCoChecker;
import automata._parser.AutomataParser;
import automata._symboltable.*;
import automata._symboltable.serialization.AutomataScopeDeSer;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Main class for the Automaton DSL tool.
 */
public class AutomataTool {
  
  public static final String DEFAULT_SYMBOL_LOCATION = "target";

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
    
    // setup the language infrastructure
    final AutomataLanguage lang = AutomataSymTabMill.automataLanguageBuilder().build();
    final AutomataScopeDeSer deser = new AutomataScopeDeSer();

    // parse the model and create the AST representation
    final ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", AutomataTool.class.getName());
    
    // setup the symbol table
    AutomataArtifactScope modelTopScope = createSymbolTable(lang, ast);

    // can be used for resolving things in the model
    Optional<StateSymbol> aSymbol = modelTopScope.resolveState("Ping");
    if (aSymbol.isPresent()) {
      Log.info("Resolved state symbol \"Ping\"; FQN = " + aSymbol.get().toString(),
          AutomataTool.class.getName());
    }
    
    
    // store artifact scope
    deser.store(modelTopScope,lang, DEFAULT_SYMBOL_LOCATION);
  
    GeneratorSetup s = new GeneratorSetup();
    s.setOutputDirectory(new File("target/statepattern"));
    GeneratorEngine ge = new GeneratorEngine(s);
  
    
    
    // execute generator
    Log.info("Generating code for the parsed automata:"+ ast.getName(), AutomataTool.class.getName());
    ge.generate("Statechart.ftl", Paths.get(ast.getName() +".java"), ast);
    ge.generate("AbstractState.ftl", Paths.get("AbstractState.java"), ast);
    for(ASTState state : ast.getStateList()) {
      ge.generate("ConcreteState.ftl", Paths.get(state.getName()+"State.java"), ast);
    }
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
   * @param lang
   * @param ast
   * @return
   */
  public static AutomataArtifactScope createSymbolTable(AutomataLanguage lang, ASTAutomaton ast) {
    
    AutomataGlobalScope globalScope = AutomataSymTabMill.automataGlobalScopeBuilder()
        .setModelPath(new ModelPath()).setAutomataLanguage(lang).build();
    
    AutomataSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    return symbolTable.createFromAST(ast);
  }
  
  
}
