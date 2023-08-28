/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_new;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ASTDecoratorTest extends DecoratorTestCase {

  private MCTypeFacade mcTypeFacade = MCTypeFacade.getInstance();

  private ASTCDClass astClass;

  private static final String AST_SCOPE = "de.monticore.codegen.ast.ast._symboltable.IASTScope";

  private static final String AST_SYMBOL = "de.monticore.codegen.ast.ast._symboltable.ASymbol";

  @Before
  public void setup() {
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "ast", "AST");

    this.glex.setGlobalValue("service", new AbstractService(ast));
    SymbolTableService symbolTableService = new SymbolTableService(ast);
    ASTDecorator decorator = new ASTDecorator(this.glex, new ASTService(ast), new VisitorService(ast),
        new ASTSymbolDecorator(glex, symbolTableService), new ASTScopeDecorator(glex, symbolTableService), new MethodDecorator(glex, symbolTableService),
        new SymbolTableService(ast));
    ASTCDClass clazz = getClassBy("A", ast);
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(clazz.getName())
        .setModifier(clazz.getModifier())
        .build();
    this.astClass = decorator.decorate(clazz, changedClass);
  }

  @Test
  public void testClassName() {
    assertEquals("A", astClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testClassModifier() {
    // because it defines a symbol but has no name attribute or a getName method
    assertTrue(astClass.getModifier().isAbstract());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperClass() {
    assertEquals("ASTCNode", astClass.printSuperclasses());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBaseInterface() {
    assertEquals(1, astClass.getCDInterfaceUsage().sizeInterface());
    assertEquals("de.monticore.codegen.ast.ast._ast.ASTASTNode", astClass.printInterfaces());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(3, astClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEmptyConstructors() {
    assertEquals(0, astClass.getCDConstructorList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethods() {
    assertFalse(astClass.getCDMethodList().isEmpty());
    assertEquals(14, astClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * abstract method generated because A is a symbol but has no name
   */
  @Test
  public void testGetNameMethod() {
    ASTCDMethod method = getMethodBy("getName", astClass);

    assertDeepEquals(PUBLIC_ABSTRACT, method.getModifier());

    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());

    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAcceptMethod() {
    List<ASTCDMethod> methods = getMethodsBy("accept", 1, astClass);
    ASTMCType visitorType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen.ast.ast._visitor.ASTTraverser");

    methods = methods.stream().filter(m -> visitorType.deepEquals(m.getCDParameter(0).getMCType())).collect(Collectors.toList());
    assertEquals(1, methods.size());

    ASTCDMethod method = methods.get(0);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);

    assertDeepEquals(visitorType, parameter.getMCType());
    assertEquals("visitor", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAcceptSuperMethod() {
    List<ASTCDMethod> methods = getMethodsBy("accept", 1, astClass);
    ASTMCType visitorType = this.mcTypeFacade.createQualifiedType("de.monticore.codegen.ast.supercd._visitor.SuperCDTraverser");

    methods = methods.stream().filter(m -> visitorType.deepEquals(m.getCDParameter(0).getMCType())).collect(Collectors.toList());
    assertEquals(1, methods.size());

    ASTCDMethod method = methods.get(0);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertFalse(method.isEmptyCDParameters());
    assertEquals(1, method.sizeCDParameters());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(visitorType, parameter.getMCType());
    assertEquals("visitor", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstructMethod() {
    ASTCDMethod method = getMethodBy("_construct", astClass);
    assertDeepEquals(PROTECTED, method.getModifier());
    ASTMCType astType = this.mcTypeFacade.createQualifiedType(astClass.getName());
    assertDeepEquals(astType, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, astClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetScopeMethod() {
    ASTCDMethod method = getMethodBy("getSpannedScope", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(AST_SCOPE, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsSetScopeMethod() {
    ASTCDMethod method = getMethodBy("setSpannedScope", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("spannedScope", method.getCDParameter(0).getName());
    assertDeepEquals(AST_SCOPE, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetSymbolMethod() {
    ASTCDMethod method = getMethodBy("getSymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(AST_SYMBOL, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsPresentSymbolMethod() {
    ASTCDMethod method = getMethodBy("isPresentSymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsSetSymbolMethod() {
    ASTCDMethod method = getMethodBy("setSymbol", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertEquals(1, method.sizeCDParameters());
    assertEquals("symbol", method.getCDParameter(0).getName());
    assertDeepEquals(AST_SYMBOL, method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsSetSymbolAbsentMethod() {
    ASTCDMethod method = getMethodBy("setSymbolAbsent", astClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
