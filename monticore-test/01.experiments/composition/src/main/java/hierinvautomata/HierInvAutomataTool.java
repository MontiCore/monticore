package hierinvautomata;/* (c) https://github.com/MontiCore/monticore */
import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import de.se_rwth.commons.logging.Log;
import hierinvautomata.HierInvAutomataMill;
import hierinvautomata._parser.HierInvAutomataParser;
import hierinvautomata._visitor.HierInvAutomataTraverser;
import invautomata._ast.ASTAutomaton;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


/**
 * Main class for the Automata DSL tool.
 *
 */
public class HierInvAutomataTool extends HierInvAutomataToolTOP {
  
  /**
   * Use the single argument for specifying the single input automata file.
   * 
   * @param args
   */
  public static void main(String[] args) {
    HierInvAutomataTool tool = new HierInvAutomataTool();
    tool.run(args);
  }

  @Override
  public void run(String[] args){
    // use normal logging (no DEBUG, TRACE)
    HierInvAutomataMill.init();
    Log.ensureInitalization();

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

      Log.info("HierInvAutomata DSL Tool", "HierIAT");
      Log.info("------------------", "HierIAT");

      if (cmd.hasOption("i")) {
        // parse the model and create the AST representation
        String model = cmd.getOptionValue("i");
        ASTAutomaton ast = parse(model);
        Log.info(model + " parsed successfully!", "HierIAT");

        // Example, how to use a monolithic visitor on the model
        Log.info("=== 2: HierInvAutomatonCheapVisit =============", "HierIAT");
        HierInvAutomataTraverser traverser = HierInvAutomataMill.traverser();
        HierInvAutomataCheapVisit acv1 = new HierInvAutomataCheapVisit();
        traverser.add4HierInvAutomata(acv1);
        traverser.add4Automata3(acv1);
        traverser.add4Expression(acv1);
        traverser.add4InvAutomata(acv1);
        ast.accept(traverser);
        // XXXYX // acv1.setVerbosity();
        // Log.info("=== 3: HierInvAutomatonCheapVisit.verbose() ===", "HierIAT");
        // ast.accept(acv1);
      }else{
        printHelp(options);
      }
    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xEE742 Could not process HierInvAutomataTool parameters: " + e.getMessage());
    }
  }
  
}
