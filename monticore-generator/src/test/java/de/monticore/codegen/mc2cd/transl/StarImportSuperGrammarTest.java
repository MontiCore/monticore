/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StarImportSuperGrammarTest extends TranslationTestCase {
  
  private ASTCDCompilationUnit cdCompilationUnit;

  @BeforeEach
  public void setupStarImportSuperGrammarTest() {
    cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/OverridingClassProdGrammar.mc4")).get();
  }
  
  @Test
  public void testStarImport() {
    ASTMCImportStatement importStatement = cdCompilationUnit.getMCImportStatementList().get(0);
    assertTrue(importStatement.isStar());
    assertEquals("mc2cdtransformation.Supergrammar", importStatement.getQName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
