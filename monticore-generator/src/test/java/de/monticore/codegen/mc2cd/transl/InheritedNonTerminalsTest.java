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

package de.monticore.codegen.mc2cd.transl;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.umlcd4a.CD4AnalysisHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClassList;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

public class InheritedNonTerminalsTest {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;
  
  public InheritedNonTerminalsTest() {
    Optional<ASTCDCompilationUnit> cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/InheritedNonTerminalsGrammar.mc4"));
    assertTrue(cdCompilationUnit.isPresent());
    ASTCDClassList classList = cdCompilationUnit.get().getCDDefinition().getCDClasses();
    astA = classList.get(0);
    astB = classList.get(1);
  }
  
  @Test
  public void testName() {
    assertEquals("ASTA", astA.getName());
    assertEquals("ASTB", astB.getName());
  }
  
  @Test
  public void testSuperGrammarResolving() {
    String name = typeToString(astA.getCDAttributes().get(0).getType());
    assertEquals("mc2cdtransformation.Supergrammar.ASTX", name);
  }
}
