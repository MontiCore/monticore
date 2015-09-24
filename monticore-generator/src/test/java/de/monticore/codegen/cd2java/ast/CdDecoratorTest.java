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

package de.monticore.codegen.cd2java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTVoidType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParserFactory;
import de.se_rwth.commons.Names;

/**
 * Test for the {@link CdDecorator} class.
 *
 * @author Galina Volkova
 */
public class CdDecoratorTest {
  
  private ASTCDDefinition cdDefinition;
  
  private GlobalExtensionManagement glex;
  
  private AstGeneratorHelper astHelper;
  
  private CdDecorator cdDecorator;
  
  @Before
  public void setup() {
    String classDiagram = "src/test/resources/de/monticore/Simple.cd";
    try {
      Optional<ASTCDCompilationUnit> topNode = CD4AnalysisParserFactory
          .createCDCompilationUnitMCParser().parse(new FileReader(classDiagram));
      assertTrue(topNode.isPresent());
      assertNotNull(topNode.get().getCDDefinition());
      cdDefinition = topNode.get().getCDDefinition();
      assertEquals("Simple", cdDefinition.getName());
      assertEquals(2, cdDefinition.getCDClasses().size());
      ASTCDClass classA = cdDefinition.getCDClasses().get(0);
      assertEquals("ASTA", classA.getName());
      glex = new GlobalExtensionManagement();
      cdDecorator = new CdDecorator(glex, null, IterablePath.empty());
      astHelper = new AstGeneratorHelper(topNode.get(), null);
    }
    catch (FileNotFoundException e) {
      fail("Should not reach this, but: " + e);
    }
    catch (RecognitionException e) {
      fail("Should not reach this, but: " + e);
    }
    catch (IOException e) {
      fail("Should not reach this, but: " + e);
    }
    
  }
  
  /** {@link CdDecorator#decorateWithBuilders(ASTCDDefinition, GlobalExtensionManagement)} */
  @Test
  public void decorateWithBuilders() {
    try {
      assertEquals(2, cdDefinition.getCDClasses().size());
      List<ASTCDClass> cdClasses = Lists.newArrayList(cdDefinition.getCDClasses());
      
      cdDecorator.addBuilders(cdDefinition, astHelper);
      assertEquals(4, cdDefinition.getCDClasses().size());
      
      for (ASTCDClass clazz : cdClasses) {
        assertTrue(astHelper.getASTBuilder(clazz).isPresent());
        ASTCDClass builderClass = astHelper.getASTBuilder(clazz).get();
        assertTrue(builderClass.getName().startsWith("Builder_"));
        assertTrue(builderClass.getName().endsWith(clazz.getName()));
      }
    }
    catch (org.antlr.v4.runtime.RecognitionException e) {
      fail("Should not reach this, but: " + e);
    }
  }
  
  /** {@link CdDecorator#addGetter(ASTCDClass, AstGeneratorHelper)} */
  @Test
  public void addGetter() {
    try {
      cdDecorator.addBuilders(cdDefinition, astHelper);
      for (ASTCDClass classes : cdDefinition.getCDClasses()) {
        cdDecorator.addGetter(classes, astHelper);
      }
      
      for (ASTCDClass clazz : cdDefinition.getCDClasses()) {
        if (clazz.getName().equals("ASTA")) {
          assertEquals(1, clazz.getCDMethods().size());
          ASTCDMethod method = clazz.getCDMethods().get(0);
          assertEquals("getName", method.getName());
          assertTrue(method.getReturnType() instanceof ASTSimpleReferenceType);
          assertEquals("String", ((ASTSimpleReferenceType) method.getReturnType()).getNames()
              .get(0));
        }
        else if (clazz.getName().equals("ASTB")) {
          assertEquals(1, clazz.getCDMethods().size());
          ASTCDMethod method = clazz.getCDMethods().get(0);
          assertEquals("getA", method.getName());
          assertTrue(method.getReturnType() instanceof ASTSimpleReferenceType);
          assertEquals("ASTA", ((ASTSimpleReferenceType) method.getReturnType()).getNames().get(0));
        }
      }
    }
    catch (RecognitionException e) {
      fail("Should not reach this, but: " + e);
    }
    
  }
  
  /** {@link CdDecorator#addSetter(ASTCDClass, AstGeneratorHelper)} */
  @Test
  public void addSetter() {
    try {
      
      for (ASTCDClass classes : cdDefinition.getCDClasses()) {
        cdDecorator.addSetter(classes, astHelper);
      }
      
      for (ASTCDClass clazz : cdDefinition.getCDClasses()) {
        if (clazz.getName().equals("ASTA")) {
          assertEquals(1, clazz.getCDMethods().size());
          ASTCDMethod method = clazz.getCDMethods().get(0);
          assertEquals("setName", method.getName());
          assertTrue(method.getReturnType() instanceof ASTVoidType);
          assertEquals(1, method.getCDParameters().size());
          assertEquals("name", method.getCDParameters().get(0).getName());
          assertEquals(
              "String",
              Names.getQualifiedName(((ASTSimpleReferenceType) method
                  .getCDParameters().get(0).getType()).getNames()));
        }
        else if (clazz.getName().equals("ASTB")) {
          assertEquals(1, clazz.getCDMethods().size());
          ASTCDMethod method = clazz.getCDMethods().get(0);
          assertEquals("setA", method.getName());
          assertTrue(method.getReturnType() instanceof ASTVoidType);
          assertEquals("a", method.getCDParameters().get(0).getName());
          assertEquals(
              "ASTA",
              Names.getQualifiedName(((ASTSimpleReferenceType) method
                  .getCDParameters().get(0).getType()).getNames()));
        }
      }
    }
    catch (RecognitionException e) {
      fail("Should not reach this, but: " + e);
    }
  }
}
