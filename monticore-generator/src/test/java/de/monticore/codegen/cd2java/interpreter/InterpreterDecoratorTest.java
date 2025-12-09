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
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
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
    assertEquals(5, decoratedClass.getCDMethodList().size());
  }

  @Test
  public void testConstructors() {
    List<ASTCDConstructor> constructors = decoratedClass.getCDConstructorList();

    assertEquals(2, constructors.size());
    assertTrue(constructors.get(0).getCDParameterList().isEmpty());

    assertEquals(
            1,
            constructors.get(1).getCDParameterList().size());
    assertEquals(
            "realThis",
            constructors.get(1).getCDParameter(0).getName());
    assertEquals(
            InterpreterConstants.MODELINTERPRETER_FULLNAME,
            constructors.get(1).getCDParameter(0).getMCType().printType());
  }

  @Test
  public void testSuperInterfaces() {
    List<ASTMCObjectType> interfaces = decoratedClass.getInterfaceList();

    assertEquals(1, interfaces.size());

    assertEquals(
            "IAutomatonInterpreter",
            ((ASTMCQualifiedType) interfaces.get(0)).getMCQualifiedName().getQName());
  }

  @Test
  public void testClassAttributes() {
    List<ASTCDAttribute> attributes = decoratedClass.getCDAttributeList();

    assertEquals(3, attributes.size());

    assertEquals(
            "lexicalsInterpreter",
            attributes.get(0).getName());
    assertEquals(
            "de.monticore.codegen.ast.lexicals._visitor.ILexicalsInterpreter",
            attributes.get(0).getMCType().printType());

    assertEquals(
            "realThis",
            attributes.get(1).getName());
    assertEquals(
            InterpreterConstants.MODELINTERPRETER_FULLNAME,
            attributes.get(1).getMCType().printType());

    assertEquals(
            "contextMap",
            attributes.get(2).getName());
    assertTrue(attributes.get(2).getMCType() instanceof ASTMCMapType);
    assertEquals(
            InterpreterConstants.SYMBOL_FULLNAME,
            ((ASTMCMapType) attributes.get(2).getMCType()).getMCTypeArgument(0).printType());
    assertEquals(
            InterpreterConstants.VALUE_FULLNAME,
            ((ASTMCMapType) attributes.get(2).getMCType()).getMCTypeArgument(1).printType());
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
            InterpreterConstants.MODELINTERPRETER_FULLNAME,
            getMethod.getMCReturnType().printType());

    Optional<ASTCDMethod> optSetMethod = decoratedClass.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("setRealThis"))
        .findAny();

    assertTrue(optSetMethod.isPresent());
    ASTCDMethod setMethod = optSetMethod.get();

    assertEquals(1, setMethod.getCDParameterList().size());
    assertEquals(
            InterpreterConstants.MODELINTERPRETER_FULLNAME,
            setMethod.getCDParameter(0).getMCType().printType());
    assertEquals(
            "realThis",
            setMethod.getCDParameter(0).getName());
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
    assertEquals("symbol", storeMethod.getCDParameter(0).getName());
    assertEquals(
            InterpreterConstants.SYMBOL_FULLNAME,
            storeMethod.getCDParameter(0).getMCType().printType());
    assertEquals("value", storeMethod.getCDParameter(1).getName());
    assertEquals(
            InterpreterConstants.VALUE_FULLNAME,
            storeMethod.getCDParameter(1).getMCType().printType());

    Optional<ASTCDMethod> optLoadMethod = decoratedClass.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("load"))
        .findAny();

    assertTrue(optLoadMethod.isPresent());
    ASTCDMethod loadMethod = optLoadMethod.get();

    assertEquals(
            InterpreterConstants.VALUE_FULLNAME,
            loadMethod.getMCReturnType().printType());
    assertEquals(1, loadMethod.getCDParameterList().size());
    assertEquals(
            InterpreterConstants.SYMBOL_FULLNAME,
            loadMethod.getCDParameter(0).getMCType().printType());
    assertEquals(
            "symbol",
            loadMethod.getCDParameter(0).getName());

    Optional<ASTCDMethod> optGetMap = decoratedClass.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("getContextMap"))
        .findAny();

    assertTrue(optGetMap.isPresent());
    ASTCDMethod getMapMethod = optGetMap.get();

    assertTrue(getMapMethod.getCDParameterList().isEmpty());
    assertTrue(getMapMethod.getMCReturnType().getMCType() instanceof ASTMCMapType);
    assertEquals(
            InterpreterConstants.SYMBOL_FULLNAME,
            ((ASTMCMapType) getMapMethod.getMCReturnType().getMCType()).getMCTypeArgument(0).printType());
    assertEquals(
            InterpreterConstants.VALUE_FULLNAME,
            ((ASTMCMapType) getMapMethod.getMCReturnType().getMCType()).getMCTypeArgument(1).printType());
  }

  @Test
  @Ignore
  public void testInterpretMethods() {
    List<ASTCDMethod> interpretMethods = decoratedClass.getCDMethodList()
        .stream()
        .filter(m -> m.getName().equals("interpret"))
        .collect(Collectors.toList());

    assertEquals(0, interpretMethods.size());
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
