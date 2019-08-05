/* (c)  https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar_withconcepts._parser;

import com.google.common.io.Files;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.transformation.GrammarTransformer;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class Grammar_WithConceptsParser extends Grammar_WithConceptsParserTOP {

  /**
   * @see MCConcreteParser#parse(String)
   */
  @Override
  public Optional<de.monticore.grammar.grammar._ast.ASTMCGrammar> parse(String fileName) throws IOException {
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    java.util.Optional<ASTMCGrammar> ast = parseMCGrammar(fileName);
    if (ast.isPresent()) {

      // Use pathName instead of filename (because of correct separators)
      String pathName = Paths.get(fileName).toString();
      String simpleFileName = Files.getNameWithoutExtension(pathName);
      String modelName = ast.get().getName();
      String packageName = Names.getPackageFromPath(Names.getPathFromFilename(pathName));
      String packageDeclaration = Names.getQualifiedName(ast.get().getPackageList());
      if (!modelName.equals(simpleFileName)) {
        Log.error("0xA4003 The grammar name " + modelName + " must be identical to the file name "
                + simpleFileName + " of "
                + "the grammar (without its file extension).");
      }

      if(!packageName.endsWith(packageDeclaration)){
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

}

