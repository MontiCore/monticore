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

package de.monticore.codegen.mc2cd.transl;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import de.monticore.grammar.DirectLeftRecursionDetector;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import parser.MCGrammarParser;

/**
 * Tests the helper class on a concrete grammar containing left recursive and normal rules.
 *
 */
public class DirectLeftRecursionDetectorTest {

  private Optional<ASTMCGrammar> astMCGrammarOptional;
  
  private DirectLeftRecursionDetector directLeftRecursionDetector = new DirectLeftRecursionDetector();


  @Before
  public void setup() {
    final Path modelPath = Paths.get("src/test/resources/mc2cdtransformation/DirectLeftRecursionDetector.mc4");
    astMCGrammarOptional = MCGrammarParser.parse(modelPath);;
    assertTrue(astMCGrammarOptional.isPresent());
  }

  @Test
  public void testRecursiveRule() {
    // firs
    final List<ASTClassProd> productions = astMCGrammarOptional.get().getClassProdList();

    // TODO: add ASTAlt parameter to isAlternativeLeftRecursive-method
    final ASTClassProd exprProduction = productions.get(0);
    /*
    boolean isLeftRecursive = directLeftRecursionDetector.isAlternativeLeftRecursive(exprProduction);
    Assert.assertTrue(isLeftRecursive);

    final ASTClassProd nonRecursiveProudction1 = productions.get(1);
    isLeftRecursive = directLeftRecursionDetector.isAlternativeLeftRecursive(nonRecursiveProudction1);
    Assert.assertFalse(isLeftRecursive);

    final ASTClassProd nonRecursiveProudction2 = productions.get(2);
    isLeftRecursive = directLeftRecursionDetector.isAlternativeLeftRecursive(nonRecursiveProudction2);
    Assert.assertFalse(isLeftRecursive);
    */
  }

}
