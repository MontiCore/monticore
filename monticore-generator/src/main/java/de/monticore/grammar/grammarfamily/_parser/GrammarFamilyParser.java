/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammarfamily._parser;

import com.google.common.io.Files;
import de.monticore.antlr4.MCConcreteParser;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._parser.GrammarTransformer;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.Optional;

public class GrammarFamilyParser extends GrammarFamilyParserTOP {

  @Override
  public Optional<ASTMCGrammar> parse_String(String str) throws IOException {
    Optional<ASTMCGrammar> grammar = super.parse_String(str);
    if (grammar.isPresent()) {
      GrammarTransformer.transform(grammar.get());
    }
    return grammar;
  }

  @Override
  public Optional<ASTMCGrammar> parse(Reader reader) throws IOException {
    Optional<ASTMCGrammar> grammar = super.parse(reader);
    if (grammar.isPresent()) {
      GrammarTransformer.transform(grammar.get());
    }
    return grammar;
  }

  /**
   * @see MCConcreteParser#parse(String)
   */
  @Override
  public Optional<ASTMCGrammar> parse(String fileName) throws IOException {
    Optional<ASTMCGrammar> ast = parseMCGrammar(fileName);
    if (ast.isPresent()) {

      // Use pathName instead of filename (because of correct separators)
      String pathName = Paths.get(fileName).toString();
      String simpleFileName = Files.getNameWithoutExtension(fileName);
      String modelName = ast.get().getName();
      String packageName = Names.getPathFromFilename(pathName);
      String packageDeclaration = Names.getPathFromPackage(Names.getQualifiedName(ast.get().getPackageList()));
      if (!modelName.equals(simpleFileName)) {
        Log.error("0xA4061 The grammar name " + modelName + " must be identical to the file name "
                + simpleFileName + " of "
                + "the grammar (without its file extension).");
      }

      if(!packageName.endsWith(packageDeclaration)){
        Log.error("0xA4062 The package declaration " + Names.getQualifiedName(ast.get().getPackageList()) + " of the grammar must not differ from the "
                + "package of the grammar file.");
      }

      // Transform
      GrammarTransformer.transform(ast.get());
    }

    return ast;
  }

}

