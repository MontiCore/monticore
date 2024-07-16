/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests that attributes that are redefined in ASTRules correctly override their counterparts in the
 * corresponding ClassProds.
 */
public class AttributeInASTOverridingTest extends TranslationTestCase {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;

  @BeforeEach
  public void setupAttributeInASTOverridingTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AttributeInASTOverridingGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeOverridden() {
    List<ASTCDAttribute> attributes = astA.getCDAttributeList();
    Assertions.assertEquals(1, attributes.size());
    Assertions.assertEquals("mc2cdtransformation.AttributeInASTOverridingGrammar.ASTY", TransformationHelper.typeToString(attributes.get(0).getMCType()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testAttributeNotOverridden() {
    List<ASTCDAttribute> attributes = astB.getCDAttributeList();
    Assertions.assertEquals(2, attributes.size());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
