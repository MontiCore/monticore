/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class LeftRecursiveTranslationTest {

  private ASTCDCompilationUnit leftRecursive;

  @BeforeClass
  public static void setup(){
    Log.init();
    Log.enableFailQuick(false);
    GrammarFamilyMill.init();
  }

  @Before
  public void setUp() {
    leftRecursive = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/LeftRecursiveProd.mc4")).get();
  }

  @Test
  public void testLeftRecursiveProd(){
    ASTCDClass a = getClassBy("ASTA", leftRecursive);
    assertTrue(a.getModifier().isPresentStereotype());
    assertEquals(1, a.getModifier().getStereotype().sizeValues());
    assertEquals("left_recursive", a.getModifier().getStereotype().getValues(0).getName());
    assertFalse(a.getModifier().getStereotype().getValues(0).isPresentText());
  }

}
