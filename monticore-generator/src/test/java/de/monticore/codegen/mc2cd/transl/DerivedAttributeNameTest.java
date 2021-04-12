/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.transl;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TestHelper;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.*;

public class DerivedAttributeNameTest {

  private ASTCDCompilationUnit compilationUnit;

  @BeforeClass
  public static void setup(){
    GrammarFamilyMill.init();
  }

  @Before
  public void setUp() {
    this.compilationUnit = TestHelper.parseAndTransform(Paths
        .get("src/test/resources/mc2cdtransformation/DerivedAttributeNameGrammar.mc4")).get();
  }

  protected boolean hasDerivedAttributeName(ASTCDAttribute astcdAttribute) {
    return astcdAttribute.isPresentModifier() && astcdAttribute.getModifier().isPresentStereotype()
        && astcdAttribute.getModifier().getStereotype().sizeValues() > 0 &&
        astcdAttribute.getModifier().getStereotype().getValuesList()
            .stream()
            .anyMatch(v -> v.getName().equals(MC2CDStereotypes.DERIVED_ATTRIBUTE_NAME.toString()));
  }

  @Test
  public void testFoo() {
    Optional<ASTCDClass> fooClass = TestHelper.getCDClass(compilationUnit, "ASTFoo");
    assertTrue(fooClass.isPresent());
    assertEquals(1, fooClass.get().getCDAttributeList().size());
    assertEquals("foo", fooClass.get().getCDAttributeList().get(0).getName());
    assertFalse(hasDerivedAttributeName(fooClass.get().getCDAttributeList().get(0)));
  }

  @Test
  public void testBar() {
    Optional<ASTCDClass> bar = TestHelper.getCDClass(compilationUnit, "ASTBar");
    assertTrue(bar.isPresent());
    assertEquals(2, bar.get().getCDAttributeList().size());
    assertEquals("abc", bar.get().getCDAttributeList().get(0).getName());
    assertFalse(hasDerivedAttributeName(bar.get().getCDAttributeList().get(0)));
    assertEquals("d", bar.get().getCDAttributeList().get(1).getName());
    assertTrue(hasDerivedAttributeName(bar.get().getCDAttributeList().get(1)));
  }

  @Test
  public void testBlub() {
    Optional<ASTCDClass> blub = TestHelper.getCDClass(compilationUnit, "ASTBlub");
    assertTrue(blub.isPresent());
    assertEquals(4, blub.get().getCDAttributeList().size());
    assertEquals("foo", blub.get().getCDAttributeList().get(0).getName());
    assertTrue(hasDerivedAttributeName(blub.get().getCDAttributeList().get(0)));
    assertEquals("bar2", blub.get().getCDAttributeList().get(1).getName());
    assertFalse(hasDerivedAttributeName(blub.get().getCDAttributeList().get(1)));
    assertEquals("fooOpt", blub.get().getCDAttributeList().get(2).getName());
    assertFalse(hasDerivedAttributeName(blub.get().getCDAttributeList().get(2)));
    assertEquals("efg", blub.get().getCDAttributeList().get(3).getName());
    assertFalse(hasDerivedAttributeName(blub.get().getCDAttributeList().get(3)));
  }

  @Test
  public void testTest() {
    Optional<ASTCDClass> test = TestHelper.getCDClass(compilationUnit, "ASTTest");
    assertTrue(test.isPresent());
    assertEquals(4, test.get().getCDAttributeList().size());
    assertEquals("blub", test.get().getCDAttributeList().get(0).getName());
    assertTrue(hasDerivedAttributeName(test.get().getCDAttributeList().get(0)));
    assertEquals("faa", test.get().getCDAttributeList().get(1).getName());
    assertFalse(hasDerivedAttributeName(test.get().getCDAttributeList().get(1)));
    assertEquals("bar", test.get().getCDAttributeList().get(2).getName());
    assertTrue(hasDerivedAttributeName(test.get().getCDAttributeList().get(2)));
    assertEquals("k", test.get().getCDAttributeList().get(3).getName());
    assertFalse(hasDerivedAttributeName(test.get().getCDAttributeList().get(3)));
  }
}
