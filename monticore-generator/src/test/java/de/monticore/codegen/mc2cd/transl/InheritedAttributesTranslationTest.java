/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.codegen.mc2cd.TranslationTestCase;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Optional;

import static de.monticore.codegen.cd2java.DecoratorAssert.assertDeepEquals;
import static de.monticore.codegen.cd2java.DecoratorTestUtil.getClassBy;

public class InheritedAttributesTranslationTest extends TranslationTestCase {

  private ASTCDClass astASuper;

  private ASTCDClass astB;

  private ASTCDClass astESuper;

  private ASTCDClass astC;

  private ASTCDClass astF;

  private ASTCDClass astASub;

  private ASTCDClass astESub;

  @BeforeEach
  public void setupInheritedAttributesTranslationTest() {
    Optional<ASTCDCompilationUnit> cdCompilationUnitSuper = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/SuperInheritedAttributesGrammar.mc4"));
    Assertions.assertTrue(cdCompilationUnitSuper.isPresent());

    //get classes from super grammar
    astASuper = getClassBy("ASTA", cdCompilationUnitSuper.get());
    astB = getClassBy("ASTB", cdCompilationUnitSuper.get());
    astESuper = getClassBy("ASTE", cdCompilationUnitSuper.get());
    astC = getClassBy("ASTC", cdCompilationUnitSuper.get());
    astF = getClassBy("ASTF", cdCompilationUnitSuper.get());

    Optional<ASTCDCompilationUnit> cdCompilationUnitSub = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/SubInheritedAttributesGrammar.mc4"));
    Assertions.assertTrue(cdCompilationUnitSub.isPresent());

    //get classes from sub grammar
    astASub = getClassBy("ASTA", cdCompilationUnitSub.get());
    astESub = getClassBy("ASTE", cdCompilationUnitSub.get());
  }

  @Test
  public void testASuper() {
    Assertions.assertTrue(astASuper.getCDAttributeList().stream().noneMatch(this::hasInheritedStereotype));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testESuper() {
    Assertions.assertTrue(astESuper.getCDAttributeList().stream().noneMatch(this::hasInheritedStereotype));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBSuper() {
    Assertions.assertTrue(astB.getCDAttributeList().stream().allMatch(this::hasInheritedStereotype));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCSuper() {
    for (ASTCDAttribute astcdAttribute : astC.getCDAttributeList()) {
      if (!astcdAttribute.getName().equals("name2")) {
        Assertions.assertTrue(hasInheritedStereotype(astcdAttribute));
      } else {
        Assertions.assertFalse(hasInheritedStereotype(astcdAttribute));
      }
    }
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testFSuper() {
    for (ASTCDAttribute astcdAttribute : astF.getCDAttributeList()) {
      if (!astcdAttribute.getName().equals("name2")) {
        Assertions.assertTrue(hasInheritedStereotype(astcdAttribute));
      } else {
        Assertions.assertFalse(hasInheritedStereotype(astcdAttribute));
      }
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testASub() {
    Assertions.assertTrue(astASub.getCDAttributeList().stream().allMatch(this::hasInheritedStereotype));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testESub() {
    Assertions.assertEquals(1, astESub.getCDAttributeList().size());
    ASTCDAttribute name2Attr = astESub.getCDAttributeList().get(0);
    Assertions.assertEquals("name2", name2Attr.getName());
    assertDeepEquals(String.class, name2Attr.getMCType());
    Assertions.assertFalse(hasInheritedStereotype(name2Attr));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
