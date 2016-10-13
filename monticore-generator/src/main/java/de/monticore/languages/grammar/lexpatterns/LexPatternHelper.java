/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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

package de.monticore.languages.grammar.lexpatterns;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.symboltable.EssentialMCGrammarSymbol;
import de.se_rwth.commons.logging.Log;

public class LexPatternHelper {

  private LexPatternHelper() {
  }

  private static String getLexString(EssentialMCGrammarSymbol grammar, ASTLexProd lexNode) {
    StringBuilder builder = new StringBuilder();
    RegExpBuilder regExp = new RegExpBuilder(builder, grammar);
    regExp.handle(lexNode);
    return builder.toString();
  }

  public static Optional<Pattern> calculateLexPattern(EssentialMCGrammarSymbol grammar, ASTLexProd lexNode) {
    Pattern ret = null;

    final String lexString = getLexString(grammar, lexNode);
    try {
      if ("[[]".equals(lexString)) {
        ret = Pattern.compile("[\\[]");
      } else  {
        ret = Pattern.compile(lexString);
      }
    }
    catch (PatternSyntaxException e) {
      Log.error("0xA0913 Internal error with pattern handling for lex rules. Pattern: " + lexString + "\n", e);
    }
    return Optional.ofNullable(ret);
  }

}
