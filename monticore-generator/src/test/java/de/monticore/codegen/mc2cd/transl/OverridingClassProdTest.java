/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OverridingClassProdTest {
  
  private ASTCDClass astX;
  
  public OverridingClassProdTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/OverridingClassProdGrammar.mc4")).get();
    astX = TestHelper.getCDClass(cdCompilationUnit, "ASTX").get();
  }
  
  /**
   * Checks that the production "X" overriding "X" in a supergrammar results in sub.ASTX having
   * super.ASTX as a superclass
   */
  @Test
  public void testOverride() {
    assertTrue(astX.isPresentSuperclass());
    String name = typeToString(astX.getSuperclass());
    assertEquals("mc2cdtransformation.Supergrammar.ASTX", name);
  }
}
