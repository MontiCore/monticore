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
public class InterfaceNTOnlyExtendInterfaceNTsTest extends CocoTest {

  private final String MESSAGE = " The interface nonterminal B must not extend the%s nonterminal A. " +
          "Interface nonterminals may only extend interface nonterminals.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A2116.A2116";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new InterfaceNTOnlyExtendInterfaceNTs());
  }

  @Test
  public void testExtendAbstractNT() {
    testInvalidGrammar(grammar + "a", InterfaceNTOnlyExtendInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, " abstract"), checker);
  }
  
  @Test
  public void testExtendExternalNT() {
    testInvalidGrammar(grammar + "b", InterfaceNTOnlyExtendInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, " external"), checker);
  }
  
  @Test
  public void testExtendNormalNT() {
    testInvalidGrammar(grammar + "c", InterfaceNTOnlyExtendInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, ""), checker);
  }
  
  @Test
  public void testExtendNormalNT2() {
    testInvalidGrammar(grammar + "d", InterfaceNTOnlyExtendInterfaceNTs.ERROR_CODE,
        String.format(MESSAGE, ""), checker);
  }

  @Test
  public void testExtendInterfaceNT(){
    testValidGrammar("cocos.valid.ExtendNTs", checker);
  }
}
