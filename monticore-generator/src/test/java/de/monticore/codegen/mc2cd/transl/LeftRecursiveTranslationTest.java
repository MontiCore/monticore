/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;

public class LeftRecursiveTranslationTest extends TranslationTestCase {

  private ASTCDCompilationUnit leftRecursive;

  @BeforeEach
  public void setUpLeftRecursiveTranslationTest() {
    leftRecursive = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/LeftRecursiveProd.mc4")).get();
  }

  @Test
  public void testLeftRecursiveProd(){
    ASTCDClass a = getClassBy("ASTA", leftRecursive);
    Assertions.assertTrue(a.getModifier().isPresentStereotype());
    Assertions.assertEquals(1, a.getModifier().getStereotype().sizeValues());
    Assertions.assertEquals("left_recursive", a.getModifier().getStereotype().getValues(0).getName());
    Assertions.assertFalse(a.getModifier().getStereotype().getValues(0).isPresentText());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
