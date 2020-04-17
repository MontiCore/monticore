/* (c) https://github.com/MontiCore/monticore */
import java.io.IOException;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import automata3._parser.Automata3Parser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import invautomata._ast.ASTAutomaton;


/**
 * Main class for the Automata DSL tool.
 *
 */
public class Automata3Tool {
  
  /**
   * Use the single argument for specifying the single input automata file.
   * 
   * @param args
   */
  public static void main(String[] args) throws IOException {

    // use normal logging (no DEBUG, TRACE)
    Log.init();

    // Retrieve the model name
    if (args.length != 1) {
      Log.error("Please specify only one single path to the input model.");
      return;
    }
    Log.info("Automata3 DSL Tool", "Automata3Tool");
    Log.info("------------------", "Automata3Tool");
    String model = args[0];
    
    
    // // Example how to use the pure Expression Visitor
    // // (this works only when Expression is not a component grammar)
    // Log.info("=== 1: ExpressionCheapVisit =============", "Automaton3Tool");
    // ExpressionParser eparse = new ExpressionParser() ;
    // ASTLogicExpr expr = eparse.parse_String(
    //    "true && v1 && !false && v2"
    // ).get();
    // ExpressionCheapVisit ecv1 = new ExpressionCheapVisit();
    // expr.accept(ecv1);

    // parse the model and create the AST representation
    ASTAutomaton ast = parse(model);
    Log.info(model + " parsed successfully!", "Automata3Tool");

    // // Example, how to use a monolithic visitor on the model
    // Log.info("=== 2: Automaton3CheapVisit =============", "Automaton3Tool");
    // Automaton3CheapVisit acv1 = new Automaton3CheapVisit();
    // ast.accept(acv1);


    // // Example, how to use a composed visitor on the model
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

    // Common storage for all pretty printers
    IndentPrinter ppi = new IndentPrinter();

    // The composite visitor
    Automata3PrettyPrinter acpp = new Automata3PrettyPrinter(ppi);
    
    // run the visitor
    ast.accept(acpp);

    Log.info("Pretty printing into console:", "Automata3Tool");
    System.out.println(ppi.getContent());

  }
  
  /**
   * Parse the model contained in the specified file.
   * 
   * @param model - file to parse
   * @return
   */
  public static ASTAutomaton parse(String model) {
    try {
      Automata3Parser parser = new Automata3Parser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model);
      
      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }
  
}
