/* (c) https://github.com/MontiCore/monticore */

package parser;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * The MontiCore grammar parser.
 */
public class MCGrammarParser {

  /**
   * Parses the specified grammar file and returns the corresponding grammar AST
   * instance.
   *
   * @param grammarFile path to the grammar file (.mc4) to parse
   * @return the corresponding grammar AST as optional
   */
  public static Optional<ASTMCGrammar> parse(Path grammarFile) {
    try {
      return GrammarFamilyMill.parser().parse(grammarFile.toString());
    }
    catch (IOException e) {
      Log.error("0XA0115 IOException during parsing of " + grammarFile.toString());
    }
    return Optional.empty();
  }

}
