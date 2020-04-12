/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.*;
import automata._symboltable.*;
import automata._parser.AutomataParser;
import automata._symboltable.serialization.AutomataScopeDeSer;
import com.google.common.collect.Lists;
import de.monticore.generating.*;
import de.monticore.generating.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.io.paths.*;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main class for the Automaton DSL tool.
 */
public class AutomataTool {

  /** Configurational values:
   */ 
  public static final Path SYMBOL_LOCATION = Paths.get("target");
  public static final String TOP_NAME_EXTENSION = "TOP";

  /**
   * The tool calculates and uses the following
   * values along it's generation process:
   */

  // Filename of the model: args[0]
  static protected String modelfilename = "";

  //  handcodedPath: args[1]
  static protected IterablePath handcodedPath;

  // output directory: args[2]
  static File outputDir;
  
  // The AST of the model to be handled (will result from parsing)
  protected ASTAutomaton ast = null;
  
  // the symbol table of the model (after parsing and SymTab creation)
  AutomataArtifactScope modelTopScope  = null;
  
  
  // parse the model and create the AST representation
// XXX  final ASTAutomaton ast = parse(model);

// XXX
  protected List<ASTTransition> transitionsWithoutDuplicateInputs;

  protected GeneratorEngine ge;

  protected ASTAutomaton model;

  // XXX todel
  public AutomataTool(ASTAutomaton ast, IterablePath handcodedPath, File outputDir) {
    this.model = ast;
    this.handcodedPath = handcodedPath;
    this.ge = initGeneratorEngine(ast.getName(), outputDir);
    this.transitionsWithoutDuplicateInputs = getRepresentatives(ast.getTransitionList());
  }

  /**
   * Use three arguments to specify the automata model,
   * the path containing handwritten extensions of
   * the generated code and the output directory.
   *
   * @param args requires 3 arguments:
   *     1. automata modelfile,
   *     2. handcodedPath,
   *     3. output directory
   */
  public static void main(String[] args) {
    new AutomataTool(args);
  }

  /**
   * Entry method of the AutomataTool:
   * It extracts the relevant three arguments from the command line argumemnts
   * and calls the tool execution workflow
   */
  public AutomataTool(String[] args) {
    if (args.length != 3) {
      Log.error("Please specify 3 arguments: \n"
          + "1. automata modelfile,\n"
          + "2. handcodedPath,\n"
          + "3. output directory.");
      return;
    }
    // get the model from args
    modelfilename = args[0];

    // get handcodedPath from arg[1]
    handcodedPath = IterablePath.from(new File(args[1]), "java");

    // get output directory from arg[2]
    outputDir = new File(args[2]);
    
    executeWorkflow();
  }
  
  /**
   * Second entry method of the AutomataTool:
   * it stores the input parameters and calls the execution workflow
   */
  public AutomataTool(String modelfilename, IterablePath handcodedPath, File outputDir) {
    this.modelfilename = modelfilename;
    this.handcodedPath = handcodedPath;
    this.outputDir = outputDir;
    
    executeWorkflow();
  }
  
  
// XXX To be added:    this.ge = initGeneratorEngine(ast.getName(), outputDir);
// XXX To be added:    this.transitionsWithoutDuplicateInputs = getRepresentatives(ast.getTransitionList());

  
  /**
   * The execution workflow:
   * a single larger method calling all the individual steps needed
   */
  public void executeWorkflow() {

    // parse the model and create the AST representation
    ast = parse(modelfilename);
    Log.info(modelfilename + " parsed successfully", this.getClass().getName());

    // setup the symbol table
    modelTopScope = createSymbolTable(ast);

    // store artifact scope and its symbols
    AutomataScopeDeSer deser = new AutomataScopeDeSer();
    deser.setSymbolFileExtension("autsym");
    deser.store(modelTopScope, SYMBOL_LOCATION);
    Log.info(modelfilename + " symboltable stored successfully", this.getClass().getName());

    // execute generator
    generate(ast, handcodedPath, outputDir);
    Log.info(modelfilename + " code generated successfully", this.getClass().getName());

  }

  /**
   * Generates the different classes for the state pattern implementation.
   *
   * @param ast the model the state pattern is generated for
   * @param handcodedPath path including handwritten extension of generated code
   * @param outputDir the target directory
   */
  protected static void generate(ASTAutomaton ast, IterablePath handcodedPath, File outputDir) {

    AutomataTool tool = new AutomataTool(ast, handcodedPath, outputDir);

    //generate the class for the whole statechart
    tool.generateStatechart();

    //generate the factory class for the states
    tool.generateFactory();

    //generate the abstract class for the states
    tool.generateAbstractState();

    // generate the class for each state
    for(ASTState state : ast.getStateList()) {
      tool.generateState(state);
    }
  }

  /**
   * Generates the class for the given state
   *
   * @param state the state the code is generated for.
   */
  protected void generateState(ASTState state) {
    String stateClassName = state.getName()+"State";
    boolean stateIsHandwritten = existsHandwrittenClass(handcodedPath,stateClassName);
    if(stateIsHandwritten){
      stateClassName=stateClassName+ TOP_NAME_EXTENSION;
    }
    List<ASTTransition> existingTransitions = getOutgoingTransitions(model.getTransitionList(), state);
    //get representatives for transitions whose input is no accepted by this state
    // -> every state needs to have a method for every input in the statechart

    List<String> acceptedInputs = existingTransitions.stream().map(t -> t.getInput()).collect(
        Collectors.toList());
    List<ASTTransition> nonExistingTransitions = getRepresentatives(model.getTransitionList(), acceptedInputs);
    //generate the concrete state classes and use the template ConcreteState.ftl for this
    ge.generate("ConcreteState.ftl", Paths.get(stateClassName+ ".java"),
                 model, existingTransitions, nonExistingTransitions, stateClassName, stateIsHandwritten);
  }

  /**
   * Generates the class for the statechart itself
   */
  protected void generateStatechart() {
    String modelClassName = model.getName();
    ASTState initialState = model.getStateList().stream().filter(ASTState::isInitial).findAny().get();
    boolean statechartIsHandwritten = existsHandwrittenClass(handcodedPath,modelClassName);
    if(statechartIsHandwritten){
      modelClassName = model.getName() + TOP_NAME_EXTENSION;
    }
    ge.generate("Statechart.ftl", Paths.get(modelClassName + ".java"),
        model,initialState, transitionsWithoutDuplicateInputs, model.getStateList(),
        modelClassName, statechartIsHandwritten);
  }

  /**
   * Generates the class for the state factory
   */
  protected void generateFactory() {
    String modelFactoryClassName = model.getName()+"Factory";
    boolean factoryIsHandwritten = existsHandwrittenClass(handcodedPath,modelFactoryClassName);
    if(factoryIsHandwritten){
      modelFactoryClassName = modelFactoryClassName+ TOP_NAME_EXTENSION;
    }
    ge.generate("StatechartFactory.ftl", Paths.get(modelFactoryClassName+ ".java"),
        model, model.getStateList(), modelFactoryClassName, factoryIsHandwritten);
  }

  /**
   * Generates the class for the abstract super class for all state classes
   */
  protected void generateAbstractState() {
    String abstractStateClassName = "Abstract"+ model.getName() +"State";
    if(existsHandwrittenClass(handcodedPath,abstractStateClassName)){
      abstractStateClassName = abstractStateClassName+ TOP_NAME_EXTENSION;
    }
    ge.generate("AbstractState.ftl", Paths.get(abstractStateClassName+ ".java"),
                 model, transitionsWithoutDuplicateInputs, abstractStateClassName);
  }

  /**
   * Initializes the generator engine
   *
   * @param modelName the name of the model used to generate code
   * @return the initialized generator engine
   */
  protected static GeneratorEngine initGeneratorEngine(String modelName, File outputDir) {
    GeneratorSetup s = new GeneratorSetup();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("modelName",modelName);
    s.setGlex(glex);
    s.setOutputDirectory(outputDir);
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
   * @param ast the model
   * @return
   */
  public static AutomataArtifactScope createSymbolTable(ASTAutomaton ast) {

    final AutomataLanguage lang = AutomataSymTabMill.automataLanguageBuilder().build();

    AutomataGlobalScope globalScope = AutomataSymTabMill.automataGlobalScopeBuilder()
        .setModelPath(new ModelPath()).setAutomataLanguage(lang).build();

    AutomataSymbolTableCreatorDelegator symbolTable = lang.getSymbolTableCreator(globalScope);
    return symbolTable.createFromAST(ast);
  }

  public static boolean existsHandwrittenClass(IterablePath targetPath, String qualifiedName) {
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
