/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import com.google.common.collect.Iterables;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TerminalWithUsageNameTest {
  
  private ASTCDClass astA;

  @Before
  public void setup(){
    GrammarFamilyMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  public TerminalWithUsageNameTest() {
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
    
    assertEquals("testname", cdAttribute.getName());
    assertEquals("String", TransformationHelper.typeToString(cdAttribute.getMCType()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
