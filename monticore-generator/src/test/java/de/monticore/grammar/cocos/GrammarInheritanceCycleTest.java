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

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;
import parser.MCGrammarParser;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by
 *
 * @author KH
 */
public class GrammarInheritanceCycleTest extends CocoTest{

  private final String CODE = "0xA4023";
  private final String MESSAGE = " The grammar A4023%s introduces an inheritance cycle.";
  private static final Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
  private final String grammar = "cocos.invalid.A4023.A4023";

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
    checker.addCoCo(new GrammarInheritanceCycle());
  }

  @Test
  public void testInvalid(){
    Optional<ASTMCGrammar> grammar = MCGrammarParser
        .parse(Paths.get("src/test/resources/cocos/invalid/A4023/A4023.mc4"));

    Log.getFindings().clear();
    checker.handle(grammar.get());


    assertFalse(Log.getFindings().isEmpty());
    assertEquals(1, Log.getFindings().size());
    for(Finding f : Log.getFindings()){
      assertEquals(String.format(CODE + MESSAGE, ""), f.getMsg());
    }
  }

  @Test
  public void testCorrect(){
    testValidGrammar("cocos.valid.Overriding", checker);
  }

  @Test
  public void testCorrectB(){
    testValidGrammar("cocos.valid.enum.Enum", checker);
  }

}
