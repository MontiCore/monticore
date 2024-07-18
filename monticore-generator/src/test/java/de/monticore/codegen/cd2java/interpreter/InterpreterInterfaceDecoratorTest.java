/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.interpreter;

import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static groovy.test.GroovyTestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class InterpreterInterfaceDecoratorTest extends DecoratorTestCase {


  protected ASTCDCompilationUnit originalCompilationUnit;

  protected ASTCDInterface decoratedInterface;

  @Before
  public void before() {
    originalCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    this.glex.setGlobalValue("service", new VisitorService(originalCompilationUnit));

    InterpreterInterfaceDecorator decorator = new InterpreterInterfaceDecorator(this.glex, visitorService);
    this.decoratedInterface = decorator.decorate(originalCompilationUnit);
  }

  @Test
  public void testMethodCount() {
    assertEquals(6, decoratedInterface.getCDMethodList().size());
  }

  @Test
  public void testSuperInterfaces() {
    List<ASTMCObjectType> interfaces = decoratedInterface.getInterfaceList();
    assertEquals(2, interfaces.size());
    assertEquals(
        ((ASTMCQualifiedType) interfaces.get(0)).getMCQualifiedName().getQName(),
        "de.monticore.codegen.ast.lexicals._visitor.ILexicalsInterpreter");
    assertEquals(
        ((ASTMCQualifiedType) interfaces.get(1)).getMCQualifiedName().getQName(),
        InterpreterConstants.MODELINTERPRETER_FULLNAME);
  }

  @Test
  public void testInterpretMethods() {
    List<ASTCDMethod> interpretMethods = decoratedInterface.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("interpret"))
        .collect(Collectors.toList());

    assertEquals(6, interpretMethods.size());
    ASTCDMethod method = interpretMethods.get(0);

    assertEquals(1, method.getCDParameterList().size());
    assertEquals("node", method.getCDParameter(0).getName());
    assertEquals(InterpreterConstants.VALUE_FULLNAME, method.getMCReturnType().printType());

    assertTrue(method.getModifier().isAbstract());
  }

  @After
  public void after() {
    assertTrue(Log.getFindings().isEmpty());
    Log.getFindings().clear();
  }

}
