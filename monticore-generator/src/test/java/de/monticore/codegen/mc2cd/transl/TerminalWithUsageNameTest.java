/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import com.google.common.collect.Iterables;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.ASTNodes;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class TerminalWithUsageNameTest {
  
  private ASTCDClass astA;
  
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
    ASTCDAttribute cdAttribute = Iterables.getOnlyElement(ASTNodes.getSuccessors(astA,
        ASTCDAttribute.class));
    
    assertEquals("testname", cdAttribute.getName());
    assertEquals("String", TransformationHelper.typeToString(cdAttribute.getType()));
  }
}
