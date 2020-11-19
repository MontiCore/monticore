/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._cocos.AutomataCoCoChecker;
import automata._parser.AutomataParser;
import automata._visitor.AutomataTraverser;
import automata._symboltable.*;
import automata.cocos.AtLeastOneInitialAndFinalState;
import automata.cocos.StateNameStartsWithCapitalLetter;
import automata.cocos.TransitionSourceExists;
import automata.prettyprint.PrettyPrinter;
import automata.visitors.CountStates;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import automata.AutomataMill;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Main class for the Automata DSL tool.
 *
 */
public class AutomataTool {

  /**
   * Main method of the Tool
   *
   * Arguments expected:
   * * input automaton file.
   * * the path to store the symbol table
   *
   * @param args
   */
  public static void main(String[] args) {
    // delegate main to instantiatable method for better integration,
    // reuse, etc.
    new AutomataTool().run(args);
  }

  /**
   * Run implements the main method of the Automata tool workflow:
   *
   * Arguments expected:
   * * input automaton file.
   * * the path to store the symbol table
   *
   * @param args
   */
  public void run(String[] args) {

    // use normal logging (no DEBUG, TRACE)
    Log.ensureInitalization();
    
    // Retrieve the model name
    if (args.length != 2) {
      Log.error("0xEE7400 Arguments are: (1) input model and (2) symbol store.");
      return;
    }
    Log.info("Automata DSL Tool", "AutomataTool");
    String model = args[0];

    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", "AutomataTool");

    // setup the symbol table
    IAutomataArtifactScope modelTopScope = createSymbolTable(ast);
    
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

    // setup context condition infrastructure
    AutomataCoCoChecker checker = new AutomataCoCoChecker();

    // add a custom set of context conditions
    checker.addCoCo(new StateNameStartsWithCapitalLetter());
    checker.addCoCo(new AtLeastOneInitialAndFinalState());
    checker.addCoCo(new TransitionSourceExists());

    // check the CoCos
    checker.checkAll(ast);

    // Now we know the model is well-formed and start backend

    // store artifact scope and its symbols
    AutomataScopeDeSer deser = new AutomataScopeDeSer();
    deser.store(modelTopScope, args[1]);
    Log.info("Symbol table stored in " + args[1] +".", "AutomataTool");

    // analyze the model with a visitor
    CountStates cs = new CountStates();
    AutomataTraverser traverser = AutomataMill.traverser();
    traverser.setAutomataVisitor(cs);
    ast.accept(traverser);
    Log.info("Automaton has " + cs.getCount() + " states.", "AutomataTool");

    // execute a pretty printer
    PrettyPrinter pp = new PrettyPrinter();
    AutomataTraverser traverser1 = AutomataMill.traverser();
    traverser1.setAutomataVisitor(pp);
    traverser1.setAutomataHandler(pp);
    ast.accept(traverser1);
    Log.info("Pretty printing automaton into console:", "AutomataTool");
    // print the result
    Log.println(pp.getResult());
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
   * @param ast
   * @return
   */
  public static IAutomataArtifactScope createSymbolTable(ASTAutomaton ast) {
    AutomataMill.globalScope().setFileExt("aut");
    return AutomataMill.automataSymbolTableCreator().createFromAST(ast);
  }

}
