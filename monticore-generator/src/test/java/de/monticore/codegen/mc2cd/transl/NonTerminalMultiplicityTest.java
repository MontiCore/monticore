/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for the proper transformation of NonTerminals to corresponding ASTCDAttributes
 * 
 */
public class NonTerminalMultiplicityTest extends TranslationTestCase {

  private ASTCDClass astA;

  private ASTCDClass astB;

  private ASTCDClass astC;

  private ASTCDClass astD;

  private ASTCDClass astE;

  private ASTCDClass astF;

  private ASTCDClass astG;

  private ASTCDClass astH;

  private ASTCDClass astJ;

  @Before
  public void setupNonTerminalMultiplicityTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/NonTerminalMultiplicityGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
    astC = TestHelper.getCDClass(cdCompilationUnit, "ASTC").get();
    astD = TestHelper.getCDClass(cdCompilationUnit, "ASTD").get();
    astE = TestHelper.getCDClass(cdCompilationUnit, "ASTE").get();
    astF = TestHelper.getCDClass(cdCompilationUnit, "ASTF").get();
    astG = TestHelper.getCDClass(cdCompilationUnit, "ASTG").get();
    astH = TestHelper.getCDClass(cdCompilationUnit, "ASTH").get();
    astJ = TestHelper.getCDClass(cdCompilationUnit, "ASTJ").get();
  }
  
  /**
   * Tests that the production "A = X;" generates a reference with the variable name X in the class
   * ASTA.
   */
  @Test
  public void testNonTerminalName() {
    List<ASTCDAttribute> attributes = astA.getCDAttributeList();
    assertEquals("x", attributes.get(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Tests that the production "A = X*;" results in the class ASTA having a field reference to
   * ASTXList.
   */
  @Test
  public void testStarMultiplicity() {
    List<ASTCDAttribute> attributes = astA.getCDAttributeList();
    assertTrue(TestHelper.isListOfType(attributes.get(0).getMCType(),
        "mc2cdtransformation.NonTerminalMultiplicityGrammar.ASTX"));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Tests that the production "B = (X)*;" results in the class ASTB having a field reference to
   * ASTXList.
   */
  @Test
  public void testParenthesizedStarMultiplicity() {
    List<ASTCDAttribute> attributes = astB.getCDAttributeList();
    assertTrue(TestHelper.isListOfType(attributes.get(0).getMCType(),
        "mc2cdtransformation.NonTerminalMultiplicityGrammar.ASTX"));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Tests that the production "C = X+;" results in the class ASTC having a field reference to
   * ASTXList.
   */
  @Test
  public void testPlusMultiplicity() {
    List<ASTCDAttribute> attributes = astC.getCDAttributeList();
    assertTrue(TestHelper.isListOfType(attributes.get(0).getMCType(),
        "mc2cdtransformation.NonTerminalMultiplicityGrammar.ASTX"));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Tests that the production "D = (X)+;" results in the class ASTD having a field reference to
   * ASTXList.
   */
  @Test
  public void testParenthesizedPlusMultiplicity() {
    List<ASTCDAttribute> attributes = astD.getCDAttributeList();
    assertTrue(TestHelper.isListOfType(attributes.get(0).getMCType(),
        "mc2cdtransformation.NonTerminalMultiplicityGrammar.ASTX"));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Tests that the production "E = X?;" results in the class ASTE having a field reference to
   * Optional<ASTX>.
   */
  @Test
  public void testOptionalMultiplicity() {
    List<ASTCDAttribute> attributes = astE.getCDAttributeList();
    String name = typeToString(attributes.get(0).getMCType());
    assertEquals("Optional", name);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Tests that the production "F = (X)?;" results in the class ASTF having a field reference to
   * Optional<ASTX>.
   */
  @Test
  public void testParenthesizedOptionalMultiplicity() {
    List<ASTCDAttribute> attributes = astF.getCDAttributeList();
    String name = typeToString(attributes.get(0).getMCType());
    assertEquals("Optional", name);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Tests that the production "G = (X) (X);" results in the class ASTG having a field reference to
   * ASTXList.
   */
  @Test
  public void testDuplicateMultiplicity() {
    List<ASTCDAttribute> attributes = astG.getCDAttributeList();
    assertTrue(TestHelper.isListOfType(attributes.get(0).getMCType(),
        "mc2cdtransformation.NonTerminalMultiplicityGrammar.ASTX"));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Tests that the production "H = X | Y;" results in the class ASTH having field references to
   * Optional<ASTX> and Optional<ASTY>.
   */
  @Test
  public void testAlternative() {
    List<ASTCDAttribute> attributes = astH.getCDAttributeList();
    
    String xTypeName = typeToString(attributes.get(0).getMCType());
    assertEquals("Optional", xTypeName);
    
    String yTypeName = typeToString(attributes.get(1).getMCType());
    assertEquals("Optional", yTypeName);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  /**
   * Tests that the production "J = X | X;" results in the class ASTJ having only a single reference to
   * Optional<ASTX> .
   */
  @Test
  public void testTwinAlternative() {
    List<ASTCDAttribute> attributes = astJ.getCDAttributeList();
    assertEquals(1, attributes.size());
    String xTypeName = typeToString(attributes.get(0).getMCType());
    assertEquals("Optional", xTypeName);
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
