/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OverridingClassProdTest extends TranslationTestCase {
  
  private ASTCDClass astX;

  @BeforeEach
  public void setupOverridingClassProdTest() {
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
    Assertions.assertTrue(astX.isPresentCDExtendUsage());
    String name = typeToString(astX.getCDExtendUsage().getSuperclass(0));
    Assertions.assertEquals("mc2cdtransformation.Supergrammar.ASTX", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
