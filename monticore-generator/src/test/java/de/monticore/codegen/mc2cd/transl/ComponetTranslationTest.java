/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ComponetTranslationTest {

  private ASTCDCompilationUnit componentCD;

  private ASTCDCompilationUnit nonComponentCD;

  @Before
  public void setUp() {
    componentCD = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AbstractProd.mc4")).get();
    nonComponentCD = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AstRule.mc4")).get();
  }

  @Test
  public void testIsComponent() {
    assertTrue(componentCD.getCDDefinition().isPresentModifier());
    assertTrue(componentCD.getCDDefinition().getModifier().isPresentStereotype());
    assertEquals(1, componentCD.getCDDefinition().getModifier().getStereotype().sizeValues());
    assertEquals("component",componentCD.getCDDefinition().getModifier().getStereotype().getValue(0).getName());
    assertFalse(componentCD.getCDDefinition().getModifier().getStereotype().getValue(0).isPresentValue());
  }

  @Test
  public void testIsNotComponent() {
    assertFalse(nonComponentCD.getCDDefinition().isPresentModifier());
  }

}
