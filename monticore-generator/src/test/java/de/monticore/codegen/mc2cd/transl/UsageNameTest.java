/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import com.google.common.collect.Iterables;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class UsageNameTest {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;
  
  public UsageNameTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/UsageNameGrammar.mc4")).get();
    astA = TestHelper.getCDClasss(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClasss(cdCompilationUnit, "ASTB").get();
  }
  
  @Test
  public void testNonTerminal() {
    ASTCDAttribute cdAttribute = Iterables.getOnlyElement(astA.getCDAttributesList());
    assertEquals("nonTerminalUsageName", cdAttribute.getName());
  }
  
  @Test
  public void testConstant() {
    ASTCDAttribute cdAttribute = Iterables.getOnlyElement(astB.getCDAttributesList());
    assertEquals("constantUsageName", cdAttribute.getName());
  }
}
