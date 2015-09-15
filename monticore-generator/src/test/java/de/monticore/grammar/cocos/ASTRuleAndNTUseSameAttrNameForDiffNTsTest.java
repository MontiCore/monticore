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

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by
 *
 * @author KH
 */
public class ASTRuleAndNTUseSameAttrNameForDiffNTsTest extends CocoTest{

  private final String CODE = "0xA4028";
  private final String MESSAGE = " The AST rule for the nonterminal E must not use the same attribute"
      + " name a as the corresponding production with the type B as B is not identical to or a super type of A.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4028.A4028";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new ASTRuleAndNTUseSameAttrNameForDiffNTs());
  }

  @Test
  public void testInvalid(){
    testInvalidGrammar(grammar, CODE, MESSAGE, checker);
  }


  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.ASTRules", checker);
  }

}
