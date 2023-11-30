/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.interpreter.InterpreterConstants;
import de.monticore.codegen.cd2java.interpreter.InterpreterDecorator;
import de.monticore.codegen.cd2java.interpreter.InterpreterInterfaceDecorator;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
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
    assertEquals(7, decoratedInterface.getCDMethodList().size());
  }

  @Test
  public void testSuperInterfaces() {
    String interfaces = decoratedInterface.printInterfaces();
    String[] i = interfaces.split(" ");
    assertEquals(i[0], InterpreterConstants.MODELINTERPRETER_FULLNAME);
    assertEquals(i[1], "de.monticore.codegen.ast.lexicals._visitor.ILexicalsInterpreter");
  }

  @Test
  public void testInterpretMethods() {
    List<ASTCDMethod> interpretMethods = decoratedInterface.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("interpret"))
        .collect(Collectors.toList());

    interpretMethods.forEach(m -> {
      assertEquals(1, m.getCDParameterList().size());
      assertEquals("node", m.getCDParameter(0).getName());
      assertEquals(InterpreterConstants.VALUE_FULLNAME, m.getMCReturnType().printType());
    });
  }

  @After
  public void after() {
    assertTrue(Log.getFindings().isEmpty());
    Log.getFindings().clear();
  }

}
