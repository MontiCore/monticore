/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.TestHelper;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorAssert.assertInt;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getAttributeBy;
import static org.junit.Assert.assertEquals;

/**
 * this test checks the addition of attributes with astrules
 */
public final class AstRuleTest {

  private final ASTCDClass astC;

  private final ASTCDClass impl;

  @BeforeClass
  public static void setup() {
    LogStub.init();
    LogStub.enableFailQuick(false);
  }

  public AstRuleTest() {
    ASTCDCompilationUnit cdCompilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/AstRule.mc4")).get();
    astC = TestHelper.getCDClass(cdCompilationUnit, "ASTC").get();
    impl = TestHelper.getCDClass(cdCompilationUnit, "ASTImpl").get();
  }

  @Test
  public void testAstRuleAddedAttribute() {
    assertEquals(1, astC.sizeCDAttributes());
    assertEquals("dimensions", astC.getCDAttribute(0).getName());
    assertInt(astC.getCDAttribute(0).getMCType());
  }

  @Test
  public void testAstRuleDoubleInheritance() {
    // attributes from super interfaces are inherited
    assertEquals(2, impl.getCDAttributeList().size());

    ASTCDAttribute varName = getAttributeBy("varName", impl);
    assertDeepEquals("varType", varName.getMCType());

    ASTCDAttribute varName2 = getAttributeBy("varName2", impl);
    assertDeepEquals("varType2", varName2.getMCType());
  }

}
