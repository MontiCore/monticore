/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.method.accessor;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.methods.accessor.ListAccessorDecorator;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ListAccessorDecoratorTest extends DecoratorTestCase {

  private List<ASTCDMethod> methods;

  
  @Before
  public void setup() {
    ASTMCType listType = MCTypeFacade.getInstance().createListTypeOf(String.class);
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), listType, "a");
    ListAccessorDecorator listAccessorDecorator = new ListAccessorDecorator(glex);
    this.methods = listAccessorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(19, methods.size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetListMethod() {
    ASTCDMethod method = getMethodBy("getAList", 0, this.methods);
    ASTMCType expectedReturnType = MCTypeFacade.getInstance().createListTypeOf("String");
    assertDeepEquals(expectedReturnType, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testContainsMethod() {
    ASTCDMethod method = getMethodBy("containsA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testContainsAllMethod() {
    ASTCDMethod method = getMethodBy("containsAllA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    ASTMCType expectedParameterType = MCTypeFacade.getInstance().createCollectionTypeOf("?");
    assertDeepEquals(expectedParameterType, parameter.getMCType());
    assertEquals("collection", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsEmptyMethod() {
    ASTCDMethod method = getMethodBy("isEmptyA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSizeMethod() {
    ASTCDMethod method = getMethodBy("sizeA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertInt(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testToArrayWithParamMethod() {
    ASTCDMethod method = getMethodBy("toArrayA", 1, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertArrayOf(String.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertArrayOf(String.class, parameter.getMCType());
    assertEquals("array", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testToArrayMethod() {
    ASTCDMethod method = getMethodBy("toArrayA", 0, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertArrayOf(Object.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSpliteratorMethod() {
    ASTCDMethod method = getMethodBy("spliteratorA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("Spliterator<String>", method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testStreamMethod() {
    ASTCDMethod method = getMethodBy("streamA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("Stream<String>", method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testParallelStreamMethod() {
    ASTCDMethod method = getMethodBy("parallelStreamA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("Stream<String>", method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetWithIndexMethod() {
    ASTCDMethod method = getMethodBy("getA", 1, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("index", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIndexOfMethod() {
    ASTCDMethod method = getMethodBy("indexOfA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertInt(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLastIndexOfMethod() {
    ASTCDMethod method = getMethodBy("lastIndexOfA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertInt(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("element", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEqualsMethod() {
    ASTCDMethod method = getMethodBy("equalsA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(Object.class, parameter.getMCType());
    assertEquals("o", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testHashCodeMethod() {
    ASTCDMethod method = getMethodBy("hashCodeA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertEquals("int", CD4CodeMill.prettyPrint(method.getMCReturnType(), false));
    assertDeepEquals(PUBLIC, method.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testListIteratorMethod() {
    ASTCDMethod method = getMethodBy("listIteratorA", 0, this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("ListIterator<String>", method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testListIteratorWithIndexMethod() {
    ASTCDMethod method = getMethodBy("listIteratorA", 1, this.methods);
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals("ListIterator<String>", method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("index", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSubListMethod() {
    ASTCDMethod method = getMethodBy("subListA", this.methods);
    ASTMCType expectedReturnType = MCTypeFacade.getInstance().createListTypeOf("String");
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(expectedReturnType, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());

    assertEquals(2, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertInt(parameter.getMCType());
    assertEquals("start", parameter.getName());

    parameter = method.getCDParameter(1);
    assertInt(parameter.getMCType());
    assertEquals("end", parameter.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
