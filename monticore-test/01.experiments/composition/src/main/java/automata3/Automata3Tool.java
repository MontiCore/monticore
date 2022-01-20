package automata3;/* (c) https://github.com/MontiCore/monticore */

import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import invautomata._ast.ASTAutomaton;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


/**
 * Main class for the Automata DSL tool.
 *
 */
public class Automata3Tool extends Automata3ToolTOP {
  
  /**
   * Use the single argument for specifying the single input automata file.
   * 
   * @param args
   */
  public static void main(String[] args) {
    Automata3Tool tool = new Automata3Tool();
    tool.run(args);
  }

  @Override
  public void run(String[] args){
    // use normal logging (no DEBUG, TRACE)
    Automata3Mill.init();
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

      Log.info("Automata3 DSL Tool", "Automata3Tool");
      Log.info("------------------", "Automata3Tool");

      if (cmd.hasOption("i")) {
        // XXYX // Example how to use the pure Expression Visitor
        // // (this works only when Expression is not a component grammar)
        // Log.info("=== 1: ExpressionCheapVisit =============", "Automaton3Tool");
        // ExpressionParser eparse = new ExpressionParser() ;
        // ASTLogicExpr expr = eparse.parse_String(
        //    "true && v1 && !false && v2"
        // ).get();
        // ExpressionCheapVisit ecv1 = new ExpressionCheapVisit();
        // expr.accept(ecv1);
        String model = cmd.getOptionValue("i");
        final ASTAutomaton ast = parse(model);
        Log.info(model + " parsed successfully!", "Automata3Tool");

        // XXYX // Example, how to use a monolithic visitor on the model
        // Log.info("=== 2: Automaton3CheapVisit =============", "Automaton3Tool");
        // Automaton3CheapVisit acv1 = new Automaton3CheapVisit();
        // ast.accept(acv1);


        // XXYX // Example, how to use a composed visitor on the model
        // Log.info("=== 3: Automaton3ComposedVisit =============", "Automaton3Tool");
        // Automaton3ComposedVisit acompov = new Automaton3ComposedVisit();
        // acompov.set_invautomaton__visitor_InvAutomatonVisitor(
        //                  new InvAutomatonCheapVisit());
        // acompov.set_expression__visitor_ExpressionVisitor(
        //                  new ExpressionCheapVisit());
        // // necessary to complete the composition, but only used for
        // // the newly define nonterminal Invariant
        // acompov.set_automaton3__visitor_Automaton3Visitor(
        //                  new Automaton3CheapVisit());
        // ast.accept(acompov);

        // Example, how to use a composed visitor on the model
        Log.info("=== 4: Automata3PP ===============", "Automata3Tool");

        prettyPrint(ast, "");
      }else{
        printHelp(options);
      }
    } catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xEE741 Could not process Automata3Tool parameters: " + e.getMessage());
    }
  }

  @Override
  public void prettyPrint(ASTAutomaton ast, String file){
    // The composite visitor
    IndentPrinter ppi = new IndentPrinter();
    Automata3PrettyPrinter acpp = new Automata3PrettyPrinter(ppi);

    // run the visitor
    acpp.print(ast);

    Log.info("Pretty printing into console:", "Automata3Tool");
    Log.println(ppi.getContent());
  }
  
}
