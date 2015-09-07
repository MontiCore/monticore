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
import de.monticore.types.types._ast.ASTReferenceTypeList;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for the proper transformation of ASTAbstractProds to corresponding
 * ASTCDClasses
 * 
 * @author Sebastian Oberhoff
 */
@Ignore
// TODO: refactor - this test is a mess
public class AstRuleTest {
  
  private ASTCDClass astA;
  
  private ASTCDInterface astB;

  private ASTCDClass astC;
  
  private ASTCDClass astD;
  
  private ASTCDInterface astE;
  
  private ASTCDClass astF;
  
  public AstRuleTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AstRule.mc4")).get();

    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDInterface(cdCompilationUnit, "ASTB").get();
    astC = TestHelper.getCDClass(cdCompilationUnit, "ASTC").get();
    astD = TestHelper.getCDClass(cdCompilationUnit, "ASTD").get();
    astE = TestHelper.getCDInterface(cdCompilationUnit, "ASTE").get();
    astF = TestHelper.getCDClass(cdCompilationUnit, "ASTF").get();
  }
  
  /**
   * Checks that the production "abstract A extends X" results in ASTA having
   * ASTX as a superclass
   */
  @Test
  public void testAstSuperClass() {
    java.util.Optional<ASTReferenceType> superClasses = astA.getSuperclass();
    assertTrue(superClasses.isPresent());
    String name = typeToString(superClasses.get());
    assertEquals("ASTExternalProd", name);
    
    superClasses = astC.getSuperclass();
    assertTrue(superClasses.isPresent());
    name = typeToString(superClasses.get());
    assertEquals("ASTA", name);
    
    superClasses = astD.getSuperclass();
    assertTrue(superClasses.isPresent());
    name = typeToString(superClasses.get());
    assertEquals("mc2cdtransformation.super._ast.ASTSuperProd", name);
    
    superClasses = astF.getSuperclass();
    assertTrue(superClasses.isPresent());
    name = typeToString(superClasses.get());
    assertEquals("java.util.Observable", name);
  }
  
  /**
   * Checks that the production "abstract A extends X" results in ASTA having
   * ASTX as a superclass
   */
  @Test
  public void testStereotypesForAstSuperclass() {
    assertTrue(astA.getModifier().isPresent());
    assertTrue(astA.getModifier().get().getStereotype().isPresent());
    assertEquals(1, astA.getModifier().get().getStereotype().get().getValues().size());
    assertEquals(astA.getModifier().get().getStereotype().get().getValues().get(0).getName(),
        "externalType");
    assertTrue(astA.getModifier().get().getStereotype().get().getValues().get(0).getValue()
        .isPresent());
    assertEquals(astA.getModifier().get().getStereotype().get().getValues().get(0).getValue()
        .get(), "ASTExternalProd");
    
    assertTrue(astF.getModifier().isPresent());
    assertTrue(astF.getModifier().get().getStereotype().isPresent());
    assertEquals(1, astF.getModifier().get().getStereotype().get().getValues().size());
    assertEquals(astF.getModifier().get().getStereotype().get().getValues().get(0).getName(),
        "externalType");
    assertTrue(astF.getModifier().get().getStereotype().get().getValues().get(0).getValue()
        .isPresent());
    assertEquals(astF.getModifier().get().getStereotype().get().getValues().get(0).getValue()
        .get(), "java.util.Observable");
  }
  
  /**
   * Checks that the production "abstract A extends X" results in ASTA having
   * ASTX as a superclass
   */
  @Test
  public void testStereotypesForAstInterfaces() {
    assertTrue(astD.getModifier().isPresent());
    assertTrue(astD.getModifier().get().getStereotype().isPresent());
    assertEquals(1, astD.getModifier().get().getStereotype().get().getValues().size());
    assertEquals(astD.getModifier().get().getStereotype().get().getValues().get(0).getName(),
        "externalType");
    assertTrue(astD.getModifier().get().getStereotype().get().getValues().get(0).getValue()
        .isPresent());
    assertEquals(astD.getModifier().get().getStereotype().get().getValues().get(0).getValue()
        .get(), "java.io.Serializable");
    
    assertTrue(astE.getModifier().isPresent());
    assertTrue(astE.getModifier().get().getStereotype().isPresent());
    assertEquals(2, astE.getModifier().get().getStereotype().get().getValues().size());
    assertEquals(astE.getModifier().get().getStereotype().get().getValues().get(0).getName(),
        "externalType");
    assertTrue(astE.getModifier().get().getStereotype().get().getValues().get(0).getValue()
        .isPresent());
    assertEquals(astE.getModifier().get().getStereotype().get().getValues().get(0).getValue()
        .get(), "ASTExternalInterface");
    assertEquals(astE.getModifier().get().getStereotype().get().getValues().get(1).getName(),
        "externalType");
    assertTrue(astE.getModifier().get().getStereotype().get().getValues().get(1).getValue()
        .isPresent());
    assertEquals(astE.getModifier().get().getStereotype().get().getValues().get(1).getValue()
        .get(), "java.io.Serializable");
  }
  
  /**
   * Checks that the production "abstract B implements Y" results in ASTB having
   * ASTY as a superinterface
   */
  @Test
  public void testAstInterfaces() {
    ASTReferenceTypeList superInterfaces = astD.getInterfaces();
    assertEquals(3, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("ASTB", name);
    name = typeToString(superInterfaces.get(1));
    assertEquals("mc2cdtransformation.super._ast.ASTSuperInterface", name);
    name = typeToString(superInterfaces.get(2));
    assertEquals("java.io.Serializable", name);
    
    superInterfaces = astE.getInterfaces();
    assertEquals(4, superInterfaces.size());
    name = typeToString(superInterfaces.get(0));
    assertEquals("ASTB", name);
    name = typeToString(superInterfaces.get(1));
    assertEquals("mc2cdtransformation.super._ast.ASTSuperInterface", name);
    name = typeToString(superInterfaces.get(2));
    assertEquals("ASTExternalInterface", name);
    name = typeToString(superInterfaces.get(3));
    assertEquals("java.io.Serializable", name);
  }
  
}
