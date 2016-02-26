package de.monticore.codegen.mc2cd.transl;

import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * @author Sebastian Oberhoff
 */
public final class AstRuleInheritanceTest {

  private final ASTCDClass astC;

  public AstRuleInheritanceTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AstRuleInheritance.mc4")).get();
    astC = TestHelper.getCDClass(cdCompilationUnit, "ASTC").get();
  }

  @Test
  public void testAstRuleInheritance() {
    assertEquals("dimensions", astC.getCDAttributes().get(0).getName());
  }
  
}
