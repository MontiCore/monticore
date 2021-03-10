/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests that attributes that are redefined in ASTRules correctly override their counterparts in the
 * corresponding ClassProds.
 */
public class AttributeInASTOverridingTest {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;

  @BeforeClass
  public static void setup(){
    GrammarFamilyMill.init();
  }

  public AttributeInASTOverridingTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AttributeInASTOverridingGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
  }

  @Test
  public void testAttributeOverridden() {
    List<ASTCDAttribute> attributes = astA.getCDAttributeList();
    assertEquals(1, attributes.size());
    assertEquals("mc2cdtransformation.AttributeInASTOverridingGrammar.ASTY",
        TransformationHelper.typeToString(attributes.get(0).getMCType()));
  }
  
  @Test
  public void testAttributeNotOverridden() {
    List<ASTCDAttribute> attributes = astB.getCDAttributeList();
    assertEquals(2, attributes.size());
  }
}
