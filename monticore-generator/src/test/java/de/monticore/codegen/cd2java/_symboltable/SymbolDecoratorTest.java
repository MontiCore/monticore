package de.monticore.codegen.cd2java._symboltable;

import com.github.javaparser.StaticJavaParser;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.prettyprint.CD4CodePrinter;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static org.junit.Assert.*;

public class SymbolDecoratorTest extends DecoratorTestCase {

  private ASTCDClass symbolClass;

  private GlobalExtensionManagement glex;

  private CDTypeFacade cdTypeFacade;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  private ASTCDCompilationUnit originalCompilationUnit;

  private static final String ENCLOSING_SCOPE_TYPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  private static final String A_NODE_TYPE_OPT = "Optional<de.monticore.codegen.ast.automaton._ast.ASTAutomaton>";

  private static final String A_NODE_TYPE = "de.monticore.codegen.ast.automaton._ast.ASTAutomaton";

  private static final String ACCESS_MODIFIER_TYPE = "de.monticore.symboltable.modifiers.AccessModifier";

  private static final String I_AUTOMATON_SCOPE = "de.monticore.codegen.ast.automaton._symboltable.IAutomatonScope";

  private static final String AUTOMATON_VISITOR = "de.monticore.codegen.ast.automaton._visitor.AutomatonSymbolVisitor";

  @Before
  public void setUp() {
    Log.init();
    this.cdTypeFacade = CDTypeFacade.getInstance();
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", new DecorationHelper());
    this.glex.setGlobalValue("cdPrinter", new CD4CodePrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));


    SymbolDecorator decorator = new SymbolDecorator(this.glex, new SymbolTableService(decoratedCompilationUnit), new VisitorService(decoratedCompilationUnit),
        new MethodDecorator(glex));
    ASTCDClass clazz = getClassBy("ASTAutomaton", decoratedCompilationUnit);

    this.symbolClass = decorator.decorate(clazz);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  }

  @Test
  public void testClassName() {
    assertEquals("AutomatonSymbol", symbolClass.getName());
  }

  @Test
  public void testSuperInterfacesCount() {
    assertEquals(2, symbolClass.sizeInterfaces());
  }

  @Test
  public void testSuperInterfaces() {
    assertDeepEquals("de.monticore.codegen.ast.automaton._symboltable.ICommonAutomatonSymbol", symbolClass.getInterface(0));
    assertDeepEquals("de.monticore.symboltable.IScopeSpanningSymbol", symbolClass.getInterface(1));
  }

  @Test
  public void testNoSuperClass() {
    assertFalse(symbolClass.isPresentSuperclass());
  }

  @Test
  public void testConstructorCount() {
    assertEquals(1, symbolClass.sizeCDConstructors());
  }

  @Test
  public void testDefaultConstructor() {
    ASTCDConstructor cdConstructor = symbolClass.getCDConstructor(0);
    assertDeepEquals(PUBLIC, cdConstructor.getModifier());
    assertEquals("AutomatonSymbol", cdConstructor.getName());

    assertEquals(1, cdConstructor.sizeCDParameters());
    assertDeepEquals(String.class, cdConstructor.getCDParameter(0).getMCType());
    assertEquals("name", cdConstructor.getCDParameter(0).getName());

    assertTrue(cdConstructor.isEmptyExceptions());
  }

  @Test
  public void testAttributeCount() {
    assertEquals(7, symbolClass.sizeCDAttributes());
  }

  @Test
  public void testNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("name", symbolClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testFullNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("fullName", symbolClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testEnclosingScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("enclosingScope", symbolClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(cdTypeFacade.createQualifiedType(ENCLOSING_SCOPE_TYPE),
        astcdAttribute.getMCType());
  }

  @Test
  public void testASTNodeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("aSTNode", symbolClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(cdTypeFacade.createQualifiedType(A_NODE_TYPE_OPT), astcdAttribute.getMCType());
  }

  @Test
  public void testPackageNameAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("packageName", symbolClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(String.class, astcdAttribute.getMCType());
  }

  @Test
  public void testAccessModifierAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("accessModifier", symbolClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(cdTypeFacade.createQualifiedType(ACCESS_MODIFIER_TYPE), astcdAttribute.getMCType());
  }

  @Test
  public void testSpannedScopeAttribute() {
    ASTCDAttribute astcdAttribute = getAttributeBy("spannedScope", symbolClass);
    assertDeepEquals(PROTECTED, astcdAttribute.getModifier());
    assertDeepEquals(cdTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE), astcdAttribute.getMCType());
  }

  @Test
  public void testMethods() {
    assertEquals(20, symbolClass.getCDMethodList().size());
  }

  @Test
  public void testAcceptMethod() {
    ASTCDMethod method = getMethodBy("accept", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createQualifiedType(AUTOMATON_VISITOR),
        method.getCDParameter(0).getMCType());
    assertEquals("visitor", method.getCDParameter(0).getName());
  }

  @Test
  public void testDetermineFullNameMethod() {
    ASTCDMethod method = getMethodBy("determineFullName", symbolClass);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testDeterminePackageNameMethod() {
    ASTCDMethod method = getMethodBy("determinePackageName", symbolClass);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetNameMethod() {
    ASTCDMethod method = getMethodBy("getName", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetFullNameMethod() {
    ASTCDMethod method = getMethodBy("getFullName", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("getPackageName", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetEnclosingScopeNameMethod() {
    ASTCDMethod method = getMethodBy("getEnclosingScope", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(cdTypeFacade.createQualifiedType(ENCLOSING_SCOPE_TYPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTNodeMethod() {
    ASTCDMethod method = getMethodBy("getASTNode", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(cdTypeFacade.createQualifiedType(A_NODE_TYPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testGetASTNodeOptMethod() {
    ASTCDMethod method = getMethodBy("getASTNodeOpt", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(cdTypeFacade.createQualifiedType(A_NODE_TYPE_OPT)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testisPresentASTNodeMethod() {
    ASTCDMethod method = getMethodBy("isPresentASTNode", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertBoolean(method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }


  @Test
  public void testGetAccessModifierNameMethod() {
    ASTCDMethod method = getMethodBy("getAccessModifier", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(cdTypeFacade.createQualifiedType(ACCESS_MODIFIER_TYPE)
        , method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetNameMethod() {
    ASTCDMethod method = getMethodBy("setName", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("name", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetFullNameMethod() {
    ASTCDMethod method = getMethodBy("setFullName", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("fullName", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetPackageNameMethod() {
    ASTCDMethod method = getMethodBy("setPackageName", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(String.class, method.getCDParameter(0).getMCType());
    assertEquals("packageName", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetEnclosingScopeMethod() {
    ASTCDMethod method = getMethodBy("setEnclosingScope", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createQualifiedType(ENCLOSING_SCOPE_TYPE),
        method.getCDParameter(0).getMCType());
    assertEquals("enclosingScope", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetASTNodeMethod() {
    ASTCDMethod method = getMethodBy("setASTNode", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createQualifiedType(A_NODE_TYPE),
        method.getCDParameter(0).getMCType());
    assertEquals("aSTNode", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetASTNodeOptMethod() {
    ASTCDMethod method = getMethodBy("setASTNodeOpt", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createQualifiedType(A_NODE_TYPE_OPT),
        method.getCDParameter(0).getMCType());
    assertEquals("aSTNode", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetASTNodeAbsentMethod() {
    ASTCDMethod method = getMethodBy("setASTNodeAbsent", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertTrue(method.isEmptyCDParameters());
  }

  @Test
  public void testSetAccessModifierMethod() {
    ASTCDMethod method = getMethodBy("setAccessModifier", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createQualifiedType(ACCESS_MODIFIER_TYPE),
        method.getCDParameter(0).getMCType());
    assertEquals("accessModifier", method.getCDParameter(0).getName());
  }

  @Test
  public void testSetSpannedScopeMethod() {
    ASTCDMethod method = getMethodBy("setSpannedScope", symbolClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertDeepEquals(cdTypeFacade.createQualifiedType(I_AUTOMATON_SCOPE),
        method.getCDParameter(0).getMCType());
    assertEquals("spannedScope", method.getCDParameter(0).getName());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CoreTemplates.CLASS, symbolClass, symbolClass);
    System.out.println(sb.toString());
    StaticJavaParser.parse(sb.toString());
  }
}
