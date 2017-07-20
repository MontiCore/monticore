/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
          Log.error("0xA4003 The grammar name " + modelName + " must not differ from the file name "
              + simpleFileName + " of "
              + "the grammar (without its file extension).");
        }
        
        if(!packageName.endsWith(Names.getQualifiedName(ast.get().getPackage()))){
          Log.error("0xA4004 The package declaration " + Names.getQualifiedName(ast.get().getPackage()) + " of the grammar must not differ from the "
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
