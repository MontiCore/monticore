package de.monticore.codegen.cd2java.ast_new;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaDecorator;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ASTDecoratorWithRefSymbolMandatoryTest {

  private ASTCDCompilationUnit cdCompilationUnit;

  private ASTCDClass astcdClass;


  private GlobalExtensionManagement glex;

  private static final String SYMBOL_TYPE = "de.monticore.codegen.ast.asttest._symboltable.MandSymbol";

  private static final String OPTIONAL_SYMBOL_TYPE = "Optional<de.monticore.codegen.ast.asttest._symboltable.MandSymbol>";

  private static final String DEFINITION_TYPE = "de.monticore.codegen.ast.asttest._ast.ASTMand";

  private static final String OPTIONAL_DEFINITION_TYPE = "Optional<de.monticore.codegen.ast.asttest._ast.ASTMand>";

  private static final String PUBLIC = "public";

  private static final String PRIVATE = "private";


  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/codegen/ast/ASTTest.mc4").getAbsolutePath()));
    assertTrue(grammar.isPresent());

    //create ASTCDDefinition from MontiCoreScript
    MontiCoreScript script = new MontiCoreScript();
    GlobalScope globalScope = TestHelper.createGlobalScope(modelPath);
    script.createSymbolsFromAST(globalScope, grammar.get());
    cdCompilationUnit = script.deriveCD(grammar.get(), new GlobalExtensionManagement(),
        globalScope);

    cdCompilationUnit.setEnclosingScope(globalScope);
    //make types java compatible
    TypeCD2JavaDecorator typeDecorator = new TypeCD2JavaDecorator();
    typeDecorator.decorate(cdCompilationUnit);

    glex.setGlobalValue("astHelper", new DecorationHelper());
    ASTWithSymbolDecorator symbolDecorator = new ASTWithSymbolDecorator(glex, cdCompilationUnit);
    this.astcdClass = symbolDecorator.decorate(cdCompilationUnit.getCDDefinition().getCDClass(3));
  }

  @Test
  public void testClass() {
    assertEquals("ASTRef", cdCompilationUnit.getCDDefinition().getCDClass(3).getName());
  }

  @Test
  public void testAttributes() {
    assertFalse(astcdClass.isEmptyCDAttributes());
    assertEquals(2, astcdClass.sizeCDAttributes());
  }

  @Test
  public void testSymbolAttribute() {
    Optional<ASTCDAttribute> symbolAttribute = astcdClass.getCDAttributeList().stream().filter(m -> "nameSymbol".equals(m.getName())).findFirst();
    assertTrue(symbolAttribute.isPresent());
    assertEquals(PRIVATE, symbolAttribute.get().printModifier().trim());
    assertEquals(OPTIONAL_SYMBOL_TYPE, symbolAttribute.get().printType());
  }

  @Test
  public void testMethods() {
    assertEquals(19, astcdClass.sizeCDMethods());
  }

  @Test
  public void testGetSymbol() {
    Optional<ASTCDMethod> getMethod = astcdClass.getCDMethodList().stream().filter(m -> "getNameSymbol".equals(m.getName())).findFirst();
    assertTrue(getMethod.isPresent());
    assertTrue(getMethod.get().getCDParameterList().isEmpty());
    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
    assertEquals(SYMBOL_TYPE, getMethod.get().printReturnType());
  }

  @Test
  public void testGetOptSymbol() {
    Optional<ASTCDMethod> getMethod = astcdClass.getCDMethodList().stream().filter(m -> "getNameSymbolOpt".equals(m.getName())).findFirst();
    assertTrue(getMethod.isPresent());
    assertTrue(getMethod.get().getCDParameterList().isEmpty());
    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
    assertEquals(OPTIONAL_SYMBOL_TYPE, getMethod.get().printReturnType());
  }

  @Test
  public void testIsPresentSymbol() {
    Optional<ASTCDMethod> getMethod = astcdClass.getCDMethodList().stream().filter(m -> "isPresentNameSymbol".equals(m.getName())).findFirst();
    assertTrue(getMethod.isPresent());
    assertTrue(getMethod.get().getCDParameterList().isEmpty());
    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
    assertEquals("boolean",getMethod.get().printReturnType());
  }



  @Test
  public void testGetDefinition() {
    Optional<ASTCDMethod> getMethod = astcdClass.getCDMethodList().stream().filter(m -> "getNameDefinition".equals(m.getName())).findFirst();
    assertTrue(getMethod.isPresent());
    assertTrue(getMethod.get().getCDParameterList().isEmpty());
    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
    assertEquals(DEFINITION_TYPE, getMethod.get().printReturnType());
  }

  @Test
  public void testGetOptDefinition() {
    Optional<ASTCDMethod> getMethod = astcdClass.getCDMethodList().stream().filter(m -> "getNameDefinitionOpt".equals(m.getName())).findFirst();
    assertTrue(getMethod.isPresent());
    assertTrue(getMethod.get().getCDParameterList().isEmpty());
    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
    assertEquals(OPTIONAL_DEFINITION_TYPE, getMethod.get().printReturnType());
  }

  @Test
  public void testIsPresentDefinition() {
    Optional<ASTCDMethod> getMethod = astcdClass.getCDMethodList().stream().filter(m -> "isPresentNameDefinition".equals(m.getName())).findFirst();
    assertTrue(getMethod.isPresent());
    assertTrue(getMethod.get().getCDParameterList().isEmpty());
    assertEquals(PUBLIC, getMethod.get().printModifier().trim());
    assertEquals("boolean",getMethod.get().printReturnType());
  }

  
  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, astcdClass, astcdClass);
    System.out.println(sb.toString());
  }

  @Test
  public void testGeneratedCodeInFile() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(Paths.get("target/generated-test-sources/de/monticore/codegen/ast").toFile());
    Path generatedFiles = Paths.get("ASTRef.java");
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    generatorEngine.generate(CoreTemplates.CLASS, generatedFiles, astcdClass, astcdClass);
  }
}
