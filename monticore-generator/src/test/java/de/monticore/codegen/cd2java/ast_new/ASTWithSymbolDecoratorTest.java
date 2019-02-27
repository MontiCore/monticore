package de.monticore.codegen.cd2java.ast_new;

import de.monticore.MontiCoreScript;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.typecd2java.TypeCD2JavaDecorator;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class ASTWithSymbolDecoratorTest {

  private CDTypeFactory cdTypeFacade;

  private CDParameterFactory cdParameterFacade;

  private ASTCDCompilationUnit cdCompilationUnit;

  private ASTCDClass automatonClass;

  private GlobalExtensionManagement glex;

  private final static String PUBLIC = "public";

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
            "src/test/resources/Automaton.mc4").getAbsolutePath()));
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
    ASTWithSymbolDecorator factoryDecorator = new ASTWithSymbolDecorator(glex, cdCompilationUnit);
    this.automatonClass = factoryDecorator.decorate(cdCompilationUnit.getCDDefinition().getCDClass(0));
  }

  @Test
  public void testClass() {
    assertEquals("ASTAutomaton", automatonClass.getName());
  }


  @Test
  public void testClassModifier() {
    assertEquals(PUBLIC, automatonClass.printModifier().trim());
  }

  @Test
  public void testAttributes() {
    assertFalse(automatonClass.isEmptyCDAttributes());
    assertEquals(5, automatonClass.sizeCDAttributes());
  }

  @Test
  public void testAttributeModifier() {
    for (ASTCDAttribute attribute : automatonClass.getCDAttributeList()) {
      assertTrue(PROTECTED.build().deepEquals(attribute.getModifier()));
    }
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute attribute = automatonClass.getCDAttribute(0);
    assertEquals("name", attribute.getName());
    assertTrue(cdTypeFacade.createTypeByDefinition("String").deepEquals(attribute.getType()));
  }

  @Test
  public void testStatesAttribute() {
    ASTCDAttribute attribute = automatonClass.getCDAttribute(1);
    assertEquals("states", attribute.getName());
    assertTrue(cdTypeFacade.createTypeByDefinition("java.util.List<automaton._ast.ASTState>").deepEquals(attribute.getType()));
  }

  @Test
  public void testTransitionsAttribute() {
    ASTCDAttribute attribute = automatonClass.getCDAttribute(2);
    assertEquals("transitions", attribute.getName());
    assertTrue(cdTypeFacade.createTypeByDefinition("java.util.List<automaton._ast.ASTTransition>").deepEquals(attribute.getType()));
  }

  @Test
  public void testConstructors() {
    assertFalse(automatonClass.isEmptyCDConstructors());
    assertEquals(2, automatonClass.sizeCDConstructors());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor defaultConstructor = automatonClass.getCDConstructor(0);
    assertTrue(PROTECTED.build().deepEquals(defaultConstructor.getModifier()));
    assertTrue(defaultConstructor.isEmptyCDParameters());
  }

  @Test
  public void testFullConstructor() {
    ASTCDConstructor fullConstructor = automatonClass.getCDConstructor(1);
    assertTrue(PROTECTED.build().deepEquals(fullConstructor.getModifier()));
    assertFalse(fullConstructor.isEmptyCDParameters());
    assertEquals(3, fullConstructor.sizeCDParameters());

    ASTCDParameter name = this.cdParameterFacade.createParameter(this.cdTypeFacade.createTypeByDefinition("String"), "name");
    ASTCDParameter states = this.cdParameterFacade.createParameter(this.cdTypeFacade.createTypeByDefinition("java.util.List<automaton._ast.ASTState>"), "states");
    ASTCDParameter transitions = this.cdParameterFacade.createParameter(this.cdTypeFacade.createTypeByDefinition("java.util.List<automaton._ast.ASTTransition>"), "transitions");
    assertTrue(name.deepEquals(fullConstructor.getCDParameter(0)));
    assertTrue(states.deepEquals(fullConstructor.getCDParameter(1)));
    assertTrue(transitions.deepEquals(fullConstructor.getCDParameter(2)));
  }

  @Test
  public void testMethods() {
    assertFalse(automatonClass.isEmptyCDMethods());
    assertEquals(93, automatonClass.sizeCDMethods());
  }

  @Test
  public void testAcceptAutomatonVisitor() {
    ASTCDMethod method = automatonClass.getCDMethod(0);
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals("accept", method.getName());

    assertTrue(cdTypeFacade.createVoidType().deepEquals(method.getReturnType()));

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    assertEquals("visitor", method.getCDParameter(0).getName());
    ASTType visitorType = this.cdTypeFacade.createTypeByDefinition("automaton._visitor.AutomatonVisitor");
    assertTrue(visitorType.deepEquals(method.getCDParameter(0).getType()));
  }

  @Test
  public void testAcceptLexicalsVisitor() {
    ASTCDMethod method = automatonClass.getCDMethod(1);
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals("accept", method.getName());

    assertTrue(cdTypeFacade.createVoidType().deepEquals(method.getReturnType()));

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    assertEquals("visitor", method.getCDParameter(0).getName());
    ASTType visitorType = this.cdTypeFacade.createTypeByDefinition("mc.grammars.lexicals.testlexicals._visitor.TestLexicalsVisitor");
    assertTrue(visitorType.deepEquals(method.getCDParameter(0).getType()));
  }

  @Test
  public void testDeepEqualsForceSameOrder() {
    ASTCDMethod method = automatonClass.getCDMethod(2);
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals("deepEquals", method.getName());

    assertTrue(cdTypeFacade.createBooleanType().deepEquals(method.getReturnType()));

    assertFalse(method.isEmptyCDParameters());
    assertEquals(2, method.sizeCDParameters());

    assertEquals("o", method.getCDParameter(0).getName());
    ASTType objectType = this.cdTypeFacade.createTypeByDefinition("Object");
    assertTrue(objectType.deepEquals(method.getCDParameter(0).getType()));

    assertEquals("forceSameOrder", method.getCDParameter(1).getName());
    assertTrue(this.cdTypeFacade.createBooleanType().deepEquals(method.getCDParameter(1).getType()));
  }

  @Test
  public void testDeepEquals() {
    ASTCDMethod method = automatonClass.getCDMethod(3);
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals("deepEquals", method.getName());

    assertTrue(cdTypeFacade.createBooleanType().deepEquals(method.getReturnType()));

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    assertEquals("o", method.getCDParameter(0).getName());
    ASTType objectType = this.cdTypeFacade.createTypeByDefinition("Object");
    assertTrue(objectType.deepEquals(method.getCDParameter(0).getType()));
  }

  @Test
  public void testDeepEqualsWithCommentsForceSameOrder() {
    ASTCDMethod method = automatonClass.getCDMethod(4);
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals("deepEqualsWithComments", method.getName());

    assertTrue(cdTypeFacade.createBooleanType().deepEquals(method.getReturnType()));

    assertFalse(method.isEmptyCDParameters());
    assertEquals(2, method.sizeCDParameters());

    assertEquals("o", method.getCDParameter(0).getName());
    ASTType objectType = this.cdTypeFacade.createTypeByDefinition("Object");
    assertTrue(objectType.deepEquals(method.getCDParameter(0).getType()));

    assertEquals("forceSameOrder", method.getCDParameter(1).getName());
    assertTrue(this.cdTypeFacade.createBooleanType().deepEquals(method.getCDParameter(1).getType()));
  }

  @Test
  public void testDeepEqualsWithComments() {
    ASTCDMethod method = automatonClass.getCDMethod(5);
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals("deepEqualsWithComments", method.getName());

    assertTrue(cdTypeFacade.createBooleanType().deepEquals(method.getReturnType()));

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    assertEquals("o", method.getCDParameter(0).getName());
    ASTType objectType = this.cdTypeFacade.createTypeByDefinition("Object");
    assertTrue(objectType.deepEquals(method.getCDParameter(0).getType()));
  }

  @Test
  public void testEqualAttributes() {
    ASTCDMethod method = automatonClass.getCDMethod(6);
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals("equalAttributes", method.getName());

    assertTrue(cdTypeFacade.createBooleanType().deepEquals(method.getReturnType()));

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    assertEquals("o", method.getCDParameter(0).getName());
    ASTType objectType = this.cdTypeFacade.createTypeByDefinition("Object");
    assertTrue(objectType.deepEquals(method.getCDParameter(0).getType()));
  }

  @Test
  public void testEqualsWithComments() {
    ASTCDMethod method = automatonClass.getCDMethod(7);
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals("equalsWithComments", method.getName());

    assertTrue(cdTypeFacade.createBooleanType().deepEquals(method.getReturnType()));

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    assertEquals("o", method.getCDParameter(0).getName());
    ASTType objectType = this.cdTypeFacade.createTypeByDefinition("Object");
    assertTrue(objectType.deepEquals(method.getCDParameter(0).getType()));
  }

  @Test
  public void testDeepCloneWithResult() {
    ASTCDMethod method = automatonClass.getCDMethod(8);
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals("deepClone", method.getName());

    ASTType astType = this.cdTypeFacade.createTypeByDefinition("ASTAutomaton");
    assertTrue(astType.deepEquals(method.getReturnType()));

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    assertEquals("result", method.getCDParameter(0).getName());
    assertTrue(astType.deepEquals(method.getCDParameter(0).getType()));
  }

  @Test
  public void testDeepClone() {
    ASTCDMethod method = automatonClass.getCDMethod(9);
    assertEquals(PUBLIC, method.printModifier().trim());

    assertEquals("deepClone", method.getName());

    ASTType astType = this.cdTypeFacade.createTypeByDefinition("ASTAutomaton");
    assertTrue(astType.deepEquals(method.getReturnType()));

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testConstruct() {
    ASTCDMethod method = automatonClass.getCDMethod(10);
    assertTrue(PROTECTED.build().deepEquals(method.getModifier()));

    assertEquals("_construct", method.getName());

    ASTType astType = this.cdTypeFacade.createTypeByDefinition("ASTAutomaton");
    assertTrue(astType.deepEquals(method.getReturnType()));

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, automatonClass, automatonClass);
    System.out.println(sb.toString());
  }
}
