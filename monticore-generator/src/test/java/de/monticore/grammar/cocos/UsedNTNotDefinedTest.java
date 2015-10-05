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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author KH
 */
public class UsedNTNotDefinedTest extends CocoTest {

  private final String MESSAGE =" The production A must not use the nonterminal " +
          "B because there exists no production defining B.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A2031.A2031";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new UsedNTNotDefined());
  }

  @Test
  public void testInvalid() {
    try{
    testInvalidGrammar(grammar, UsedNTNotDefined.ERROR_CODE, MESSAGE, checker);
      fail("expected NullPointerException");
    } catch (NullPointerException e){
      assertFalse(Log.getFindings().isEmpty());
      assertEquals(5, Log.getFindings().size());
      boolean found = false;
      for (Finding f: Log.getFindings()) {
        found |= f.getMsg().equals(UsedNTNotDefined.ERROR_CODE + MESSAGE);
      }
      assertTrue(found);
    }
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
