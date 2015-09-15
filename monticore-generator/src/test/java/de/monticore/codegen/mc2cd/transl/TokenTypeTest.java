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
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * @author Sebastian Oberhoff
 */
public class TokenTypeTest {

  private final ASTCDClass astTest;

  public TokenTypeTest() {
    Optional<ASTCDCompilationUnit> cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/LexerFormat.mc4"));
    astTest = TestHelper.getCDClass(cdCompilationUnit.get(), "ASTTest").get();
  }

  private Optional<ASTCDAttribute> getCDAttributeByName(String name) {
    return astTest.getCDAttributes().stream()
        .filter(cdAttribute -> name.equals(cdAttribute.getName()))
        .findAny();
  }

  @Test
  public void testNumber() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("a").get();
    assertTrue(TransformationHelper.prettyPrint(cdAttribute).contains("int"));
  }

  @Test
  public void testBoolean() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("b").get();
    assertTrue(TransformationHelper.prettyPrint(cdAttribute).contains("boolean"));
  }

  @Test
  public void testChar() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("c").get();
    assertTrue(TransformationHelper.prettyPrint(cdAttribute).contains("char"));
  }

  @Test
  public void testInt() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("d").get();
    assertTrue(TransformationHelper.prettyPrint(cdAttribute).contains("int"));
  }

  @Test
  public void testFloat() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("e").get();
    assertTrue(TransformationHelper.prettyPrint(cdAttribute).contains("float"));
  }

  @Test
  public void testDouble() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("f").get();
    assertTrue(TransformationHelper.prettyPrint(cdAttribute).contains("double"));
  }

  @Test
  public void testLong() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("g").get();
    assertTrue(TransformationHelper.prettyPrint(cdAttribute).contains("long"));
  }

  @Test
  public void testCard() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("h").get();
    assertTrue(TransformationHelper.prettyPrint(cdAttribute).contains("int"));
  }

  @Test
  public void testShort() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("i").get();
    assertTrue(TransformationHelper.prettyPrint(cdAttribute).contains("short"));
  }

  @Test
  public void testByte() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("j").get();
    assertTrue(TransformationHelper.prettyPrint(cdAttribute).contains("byte"));
  }

  @Test
  public void testByte2() {
    ASTCDAttribute cdAttribute = getCDAttributeByName("k").get();
    assertTrue(TransformationHelper.prettyPrint(cdAttribute).contains("byte"));
  }
}
