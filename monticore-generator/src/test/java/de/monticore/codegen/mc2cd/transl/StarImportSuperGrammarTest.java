/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StarImportSuperGrammarTest {
  
  private ASTCDCompilationUnit cdCompilationUnit;
  
  public StarImportSuperGrammarTest() {
    cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/OverridingClassProdGrammar.mc4")).get();
  }
  
  @Test
  public void testStarImport() {
    ASTImportStatement importStatement = cdCompilationUnit.getImportStatementList().get(0);
    assertTrue(importStatement.isStar());
    assertEquals("mc2cdtransformation", importStatement.getImportList().get(0));
    assertEquals("Supergrammar", importStatement.getImportList().get(1));
  }
}
