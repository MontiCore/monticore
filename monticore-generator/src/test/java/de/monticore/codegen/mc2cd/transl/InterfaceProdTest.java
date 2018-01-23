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

import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;

/**
 * Test for the proper transformation of ASTInterfaceProds to corresponding ASTCDInterfaces
 * 
 * @author Sebastian Oberhoff
 */
public class InterfaceProdTest {
  
  private ASTCDInterface astA;
  
  private ASTCDInterface astB;
  
  private ASTCDInterface astC;
  
  public InterfaceProdTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/InterfaceProd.mc4")).get();
    astA = TestHelper.getCDInterface(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDInterface(cdCompilationUnit, "ASTB").get();
    astC = TestHelper.getCDInterface(cdCompilationUnit, "ASTC").get();
  }
  
  /**
   * Checks that the production "interface A extends X" results in ASTA having ASTX as a
   * superinterface
   */
  @Test
  public void testExtends() {
    List<ASTReferenceType> superInterfaces = astA.getInterfaceList();
    assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("mc2cdtransformation.InterfaceProd.ASTextendedProd", name);
  }
  
  /**
   * Checks that the production "interface A astextends X" results in ASTA having X as a
   * superinterface
   */
  @Test
  public void testAstextends() {
    List<ASTReferenceType> superInterfaces = astB.getInterfaceList();
    assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("AstExtendedType", name);
  }
  
  /**
   * Checks that the production "abstract D astimplements x.y.Z" results in ASTD having x.y.Z as a
   * superinterface
   */
  @Test
  public void testAstimplementsQualified() {
    List<ASTReferenceType> superInterfaces = astC.getInterfaceList();
    assertEquals(1, superInterfaces.size());
    String name = typeToString(superInterfaces.get(0));
    assertEquals("java.io.Serializable", name);
  }
  
}
