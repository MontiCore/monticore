/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import org.junit.Ignore;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests that attributes that are redefined in ASTRules correctly override their counterparts in the
 * corresponding ClassProds.
 */
@Ignore
public class AttributeInASTOverridingTest {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;
  
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
