package de.monticore.codegen.cd2java.cocos_new;

import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.ast_new.ASTService;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class CoCoCheckerDecoratorTest extends DecoratorTestCase {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();


  private ASTCDClass cocoChecker;

  private CDTypeFactory cdTypeFactory = CDTypeFactory.getInstance();

  private static final String COCOCHECKER = "de.monticore.codegen.ast.automaton._visitor.AutomatonVisitor"; //TODO: wie ist der String?

  @Before
  public void setup() {
    LogStub.init();
    ASTCDCompilationUnit ast = parse("de", "monticore", "codegen", "ast", "Automaton");
    MethodDecorator methodDecorator = new MethodDecorator(glex);
    CoCoCheckerDecorator coCoCheckerDecorator = new CoCoCheckerDecorator(glex, methodDecorator, new CoCoService(ast), new VisitorService(ast), new ASTService(ast));
    this.cocoChecker = coCoCheckerDecorator.decorate(ast);
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, cocoChecker, cocoChecker);
    System.out.println(sb.toString());
  }

  @Ignore
  @Test
  public void classTest(){ //schlaegt fehl, weil der Name noch vollqualifiziert ist
    assertEquals("AutomatonCoCoChecker",cocoChecker.getName());
  }

  @Ignore
  @Test
  public void countAttributesTest(){ // schlaegt fehl, da das Attribut vom Typ ASTAutomatonNode noch fehlt
    List<ASTCDAttribute> astcdAttributes = cocoChecker.getCDAttributeList();
    assertEquals(7, cocoChecker.getCDAttributeList().size());
  }

  @Ignore
  @Test
  public void realThisAttributeTest(){ // schlaegt fehl, da das realThis-Attribut noch protected ist
    ASTCDAttribute attribute = getAttributeBy("realThis",cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Test
  public void automatonCocoCheckerAttributeTest(){
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_automaton__coco_AutomatonCoCoChecker",cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Test
  public void lexicalsCocoCheckerAttributeTest(){
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_lexicals__coco_LexicalsCoCoChecker",cocoChecker);
    assertDeepEquals(PRIVATE,attribute.getModifier());
  }

  @Ignore
  @Test
  public void automatonNodeCocosAttributeTest(){ //schlaegt fehl, da Attribut vom Typ ASTAutomatonNode noch fehlt
    //TODO: attribut fehlt noch, falscher Pfad bisher im Attributnamen
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_automaton__ast_ASTAutomatonNodeCoCos", cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Ignore
  @Test
  public void stateCocosAttributeTest(){ // schlaegt fehl, da das Attribut noch den falschen Namen hat
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_automaton__ast_ASTStateCoCos", cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Ignore
  @Test
  public void transitionCocosAttributeTest(){ // schlaegt fehl, da das Attribut noch den falschen Namen hat
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_automaton__ast_ASTTransitionCoCos", cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Ignore
  @Test
  public void automatonCocosAttributeTest(){ // schlaegt fehl, da das Attribut noch den falschen Namen hat
    ASTCDAttribute attribute = getAttributeBy("de_monticore_codegen_ast_automaton__ast_ASTAutomatonCoCos", cocoChecker);
    assertDeepEquals(PRIVATE, attribute.getModifier());
  }

  @Test
  public void testConstructors(){
    assertFalse(cocoChecker.getCDConstructorList().isEmpty());
    assertEquals(1,cocoChecker.getCDConstructorList().size());
  }

  @Ignore
  @Test
  public void defaultConstructorTest(){ // schlaegt fehl, weil der Konstruktor noch private ist
    ASTCDConstructor defaultConstructor = cocoChecker.getCDConstructor(0);
    assertDeepEquals(PUBLIC, defaultConstructor.getModifier());
    assertTrue(defaultConstructor.isEmptyCDParameters());
  }

  @Ignore
  @Test
  public void testMethods(){ // schlaegt fehl, weil noch die beiden Methoden zu ASTAutomatonNode und die beiden CheckAll Methoden fehlen
    List<ASTCDMethod> methods = cocoChecker.getCDMethodList();
    assertEquals(16,cocoChecker.getCDMethodList().size());
  }

  @Test
  public void setRealThisMethodTest(){
    ASTCDMethod method = getMethodBy("setRealThis",cocoChecker);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(COCOCHECKER);
    assertVoid(method.getReturnType());
    assertFalse(method.isEmptyCDParameters());
    assertEquals(1,method.getCDParameterList().size());
    assertDeepEquals(astType, method.getCDParameter(0).getType());

  }

  @Test
  public void getRealThisMethodTest(){
    ASTCDMethod method = getMethodBy("getRealThis", cocoChecker);
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTType astType = this.cdTypeFactory.createSimpleReferenceType(COCOCHECKER);
    assertDeepEquals(astType, method.getReturnType());
    assertTrue(method.isEmptyCDParameters());
  }




}
