/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import com.google.common.collect.Iterables;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UsageNameTest {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;

  @Before
  public void setup(){
    GrammarFamilyMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  public UsageNameTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/UsageNameGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
  }
  
  @Test
  public void testNonTerminal() {
    ASTCDAttribute cdAttribute = Iterables.getOnlyElement(astA.getCDAttributeList());
    assertEquals("nonTerminalUsageName", cdAttribute.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testConstant() {
    ASTCDAttribute cdAttribute = Iterables.getOnlyElement(astB.getCDAttributeList());
    assertEquals("constantUsageName", cdAttribute.getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
