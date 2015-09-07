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
public class NTNotExtendInterfaceOrExternalNTsTest extends CocoTest {

  private final String CODE = "0xA2103";
  private final String MESSAGE = " The nonterminal B must not extend the %s nonterminal A. " +
          "Nonterminals may only extend abstract or normal nonterminals.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A2103.A2103";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new NTNotExtendInterfaceOrExternalNTs());
  }

  @Test
  public void testExtendInterfaceNT(){
    testInvalidGrammar(grammar+"a", CODE, String.format(MESSAGE,"interface"), checker);
  }

  @Test
  public void testExtendExternalNT(){
    testInvalidGrammar(grammar+"b", CODE, String.format(MESSAGE,"external"), checker);
  }

  @Test
  public void testExtendNT(){
    testValidGrammar("cocos.valid.ExtendNTs", checker);
  }
}
