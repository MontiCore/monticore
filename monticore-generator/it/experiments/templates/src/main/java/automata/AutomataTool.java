/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._ast.ASTTransition;
import automata._cocos.AutomataCoCoChecker;
import automata._parser.AutomataParser;
import automata._symboltable.*;
import automata._symboltable.serialization.AutomataScopeDeSer;
import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Main class for the Automaton DSL tool.
 *
 * @author (last commit) $Author$
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
    
    // store artifact scope
    deser.store(modelTopScope,lang, DEFAULT_SYMBOL_LOCATION);
  
    GeneratorSetup s = new GeneratorSetup();
    s.setOutputDirectory(new File("target/statepattern"));
    GeneratorEngine ge = new GeneratorEngine(s);

    // execute generator
    Log.info("Generating code for the parsed automata:"+ ast.getName(), AutomataTool.class.getName());

    //TODO ND: verschönern, Test schreiben, überlegen: completion-strategie error-state oder ignore

    //get the initial state of the statechart
    ASTState initialState = ast.getStateList()
        .stream()
        .filter(ASTState::isInitial)
        .collect(Collectors.toList())
        .get(0);

    //get all transitions of the statechart
    List<ASTTransition> transitions = ast.getTransitionList();
    //get unique transition names -> some transitions inputs are used multiple times in the model
    List<String> transitionNames = transitions.stream().map(ASTTransition::getInput).distinct().collect(Collectors.toList());

    String modelName = ast.getName();
    //generate the class for the whole statechart
    ge.generate("Statechart.ftl", Paths.get(ast.getName() +".java"), ast,modelName,initialState.getName(), transitionNames);
    //generate the abstract class for the states
    ge.generate("AbstractState.ftl", Paths.get("AbstractState.java"), ast, transitionNames,modelName);
    for(ASTState state : ast.getStateList()) {
      //get the transitions that have this state as their source state
      List<ASTTransition> existingTransitions = transitions.stream().filter(t -> t.getFrom().equals(state.getName())).collect(Collectors.toList());
      //get the names of the transitions that this state does not have -> every state needs to have a method for every transition in the statechart
      List<String> nonExistingTransitionNames = transitionNames.stream().filter((name)->(!existingTransitions.stream().map(ASTTransition::getInput).collect(Collectors.toList()).contains(name))).collect(Collectors.toList());
      //generate the concrete state classes and use the template ConcreteState.ftl for this
      ge.generate("ConcreteState.ftl", Paths.get(state.getName()+"State.java"), ast, state.getName(), ast.getName(),existingTransitions, nonExistingTransitionNames);
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
