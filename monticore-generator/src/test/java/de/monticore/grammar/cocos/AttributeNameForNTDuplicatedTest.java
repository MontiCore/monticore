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
public class AttributeNameForNTDuplicatedTest extends CocoTest{

  private final String CODE = "0xA4006";
  private final String MESSAGE = " The production C must not use the attribute name a for different nonterminals.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4006.A4006";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testDuplicatedAttribute(){
    Log.getFindings().clear();
    try {
      testInvalidGrammar(grammar, CODE, MESSAGE, checker);
      fail("NullPointerException expected");
    } catch (NullPointerException e) {

      assertFalse(Log.getFindings().isEmpty());
      assertEquals(1, Log.getFindings().size());
      for (Finding f : Log.getFindings()) {
        assertEquals(CODE + MESSAGE, f.getMsg());
      }
    }


  }


  @Test
  public void testAttributes(){
    testValidGrammar("cocos.valid.Attributes", checker);
  }

}
