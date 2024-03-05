/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.interpreter;

import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static groovy.test.GroovyTestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class InterpreterDecoratorTest extends DecoratorTestCase {


  protected ASTCDCompilationUnit originalCompilationUnit;

  protected ASTCDClass decoratedClass;

  @Before
  public void before() {
    originalCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    this.glex.setGlobalValue("service", new VisitorService(originalCompilationUnit));

    InterpreterDecorator decorator = new InterpreterDecorator(this.glex, visitorService);
    this.decoratedClass = decorator.decorate(originalCompilationUnit);
  }

  @Test
  public void testMethodCount() {
    assertEquals(4, decoratedClass.getCDMethodList().size());
  }

  @Test
  public void testConstructorCount() {
    List<ASTCDConstructor> constructors = decoratedClass.getCDConstructorList();

    assertEquals(2, constructors.size());
    assertTrue(constructors.get(0).getCDParameterList().isEmpty());
    assertEquals(constructors.get(1).getCDParameterList().size(), 1);
    assertEquals(constructors.get(1).getCDParameter(0).getName(), "realThis");
  }

  @Test
  public void testSuperInterfaces() {
    String classInterfaces = decoratedClass.printInterfaces();
    String[] interfaces = classInterfaces.split(" ");

    assertEquals(interfaces[0], InterpreterConstants.MODELINTERPRETER_FULLNAME);
    assertEquals(interfaces[1], "de.monticore.codegen.ast.automaton._visitor.IAutomatonInterpreter");
  }

  @Test
  public void testClassAttributes() {
    List<ASTCDAttribute> attributes = decoratedClass.getCDAttributeList();

    assertEquals(attributes.size(), 3);
    assertEquals(attributes.get(0).getName(), "lexicalsInterpreter");
    assertEquals(attributes.get(1).getName(), "realThis");
  }

  @Test
  public void testInterpretMethods() {
    List<ASTCDMethod> interpretMethods = decoratedClass.getCDMethodList()
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
