package de.monticore.codegen.cd2java.method.mutator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java.factories.CDAttributeFacade;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.methods.mutator.ListMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodsBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ListMutatorDecoratorTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDMethod> methods;

  @Before
  public void setup() {
    LogStub.init();
    ASTMCType listType = CDTypeFacade.getInstance().createListTypeOf(String.class);
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttribute(PROTECTED, listType, "a");
    ListMutatorDecorator listMutatorDecorator = new ListMutatorDecorator(glex);
    this.methods = listMutatorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(15, methods.size());
  }

  @Test
  public void testSetListMethod() {
    ASTCDMethod method = getMethodBy("setAList", 1, this.methods);
    assertVoid(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1,method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameterList().get(0);
    assertListOf(String.class, parameter.getMCType());
    assertEquals("a", parameter.getName());
  }

  @Test
  public void testClearMethod() {
    ASTCDMethod method = getMethodBy("clearA", this.methods);
    assertVoid(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getCDParameterList().isEmpty());
  }

  @Test
  public void testAddMethod() {
    ASTCDMethod method = getMethodBy("addA", 1, this.methods);
    assertBoolean(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(String.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testAddAllMethod() {
    ASTCDMethod method = getMethodBy("addAllA", 1, this.methods);
    assertBoolean(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    ASTMCType expectedParameterType = CDTypeFacade.getInstance().createCollectionTypeOf("? extends String");
    assertDeepEquals(expectedParameterType, parameter.getMCType());
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testRemoveMethod() {
    List<ASTCDMethod> methods = getMethodsBy("removeA", 1, this.methods);
    assertEquals(2, methods.size());
    ASTMCType expectedReturnType = CDTypeFacade.getInstance().createBooleanType();
    methods = methods.stream().filter(m -> m.getMCReturnType().deepEquals(expectedReturnType)).collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertBoolean(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testRemoveAllMethod() {
    ASTCDMethod method = getMethodBy("removeAllA", this.methods);
    assertBoolean(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    ASTMCType expectedParameterType = CDTypeFacade.getInstance().createCollectionTypeOf("?");
    assertDeepEquals(expectedParameterType, parameter.getMCType());
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testRetainAllMethod() {
    ASTCDMethod method = getMethodBy("retainAllA", this.methods);
    assertBoolean(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    ASTMCType expectedParameterType = CDTypeFacade.getInstance().createCollectionTypeOf("?");
    assertDeepEquals(expectedParameterType, parameter.getMCType());
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testRemoveIfMethod() {
    ASTCDMethod method = getMethodBy("removeIfA", this.methods);
    assertBoolean(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    ASTMCType expectedParameterType = CDTypeFacade.getInstance().createTypeByDefinition("Predicate<? super String>");
    assertDeepEquals(expectedParameterType, parameter.getMCType());
    assertEquals("filter", parameter.getName());
  }

  @Test
  public void testForEachMethod() {
    ASTCDMethod method = getMethodBy("forEachA", this.methods);
    assertVoid(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    ASTMCType expectedParameterType = CDTypeFacade.getInstance().createTypeByDefinition("Consumer<? super String>");
    assertDeepEquals(expectedParameterType, parameter.getMCType());
    assertEquals("action", parameter.getName());
  }

  @Test
  public void testAddWithIndexMethod() {
    ASTCDMethod method = getMethodBy("addA", 2, this.methods);
    assertVoid(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("index", parameter.getName());
    parameter = method.getCDParameter(1);
    assertDeepEquals(String.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testAddAllWithIndexMethod() {
    ASTCDMethod method = getMethodBy("addAllA", 2, this.methods);
    assertBoolean(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("index", parameter.getName());
    parameter = method.getCDParameter(1);
    ASTMCType expectedParameterType = CDTypeFacade.getInstance().createCollectionTypeOf("? extends String");

    assertDeepEquals(expectedParameterType, parameter.getMCType());
    assertEquals("collection", parameter.getName());
  }

  @Test
  public void testRemoveWithIndexMethod() {
    List<ASTCDMethod> methods = getMethodsBy("removeA", 1, this.methods);
    assertEquals(2, methods.size());
    ASTMCType exptectedReturnType = CDTypeFacade.getInstance().createQualifiedType(String.class);
    methods = methods.stream().filter(m -> m.getMCReturnType().deepEquals(exptectedReturnType)).collect(Collectors.toList());
    assertEquals(1, methods.size());
    ASTCDMethod method = methods.get(0);
    assertDeepEquals(exptectedReturnType, method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("index", parameter.getName());
  }

  @Test
  public void testSetWithIndexMethod() {
    ASTCDMethod method = getMethodBy("setA", 2, this.methods);
    assertDeepEquals(String.class, method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("index", parameter.getName());
    parameter = method.getCDParameter(1);
    assertDeepEquals(String.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  }

  @Test
  public void testReplaceAllMethod() {
    ASTCDMethod method = getMethodBy("replaceAllA", this.methods);
    assertVoid(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    ASTMCType expectedParameterType = CDTypeFacade.getInstance().createTypeByDefinition("UnaryOperator<String>");
    assertDeepEquals(expectedParameterType, parameter.getMCType());
    assertEquals("operator", parameter.getName());
  }

  @Test
  public void testSortMethod() {
    ASTCDMethod method = getMethodBy("sortA", this.methods);
    assertVoid(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    ASTMCType expectedParameterType =CDTypeFacade.getInstance().createTypeByDefinition("Comparator<? super String>");
    assertDeepEquals(expectedParameterType, parameter.getMCType());
    assertEquals("comparator", parameter.getName());
  }
}
