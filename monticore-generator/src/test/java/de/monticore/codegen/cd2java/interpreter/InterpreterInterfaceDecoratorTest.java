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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InterpreterInterfaceDecoratorTest extends DecoratorTestCase {


  protected ASTCDCompilationUnit originalCompilationUnit;

  protected ASTCDInterface decoratedInterface;

  @BeforeEach
  public void before() {
    originalCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    this.glex.setGlobalValue("service", new VisitorService(originalCompilationUnit));

    InterpreterInterfaceDecorator decorator = new InterpreterInterfaceDecorator(this.glex, visitorService);
    this.decoratedInterface = decorator.decorate(originalCompilationUnit);
  }

  @Test
  public void testMethodCount() {
    assertEquals(0, decoratedInterface.getCDMethodList().size());
  }

  @Test
  public void testSuperInterfaces() {
    List<ASTMCObjectType> interfaces = decoratedInterface.getInterfaceList();
    assertEquals(2, interfaces.size());
    assertEquals(
            "de.monticore.codegen.ast.lexicals._visitor.ILexicalsInterpreter",
            ((ASTMCQualifiedType) interfaces.get(0)).getMCQualifiedName().getQName());
    assertEquals(
            InterpreterConstants.MODELINTERPRETER_FULLNAME,
            ((ASTMCQualifiedType) interfaces.get(1)).getMCQualifiedName().getQName());
  }

  @Test
  @Disabled
  public void testInterpretMethods() {
    List<ASTCDMethod> interpretMethods = decoratedInterface.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("interpret"))
        .collect(Collectors.toList());

    assertEquals(0, interpretMethods.size());
    ASTCDMethod method = interpretMethods.get(0);

    assertEquals(1, method.getCDParameterList().size());
    assertEquals("node", method.getCDParameter(0).getName());
    assertEquals(InterpreterConstants.VALUE_FULLNAME, method.getMCReturnType().printType());

    assertTrue(method.getModifier().isAbstract());
  }

  @AfterEach
  public void after() {
    assertTrue(Log.getFindings().isEmpty());
    Log.getFindings().clear();
  }

}
