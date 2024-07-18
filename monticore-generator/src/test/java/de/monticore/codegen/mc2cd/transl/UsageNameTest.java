/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import com.google.common.collect.Iterables;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UsageNameTest extends TranslationTestCase {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;

  @BeforeEach
   public void setupUsageNameTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/UsageNameGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
  }
  
  @Test
  public void testNonTerminal() {
    ASTCDAttribute cdAttribute = Iterables.getOnlyElement(astA.getCDAttributeList());
    Assertions.assertEquals("nonTerminalUsageName", cdAttribute.getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testConstant() {
    ASTCDAttribute cdAttribute = Iterables.getOnlyElement(astB.getCDAttributeList());
    Assertions.assertEquals("constantUsageName", cdAttribute.getName());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
