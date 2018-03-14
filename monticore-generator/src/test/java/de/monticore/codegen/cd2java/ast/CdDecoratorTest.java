/* (c) https://github.com/MontiCore/monticore */

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
  
  private ASTCDDefinition cdDefinitionBuilder;
  
  private GlobalExtensionManagement glex;
  
  private AstGeneratorHelper astHelper;
  
  private AstGeneratorHelper astHelperBuilder;
  
  private CdDecorator cdDecorator;
  
  private ASTCDCompilationUnit cdComilationUnit;
  
  private static GlobalScope globalScope;
  
  @BeforeClass
  public static void setup() {
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    CD4AnalysisLanguage cd4AnalysisLanguage = new CD4AnalysisLanguage();
    resolvingConfiguration.addDefaultFilters(cd4AnalysisLanguage.getResolvingFilters());
    
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
      assertEquals(2, cdDefinition.getCDClassList().size());
      ASTCDClass classA = cdDefinition.getCDClassList().get(0);
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
  
  @Before
  public void initBuilderCD() {
    String classDiagram = "src/test/resources/de/monticore/Builder.cd";
    try {
      Optional<ASTCDCompilationUnit> topNode = new CD4AnalysisParser().parse(new FileReader(classDiagram));
      assertTrue(topNode.isPresent());
      ASTCDCompilationUnit compUnit = topNode.get();
      assertNotNull(compUnit.getCDDefinition());
      cdDefinitionBuilder = compUnit.getCDDefinition();
      astHelperBuilder = new AstGeneratorHelper(compUnit, globalScope);
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
    assertEquals(2, cdDefinition.getCDClassList().size());
    List<ASTCDClass> nativeClasses = Lists.newArrayList(cdDefinition.getCDClassList());
    
    cdDecorator.addBuilders(cdDefinition, astHelper);
    assertEquals(4, cdDefinition.getCDClassList().size());
    
    for (ASTCDClass clazz : nativeClasses) {
      assertTrue(astHelper.getASTBuilder(clazz).isPresent());
      ASTCDClass builderClass = astHelper.getASTBuilder(clazz).get();
      assertTrue(builderClass.getName().endsWith(AstGeneratorHelper.AST_BUILDER));
      assertTrue(builderClass.getName().startsWith(clazz.getName()));
    }
  }
  
  @Test
  public void decorateBuilderMethods() {
    assertEquals(6, cdDefinitionBuilder.getCDClassList().size());
    List<ASTCDClass> nativeClasses = Lists.newArrayList(cdDefinitionBuilder.getCDClassList());
    
    cdDecorator.addBuilders(cdDefinitionBuilder, astHelperBuilder);
    assertEquals(12, cdDefinitionBuilder.getCDClassList().size());
    
    for(ASTCDClass clazz : nativeClasses) {
      ASTCDClass builder = astHelperBuilder.getASTBuilder(clazz).get();
      cdDecorator.decorateBuilderClass(builder, astHelperBuilder, cdDefinitionBuilder);
    }
    
    // builder class with list attribute -> 34 additional list methods
    ASTCDClass astA = cdDefinitionBuilder.getCDClass(6);
    assertEquals(34, astA.getCDMethodList().size());
    
    // builder class with boolean attribute -> 2 additional methods
    ASTCDClass astB = cdDefinitionBuilder.getCDClass(7);
    assertEquals(2, astB.getCDMethodList().size());
    assertEquals("isBool", astB.getCDMethod(0).getName());
    assertEquals("setBool", astB.getCDMethod(1).getName());
    
    // builder class with optional attribute -> 6 additional methods
    ASTCDClass astC = cdDefinitionBuilder.getCDClass(8);
    assertEquals(6, astC.getCDMethodList().size());
    
    // builder class with normal attribute -> 2 additional methods
    ASTCDClass astD = cdDefinitionBuilder.getCDClass(9);
    assertEquals(2, astD.getCDMethodList().size());
    assertEquals("getA", astD.getCDMethod(0).getName());
    assertEquals("setA", astD.getCDMethod(1).getName());
    
    // builder class with explicit superclass -> 45 additional methods (overridden from ASTNodeBuilder)
    ASTCDClass astE = cdDefinitionBuilder.getCDClass(10);
    assertEquals(45, astE.getCDMethodList().size());
    
    
  }
  
  /** {@link CdDecorator#addGetter(ASTCDClass, AstGeneratorHelper)} */
  @Test
  public void addGetter() {
    cdDecorator.addBuilders(cdDefinition, astHelper);
    for (ASTCDClass clazz : cdDefinition.getCDClassList()) {
      cdDecorator.addGetter(clazz, astHelper);
    }
    
    for (ASTCDClass clazz : cdDefinition.getCDClassList()) {
      if (clazz.getName().equals("ASTA")) {
        assertEquals(1, clazz.getCDMethodList().size());
        ASTCDMethod method = clazz.getCDMethodList().get(0);
        assertEquals("getName", method.getName());
        assertTrue(method.getReturnType() instanceof ASTSimpleReferenceType);
        assertEquals("String", ((ASTSimpleReferenceType) method.getReturnType()).getNameList()
            .get(0));
      }
      else if (clazz.getName().equals("ASTB")) {
        assertEquals(1, clazz.getCDMethodList().size());
        ASTCDMethod method = clazz.getCDMethodList().get(0);
        assertEquals("getA", method.getName());
        assertTrue(method.getReturnType() instanceof ASTSimpleReferenceType);
        assertEquals("ASTA", ((ASTSimpleReferenceType) method.getReturnType()).getNameList().get(0));
      }
    }
  }
  
  /** {@link CdDecorator#addSetter(ASTCDClass, AstGeneratorHelper)} */
  @Test
  public void addSetter() {
    for (ASTCDClass clazz : cdDefinition.getCDClassList()) {
      cdDecorator.addSetter(clazz, astHelper, cdDefinition);
    }
    
    for (ASTCDClass clazz : cdDefinition.getCDClassList()) {
      if (clazz.getName().equals("ASTA")) {
        assertEquals(1, clazz.getCDMethodList().size());
        ASTCDMethod method = clazz.getCDMethodList().get(0);
        assertEquals("setName", method.getName());
        assertTrue(method.getReturnType() instanceof ASTVoidType);
        assertEquals(1, method.getCDParameterList().size());
        assertEquals("name", method.getCDParameterList().get(0).getName());
        assertEquals(
            "String",
            Names.getQualifiedName(((ASTSimpleReferenceType) method
                .getCDParameterList().get(0).getType()).getNameList()));
      }
      else if (clazz.getName().equals("ASTB")) {
        assertEquals(1, clazz.getCDMethodList().size());
        ASTCDMethod method = clazz.getCDMethodList().get(0);
        assertEquals("setA", method.getName());
        assertTrue(method.getReturnType() instanceof ASTVoidType);
        assertEquals("a", method.getCDParameterList().get(0).getName());
        assertEquals(
            "ASTA",
            Names.getQualifiedName(((ASTSimpleReferenceType) method
                .getCDParameterList().get(0).getType()).getNameList()));
      }
    }
  }
  
  /** {@link CdDecorator#addNodeFactoryClass(ASTCDCompilationUnit, List, AstGeneratorHelper)  */
  @Test
  public void addNodeFactoryClass() {
    assertEquals(2, cdDefinition.getCDClassList().size());
    cdDecorator.addNodeFactoryClass(cdComilationUnit, cdDefinition.getCDClassList(), astHelper);
    assertEquals(3, cdDefinition.getCDClassList().size());
    Optional<ASTCDClass> nodeFactoryClass = cdDefinition.getCDClassList().stream()
        .filter(c -> c.getName().equals("SimpleNodeFactory")).findAny();
    assertTrue(nodeFactoryClass.isPresent());
    assertEquals(8, nodeFactoryClass.get().getCDMethodList().size());
    assertEquals(2, nodeFactoryClass.get().getCDAttributeList().size());
  }
  
  /** {@link CdDecorator#additionalMethods(ASTCDClass, AstGeneratorHelper)  */
  @Test
  public void additionalMethods() {
    for (ASTCDClass clazz : cdDefinition.getCDClassList()) {
      cdDecorator.addAdditionalMethods(clazz, astHelper);
    }
    
    for (ASTCDClass clazz : cdDefinition.getCDClassList()) {
      assertEquals(11, clazz.getCDMethodList().size());
    }
    
    // Check if there are all additional methods defined in the given CD class
    Set<String> additionalMethods = Sets.newLinkedHashSet();
    for (AstAdditionalMethods additionalMethod : AstAdditionalMethods.class.getEnumConstants()) {
      additionalMethods.add(additionalMethod.name());
    }
    List<String> methods = Lists.newArrayList();
    for (ASTCDClass cdClass : cdDefinition.getCDClassList()) {
      // All methods of CD class
      for (ASTCDMethod method : cdClass.getCDMethodList()) {
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
    for (ASTCDClass clazz : cdDefinition.getCDClassList()) {
      cdDecorator.addConstructors(clazz, astHelper);
    }
    
    for (ASTCDClass clazz : cdDefinition.getCDClassList()) {
      assertEquals(2, clazz.getCDConstructorList().size());
    }
  }
  
}
