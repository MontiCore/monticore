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

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author KH
 */
public class AbstractNTOnlyExtendsOneNTOrClassTest extends CocoTest{

  private final String CODE = "0xA4012";
  private final String MESSAGE = " The abstract nonterminal %s must not %s more than one %s.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4012.A4012";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new AbstractNTOnlyExtendsOneNTOrClass());
  }

  @Test
  public void testExtendMultiple(){
    testInvalidGrammar(grammar+"a", CODE, String.format(MESSAGE,"C", "extend", "nonterminal"), checker);
  }

  @Test
  public void testAstExtendMultiple(){
    testInvalidGrammar(grammar+"b", CODE, String.format(MESSAGE,"A", "astextend", "class"), checker);
  }

  @Test
  public void testExtendNT(){
    testValidGrammar("cocos.valid.ExtendNTs", checker);
  }

}
