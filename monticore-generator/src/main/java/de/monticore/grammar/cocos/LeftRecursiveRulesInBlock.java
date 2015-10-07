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

package de.monticore.grammar.cocos;

import de.monticore.grammar.DirectLeftRecursionDetector;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTBlock;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._cocos.GrammarASTClassProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that blocks do not contain left recursive rules
 * If Antlr (Antlr 4.5 throws an exception) can take care of it,  the check is 
 * no longer necessary.
 *
 * @author MB
 */
public class LeftRecursiveRulesInBlock implements GrammarASTClassProdCoCo {
  
  public static final String ERROR_CODE = "0xA4041";
  
  public static final String ERROR_MSG_FORMAT = " The left recursive rule %s is not allowed in blocks, because it doesn't work in Antlr. ";
  
  @Override
  public void check(ASTClassProd a) {
    DirectLeftRecursionDetector detector = new DirectLeftRecursionDetector();
    String ruleName = a.getName();
    for (ASTAlt alt : a.getAlts()) {
      if (alt.getComponents().size() > 0 && alt.getComponents().get(0) instanceof ASTBlock) {
        if (detector.isAlternativeLeftRecursive(alt, ruleName)) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, ruleName),
              a.get_SourcePositionStart());
          return;
        }
      }
    }
  }
  
}
