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

import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.mc2cd.EssentialTransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OverridingClassProdTest {
  
  private ASTCDClass astX;
  
  public OverridingClassProdTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/OverridingClassProdGrammar.mc4")).get();
    astX = TestHelper.getCDClass(cdCompilationUnit, "ASTX").get();
  }
  
  /**
   * Checks that the production "X" overriding "X" in a supergrammar results in sub.ASTX having
   * super.ASTX as a superclass
   */
  @Test
  public void testOverride() {
    java.util.Optional<ASTReferenceType> superClasses = astX.getSuperclass();
    assertTrue(superClasses.isPresent());
    String name = typeToString(superClasses.get());
    assertEquals("mc2cdtransformation.Supergrammar.ASTX", name);
  }
}
