/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import org.junit.BeforeClass;

import java.nio.file.Paths;

public class ConstantTest {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;

  @BeforeClass
  public static void setup(){
    Grammar_WithConceptsMill.init();
  }
  
  public ConstantTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/ConstantsGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
  }
}
