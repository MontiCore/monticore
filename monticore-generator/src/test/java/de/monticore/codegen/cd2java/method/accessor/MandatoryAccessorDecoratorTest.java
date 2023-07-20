/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.method.accessor;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java.methods.accessor.MandatoryAccessorDecorator;
import de.se_rwth.commons.logging.Log;
import org.junit.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MandatoryAccessorDecoratorTest extends DecoratorTestCase {

  @Test
  public void testGetMethodString() {
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), String.class, "a");

    MandatoryAccessorDecorator mandatoryAccessorDecorator = new MandatoryAccessorDecorator(glex);
    List<ASTCDMethod> methods = mandatoryAccessorDecorator.decorate(attribute);

    assertEquals(1, methods.size());
    ASTCDMethod method = getMethodBy("getA", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetMethodBoolean() {
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), String.class, "a");
    MandatoryAccessorDecorator mandatoryAccessorDecorator = new MandatoryAccessorDecorator(glex);
    List<ASTCDMethod> methods = mandatoryAccessorDecorator.decorate(attribute);

    assertEquals(1, methods.size());
    ASTCDMethod method = getMethodBy("getA", methods);
    assertTrue(method.getCDParameterList().isEmpty());
    assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
