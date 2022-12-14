/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MillForSuperDecoratorTest extends DecoratorTestCase {

  private ASTCDClass millClass;

  private GlobalExtensionManagement glex;

  private ASTCDCompilationUnit originalCompilationUnit;

  private ASTCDCompilationUnit decoratedCompilationUnit;

  @Before
  public void setUp() {
    this.glex = new GlobalExtensionManagement();

    this.glex.setGlobalValue("astHelper", DecorationHelper.getInstance());
    this.glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    decoratedCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "ExtAutomaton");
    originalCompilationUnit = decoratedCompilationUnit.deepClone();
    this.glex.setGlobalValue("service", new AbstractService(decoratedCompilationUnit));
    VisitorService visitorService = new VisitorService(decoratedCompilationUnit);
    ParserService parserService = new ParserService(decoratedCompilationUnit);

    MillForSuperDecorator decorator = new MillForSuperDecorator(this.glex, new ASTService(decoratedCompilationUnit), visitorService, parserService);
    List<ASTCDClass> millClassList = decorator.decorate(decoratedCompilationUnit);
    millClassList = millClassList.stream().filter(c -> "AutomatonMillForExtAutomaton".equals(c.getName())).collect(Collectors.toList());

    assertEquals(1, millClassList.size());
    millClass = millClassList.get(0);
  }

  @Test
  public void testCompilationUnitNotChanged() {
    assertDeepEquals(originalCompilationUnit, decoratedCompilationUnit);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSuperClass() {
    assertTrue(millClass.isPresentCDExtendUsage());
    assertDeepEquals("de.monticore.codegen.ast.automaton.AutomatonMill", millClass.getCDExtendUsage().getSuperclass(0));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoInterfaces() {
    assertEquals(0, millClass.getInterfaceList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testNoAttributes() {
    assertEquals(0, millClass.getCDAttributeList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethodSize() {
    assertEquals(7, millClass.getCDMethodList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testMethods() {
    getMethodBy("_transitionBuilder", millClass);
    getMethodBy("_artifactScope", millClass);
    getMethodBy("_globalScope", millClass);
    getMethodBy("_scope", millClass);
    getMethodBy("_traverser", millClass);
    getMethodBy("_inheritanceTraverser", millClass);
    getMethodBy("_parser", millClass);
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testScopeMethod(){
    ASTCDMethod method = getMethodBy("_scope", millClass);
    assertDeepEquals(CDModifier.PROTECTED, method.getModifier());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("de.monticore.codegen.ast.extautomaton._symboltable.IExtAutomatonScope", method.getMCReturnType().getMCType());
    assertTrue(method.isEmptyCDParameters());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
