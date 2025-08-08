/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.interpreter;

import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static groovy.test.GroovyTestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class InterpreterDecoratorTest extends DecoratorTestCase {
  
  protected ASTCDCompilationUnit originalCompilationUnit;

  protected ASTCDClass decoratedClass;

  @Before
  public void before() {
    originalCompilationUnit = this.parse("de", "monticore", "codegen", "ast", "Automaton");
    ASTService astService = new ASTService(originalCompilationUnit);
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    this.glex.setGlobalValue("service", new VisitorService(originalCompilationUnit));

    InterpreterDecorator decorator = new InterpreterDecorator(this.glex, astService, visitorService);
    this.decoratedClass = decorator.decorate(originalCompilationUnit);
  }

  @Test
  public void testMethodCount() {
    assertEquals(3, decoratedClass.getCDMethodList().size());
  }

  @Test
  public void testConstructors() {
    List<ASTCDConstructor> constructors = decoratedClass.getCDConstructorList();

    assertEquals(2, constructors.size());
    assertTrue(constructors.get(0).getCDParameterList().isEmpty());

    assertEquals(1, constructors.get(1).getCDParameterList().size());
    assertEquals("realThis", constructors.get(1).getCDParameter(0).getName());
    assertEquals(InterpreterConstants.MODELINTERPRETER_FULLNAME,
        constructors.get(1).getCDParameter(0).getMCType().printType());
  }

  @Test
  public void testSuperInterfaces() {
    List<ASTMCObjectType> interfaces = decoratedClass.getInterfaceList();

    assertEquals(1, interfaces.size());

    assertEquals(
        ((ASTMCQualifiedType) interfaces.get(0)).getMCQualifiedName().getQName(),
        "IAutomatonInterpreter");
  }

  @Test
  public void testClassAttributes() {
    List<ASTCDAttribute> attributes = decoratedClass.getCDAttributeList();

    assertEquals(3, attributes.size());

    assertEquals("lexicalsInterpreter", attributes.get(0).getName());
    assertEquals("de.monticore.codegen.ast.lexicals._visitor.ILexicalsInterpreter",
        attributes.get(0).getMCType().printType());

    assertEquals("realThis", attributes.get(1).getName());
    assertEquals(InterpreterConstants.MODELINTERPRETER_FULLNAME,
        attributes.get(1).getMCType().printType());

    assertEquals("scopeCallstack", attributes.get(2).getName());
    assertEquals("java.util.Stack", ((ASTMCGenericType)attributes.get(2).getMCType()).printWithoutTypeArguments());
    assertEquals(InterpreterConstants.INTERPRETER_SCOPE_FULLNAME,
        ((ASTMCGenericType)attributes.get(2).getMCType()).getMCTypeArgument(0).printType());
  }

  @Test
  public void testRealThisMethods() {
    Optional<ASTCDMethod> optGetMethod = decoratedClass.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("getRealThis"))
        .findAny();

    assertTrue(optGetMethod.isPresent());
    ASTCDMethod getMethod = optGetMethod.get();

    assertTrue(getMethod.getCDParameterList().isEmpty());
    assertEquals(InterpreterConstants.MODELINTERPRETER_FULLNAME,
        getMethod.getMCReturnType().printType());

    Optional<ASTCDMethod> optSetMethod = decoratedClass.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("setRealThis"))
        .findAny();

    assertTrue(optSetMethod.isPresent());
    ASTCDMethod setMethod = optSetMethod.get();

    assertEquals(1, setMethod.getCDParameterList().size());
    assertEquals(InterpreterConstants.MODELINTERPRETER_FULLNAME,
        setMethod.getCDParameter(0).getMCType().printType());
    assertEquals("realThis",
        setMethod.getCDParameter(0).getName());
    assertTrue(setMethod.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testScopeCallstackMethod() {
    Optional<ASTCDMethod> optScopeCallstackMethod = decoratedClass.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("getScopeCallstack"))
        .findAny();
    
    assertTrue(optScopeCallstackMethod.isPresent());
    ASTCDMethod scopeCallstackMethod = optScopeCallstackMethod.get();

    ASTMCGenericType returnTypeType = (ASTMCGenericType)scopeCallstackMethod.getMCReturnType().getMCType();
    assertEquals("java.util.Stack", returnTypeType.printWithoutTypeArguments());
    assertEquals(InterpreterConstants.INTERPRETER_SCOPE_FULLNAME,
            returnTypeType.getMCTypeArgument(0).printType());
  }

  @After
  public void after() {
    assertTrue(Log.getFindings().isEmpty());
    Log.getFindings().clear();
  }

}
