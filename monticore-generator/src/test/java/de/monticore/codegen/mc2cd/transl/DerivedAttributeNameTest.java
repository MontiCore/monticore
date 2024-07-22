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

public class DerivedAttributeNameTest extends TranslationTestCase {

  private ASTCDCompilationUnit compilationUnit;

  @BeforeEach
  public void setUp() {
    this.compilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/DerivedAttributeNameGrammar.mc4")).get();
  }

  protected boolean hasDerivedAttributeName(ASTCDAttribute astcdAttribute) {
    return astcdAttribute.getModifier().isPresentStereotype()
        && astcdAttribute.getModifier().getStereotype().sizeValues() > 0 &&
        astcdAttribute.getModifier().getStereotype().getValuesList()
            .stream()
            .anyMatch(v -> v.getName().equals(MC2CDStereotypes.DERIVED_ATTRIBUTE_NAME.toString()));
  }

  @Test
  public void testFoo() {
    Optional<ASTCDClass> fooClass = TestHelper.getCDClass(compilationUnit, "ASTFoo");
    Assertions.assertTrue(fooClass.isPresent());
    Assertions.assertEquals(1, fooClass.get().getCDAttributeList().size());
    Assertions.assertEquals("foo", fooClass.get().getCDAttributeList().get(0).getName());
    Assertions.assertFalse(hasDerivedAttributeName(fooClass.get().getCDAttributeList().get(0)));
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBar() {
    Optional<ASTCDClass> bar = TestHelper.getCDClass(compilationUnit, "ASTBar");
    Assertions.assertTrue(bar.isPresent());
    Assertions.assertEquals(2, bar.get().getCDAttributeList().size());
    Assertions.assertEquals("abc", bar.get().getCDAttributeList().get(0).getName());
    Assertions.assertFalse(hasDerivedAttributeName(bar.get().getCDAttributeList().get(0)));
    Assertions.assertEquals("d", bar.get().getCDAttributeList().get(1).getName());
    Assertions.assertTrue(hasDerivedAttributeName(bar.get().getCDAttributeList().get(1)));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBlub() {
    Optional<ASTCDClass> blub = TestHelper.getCDClass(compilationUnit, "ASTBlub");
    Assertions.assertTrue(blub.isPresent());
    Assertions.assertEquals(4, blub.get().getCDAttributeList().size());
    Assertions.assertEquals("foo", blub.get().getCDAttributeList().get(0).getName());
    Assertions.assertTrue(hasDerivedAttributeName(blub.get().getCDAttributeList().get(0)));
    Assertions.assertEquals("bar2", blub.get().getCDAttributeList().get(1).getName());
    Assertions.assertFalse(hasDerivedAttributeName(blub.get().getCDAttributeList().get(1)));
    Assertions.assertEquals("fooOpt", blub.get().getCDAttributeList().get(2).getName());
    Assertions.assertFalse(hasDerivedAttributeName(blub.get().getCDAttributeList().get(2)));
    Assertions.assertEquals("efg", blub.get().getCDAttributeList().get(3).getName());
    Assertions.assertFalse(hasDerivedAttributeName(blub.get().getCDAttributeList().get(3)));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTest() {
    Optional<ASTCDClass> test = TestHelper.getCDClass(compilationUnit, "ASTTest");
    Assertions.assertTrue(test.isPresent());
    Assertions.assertEquals(4, test.get().getCDAttributeList().size());
    Assertions.assertEquals("blub", test.get().getCDAttributeList().get(0).getName());
    Assertions.assertTrue(hasDerivedAttributeName(test.get().getCDAttributeList().get(0)));
    Assertions.assertEquals("faa", test.get().getCDAttributeList().get(1).getName());
    Assertions.assertFalse(hasDerivedAttributeName(test.get().getCDAttributeList().get(1)));
    Assertions.assertEquals("bar", test.get().getCDAttributeList().get(2).getName());
    Assertions.assertTrue(hasDerivedAttributeName(test.get().getCDAttributeList().get(2)));
    Assertions.assertEquals("k", test.get().getCDAttributeList().get(3).getName());
    Assertions.assertFalse(hasDerivedAttributeName(test.get().getCDAttributeList().get(3)));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
