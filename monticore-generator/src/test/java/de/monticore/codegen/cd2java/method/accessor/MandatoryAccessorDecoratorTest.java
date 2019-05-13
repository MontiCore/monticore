package de.monticore.codegen.cd2java.method.accessor;

import de.monticore.codegen.cd2java.factories.CDAttributeFacade;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class MandatoryAccessorDecoratorTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();


  @Before
  public void setup() {
    LogStub.init();
  }

  @Test
  public void testGetMethodString() {
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttributeByDefinition("protected String a;");
    MandatoryAccessorDecorator mandatoryAccessorDecorator = new MandatoryAccessorDecorator(glex);
    List<ASTCDMethod> methods = mandatoryAccessorDecorator.decorate(attribute);

    assertEquals(1, methods.size());
    ASTCDMethod method = getMethodBy("getA", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertDeepEquals(String.class, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }

  @Test
  public void testGetMethodBoolean() {
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttribute(PROTECTED, String.class, "a");
    MandatoryAccessorDecorator mandatoryAccessorDecorator = new MandatoryAccessorDecorator(glex);
    List<ASTCDMethod> methods = mandatoryAccessorDecorator.decorate(attribute);

    assertEquals(1, methods.size());
    ASTCDMethod method = getMethodBy("getA", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertDeepEquals(String.class, method.getReturnType());
    assertDeepEquals(PUBLIC, method.getModifier());
  }
}
