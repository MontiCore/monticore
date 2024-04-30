/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnumProdTest extends TranslationTestCase {

  private ASTCDCompilationUnit cdCompilationUnit;

  @Before
  public void setupEnumProdTest() {
    Optional<ASTCDCompilationUnit> cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/EnumsGrammar.mc4"));
    this.cdCompilationUnit = cdCompilationUnit.get();
  }

  @Test
  public void testExist() {
    assertEquals(4, cdCompilationUnit.getCDDefinition().getCDEnumsList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
