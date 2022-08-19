/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.method.mutator;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.codegen.cd2java.methods.mutator.MandatoryMutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static de.monticore.codegen.cd2java.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MandatoryMutatorDecoratorTest {

  private final GlobalExtensionManagement glex = new GlobalExtensionManagement();

  private List<ASTCDMethod> methods;
  
  @Before
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void setup() {
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), String.class, "a");
    MandatoryMutatorDecorator mandatoryMutatorDecorator = new MandatoryMutatorDecorator(glex);
    this.methods = mandatoryMutatorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(1, methods.size());
  
    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
