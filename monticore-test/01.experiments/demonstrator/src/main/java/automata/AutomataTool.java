/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._ast.ASTTransition;
import automata._cocos.AutomataCoCoChecker;
import automata._parser.AutomataParser;
import automata._symboltable.*;
import automata._visitor.AutomataTraverser;
import automata.cocos.AtLeastOneInitialAndFinalState;
import automata.cocos.StateNameStartsWithCapitalLetter;
import automata.cocos.TransitionSourceExists;
import automata.prettyprint.PrettyPrinter;
import automata.visitors.CountStates;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;

/**
 * Main class for the Automaton DSL tool.
 */
public class AutomataTool extends AutomataToolTOP {

  /**
   * Configurable values:
   */
  public static final String TOP_NAME_EXTENSION = "TOP";

  /**
   * The tool calculates and uses the following
   * values along it's generation process.
   * <p>
   * This implementation follows the general approach to store all
   * derived data in attributes of the tool-object.
   * This PREVENTS re-entrant reuse of the AutomationTool.
   * But it allows to share calculated results in the attributes below.
   * <p>
   * Furthermore, in this approach we pre-calculate the needed information
   * (such as stimuli lists) allowing the templates to be relatively free
   * of calculations, iterations and callbacks to the java code.
   * This MAY lead to inefficient and not needed pre-calculations and
   * missing information -- in case the templates are adapted.
   */


  // The AST of the model to be handled (will result from parsing)
  protected ASTAutomaton ast;

  // The Global Scope of the symbol table
  protected IAutomataGlobalScope globalScope;
  // The generator engine used (reentrant, so only one instance needed)
  protected GeneratorEngine generatorEngine;
  // The global extension management used
  protected GlobalExtensionManagement glex;
  // the symbol table of the model (after parsing and SymTab creation)
  IAutomataArtifactScope modelTopScope;
  // Two dimensional map: SourceState x Stimulus -> Transition
  Map<ASTState, Map<String, ASTTransition>> deltaMap = new HashMap<>();

  // Map: Name -> State (== State-Symbols)
  Map<String, ASTState> stateMap = new HashMap<>();

  // List of stimuli
  Set<String> stimuli = new HashSet<>();

  /**
   * Entry method of the AutomataTool:
   * It extracts the relevant three arguments from the command line argumemnts
   * and calls the tool execution workflow
   */
  @Override
  public void run(String[] args) {
    init();
    Options options = initOptions();
   //todo kann das weg?
    /* // (i, s, hw, o, p)
    // get the model from args
    if (args.length < 4) {
      Log.error("0xDE631 Please specify at least 4 arguments: \n"
          + "1. automata modelfile,\n"
          + "2. symbol file,\n"
          + "3. handcodedPath,\n"
          + "4. output directory,\n"
          + "5. (optional) templatePath\n"
      );
      return;
    }*/
    try {
      CommandLineParser toolParser = new DefaultParser();
      CommandLine cmd = toolParser.parse(options, args);
      String input;
      String symbolFile;
      MCPath handCodedPath;
      MCPath templatePath = new MCPath();
      File outputDir;

      if(cmd.hasOption("i")) {
         input = cmd.getOptionValue("i");
      }else  {
        Log.error("0xA5C02 Must specify an Input file.");
        return;
      }
      if(cmd.hasOption("s")){
         symbolFile = cmd.getOptionValue("s");
      }else {
        Log.error("0xA5C03 Must specify a symbolFile.");
        return;
      }
      if(cmd.hasOption("hw")){
         handCodedPath =  new MCPath(cmd.getOptionValue("hw"));
      }else {
        Log.error("0xA5C04 Must specify a hand coded path.");
        return;
      }
      if(cmd.hasOption("o")){
         outputDir = new File(cmd.getOptionValue("o"));
      }else {
        Log.error("0xA5C05 Must specify an output directory.");
        return;
      }
      if(cmd.hasOption("p")){
        templatePath.addEntry(Paths.get(cmd.getOptionValue("p")));
      }

      executeWorkflow(input,handCodedPath,outputDir,symbolFile,templatePath);

    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xA5C01x75226 Could not process AutomataTool parameters: " + e.getMessage());
    }
  }

  @Override
  public void init() {
    AutomataMill.init();
  }

  /**
   * The execution workflow:
   * a single larger method calling all the individual steps needed
   */
  public void executeWorkflow(String modelfilename, MCPath handcodedPath, File outputDir, String symbolFileName, MCPath templatePath) {

    // Part 1: Frontend
    // parse the model and create the AST representation
    ast = parse(modelfilename);
    Log.info(modelfilename + " parsed successfully", this.getClass().getName());

    // setup the symbol table
    modelTopScope = createSymbolTable(ast);

    // can be used for resolving names in the model
    Optional<StateSymbol> aSymbol =
        modelTopScope.resolveState("Ping");
    if (aSymbol.isPresent()) {
      Log.info("Resolved state symbol \"Ping\"; FQN = "
              + aSymbol.get().toString(),
          "AutomataTool");
    } else {
      Log.info("This automaton does not contain a state called \"Ping\";",
          "AutomataTool");
    }

    // Part 2: CoCos
    AutomataCoCoChecker checker = new AutomataCoCoChecker();

    // add a custom set of context conditions
    checker.addCoCo(new StateNameStartsWithCapitalLetter());
    checker.addCoCo(new AtLeastOneInitialAndFinalState());
    checker.addCoCo(new TransitionSourceExists());

    // check the CoCos
    checker.checkAll(ast);

    // Now we know the model is well-formed and start backend

    // Part 3: Store Symboltable
    // store artifact scope and its symbols
    AutomataSymbols2Json deser = new AutomataSymbols2Json();
    deser.store(modelTopScope, symbolFileName);

    // analyze the model with a visitor
    CountStates cs = new CountStates();
    AutomataTraverser traverser = AutomataMill.traverser();
    traverser.add4Automata(cs);
    ast.accept(traverser);
    Log.info("Automaton has " + cs.getCount() + " states.", "AutomataTool");

    // execute a pretty printer
    PrettyPrinter pp = new PrettyPrinter();
    AutomataTraverser traverser2 = AutomataMill.traverser();
    traverser2.setAutomataHandler(pp);
    ast.accept(traverser2);
    Log.info("Pretty printing automaton into console:", "AutomataTool");
    // print the result
    Log.println(pp.getResult());
    Log.info(modelfilename + " symboltable stored successfully", this.getClass().getName());

    // Part 4: Transformation and Data Calculation
    deriveStateMap_DeltaMap();

    // Part 5: Backend for Generation: Setup Engine
    initGeneratorEngine(outputDir, templatePath);

    // Part 6: Generate.....
    //generate the class for the whole statechart
    generateStatechart(handcodedPath);

    //generate the factory class for the states
    generateFactory(handcodedPath);

    //generate the abstract class for the states
    generateAbstractState(handcodedPath);

    // generate the class for each state
    for (ASTState state : ast.getStateList()) {
      generateState(state, handcodedPath);
    }

    Log.info(modelfilename + " code generated successfully", this.getClass().getName());
  }

  /************************************************************************/
  /***          The Initialization Functions                            ***/
  /************************************************************************/

  /**
   * Initializes the generator engine
   */
  protected void initGeneratorEngine(File outputDir, MCPath templatePath) {
    initGlex();
    GeneratorSetup s = new GeneratorSetup();

    s.setGlex(glex);
    s.setOutputDirectory(outputDir);
    if (!templatePath.isEmpty()) {
      s.setAdditionalTemplatePaths(
          templatePath.getEntries().stream()
              .map(t -> t.toFile())
              .collect(Collectors.toList()));
    }
    generatorEngine = new GeneratorEngine(s);
  }

  /**
   * Initializes the global extension management
   */
  protected void initGlex() {
    glex = new GlobalExtensionManagement();

    // The modelName is used veryeher and does not change during generation
    glex.setGlobalValue("modelName", ast.getName());
  }

  /************************************************************************/
  /***          The Generator Functions: each creates a java file       ***/
  /************************************************************************/

  /**
   * Generates the class for the statechart itself
   */
  protected void generateStatechart(MCPath handcodedPath) {
    String className = ast.getName();

    // we assume there is at least one state (--> CoCo)
    // if there are more: one will arbitrarily be choosen (may be the last one)  (---> CoCo?)
    ASTState initialState = ast.getStateList().stream().filter(ASTState::isInitial).findAny().get();

    // handle TOP extension
    boolean isHW = existsHandwrittenClass(handcodedPath, className);
    if (isHW) {
      className = ast.getName() + TOP_NAME_EXTENSION;
    }

    // call generator
    generatorEngine.generate("Statechart.ftl",
        Paths.get(className + ".java"), ast,
        initialState,
        stimuli,
        className, isHW);
  }

  /**
   * Generates the abstract super class for all state classes
   */
  protected void generateAbstractState(MCPath handcodedPath) {
    String className = "Abstract" + ast.getName() + "State";

    // handle TOP extension
    if (existsHandwrittenClass(handcodedPath, className)) {
      className += TOP_NAME_EXTENSION;
    }

    generatorEngine.generate("AbstractState.ftl",
        Paths.get(className + ".java"), ast,
        stimuli, className);
  }


  /**
   * Generates a class for the given state
   *
   * @param state the state the code is generated for.
   */
  protected void generateState(ASTState state, MCPath handcodedPath) {
    String className = state.getName() + "State";

    // handle TOP extension
    boolean isHW = existsHandwrittenClass(handcodedPath, className);
    if (isHW) {
      className += TOP_NAME_EXTENSION;
    }

    // sub Map of delta: contains all transitions starting in this state
    Map<String, ASTTransition> outgoing = deltaMap.get(state);

    generatorEngine.generate("ConcreteState.ftl",
        Paths.get(className + ".java"), ast,
        outgoing, className, isHW);
  }

  /**
   * Generates the class for the state factory
   * (although the factory isn't used in this example)
   */
  protected void generateFactory(MCPath handcodedPath) {
    String className = ast.getName() + "Factory";

    // handle TOP extension
    boolean isHW = existsHandwrittenClass(handcodedPath, className);
    if (isHW) {
      className = className + TOP_NAME_EXTENSION;
    }

    generatorEngine.generate("StatechartFactory.ftl",
        Paths.get(className + ".java"), ast,
        className, isHW);
  }

  /**
   * Calculates a list of transitions that act as representatives for all occuring stimuli
   * (each stumulis is represented exactly once in that list)
   */
  protected void deriveStateMap_DeltaMap() {

    // We might also extend and use the symbol table for this extra infos
    // For demonstration we use the direct approach

    // initialize delta: transition map of maps, and state name2node
    for (ASTState s : ast.getStateList()) {
      stateMap.put(s.getName(), s);
      deltaMap.put(s, new HashMap<>());
    }

    // Add the transitions to the table
    for (ASTTransition t : ast.getTransitionList()) {
      String input = t.getInput();
      stimuli.add(input);
      ASTState from = stateMap.get(t.getFrom());
      // we assume that the automaton is deterministic --> CoCo
      // if it isn't one transition will arbitrarily choosen (may be the last one)
      // However, it may be incomplete!
      deltaMap.get(from).put(input, t);
    }
  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param file - file to parse
   * @return
   */
  public ASTAutomaton parse(String file) {
    try {
      AutomataParser parser = new AutomataParser();
      Optional<ASTAutomaton> optAutomaton = parser.parse(file);

      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("0xD38F1 Model could not be parsed: " + file + ".");
    } catch (RecognitionException | IOException e) {
      Log.error("0xD38F2 Failed to parse " + file, e);
    }
    return null;
  }

  /**
   * Create the symbol table from the parsed AST.
   *
   * @param ast the model
   * @return
   */
  public IAutomataArtifactScope createSymbolTable(ASTAutomaton ast) {

    IAutomataGlobalScope globalScope = AutomataMill.globalScope();
    globalScope.setSymbolPath(new MCPath());

    AutomataScopesGenitorDelegator symbolTable = AutomataMill
        .scopesGenitorDelegator();

    return symbolTable.createFromAST(ast);
  }

  @Override
  public Options addAdditionalOptions(Options options) {

    //path to handwritten artifact
    options.addOption(org.apache.commons.cli.Option.builder("hw")
        .longOpt("handWritten")
        .argName("path")
        .hasArg()
        .desc("Path to the hand-written java artifact.")
        .build());


    //
    options.addOption(org.apache.commons.cli.Option.builder("o")
        .longOpt("output")
        .argName("path")
        .hasArg()
        .desc("Prints out the output.")
        .build());

    return options;

  }


}
