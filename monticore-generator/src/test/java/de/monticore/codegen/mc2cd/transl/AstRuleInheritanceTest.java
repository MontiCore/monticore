/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public final class AstRuleInheritanceTest {

  private final ASTCDClass astC;

  private final ASTCDClass impl;

  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }

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
