/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    Assertions.assertTrue(importStatement.isStar());
    Assertions.assertEquals("mc2cdtransformation.Supergrammar", importStatement.getQName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
