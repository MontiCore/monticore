package de.monticore.codegen.cd2java.method.mutator;

import de.monticore.codegen.cd2java.factories.CDAttributeFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.codegen.cd2java.methods.mutator.OptionalMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.*;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
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
    ASTType optionalType = CDTypeFactory.getInstance().createOptionalTypeOf(String.class);
    ASTCDAttribute attribute = CDAttributeFactory.getInstance().createAttribute(PROTECTED, optionalType, "a");
    OptionalMutatorDecorator optionalMutatorDecorator = new OptionalMutatorDecorator(glex);
    this.methods = optionalMutatorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(3, methods.size());
  }

  @Test
  public void testGetMethod() {
    ASTCDMethod method = getMethodBy("setA", this.methods);
    assertVoid(method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertDeepEquals(String.class, parameter.getType());
    assertEquals("a", parameter.getName());
  }

  @Test
  public void testGetOptMethod() {
    ASTCDMethod method = getMethodBy("setAOpt", this.methods);
    assertVoid(method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertEquals(1, method.getCDParameterList().size());
    ASTCDParameter parameter = method.getCDParameter(0);
    assertOptionalOf(String.class, parameter.getType());
    assertEquals("a", parameter.getName());
  }

  @Test
  public void testIsPresentMethod() {
    ASTCDMethod method = getMethodBy("setAbsentA", this.methods);
    assertVoid(method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getCDParameterList().isEmpty());
  }
}
