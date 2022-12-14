/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.spi.LocaleServiceProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests that attributes that are redefined in ASTRules correctly override their counterparts in the
 * corresponding ClassProds.
 */
public class AttributeInASTOverridingTest {
  
  private ASTCDClass astA;
  
  private ASTCDClass astB;

  @Before
  public void setup(){
    GrammarFamilyMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
  }

  public AttributeInASTOverridingTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AttributeInASTOverridingGrammar.mc4")).get();
    astA = TestHelper.getCDClass(cdCompilationUnit, "ASTA").get();
    astB = TestHelper.getCDClass(cdCompilationUnit, "ASTB").get();
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAttributeOverridden() {
    List<ASTCDAttribute> attributes = astA.getCDAttributeList();
    assertEquals(1, attributes.size());
    assertEquals("mc2cdtransformation.AttributeInASTOverridingGrammar.ASTY",
        TransformationHelper.typeToString(attributes.get(0).getMCType()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testAttributeNotOverridden() {
    List<ASTCDAttribute> attributes = astB.getCDAttributeList();
    assertEquals(2, attributes.size());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
