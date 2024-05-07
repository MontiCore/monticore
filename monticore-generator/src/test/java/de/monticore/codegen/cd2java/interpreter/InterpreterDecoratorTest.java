/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.interpreter;

import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.se_rwth.commons.logging.Log;
import org.junit.After;
import org.junit.Before;
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
    VisitorService visitorService = new VisitorService(originalCompilationUnit);
    this.glex.setGlobalValue("service", new VisitorService(originalCompilationUnit));

    InterpreterDecorator decorator = new InterpreterDecorator(this.glex, visitorService);
    this.decoratedClass = decorator.decorate(originalCompilationUnit);
  }

  @Test
  public void testMethodCount() {
    assertEquals(6, decoratedClass.getCDMethodList().size());
  }

  @Test
  public void testConstructors() {
    List<ASTCDConstructor> constructors = decoratedClass.getCDConstructorList();

    assertEquals(2, constructors.size());
    assertTrue(constructors.get(0).getCDParameterList().isEmpty());

    assertEquals(
        constructors.get(1).getCDParameterList().size(),
        1);
    assertEquals(
        constructors.get(1).getCDParameter(0).getName(),
        "realThis");
    assertEquals(
        constructors.get(1).getCDParameter(0).getMCType().printType(),
        InterpreterConstants.MODELINTERPRETER_FULLNAME);
  }

  @Test
  public void testSuperInterfaces() {
    List<ASTMCObjectType> interfaces = decoratedClass.getInterfaceList();

    assertEquals(2, interfaces.size());
    assertEquals(
        ((ASTMCQualifiedType) interfaces.get(0)).getMCQualifiedName().getQName(),
        InterpreterConstants.MODELINTERPRETER_FULLNAME);
    assertEquals(
        ((ASTMCQualifiedType) interfaces.get(1)).getMCQualifiedName().getQName(),
        "de.monticore.codegen.ast.automaton._visitor.IAutomatonInterpreter");
  }

  @Test
  public void testClassAttributes() {
    List<ASTCDAttribute> attributes = decoratedClass.getCDAttributeList();

    assertEquals(attributes.size(), 3);

    assertEquals(
        attributes.get(0).getName(),
        "lexicalsInterpreter");
    assertEquals(
        attributes.get(0).getMCType().printType(),
        "de.monticore.codegen.ast.lexicals._visitor.ILexicalsInterpreter");

    assertEquals(
        attributes.get(1).getName(),
        "realThis");
    assertEquals(
        attributes.get(1).getMCType().printType(),
        InterpreterConstants.MODELINTERPRETER_FULLNAME);

    assertEquals(
        attributes.get(2).getName(),
        "contextMap");
    assertTrue(attributes.get(2).getMCType() instanceof ASTMCMapType);
    assertEquals(
        ((ASTMCMapType) attributes.get(2).getMCType()).getMCTypeArgument(0).printType(),
        InterpreterConstants.SYMBOL_FULLNAME);
    assertEquals(
        ((ASTMCMapType) attributes.get(2).getMCType()).getMCTypeArgument(1).printType(),
        InterpreterConstants.VALUE_FULLNAME);
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
    assertEquals(
        getMethod.getMCReturnType().printType(),
        InterpreterConstants.MODELINTERPRETER_FULLNAME);

    Optional<ASTCDMethod> optSetMethod = decoratedClass.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("setRealThis"))
        .findAny();

    assertTrue(optSetMethod.isPresent());
    ASTCDMethod setMethod = optSetMethod.get();

    assertEquals(setMethod.getCDParameterList().size(), 1);
    assertEquals(
        setMethod.getCDParameter(0).getMCType().printType(),
        InterpreterConstants.MODELINTERPRETER_FULLNAME);
    assertEquals(
        setMethod.getCDParameter(0).getName(),
        "realThis");
    assertTrue(setMethod.getMCReturnType().isPresentMCVoidType());
  }

  @Test
  public void testContextMethods() {
    Optional<ASTCDMethod> optStoreMethod = decoratedClass.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("store"))
        .findAny();

    assertTrue(optStoreMethod.isPresent());
    ASTCDMethod storeMethod = optStoreMethod.get();

    assertTrue(storeMethod.getMCReturnType().isPresentMCVoidType());
    assertEquals(2, storeMethod.getCDParameterList().size());
    assertEquals(storeMethod.getCDParameter(0).getName(), "symbol");
    assertEquals(
        storeMethod.getCDParameter(0).getMCType().printType(),
        InterpreterConstants.SYMBOL_FULLNAME);
    assertEquals(storeMethod.getCDParameter(1).getName(), "value");
    assertEquals(
        storeMethod.getCDParameter(1).getMCType().printType(),
        InterpreterConstants.VALUE_FULLNAME);

    Optional<ASTCDMethod> optLoadMethod = decoratedClass.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("load"))
        .findAny();

    assertTrue(optLoadMethod.isPresent());
    ASTCDMethod loadMethod = optLoadMethod.get();

    assertEquals(
        loadMethod.getMCReturnType().printType(),
        InterpreterConstants.VALUE_FULLNAME);
    assertEquals(1, loadMethod.getCDParameterList().size());
    assertEquals(
        loadMethod.getCDParameter(0).getMCType().printType(),
        InterpreterConstants.SYMBOL_FULLNAME);
    assertEquals(
        loadMethod.getCDParameter(0).getName(),
        "symbol");

    Optional<ASTCDMethod> optGetMap = decoratedClass.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("getContextMap"))
        .findAny();

    assertTrue(optGetMap.isPresent());
    ASTCDMethod getMapMethod = optGetMap.get();

    assertTrue(getMapMethod.getCDParameterList().isEmpty());
    assertTrue(getMapMethod.getMCReturnType().getMCType() instanceof ASTMCMapType);
    assertEquals(
        ((ASTMCMapType) getMapMethod.getMCReturnType().getMCType()).getMCTypeArgument(0).printType(),
        InterpreterConstants.SYMBOL_FULLNAME);
    assertEquals(
        ((ASTMCMapType) getMapMethod.getMCReturnType().getMCType()).getMCTypeArgument(1).printType(),
        InterpreterConstants.VALUE_FULLNAME);
  }

  @Test
  public void testInterpretMethods() {
    List<ASTCDMethod> interpretMethods = decoratedClass.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("interpret"))
        .collect(Collectors.toList());

    assertEquals(1, interpretMethods.size());
    ASTCDMethod method = interpretMethods.get(0);

    assertEquals(1, method.getCDParameterList().size());
    assertEquals("node", method.getCDParameter(0).getName());
    assertEquals(InterpreterConstants.VALUE_FULLNAME, method.getMCReturnType().printType());
  }

  @After
  public void after() {
    assertTrue(Log.getFindings().isEmpty());
    Log.getFindings().clear();
  }

}
