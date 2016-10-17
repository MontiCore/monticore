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

import static de.monticore.codegen.mc2cd.EssentialTransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

/**
 * Test for the proper transformation of ASTAbstractProds to corresponding
 * ASTCDClasses
 *
 * @author Sebastian Oberhoff
 */
public class AbstractProdTest {

  private ASTCDClass astA;

  private ASTCDClass astB;

  private ASTCDClass astC;

  private ASTCDClass astD;

  private ASTCDClass astE;

  private ASTCDClass astF;

  public AbstractProdTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AbstractProd.mc4")).get();

    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
    astC = TestHelper.getCDClass(cdCompilationUnit, "ASTC").get();
    astD = TestHelper.getCDClass(cdCompilationUnit, "ASTD").get();
    astE = TestHelper.getCDClass(cdCompilationUnit, "ASTE").get();
    astF = TestHelper.getCDClass(cdCompilationUnit, "ASTF").get();
  }

  @Test
  public void testAbstract() {
    assertTrue(astA.getModifier().isPresent());
    assertTrue(astA.getModifier().get().isAbstract());
    assertTrue(astB.getModifier().isPresent());
    assertTrue(astB.getModifier().get().isAbstract());
    assertTrue(astC.getModifier().isPresent());
    assertTrue(astC.getModifier().get().isAbstract());
    assertTrue(astD.getModifier().isPresent());
    assertTrue(astD.getModifier().get().isAbstract());
  }

  /**
   * Checks that the production "abstract A extends X" results in ASTA having
   * ASTX as a superclass
   */
  @Test
  public void testExtends() {
    java.util.Optional<ASTReferenceType> superClasses = astA.getSuperclass();
    assertTrue(superClasses.isPresent());
    String name = typeToString(superClasses.get());
    assertEquals("mc2cdtransformation.AbstractProd.ASTextendedProd", name);
  }

  /**
   * Checks that the production "abstract B implements Y" results in ASTB having
   * ASTY as a superinterface
   */
  @Test
  public void testImplements() {
    List<ASTReferenceType> superInterfaces = astB.getInterfaces();
    assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("mc2cdtransformation.AbstractProd.ASTimplementedProd", name);
  }

  /**
   * Checks that the production "abstract C astextends X" results in ASTC having
   * X as a superclass
   */
  @Test
  public void testAstextends() {
    java.util.Optional<ASTReferenceType> superClasses = astC.getSuperclass();
    assertTrue(superClasses.isPresent());
    String name = typeToString(superClasses.get());
    assertEquals("AstExtendedType", name);
  }

  /**
   * Checks that the production "abstract D astimplements Y" results in ASTD
   * having Y as a superinterface
   */
  @Test
  public void testAstimplements() {
    List<ASTReferenceType> superInterfaces = astD.getInterfaces();
    assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("AstImplementedType", name);
  }

  /**
   * Checks that the production "abstract C astextends x.y.Z" results in ASTC
   * having x.y.Z as a superclass
   */
  @Test
  public void testAstextendsQualified() {
    java.util.Optional<ASTReferenceType> superClasses = astE.getSuperclass();
    assertTrue(superClasses.isPresent());
    String name = typeToString(superClasses.get());
    assertEquals("java.util.Observable", name);
  }

  /**
   * Checks that the production "abstract D astimplements x.y.Z" results in ASTD
   * having x.y.Z as a superinterface
   */
  @Test
  public void testAstimplementsQualified() {
    List<ASTReferenceType> superInterfaces = astF.getInterfaces();
    assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("java.io.Serializable", name);
  }
}
