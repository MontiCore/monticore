/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class EnumProdTest {

  private final ASTCDCompilationUnit cdCompilationUnit;

  @BeforeClass
  public static void setup(){
    GrammarFamilyMill.init();
  }

  public EnumProdTest() {
    Optional<ASTCDCompilationUnit> cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/EnumsGrammar.mc4"));
    this.cdCompilationUnit = cdCompilationUnit.get();
  }

  @Test
  public void testExist() {
    assertEquals(4, cdCompilationUnit.getCDDefinition().getCDEnumsList().size());
  }
}
