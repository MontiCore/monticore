/* (c) https://github.com/MontiCore/monticore */

package parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import com.google.common.io.Files;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.grammar.transformation.GrammarTransformer;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

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
      Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
      java.util.Optional<ASTMCGrammar> ast = parser.parseMCGrammar(grammarFile.toString());
      String simplePathName = grammarFile.toString();
      String packageName = Names.getPackageFromPath(Names.getPathFromFilename(simplePathName));

      if (!parser.hasErrors() && ast.isPresent()) {
        // Checks
        String simpleFileName = Files.getNameWithoutExtension(grammarFile.toString());
        String modelName = ast.get().getName();
        if (!modelName.equals(simpleFileName)) {
          Log.error("0xA4003 The grammar name " + modelName + " must be identical to the file name "
              + simpleFileName + " of "
              + "the grammar (without its file extension).");
        }
        
        if(!packageName.endsWith(Names.getQualifiedName(ast.get().getPackageList()))){
          Log.error("0xA4004 The package declaration " + Names.getQualifiedName(ast.get().getPackageList()) + " of the grammar must not differ from the "
              + "package of the grammar file.");
        }
        
        // Transform
        GrammarTransformer.transform(ast.get());
      }

      Optional<ASTMCGrammar> result = Optional.empty();
      if (ast.isPresent()) {
        result = Optional.of(ast.get());
      }
      return result;
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
