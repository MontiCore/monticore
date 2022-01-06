/* (c) https://github.com/MontiCore/monticore */
package automata;

import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.RecognitionException;

import automata._ast.ASTAutomaton;
import automata._parser.AutomataParser;

import java.io.IOException;
import java.util.Optional;


/**
 * Main class for the Automata DSL tool.
 *
 */
public class AutomataExpTool {


  /**
   * Parse the model contained in the specified file.
   *
   * @param model - file to parse
   * @return
   */
  public ASTAutomaton parse(String model) {
    try {
      AutomataParser parser = new AutomataParser() ;
      Optional<ASTAutomaton> optAutomaton = parser.parse(model);

      if (!parser.hasErrors() && optAutomaton.isPresent()) {
        return optAutomaton.get();
      }
      Log.error("0xEE851 Model could not be parsed.");
    }
    catch (RecognitionException | IOException e) {
      Log.error("0xEE650 Failed to parse " + model, e);
    }
    System.exit(1);
    return null;
  }

}
