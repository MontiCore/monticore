/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnumProdTest extends TranslationTestCase {

  private ASTCDCompilationUnit cdCompilationUnit;

  @BeforeEach
  public void setupEnumProdTest() {
    Optional<ASTCDCompilationUnit> cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/EnumsGrammar.mc4"));
    this.cdCompilationUnit = cdCompilationUnit.get();
  }

  @Test
  public void testExist() {
    Assertions.assertEquals(4, cdCompilationUnit.getCDDefinition().getCDEnumsList().size());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
