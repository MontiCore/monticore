/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

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
import java.util.List;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for the proper transformation of AttributeInASTs to corresponding
 * CDAttributes
 *
 */
public class AttributeInASTMultiplicityTest extends TranslationTestCase {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;
  
  private ASTCDClass astC;

  @BeforeEach
  public void setupAttributeInASTMultiplicityTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AttributeInASTMultiplicityGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
    astC = TestHelper.getCDClass(cdCompilationUnit, "ASTC").get();
  }
  
  /**
   * Tests that the ASTRule "ast A = X*;" results in the class ASTA having a
   * field reference to ASTXList.
   */
  @Test
  public void testStarMultiplicity() {
    List<ASTCDAttribute> attributes = astA.getCDAttributeList();
    Assertions.assertTrue(TestHelper.isListOfType(attributes.get(0).getMCType(),
        "mc2cdtransformation.AttributeInASTMultiplicityGrammar.ASTX"));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Tests that the ASTRule "ast B = Y min=0 max=1;" results in the class ASTB
   * having a field reference to Optional<ASTY>.
   */
  @Test
  public void testOptionalCardinality() {
    List<ASTCDAttribute> attributes = astB.getCDAttributeList();
    String name = typeToString(attributes.get(0).getMCType());
    Assertions.assertEquals("Optional", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testOneCardinality() {
    List<ASTCDAttribute> attributes = astC.getCDAttributeList();
    String name = typeToString(attributes.get(0).getMCType());
    Assertions.assertEquals("mc2cdtransformation.AttributeInASTMultiplicityGrammar.ASTZ", name);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
