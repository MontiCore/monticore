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

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

/**
 * Test for the proper transformation of AttributeInASTs to corresponding
 * CDAttributes
 *
 * @author Sebastian Oberhoff
 */
public class AttributeInASTMultiplicityTest {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;
  
  private ASTCDClass astC;
  
  public AttributeInASTMultiplicityTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AttributeInASTMultiplicityGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
    astC = TestHelper.getCDClass(cdCompilationUnit, "ASTC").get();
  }
  
  /**
   * Tests that the ASTRule "ast A = X*;" results in the class ASTA having a
   * field reference to ASTXList.
   */
  @Test
  public void testStarMultiplicity() {
    List<ASTCDAttribute> attributes = astA.getCDAttributeList();
    assertTrue(TestHelper.isListOfType(attributes.get(0).getType(),
        "mc2cdtransformation.AttributeInASTMultiplicityGrammar.ASTX"));
    /*
    String name = typeToString(attributes.get(0).getType());
    assertEquals("java.util.List", name);
    assertTrue(attributes.get(0).getType() instanceof ASTSimpleReferenceType);
    ASTSimpleReferenceType type = (ASTSimpleReferenceType) attributes.get(0).getType();
    assertTrue(type.getTypeArguments().isPresent());
    assertEquals(1, type.getTypeArguments().get().getTypeArguments().size());
    assertEquals("mc2cdtransformation.AttributeInASTMultiplicityGrammar.ASTX",
        ((ASTSimpleReferenceType) type.getTypeArguments().get().getTypeArguments().get(0))
            .getNames().get(0));*/
  }
  
  /**
   * Tests that the ASTRule "ast B = Y min=0 max=1;" results in the class ASTB
   * having a field reference to Optional<ASTY>.
   */
  @Test
  public void testOptionalCardinality() {
    List<ASTCDAttribute> attributes = astB.getCDAttributeList();
    String name = typeToString(attributes.get(0).getType());
    assertEquals("Optional", name);
  }
  
  @Test
  public void testOneCardinality() {
    List<ASTCDAttribute> attributes = astC.getCDAttributeList();
    String name = typeToString(attributes.get(0).getType());
    assertEquals("mc2cdtransformation.AttributeInASTMultiplicityGrammar.ASTZ", name);
  }
}
