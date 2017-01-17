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

package de.monticore.codegen.parser.antlr;

import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;

/**
 * Adds source code positions building up code to the parsers
 * 
 */
public class SourcePositionActions {  
  
  /**
   * Create a mc.ast.SourcePosition at the beginning of a rule
   */
  public String startPosition(ASTClassProd a) {
    return "_aNode.set_SourcePositionStart( computeStartPosition(_input.LT(1)));\n";
  }
  
  public String endPosition(ASTClassProd a) {
    // Fetch last token to determine position
    return "_aNode.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));";
  }
  
  public String startPositionForLeftRecursiveRule(ASTNonTerminal a) {
    return "_aNode.set_SourcePositionStart(_localctx." 
        + ParserGeneratorHelper.getTmpVarNameForAntlrCode(a)
        + ".ret.get_SourcePositionStart());\n";
  }
  
  public String endPositionForLeftRecursiveRule(ASTNonTerminal a) {
    return "_localctx." 
        + ParserGeneratorHelper.getTmpVarNameForAntlrCode(a) 
        + ".ret.set_SourcePositionEnd( computeStartPosition(_input.LT(1)));\n";
  }
}
