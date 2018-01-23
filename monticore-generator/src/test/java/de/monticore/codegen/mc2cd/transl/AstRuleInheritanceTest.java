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

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.junit.Test;

import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

/**
 * @author Sebastian Oberhoff
 */
public final class AstRuleInheritanceTest {

  private final ASTCDClass astC;

  private final ASTCDClass impl;

  public AstRuleInheritanceTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AstRuleInheritance.mc4")).get();
    astC = TestHelper.getCDClass(cdCompilationUnit, "ASTC").get();
    impl = TestHelper.getCDClass(cdCompilationUnit, "ASTImpl").get();
  }

  @Test
  public void testAstRuleInheritance() {
    assertEquals("dimensions", astC.getCDAttributeList().get(0).getName());
  }

  @Test
  public void testAstRuleDoubleInheritance() {
    assertEquals(2, impl.getCDAttributeList().size());
  }
  
}
