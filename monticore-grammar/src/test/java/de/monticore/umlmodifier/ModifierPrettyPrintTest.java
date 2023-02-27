/*(c) https://github.com/MontiCore/monticore*/

package de.monticore.umlmodifier;

import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModifierPrettyPrintTest {

  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
    UMLModifierMill.init();
    Log.clearFindings();
  }

  @Test
  public void testLongFormsIndividual() {
    assertEquals("public", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().PUBLIC().build(), false));
    assertEquals("private", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().PRIVATE().build(), false));
    assertEquals("protected", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().PROTECTED().build(), false));
    assertEquals("final", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().FINAL().build(), false));
    assertEquals("abstract", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().ABSTRACT().build(), false));
    assertEquals("local", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().LOCAL().build(), false));
    assertEquals("derived", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().DERIVED().build(), false));
    assertEquals("readonly", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().READONLY().build(), false));
    assertEquals("static", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().STATIC().build(), false));
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLong() {
    assertEquals("public static", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().PUBLIC().STATIC().build(), false));
    assertEquals("abstract readonly", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().ABSTRACT().READONLY().build(), false));
    assertEquals("protected static", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().PROTECTED().STATIC().build(), false));
    assertTrue(Log.getFindings().isEmpty());
  }

}
