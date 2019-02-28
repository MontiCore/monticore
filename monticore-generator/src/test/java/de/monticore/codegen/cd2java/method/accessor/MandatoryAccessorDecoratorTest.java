package de.monticore.codegen.cd2java.method.accessor;

import de.monticore.codegen.cd2java.factories.CDAttributeFactory;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class MandatoryAccessorDecoratorTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDMethod> methods;

  @Before
  public void setup() {
    LogStub.init();
    ASTCDAttribute attribute = CDAttributeFactory.getInstance().createAttributeByDefinition("protected String a;");
    MandatoryAccessorDecorator mandatoryAccessorDecorator = new MandatoryAccessorDecorator(glex);
    this.methods = mandatoryAccessorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(1, methods.size());
  }

  @Test
  public void testGetMethod() {
    ASTCDMethod method = getMethodBy("getA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertDeepEquals(String.class, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }
}
