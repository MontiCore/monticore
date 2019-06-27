package de.monticore.codegen.cd2java.method.accessor;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.factories.CDAttributeFacade;
import de.monticore.codegen.cd2java.factories.CDTypeFacade;
import de.monticore.codegen.cd2java.methods.accessor.OptionalAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class OptionalAccessorDecoratorTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDMethod> methods;

  @Before
  public void setup() {
    LogStub.init();
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttributeByDefinition("protected Optional<String> a;");
    OptionalAccessorDecorator optionalAccessorDecorator = new OptionalAccessorDecorator(glex);
    this.methods = optionalAccessorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(3, methods.size());
  }

  @Test
  public void testGetMethod() {
    ASTCDMethod method = getMethodBy("getA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertDeepEquals(String.class, method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testGetOptMethod() {
    ASTCDMethod method = getMethodBy("getAOpt", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    ASTMCType expectedReturnType = CDTypeFacade.getInstance().createOptionalTypeOf("String");
    assertDeepEquals(expectedReturnType, method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testIsPresentMethod() {
    ASTCDMethod method = getMethodBy("isPresentA", this.methods);
    assertBoolean(method.getMCReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getCDParameterList().isEmpty());
  }
}
