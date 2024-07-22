/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import com.google.common.collect.Iterables;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TerminalWithUsageNameTest extends TranslationTestCase {
  
  private ASTCDClass astA;

  @BeforeEach
  public void setupTerminalWithUsageNameTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/TerminalWithUsageNameGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
  }
  
  /**
   * Checks that the terminal testname : "literal" results in a reference to String with the name
   * "testname".
   */
  @Test
  public void testTerminalUsageName() {
    ASTCDAttribute cdAttribute = Iterables.getOnlyElement(astA.getCDAttributeList());
    
    Assertions.assertEquals("testname", cdAttribute.getName());
    Assertions.assertEquals("String", TransformationHelper.typeToString(cdAttribute.getMCType()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
