/* (c) https://github.com/MontiCore/monticore */
package automata;

import automata._ast.ASTAutomaton;
import automata._cocos.AutomataCoCoChecker;
import automata._parser.AutomataParser;
import automata._symboltable.*;
import automata._visitor.AutomataTraverser;
import automata.cocos.AtLeastOneInitialAndFinalState;
import automata.cocos.StateNameStartsWithCapitalLetter;
import automata.cocos.TransitionSourceExists;
import automata.prettyprint.PrettyPrinter;
import automata.visitors.CountStates;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.Optional;


/**
 * Main class for the Automata DSL tool.
 *
 */
public class AutomataTool extends AutomataToolTOP {

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
  @Override
  public void run(String[] args) {

    // use normal logging (no DEBUG, TRACE)
    AutomataMill.init();
    Log.ensureInitialization();

    Options options = initOptions();
    try {
      //create CLI Parser and parse input options from commandline
      CommandLineParser cliparser = new org.apache.commons.cli.DefaultParser();
      CommandLine cmd = cliparser.parse(options, args);

      //help: when --help
      if (cmd.hasOption("h")) {
        printHelp(options);
        //do not continue, when help is printed.
        return;
      }
      //version: when --version
      else if (cmd.hasOption("v")) {
        printVersion();
        //do not continue when help is printed
        return;
      }

      Log.info("Automata DSL Tool", "AutomataTool");

      if (cmd.hasOption("i")) {
        String model = cmd.getOptionValue("i");
        final ASTAutomaton ast = parse(model);
        Log.info(model + " parsed successfully!", "AutomataTool");

        AutomataMill.globalScope().setFileExt("aut");
        IAutomataArtifactScope modelTopScope = createSymbolTable(ast);
        // can be used for resolving things in the model
        Optional<StateSymbol> aSymbol = modelTopScope.resolveState(ast.getName() + ".Ping");
        if (aSymbol.isPresent()) {
          Log.info("Resolved state symbol \"Ping\"; FQN = "
              + aSymbol.get().toString(),
            "AutomataTool");
        } else {
          Log.info("This automaton does not contain a state called \"Ping\";",
            "AutomataTool");
        }
        runDefaultCoCos(ast);

        if(cmd.hasOption("s")){
          String storeLocation = cmd.getOptionValue("s");
          storeSymbols(modelTopScope, storeLocation);
        }

        // analyze the model with a visitor
        CountStates cs = new CountStates();
        AutomataTraverser traverser = AutomataMill.traverser();
        traverser.add4Automata(cs);
        ast.accept(traverser);
        Log.info("Automaton has " + cs.getCount() + " states.", "AutomataTool");
        prettyPrint(ast,"");
      }else{
        printHelp(options);
      }
    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xEE752 Could not process AutomataTool parameters: " + e.getMessage());
    }
  }

  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  @Override
  public ASTAutomaton parse(String model) {
    try {
      AutomataParser parser = AutomataMill.parser() ;
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

  @Override
  public void runDefaultCoCos(ASTAutomaton ast){
    // setup context condition infrastructure
    AutomataCoCoChecker checker = new AutomataCoCoChecker();

    // add a custom set of context conditions
    checker.addCoCo(new StateNameStartsWithCapitalLetter());
    checker.addCoCo(new AtLeastOneInitialAndFinalState());
    checker.addCoCo(new TransitionSourceExists());

    // check the CoCos
    checker.checkAll(ast);
  }

  @Override
  public void prettyPrint(ASTAutomaton ast, String file) {
    // execute a pretty printer
    PrettyPrinter pp = new PrettyPrinter();
    AutomataTraverser traverser1 = AutomataMill.traverser();
    traverser1.setAutomataHandler(pp);
    ast.accept(traverser1);
    Log.info("Pretty printing automaton into console:", "AutomataTool");
    // print the result
    Log.println(pp.getResult());
  }

}
