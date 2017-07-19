/*
 * ******************************************************************************
 * MontiCore Language Workbench
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
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

/**
 * Tests that attributes that are redefined in ASTRules correctly override their counterparts in the
 * corresponding ClassProds.
 */
@Ignore
public class AttributeInASTOverridingTest {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;
  
  public AttributeInASTOverridingTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AttributeInASTOverridingGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
  }

  @Test
  public void testAttributeOverridden() {
    List<ASTCDAttribute> attributes = astA.getCDAttributes();
    assertEquals(1, attributes.size());
    assertEquals("mc2cdtransformation.AttributeInASTOverridingGrammar.ASTY",
        TransformationHelper.typeToString(attributes.get(0).getType()));
  }
  
  @Test
  public void testAttributeNotOverridden() {
    List<ASTCDAttribute> attributes = astB.getCDAttributes();
    assertEquals(2, attributes.size());
  }
}
