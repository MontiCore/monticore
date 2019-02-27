package de.monticore.codegen.cd2java.factory;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaDecorator;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static org.junit.Assert.*;

public class NodeFactoryWithInheritanceTest {

  private CDTypeFactory cdTypeFacade;

  private CDParameterFactory cdParameterFacade;

  private ASTCDCompilationUnit cdCompilationUnit;

  private ASTCDClass factoryClass;

  private GlobalExtensionManagement glex;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();
    this.cdTypeFacade = CDTypeFactory.getInstance();
    this.cdParameterFacade = CDParameterFactory.getInstance();

    //create grammar from ModelPath
    Path modelPathPath = Paths.get("src/test/resources");
    ModelPath modelPath = new ModelPath(modelPathPath);
    Optional<ASTMCGrammar> grammar = new MontiCoreScript()
        .parseGrammar(Paths.get(new File(
            "src/test/resources/de/monticore/codegen/factory/BGrammar.mc4").getAbsolutePath()));
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

    NodeFactoryDecorator factoryDecorator = new NodeFactoryDecorator(glex);
    this.factoryClass = factoryDecorator.decorate(cdCompilationUnit);
  }

  @Test
  public void testFactoryName() {
    assertEquals("BGrammarNodeFactory", factoryClass.getName());
  }

  @Test
  public void testAttributeName() {
    assertEquals(3, factoryClass.sizeCDAttributes());
    assertEquals("factory", factoryClass.getCDAttribute(0).getName());
    assertEquals("factoryASTBlub", factoryClass.getCDAttribute(1).getName());
    assertEquals("factoryASTBli", factoryClass.getCDAttribute(2).getName());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute astcdAttribute : factoryClass.getCDAttributeList()) {
      assertTrue(astcdAttribute.isPresentModifier());
      assertTrue(PROTECTED_STATIC.build().deepEquals(astcdAttribute.getModifier()));
    }
  }

  @Test
  public void testConstructor() {
    assertEquals(1, factoryClass.sizeCDConstructors());
    ASTCDConstructor astcdConstructor = CD4AnalysisMill.cDConstructorBuilder()
        .setModifier(PROTECTED.build())
        .setName("BGrammarNodeFactory")
        .build();
    assertTrue(astcdConstructor.deepEquals(factoryClass.getCDConstructor(0)));
  }

  @Test
  public void testMethodGetFactory() {
    ASTCDMethod method = factoryClass.getCDMethod(0);
    //test name
    assertEquals("getFactory", method.getName());
    //test modifier
    assertTrue(PRIVATE_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("BGrammarNodeFactory");
    assertTrue(returnType.deepEquals(method.getReturnType()));
  }

  @Test
  public void testMethodCreateDelegateASTC() {
    ASTCDMethod method = factoryClass.getCDMethod(9);
    //test name
    assertEquals("createASTC", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.cgrammar._ast.ASTC");
    assertTrue(returnType.deepEquals(method.getReturnType()));
  }

  @Test
  public void testMethodCreateDelegateASTFoo() {
    ASTCDMethod method = factoryClass.getCDMethod(10);
    //test name
    assertEquals("createASTFoo", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTFoo");
    assertTrue(returnType.deepEquals(method.getReturnType()));
  }

  @Test
  public void testMethodCreateDelegateASTBar() {
    ASTCDMethod method = factoryClass.getCDMethod(12);
    //test name
    assertEquals("createASTBar", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertTrue(method.isEmptyCDParameters());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTBar");
    assertTrue(returnType.deepEquals(method.getReturnType()));
  }

  @Test
  public void testMethodCreateDelegateASTFooWithParam() {
    ASTCDMethod method = factoryClass.getCDMethod(11);
    //test name
    assertEquals("createASTFoo", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertFalse(method.isEmptyCDParameters());
    assertEquals(1,method.sizeCDParameters());
    
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTFoo");
    assertTrue(returnType.deepEquals(method.getReturnType()));
  }

  @Test
  public void testMethodCreateDelegateASTBarWithParam() {
    ASTCDMethod method = factoryClass.getCDMethod(13);
    //test name
    assertEquals("createASTBar", method.getName());
    //test modifier
    assertTrue(PUBLIC_STATIC.build().deepEquals(method.getModifier()));
    //test parameters
    assertFalse(method.isEmptyCDParameters());
    assertEquals(2,method.sizeCDParameters());

    ASTType fooType = cdTypeFacade.createSimpleReferenceType("de.monticore.codegen.factory.agrammar._ast.ASTFoo");
    ASTCDParameter fooParameter = cdParameterFacade.createParameter(fooType, "foo");
    //assertTrue(fooParameter.getType().deepEquals(method.getCDParameter(0).getType())); todo fix ast->cd transformation
    assertEquals("de.monticore.codegen.factory.AGrammar.ASTFoo", TypesPrinter.printType(method.getCDParameter(0).getType()));
    assertEquals(fooParameter.getName(), method.getCDParameter(0).getName());
    //test returnType
    ASTType returnType = cdTypeFacade.createTypeByDefinition("de.monticore.codegen.factory.agrammar._ast.ASTBar");
    assertTrue(returnType.deepEquals(method.getReturnType()));
  }


  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, factoryClass, factoryClass);
    System.out.println(sb.toString());
  }

  @Test
  public void testGeneratedCodeInFile() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(Paths.get("target/generated-test-sources/de/monticore/codegen/factory").toFile());
    Path generatedFiles = Paths.get("BGrammarNodeFactory.java");
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    generatorEngine.generate(CoreTemplates.CLASS, generatedFiles, factoryClass, factoryClass);
  }

}
