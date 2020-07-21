/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;
import static org.junit.Assert.*;

public class InheritedAttributesTranslationTest {

  private ASTCDClass astASuper;

  private ASTCDClass astB;

  private ASTCDClass astESuper;

  private ASTCDClass astC;

  private ASTCDClass astF;

  private ASTCDClass astASub;

  private ASTCDClass astESub;

  @Before
  public void init() {
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
  }

  @Test
  public void testESuper() {
    assertTrue(astESuper.getCDAttributeList().stream().noneMatch(this::hasInheritedStereotype));
  }

  @Test
  public void testBSuper() {
    assertTrue(astB.getCDAttributeList().stream().allMatch(this::hasInheritedStereotype));
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
  }

  @Test
  public void testASub() {
    assertTrue(astASub.getCDAttributeList().stream().allMatch(this::hasInheritedStereotype));
  }

  @Test
  public void testESub() {
    assertEquals(1, astESub.sizeCDAttributes());
    ASTCDAttribute name2Attr = astESub.getCDAttribute(0);
    assertEquals("name2", name2Attr.getName());
    assertDeepEquals(String.class, name2Attr.getMCType());
    assertFalse(hasInheritedStereotype(name2Attr));
  }

  private boolean hasInheritedStereotype(ASTCDAttribute astcdAttribute) {
    if (astcdAttribute.isPresentModifier() && astcdAttribute.getModifier().isPresentStereotype() &&
        !astcdAttribute.getModifier().getStereotype().isEmptyValues()) {
      return astcdAttribute.getModifier().getStereotype().getValueList()
          .stream()
          .anyMatch(value -> value.getName().equals(MC2CDStereotypes.INHERITED.toString()));
    }
    return false;
  }
}
