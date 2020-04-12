/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.*;
import automata._symboltable.*;
import automata._parser.AutomataParser;
import automata._symboltable.serialization.AutomataScopeDeSer;
import com.google.common.collect.Lists;
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
  protected ASTAutomaton ast;
  
  // the symbol table of the model (after parsing and SymTab creation)
  AutomataArtifactScope modelTopScope;

  // The generator engine used (reentrant, so only one instance needed)
  protected GeneratorEngine generatorEngine;
  
  // XXX
  protected List<ASTTransition> transitionsWithoutDuplicateInputs;


  
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
  
  
  /**
   * The execution workflow:
   * a single larger method calling all the individual steps needed
   */
  public void executeWorkflow() {

    // Part 1: Frontend
    // parse the model and create the AST representation
    ast = parse(modelfilename);
    Log.info(modelfilename + " parsed successfully", this.getClass().getName());

    // setup the symbol table
    modelTopScope = createSymbolTable(ast);
  
    // Part 2: CoCos
    // deliberately omitted
  
    // Part 3: Store Symboltable
    // store artifact scope and its symbols
    AutomataScopeDeSer deser = new AutomataScopeDeSer();
    deser.setSymbolFileExtension("autsym");
    deser.store(modelTopScope, SYMBOL_LOCATION);
    Log.info(modelfilename + " symboltable stored successfully", this.getClass().getName());
  
  
    // Part 4: Transformation and Data Calculation
    // XXX
    transitionsWithoutDuplicateInputs = getRepresentatives(ast.getTransitionList());
    
    
    // Part 3: Backend for Generation
    generatorEngine = initGeneratorEngine(ast.getName(), outputDir);
    
    //generate the class for the whole statechart
    generateStatechart();
  
    //generate the factory class for the states
    generateFactory();
  
    //generate the abstract class for the states
    generateAbstractState();
  
    // generate the class for each state
    for(ASTState state : ast.getStateList()) {
      generateState(state);
    }
  
    Log.info(modelfilename + " code generated successfully", this.getClass().getName());
  }

  /************************************************************************/
  /***          The Generator Functions: each creates a java file       ***/
  /************************************************************************/
  
  /**
   * Generates the class for the statechart itself
   */
  protected void generateStatechart() {
    String className = ast.getName();
    // we assume there is at least one state (--> CoCo)
    ASTState initialState = ast.getStateList().stream().filter(ASTState::isInitial).findAny().get();
    
    boolean isHW = existsHandwrittenClass(handcodedPath,className);
    if(isHW){
      className = ast.getName() + TOP_NAME_EXTENSION;
    }
    generatorEngine.generate("Statechart.ftl",
            Paths.get(className + ".java"),
            ast, initialState,
            transitionsWithoutDuplicateInputs,
            ast.getStateList(),
            className, isHW);
  }


  /**
   * Generates the class for the given state
   *
   * @param state the state the code is generated for.
   */
  protected void generateState(ASTState state) {
    
    // Add Top Extension to the Class name if existsHandwrittenClass
    String className = state.getName()+"State";
    boolean stateIsHandwritten = existsHandwrittenClass(handcodedPath,className);
    if(stateIsHandwritten){
      className += TOP_NAME_EXTENSION;
    }
    // XXX
  
    List<ASTTransition> existingTransitions = getOutgoingTransitions(ast.getTransitionList(), state);
    //get representatives for transitions whose input is no accepted by this state
    // -> every state needs to have a method for every input in the statechart

    List<String> acceptedInputs = existingTransitions.stream().map(t -> t.getInput()).collect(
        Collectors.toList());
    List<ASTTransition> nonExistingTransitions = getRepresentatives(ast.getTransitionList(), acceptedInputs);
    //generate the concrete state classes and use the template ConcreteState.ftl for this
    generatorEngine.generate("ConcreteState.ftl", Paths.get(className+ ".java"),
                 ast, existingTransitions, nonExistingTransitions, className, stateIsHandwritten);
  }
  
  
  
  // XXX
  /**
   * Generates the class for the state factory
   */
  protected void generateFactory() {
    String modelFactoryClassName = ast.getName()+"Factory";
    boolean factoryIsHandwritten = existsHandwrittenClass(handcodedPath,modelFactoryClassName);
    if(factoryIsHandwritten){
      modelFactoryClassName = modelFactoryClassName+ TOP_NAME_EXTENSION;
    }
    generatorEngine.generate("StatechartFactory.ftl", Paths.get(modelFactoryClassName+ ".java"),
        ast, ast.getStateList(), modelFactoryClassName, factoryIsHandwritten);
  }
  
  // XXX
  /**
   * Generates the class for the abstract super class for all state classes
   */
  protected void generateAbstractState() {
    String abstractStateClassName = "Abstract"+ ast.getName() +"State";
    if(existsHandwrittenClass(handcodedPath,abstractStateClassName)){
      abstractStateClassName = abstractStateClassName+ TOP_NAME_EXTENSION;
    }
    generatorEngine.generate("AbstractState.ftl", Paths.get(abstractStateClassName+ ".java"),
                 ast, transitionsWithoutDuplicateInputs, abstractStateClassName);
  }
  
  // XXX
  /**
   * Initializes the generator engine
   *
   * @param modelName the name of the model used to generate code
   * @return the initialized generator engine
   */
  protected GeneratorEngine initGeneratorEngine(String modelName, File outputDir) {
    GeneratorSetup s = new GeneratorSetup();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("modelName",modelName);
    s.setGlex(glex);
    s.setOutputDirectory(outputDir);
    return new GeneratorEngine(s);
  }
  
  // XXX
  /**
   *  For  a given state and a list of transitions this method calculates
   *  a list of all transitions that are outgoing transitions of the given state
   *
   * @param transitions transitions to be filtered
   * @param state state whose outgoing transitions are calculated
   * @return a list of all outgoing transitions for the given state
   */
  protected List<ASTTransition> getOutgoingTransitions(List<ASTTransition> transitions,
      ASTState state) {
    return transitions.stream().filter(t -> t.getFrom().equals(state.getName())).collect(Collectors.toList());
  }
  
  // XXX
  /**
   * Calculates a list of transitions that act as representatives for transitions whose input is not
   *  included in the list of inputs inputsToBeFiltered
   *
   * @param allTransitions list o all transitions in the automaton
   * @param inputsToBeExcluded inputs that should be excluded
   * @return a list of transitions that act as representatives for not accepted inputs
   */
  protected List<ASTTransition> getRepresentatives(List<ASTTransition> allTransitions, List<String> inputsToBeExcluded) {
    List<ASTTransition> result = new ArrayList<>();
    for(ASTTransition transition: allTransitions){
      if(!inputsToBeExcluded.contains(transition.getInput())){
        result.add(transition);
        inputsToBeExcluded.add(transition.getInput());
      }
    }
    return result;
  }
  
  // XXX
  /**
   * Calculates a list of transitions that act as representatives for the inputs of all transitions
   * @param allTransitions list of all transitions in the automaton
   * @return a list of transitions that act as representatives for possible inputs
   */
  protected List<ASTTransition> getRepresentatives(List<ASTTransition> allTransitions){
    return getRepresentatives(allTransitions, Lists.newArrayList());
  }
  
  /**
   * Parse the model contained in the specified file.
   *
   * @param file - file to parse
   * @return
   */
  public ASTAutomaton parse(String file) {
    try {
      AutomataParser parser = new AutomataParser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(file);

      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("0x238F1 Model could not be parsed: " + file + ".");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0x238F2 Failed to parse " + file, e);
    }
    return null;
  }
  
  /**
   * Create the symbol table from the parsed AST.
   *
   * @param ast the model
   * @return
   */
  public AutomataArtifactScope createSymbolTable(ASTAutomaton ast) {

    // TODO AB: AutomataLanguage (die Klasse!) entfernen
    final AutomataLanguage lang = AutomataSymTabMill.automataLanguageBuilder().build();

    // TODO AB: es ist nicht sinnvoll jedes Mal einen GlobalScope zu instantiieren
    // --> das ist noch eine zu bereinigende technical debt
    AutomataGlobalScope globalScope = AutomataSymTabMill.automataGlobalScopeBuilder()
        .setModelPath(new ModelPath()).setAutomataLanguage(lang).build();

    // TODO AB: ersetzen durch einfaches create.
    AutomataSymbolTableCreatorDelegator stCreator = lang.getSymbolTableCreator(globalScope);
    return stCreator.createFromAST(ast);
  }
  
  /**
   * Check whewther an handwritten version of that class already exits and shall be integrated
   * (e.g. to apply TOP mechanism)
   */
  public static boolean existsHandwrittenClass(IterablePath targetPath, String qualifiedName) {
    Path hwFile = Paths.get(Names.getPathFromPackage(qualifiedName)+ ".java");
    Optional<Path> hwFilePath = targetPath.getResolvedPath(hwFile);
    boolean result = hwFilePath.isPresent();
    if (result) {
      Reporting.reportUseHandwrittenCodeFile(hwFilePath.get(),hwFile);
    }
    Reporting.reportHWCExistenceCheck(targetPath, hwFile, hwFilePath);
    return result;
  }

}
