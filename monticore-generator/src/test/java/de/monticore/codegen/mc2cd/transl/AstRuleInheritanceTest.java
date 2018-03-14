/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.junit.Test;

import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

/**
 * @author Sebastian Oberhoff
 */
public final class AstRuleInheritanceTest {

  private final ASTCDClass astC;

  private final ASTCDClass impl;

  public AstRuleInheritanceTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AstRuleInheritance.mc4")).get();
    astC = TestHelper.getCDClass(cdCompilationUnit, "ASTC").get();
    impl = TestHelper.getCDClass(cdCompilationUnit, "ASTImpl").get();
  }

  @Test
  public void testAstRuleInheritance() {
    assertEquals("dimensions", astC.getCDAttributeList().get(0).getName());
  }

  @Test
  public void testAstRuleDoubleInheritance() {
    assertEquals(2, impl.getCDAttributeList().size());
  }
  
}
