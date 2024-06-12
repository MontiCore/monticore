/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class InheritedAttributesTranslationTest extends TranslationTestCase {

  private ASTCDClass astASuper;

  private ASTCDClass astB;

  private ASTCDClass astESuper;

  private ASTCDClass astC;

  private ASTCDClass astF;

  private ASTCDClass astASub;

  private ASTCDClass astESub;

  @Before
  public void setupInheritedAttributesTranslationTest() {
    Optional<ASTCDCompilationUnit> cdCompilationUnitSuper = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/SuperInheritedAttributesGrammar.mc4"));
    assertTrue(cdCompilationUnitSuper.isPresent());

    //get classes from super grammar
    astASuper = getClassBy("ASTA", cdCompilationUnitSuper.get());
    astB = getClassBy("ASTB", cdCompilationUnitSuper.get());
    astESuper = getClassBy("ASTE", cdCompilationUnitSuper.get());
    astC = getClassBy("ASTC", cdCompilationUnitSuper.get());
    astF = getClassBy("ASTF", cdCompilationUnitSuper.get());

    Optional<ASTCDCompilationUnit> cdCompilationUnitSub = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/SubInheritedAttributesGrammar.mc4"));
    assertTrue(cdCompilationUnitSub.isPresent());

    //get classes from sub grammar
    astASub = getClassBy("ASTA", cdCompilationUnitSub.get());
    astESub = getClassBy("ASTE", cdCompilationUnitSub.get());
  }

  @Test
  public void testASuper() {
    assertTrue(astASuper.getCDAttributeList().stream().noneMatch(this::hasInheritedStereotype));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testESuper() {
    assertTrue(astESuper.getCDAttributeList().stream().noneMatch(this::hasInheritedStereotype));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBSuper() {
    assertTrue(astB.getCDAttributeList().stream().allMatch(this::hasInheritedStereotype));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCSuper() {
    for (ASTCDAttribute astcdAttribute : astC.getCDAttributeList()) {
      if (!astcdAttribute.getName().equals("name2")) {
        assertTrue(hasInheritedStereotype(astcdAttribute));
      } else {
        assertFalse(hasInheritedStereotype(astcdAttribute));
      }
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFSuper() {
    for (ASTCDAttribute astcdAttribute : astF.getCDAttributeList()) {
      if (!astcdAttribute.getName().equals("name2")) {
        assertTrue(hasInheritedStereotype(astcdAttribute));
      } else {
        assertFalse(hasInheritedStereotype(astcdAttribute));
      }
    }
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASub() {
    assertTrue(astASub.getCDAttributeList().stream().allMatch(this::hasInheritedStereotype));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testESub() {
    assertEquals(1, astESub.getCDAttributeList().size());
    ASTCDAttribute name2Attr = astESub.getCDAttributeList().get(0);
    assertEquals("name2", name2Attr.getName());
    assertDeepEquals(String.class, name2Attr.getMCType());
    assertFalse(hasInheritedStereotype(name2Attr));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  private boolean hasInheritedStereotype(ASTCDAttribute astcdAttribute) {
    if (astcdAttribute.getModifier().isPresentStereotype() &&
        !astcdAttribute.getModifier().getStereotype().isEmptyValues()) {
      return astcdAttribute.getModifier().getStereotype().getValuesList()
          .stream()
          .anyMatch(value -> value.getName().equals(MC2CDStereotypes.INHERITED.toString()));
    }
    return false;
  }
}
