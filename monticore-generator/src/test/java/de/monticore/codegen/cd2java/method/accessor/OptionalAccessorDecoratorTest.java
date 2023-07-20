/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.method.accessor;

import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecoratorTestCase;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.accessor.OptionalAccessorDecorator;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertBoolean;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getMethodBy;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class OptionalAccessorDecoratorTest extends DecoratorTestCase {

  private List<ASTCDMethod> methods;

  @Before
  public void setup() {
    // dummy cd needed for a good generated error Code
    ASTCDCompilationUnit cd = this.parse("de", "monticore", "codegen", "symboltable", "Automaton");

    ASTMCType optType = MCTypeFacade.getInstance().createOptionalTypeOf(String.class);
    ASTCDAttribute attribute = CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), optType, "a");
    OptionalAccessorDecorator optionalAccessorDecorator = new OptionalAccessorDecorator(glex, new ASTService(cd));
    this.methods = optionalAccessorDecorator.decorate(attribute);
  }

  @Test
  public void testMethods() {
    assertEquals(2, methods.size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testGetMethod() {
    ASTCDMethod method = getMethodBy("getA", this.methods);
    assertTrue(method.getCDParameterList().isEmpty());
    Assert.assertTrue(method.getMCReturnType().isPresentMCType());
    assertDeepEquals(String.class, method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIsPresentMethod() {
    ASTCDMethod method = getMethodBy("isPresentA", this.methods);
    Assert.assertTrue(method.getMCReturnType().isPresentMCType());
    assertBoolean(method.getMCReturnType().getMCType());
    assertDeepEquals(PUBLIC, method.getModifier());
    assertTrue(method.getCDParameterList().isEmpty());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
