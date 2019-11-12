/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.method.mutator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java.factories.CDAttributeFacade;
import de.monticore.codegen.cd2java.factories.MCTypeFacade;
import de.monticore.codegen.cd2java.methods.mutator.OptionalMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertOptionalOf;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class OptionalMutatorDecoratorTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDMethod> methods;

  @Before
  public void setup() {
    LogStub.init();
    ASTMCType optionalType = MCTypeFacade.getInstance().createOptionalTypeOf(String.class);
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttribute(PROTECTED, optionalType, "a");
    OptionalMutatorDecorator optionalMutatorDecorator = new OptionalMutatorDecorator(glex);
    this.methods = optionalMutatorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(2, methods.size());
  }

  @Test
  public void testGetMethod() {
    ASTCDMethod method = getMethodBy("setA", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(String.class, parameter.getMCType());
    assertEquals("a", parameter.getName());
  }

  @Test
  public void testIsPresentMethod() {
    ASTCDMethod method = getMethodBy("setAAbsent", this.methods);
    assertTrue(method.getMCReturnType().isPresentMCVoidType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getCDParameterList().isEmpty());
  }
}
