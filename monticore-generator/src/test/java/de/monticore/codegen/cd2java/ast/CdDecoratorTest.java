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

package de.monticore.codegen.cd2java.ast;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTVoidType;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.se_rwth.commons.Names;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test for the {@link CdDecorator} class.
 *
 * @author Galina Volkova
 */
public class CdDecoratorTest {
  
  private static Path modelPathPath = Paths.get("src/test/resources");
  
  private static File outputPath = new File("target/generated-test-sources");
  
  private static ModelPath modelPath = new ModelPath(modelPathPath, outputPath.toPath());
  
  private ASTCDDefinition cdDefinition;
  
  private GlobalExtensionManagement glex;
  
  private AstGeneratorHelper astHelper;
  
  private CdDecorator cdDecorator;
  
  private ASTCDCompilationUnit cdComilationUnit;
  
  private static GlobalScope globalScope;
  
  @BeforeClass
  public static void setup() {
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    CD4AnalysisLanguage cd4AnalysisLanguage = new CD4AnalysisLanguage();
    resolvingConfiguration.addTopScopeResolvers(cd4AnalysisLanguage.getResolvingFilters());
    
    globalScope = new GlobalScope(modelPath, cd4AnalysisLanguage, resolvingConfiguration);
  }
  
  @Before
  public void init() {
    String classDiagram = "src/test/resources/de/monticore/Simple.cd";
    try {
      Optional<ASTCDCompilationUnit> topNode = new CD4AnalysisParser().parse(new FileReader(classDiagram));
      assertTrue(topNode.isPresent());
      cdComilationUnit = topNode.get();
      assertNotNull(cdComilationUnit.getCDDefinition());
      cdDefinition = cdComilationUnit.getCDDefinition();
      assertEquals("Simple", cdDefinition.getName());
      assertEquals(2, cdDefinition.getCDClasses().size());
      ASTCDClass classA = cdDefinition.getCDClasses().get(0);
      assertEquals("ASTA", classA.getName());
      glex = new GlobalExtensionManagement();
      cdDecorator = new CdDecorator(glex, null, IterablePath.empty());
      astHelper = new AstGeneratorHelper(topNode.get(), globalScope);
    }
    catch (FileNotFoundException e) {
      fail("Should not reach this, but: " + e);
    }
    catch (IOException e) {
      fail("Should not reach this, but: " + e);
    }
  }
  
  /** {@link CdDecorator#decorateWithBuilders(ASTCDDefinition, GlobalExtensionManagement)} */
  @Test
  public void decorateWithBuilders() {
    assertEquals(2, cdDefinition.getCDClasses().size());
    List<ASTCDClass> nativeClasses = Lists.newArrayList(cdDefinition.getCDClasses());
    
    cdDecorator.addBuilders(cdDefinition, astHelper);
    assertEquals(4, cdDefinition.getCDClasses().size());
    
    for (ASTCDClass clazz : nativeClasses) {
      assertTrue(astHelper.getASTBuilder(clazz).isPresent());
      ASTCDClass builderClass = astHelper.getASTBuilder(clazz).get();
      assertTrue(builderClass.getName().endsWith(AstGeneratorHelper.AST_BUILDER));
      assertTrue(builderClass.getName().startsWith(clazz.getName().substring(AstGeneratorHelper.AST_PREFIX.length())));
    }
  }
  
  /** {@link CdDecorator#addGetter(ASTCDClass, AstGeneratorHelper)} */
  @Test
  public void addGetter() {
    cdDecorator.addBuilders(cdDefinition, astHelper);
    for (ASTCDClass clazz : cdDefinition.getCDClasses()) {
      cdDecorator.addGetter(clazz, astHelper);
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
  
  /** {@link CdDecorator#addSetter(ASTCDClass, AstGeneratorHelper)} */
  @Test
  public void addSetter() {
    for (ASTCDClass clazz : cdDefinition.getCDClasses()) {
      cdDecorator.addSetter(clazz, astHelper);
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
  
  /** {@link CdDecorator#addNodeFactoryClass(ASTCDCompilationUnit, List, AstGeneratorHelper)  */
  @Test
  public void addNodeFactoryClass() {
    assertEquals(2, cdDefinition.getCDClasses().size());
    cdDecorator.addNodeFactoryClass(cdComilationUnit, cdDefinition.getCDClasses(), astHelper);
    assertEquals(3, cdDefinition.getCDClasses().size());
    Optional<ASTCDClass> nodeFactoryClass = cdDefinition.getCDClasses().stream()
        .filter(c -> c.getName().equals("SimpleNodeFactory")).findAny();
    assertTrue(nodeFactoryClass.isPresent());
    assertEquals(8, nodeFactoryClass.get().getCDMethods().size());
    assertEquals(2, nodeFactoryClass.get().getCDAttributes().size());
  }
  
  /** {@link CdDecorator#additionalMethods(ASTCDClass, AstGeneratorHelper)  */
  @Test
  public void additionalMethods() {
    for (ASTCDClass clazz : cdDefinition.getCDClasses()) {
      cdDecorator.addAdditionalMethods(clazz, astHelper);
    }
    
    for (ASTCDClass clazz : cdDefinition.getCDClasses()) {
      assertEquals(10, clazz.getCDMethods().size());
    }
    
    // Check if there are all additional methods defined in the given CD class
    Set<String> additionalMethods = Sets.newLinkedHashSet();
    for (AstAdditionalMethods additionalMethod : AstAdditionalMethods.class.getEnumConstants()) {
      additionalMethods.add(additionalMethod.name());
    }
    List<String> methods = Lists.newArrayList();
    for (ASTCDClass cdClass : cdDefinition.getCDClasses()) {
      // All methods of CD class
      for (ASTCDMethod method : cdClass.getCDMethods()) {
        methods.add(method.getName());
      }
      
      String withOrder = "WithOrder";
      for (String additionalMethod : additionalMethods) {
        if (additionalMethod.endsWith(withOrder)) {
          assertTrue(methods.contains(additionalMethod.substring(0,
              additionalMethod.indexOf(withOrder))));
        }
        else {
          assertTrue(methods.contains(additionalMethod));
        }
      }
    }
  }
  
  /** {@link CdDecorator#addConstructors(ASTCDClass, AstGeneratorHelper)  */
  @Test
  public void addConstructors() {
    for (ASTCDClass clazz : cdDefinition.getCDClasses()) {
      cdDecorator.addConstructors(clazz, astHelper);
    }
    
    for (ASTCDClass clazz : cdDefinition.getCDClasses()) {
      assertEquals(2, clazz.getCDConstructors().size());
    }
  }
  
}
