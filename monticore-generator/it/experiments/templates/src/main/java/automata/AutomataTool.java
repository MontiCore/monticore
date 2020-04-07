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
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    // store artifact scope
    deser.store(modelTopScope,lang, DEFAULT_SYMBOL_LOCATION);
  
    GeneratorSetup s = new GeneratorSetup();
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    s.setGlex(glex);
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


    String modelName = ast.getName();
    glex.setGlobalValue("modelName",modelName);

    //get all transitions of the statechart
    List<ASTTransition> transitions = ast.getTransitionList();

    List<ASTTransition> transitionsWithoutDuplicateNames = handleDuplicateNames(transitions,new ArrayList<>());

    List<ASTState> states = ast.getStateList();



    //generate the class for the whole statechart
    String modelClassName = modelName;
    if(existsHandwrittenClass(IterablePath.from(new File("src/test/java"),"java"),modelClassName)){
      modelClassName = modelName+"TOP";
    }
    ge.generate("Statechart.ftl", Paths.get(modelClassName +".java"), ast,initialState, transitionsWithoutDuplicateNames, states, modelClassName);


    //generate the factory class for the states
    String modelFactoryClassName = modelName+"Factory";
    if(existsHandwrittenClass(IterablePath.from(new File("src/test/java"),"java"),modelFactoryClassName)){
      modelFactoryClassName = modelFactoryClassName+"TOP";
    }
    ge.generate("StatechartFactory.ftl",Paths.get(modelFactoryClassName+".java"),ast, states, modelFactoryClassName);


    //generate the abstract class for the states
    String abstractStateClassName = "AbstractState";
    if(existsHandwrittenClass(IterablePath.from(new File("src/test/java"),"java"),abstractStateClassName)){
      abstractStateClassName= abstractStateClassName+"TOP";
    }
    ge.generate("AbstractState.ftl", Paths.get(abstractStateClassName+".java"), ast, transitionsWithoutDuplicateNames, abstractStateClassName);
    for(ASTState state : states) {
      //get the transitions that have this state as their source state
      String stateClassName = state.getName()+"State";
      if(existsHandwrittenClass(IterablePath.from(new File("src/test/java"),"java"),stateClassName)){
        stateClassName=stateClassName+"TOP";
      }
      List<ASTTransition> existingTransitions = transitions.stream().filter(t -> t.getFrom().equals(state.getName())).collect(Collectors.toList());
      //get the names of the transitions that this state does not have -> every state needs to have a method for every transition in the statechart
      List<ASTTransition> otherTransitions = transitions.stream().filter(t -> !t.getFrom().equals(state.getName())).collect(Collectors.toList());
      List<ASTTransition> nonExistingTransitions = handleDuplicateNames(otherTransitions,existingTransitions);
      //generate the concrete state classes and use the template ConcreteState.ftl for this
      ge.generate("ConcreteState.ftl", Paths.get(stateClassName+".java"), ast, existingTransitions, nonExistingTransitions, stateClassName);
    }
  }

  private static List<ASTTransition> handleDuplicateNames(List<ASTTransition> otherTransitions, List<ASTTransition> existingTransitions) {
    List<ASTTransition> result = new ArrayList<>();
    List<String> names = existingTransitions.stream().map(ASTTransition::getInput).collect(Collectors.toList());
    for(ASTTransition transition: otherTransitions){
      if(!names.contains(transition.getInput())){
        result.add(transition);
        names.add(transition.getInput());
      }
    }
    return result;
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
    Path handwrittenFile = Paths.get(Names
        .getPathFromPackage(qualifiedName)
        + ".java");
    Optional<Path> handwrittenFilePath = targetPath.getResolvedPath(handwrittenFile);
    boolean result = handwrittenFilePath.isPresent();
    if (result) {
      Reporting.reportUseHandwrittenCodeFile(handwrittenFilePath.get(),
          handwrittenFile);
    }
    Reporting.reportHWCExistenceCheck(targetPath,
        handwrittenFile, handwrittenFilePath);
    return result;
  }
  
  
}
