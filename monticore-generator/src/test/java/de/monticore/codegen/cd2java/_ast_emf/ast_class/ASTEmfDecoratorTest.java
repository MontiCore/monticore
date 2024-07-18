/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.ast_class;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTScopeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTSymbolDecorator;
import de.monticore.codegen.cd2java._ast_emf.EmfService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertInt;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ASTEmfDecoratorTest extends DecoratorTestCase {

  private ASTCDClass emfClass;

  private ASTCDClass emfTransitionClass;

  @Before
  public void setup() {
    ASTCDCompilationUnit ast = this.parse("de", "monticore", "codegen", "_ast_emf", "Automata");

    this.glex.setGlobalValue("service", new EmfService(ast));

    SymbolTableService symbolTableService = new SymbolTableService(ast);
    ASTEmfDecorator decorator = new ASTEmfDecorator(this.glex, new ASTService(ast), new VisitorService(ast),
        new ASTSymbolDecorator(glex, symbolTableService), new ASTScopeDecorator(glex, symbolTableService), new MethodDecorator(glex,symbolTableService),
        new SymbolTableService(ast), new EmfService(ast));
    // automaton ast class
    ASTCDClass clazz = getClassBy("ASTAutomaton", ast);
    ASTCDClass changedClass = CD4AnalysisMill.cDClassBuilder().setName(clazz.getName())
        .setModifier(clazz.getModifier())
        .build();
    this.emfClass = decorator.decorate(clazz, changedClass);

    // transition ast class
    ASTCDClass clazzTransition = getClassBy("ASTTransitionWithAction", ast);
    ASTCDClass changedClassTransition = CD4AnalysisMill.cDClassBuilder().setName(clazzTransition.getName())
        .setModifier(clazzTransition.getModifier())
        .build();
    this.emfTransitionClass = decorator.decorate(clazzTransition, changedClassTransition);
  }

  @Test
  public void testClassName() {
    assertEquals("ASTAutomaton", emfClass.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperInterface() {
    assertEquals(1, emfClass.getInterfaceList().size());
    assertDeepEquals("de.monticore.codegen._ast_emf.automata._ast.ASTAutomataNode", emfClass.getCDInterfaceUsage().getInterface(0));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperClass() {
    assertTrue(emfClass.isPresentCDExtendUsage());
    assertEquals("de.monticore.emf._ast.ASTECNode", emfClass.printSuperclasses());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeSize() {
    assertEquals(3, emfClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeNames() {
    getAttributeBy("symbol", emfClass);
    getAttributeBy("spannedScope", emfClass);
    getAttributeBy("enclosingScope", emfClass);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodSize() {
    assertFalse(emfClass.getCDMethodList().isEmpty());
    assertEquals(21, emfClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEGetMethod() {
    ASTCDMethod method = getMethodBy("eGet", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(Object.class, method.getMCReturnType().getMCType());

    assertEquals(3, method.sizeCDParameters());
    assertEquals("featureID", method.getCDParameter(0).getName());
    assertInt(method.getCDParameter(0).getMCType());
    assertEquals("resolve", method.getCDParameter(1).getName());
    assertBoolean(method.getCDParameter(1).getMCType());
    assertEquals("coreType", method.getCDParameter(2).getName());
    assertBoolean(method.getCDParameter(2).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testESetMethod() {
    ASTCDMethod method = getMethodBy("eSet", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(2, method.sizeCDParameters());
    assertEquals("featureID", method.getCDParameter(0).getName());
    assertInt(method.getCDParameter(0).getMCType());
    assertEquals("newValue", method.getCDParameter(1).getName());
    assertDeepEquals(Object.class, method.getCDParameter(1).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEUnsetMethod() {
    ASTCDMethod method = getMethodBy("eUnset", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCVoidType());

    assertEquals(1, method.sizeCDParameters());
    assertEquals("featureID", method.getCDParameter(0).getName());
    assertInt(method.getCDParameter(0).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testEBaseStructuralFeatureIDMethod() {
    ASTCDMethod method = getMethodBy("eBaseStructuralFeatureID", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertInt(method.getMCReturnType().getMCType());

    assertEquals(2, method.sizeCDParameters());
    assertEquals("featureID", method.getCDParameter(0).getName());
    assertInt(method.getCDParameter(0).getMCType());
    assertEquals("baseClass", method.getCDParameter(1).getName());
    assertDeepEquals("Class<?>",
        method.getCDParameter(1).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEDerivedStructuralFeatureIDMethod() {
    ASTCDMethod method = getMethodBy("eDerivedStructuralFeatureID", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertInt(method.getMCReturnType().getMCType());

    assertEquals(2, method.sizeCDParameters());
    assertEquals("featureID", method.getCDParameter(0).getName());
    assertInt(method.getCDParameter(0).getMCType());
    assertEquals("baseClass", method.getCDParameter(1).getName());
    assertDeepEquals("Class<?>",
        method.getCDParameter(1).getMCType());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEToStringMethod() {
    ASTCDMethod method = getMethodBy("toString", emfClass);
    assertDeepEquals(PUBLIC, method.getModifier());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEStaticClassMethod() {
    ASTCDMethod method = getMethodBy("eStaticClass", emfClass);
    assertDeepEquals(PROTECTED, method.getModifier());
    assertDeepEquals("org.eclipse.emf.ecore.EClass", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * ASTTransitionWithAction already has a toString method in the classdiagramm
   * tests that no toString method is separately generated
   */
  @Test (expected = AssertionError.class)
  public void testToStringASTTransitionWithAction() {
    getMethodBy("toString", emfTransitionClass);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * no super class if AST has already a super class
   */
  @Test
  public void testSuperClassASTTransitionWithAction() {
    assertFalse(emfTransitionClass.isPresentCDExtendUsage());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGeneratedCode() {
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    GeneratorEngine generatorEngine = new GeneratorEngine(generatorSetup);
    CD4C.init(generatorSetup);
    StringBuilder sb = generatorEngine.generate(CD2JavaTemplates.CLASS, emfClass, packageDir);
    // test parsing
    ParserConfiguration configuration = new ParserConfiguration();
    JavaParser parser = new JavaParser(configuration);
    ParseResult parseResult = parser.parse(sb.toString());
    assertTrue(parseResult.isSuccessful());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
