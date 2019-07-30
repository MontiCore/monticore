/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import org.junit.Test;

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
public class AttributeInASTMultiplicityTest {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;
  
  private ASTCDClass astC;
  
  public AttributeInASTMultiplicityTest() {
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
    assertTrue(TestHelper.isListOfType(attributes.get(0).getMCType(),
        "mc2cdtransformation.AttributeInASTMultiplicityGrammar.ASTX"));
    /*
    String name = typeToString(attributes.get(0).getMCType());
    assertEquals("java.util.List", name);
    assertTrue(attributes.get(0).getMCType() instanceof ASTSimpleReferenceType);
    ASTSimpleReferenceType type = (ASTSimpleReferenceType) attributes.get(0).getMCType();
    assertTrue(type.getTypeArguments().isPresent());
    assertEquals(1, type.getTypeArguments().get().getTypeArguments().size());
    assertEquals("mc2cdtransformation.AttributeInASTMultiplicityGrammar.ASTX",
        ((ASTSimpleReferenceType) type.getTypeArguments().get().getTypeArguments().get(0))
            .getNames().get(0));*/
  }
  
  /**
   * Tests that the ASTRule "ast B = Y min=0 max=1;" results in the class ASTB
   * having a field reference to Optional<ASTY>.
   */
  @Test
  public void testOptionalCardinality() {
    List<ASTCDAttribute> attributes = astB.getCDAttributeList();
    String name = typeToString(attributes.get(0).getMCType());
    assertEquals("Optional", name);
  }
  
  @Test
  public void testOneCardinality() {
    List<ASTCDAttribute> attributes = astC.getCDAttributeList();
    String name = typeToString(attributes.get(0).getMCType());
    assertEquals("mc2cdtransformation.AttributeInASTMultiplicityGrammar.ASTZ", name);
  }
}
