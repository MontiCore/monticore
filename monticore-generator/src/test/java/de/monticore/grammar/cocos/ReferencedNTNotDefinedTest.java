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
public class ReferencedNTNotDefinedTest extends CocoTest {

  private final String MESSAGE = " The production A must not reference the " +
          "%snonterminal B because there exists no defining production for B.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A2030.A2030";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new ReferencedNTNotDefined());
  }

  @Test
  public void testInvalidA() {
    testInvalidGrammar(grammar + "a", ReferencedNTNotDefined.ERROR_CODE,
        String.format(MESSAGE, ""), checker);
  }
  
  @Test
  public void testInvalidB() {
    testInvalidGrammar(grammar + "b", ReferencedNTNotDefined.ERROR_CODE,
        String.format(MESSAGE, "interface "), checker);
  }
  
  @Test
  public void testInvalidC() {
    testInvalidGrammar(grammar + "c", ReferencedNTNotDefined.ERROR_CODE,
        String.format(MESSAGE, "interface "), checker);
  }
  
  @Test
  public void testInvalidD() {
    testInvalidGrammar(grammar + "d", ReferencedNTNotDefined.ERROR_CODE,
        String.format(MESSAGE, ""), checker);
  }
  
  @Test
  public void testInvalidE() {
    testInvalidGrammar(grammar + "e", ReferencedNTNotDefined.ERROR_CODE,
        String.format(MESSAGE, "interface "), checker);
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Attributes", checker);
  }

  @Test
  public void testCorrect2(){
    testValidGrammar("cocos.valid.Overriding", checker);
  }

}
