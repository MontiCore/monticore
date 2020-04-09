/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._ast.ASTTransition;
import automata._parser.AutomataParser;
import automata._symboltable.*;
import automata._symboltable.serialization.AutomataScopeDeSer;
import com.google.common.collect.Lists;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main class for the Automaton DSL tool.
 */
public class AutomataTool {
  
  public static final Path DEFAULT_SYMBOL_LOCATION = Paths.get("target");

  /**
   * Use the single argument for specifying the single input automata file.
   *
   * @param args
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      Log.error("Please specify only one single path to the input model.");
      return;
    }
    String model = args[0];
    // TODO ND: read handcodedPath from args
    final IterablePath handcodedPath = IterablePath.from(new File("src/extendedtest/java"), "java");
    // setup the language infrastructure
    final AutomataLanguage lang = AutomataSymTabMill.automataLanguageBuilder().build();

    // parse the model and create the AST representation
    final ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", AutomataTool.class.getName());
    
    // setup the symbol table
    AutomataArtifactScope modelTopScope = createSymbolTable(lang, ast);
    
    // store artifact scope and its symbols
    AutomataScopeDeSer deser = new AutomataScopeDeSer();
    deser.setSymbolFileExtension("autsym");
    deser.store(modelTopScope, DEFAULT_SYMBOL_LOCATION);
  
    // execute generator
    Log.info("Generating code for the parsed automata:"+ ast.getName(), AutomataTool.class.getName());
    generate(ast, handcodedPath);
  }
  
  protected static void generate(ASTAutomaton ast, IterablePath handcodedPath) {
    String modelName = ast.getName();
    GeneratorEngine ge = initGeneratorEngine(modelName);
  
    //TODO ND: verschönern, Test schreiben, überlegen: completion-strategie error-state oder ignore
  
    //get the initial state of the statechart
    ASTState initialState = ast.getStateList().stream().filter(ASTState::isInitial).findAny().get();
    
    //get all transitions of the statechart
    List<ASTTransition> transitions = ast.getTransitionList();
    
    List<ASTTransition> transitionsWithoutDuplicateInputs = getRepresentatives(transitions);
    
    List<ASTState> states = ast.getStateList();
    
    //generate the class for the whole statechart
    String modelClassName = modelName;
    boolean statechartIsHandwritten = existsHandwrittenClass(handcodedPath,modelClassName);
    if(statechartIsHandwritten){
      modelClassName = modelName+"TOP";
    }
    ge.generate("Statechart.ftl", Paths.get(modelClassName +".java"),
                 ast,initialState, transitionsWithoutDuplicateInputs, states,
                 modelClassName, statechartIsHandwritten);
    
    //generate the factory class for the states
    String modelFactoryClassName = modelName+"Factory";
    boolean factoryIsHandwritten = existsHandwrittenClass(handcodedPath,modelFactoryClassName);
    if(factoryIsHandwritten){
      modelFactoryClassName = modelFactoryClassName+"TOP";
    }
    ge.generate("StatechartFactory.ftl", Paths.get(modelFactoryClassName+".java"),
                 ast, states, modelFactoryClassName, factoryIsHandwritten);
    
    //generate the abstract class for the states
    String abstractStateClassName = "Abstract"+ modelName +"State";
    if(existsHandwrittenClass(handcodedPath,abstractStateClassName)){
      abstractStateClassName = abstractStateClassName+"TOP";
    }
    ge.generate("AbstractState.ftl", Paths.get(abstractStateClassName+".java"),
                 ast, transitionsWithoutDuplicateInputs, abstractStateClassName);
    
    for(ASTState state : states) {
      //get the transitions that have this state as their source state
      String stateClassName = state.getName()+"State";
      boolean stateIsHandwritten = existsHandwrittenClass(handcodedPath,stateClassName);
      if(stateIsHandwritten){
        stateClassName=stateClassName+"TOP";
      }
      List<ASTTransition> existingTransitions = getOutgoingTransitions(transitions, state);
      //get representatives for transitions whose input is no accepted by this state
      // -> every state needs to have a method for every input in the statechart
  
      List<String> acceptedInputs = existingTransitions.stream().map(t -> t.getInput()).collect(Collectors.toList());
      List<ASTTransition> nonExistingTransitions = getRepresentatives(transitions, acceptedInputs);
      //generate the concrete state classes and use the template ConcreteState.ftl for this
      ge.generate("ConcreteState.ftl", Paths.get(stateClassName+".java"),
                   ast, existingTransitions, nonExistingTransitions, stateClassName, stateIsHandwritten);
    }
  }
  
  /**
   * Initializes the generator engine
   *
   * @param modelName the name of the model used to generate code
   * @return the initialized generator engine
   */
  protected static GeneratorEngine initGeneratorEngine(String modelName) {
    GeneratorSetup s = new GeneratorSetup();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("modelName",modelName);
    s.setGlex(glex);
    // TODO ND: read output dir from args
    s.setOutputDirectory(new File("target/statepattern"));
    return new GeneratorEngine(s);
  }
  
  /**
   *  For  a given state and a list of transitions this method calculates
   *  a list of all transitions that are outgoing transitions of the given state
   *
   * @param transitions transitions to be filtered
   * @param state state whose outgoing transitions are calculated
   * @return a list of all outgoing transitions for the given state
   */
  protected static List<ASTTransition> getOutgoingTransitions(List<ASTTransition> transitions,
      ASTState state) {
    return transitions.stream().filter(t -> t.getFrom().equals(state.getName())).collect(Collectors.toList());
  }
  
  /**
   * Calculates a list of transitions that act as representatives for transitions whose input is not
   *  included in the list of inputs inputsToBeFiltered
   *
   * @param allTransitions list o all transitions in the automaton
   * @param inputsToBeExcluded inputs that should be excluded
   * @return a list of transitions that act as representatives for not accepted inputs
   */
  protected static List<ASTTransition> getRepresentatives(List<ASTTransition> allTransitions, List<String> inputsToBeExcluded) {
    List<ASTTransition> result = new ArrayList<>();
    for(ASTTransition transition: allTransitions){
      if(!inputsToBeExcluded.contains(transition.getInput())){
        result.add(transition);
        inputsToBeExcluded.add(transition.getInput());
      }
    }
    return result;
  }
  
  /**
   * Calculates a list of transitions that act as representatives for the inputs of all transitions
   * @param allTransitions list of all transitions in the automaton
   * @return a list of transitions that act as representatives for possible inputs
   */
  protected static List<ASTTransition> getRepresentatives(List<ASTTransition> allTransitions){
    return getRepresentatives(allTransitions, Lists.newArrayList());
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

  public static boolean existsHandwrittenClass(IterablePath targetPath,
                                               String qualifiedName) {
    Path handwrittenFile = Paths.get(Names.getPathFromPackage(qualifiedName)+ ".java");
    Optional<Path> handwrittenFilePath = targetPath.getResolvedPath(handwrittenFile);
    boolean result = handwrittenFilePath.isPresent();
    if (result) {
      Reporting.reportUseHandwrittenCodeFile(handwrittenFilePath.get(),handwrittenFile);
    }
    Reporting.reportHWCExistenceCheck(targetPath,
        handwrittenFile, handwrittenFilePath);
    return result;
  }
  
  
}
