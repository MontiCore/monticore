/*(c) https://github.com/MontiCore/monticore*/

package de.monticore.umlmodifier;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ModifierPrettyPrintTest {

  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
    UMLModifierMill.reset();
    UMLModifierMill.init();
    Log.clearFindings();
  }

  @Test
  public void testLongFormsIndividual() {
    Assertions.assertEquals("public", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().PUBLIC().build(), false));
    Assertions.assertEquals("private", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().PRIVATE().build(), false));
    Assertions.assertEquals("protected", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().PROTECTED().build(), false));
    Assertions.assertEquals("final", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().FINAL().build(), false));
    Assertions.assertEquals("abstract", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().ABSTRACT().build(), false));
    Assertions.assertEquals("local", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().LOCAL().build(), false));
    Assertions.assertEquals("derived", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().DERIVED().build(), false));
    Assertions.assertEquals("readonly", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().READONLY().build(), false));
    Assertions.assertEquals("static", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().STATIC().build(), false));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLong() {
    Assertions.assertEquals("public static", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().PUBLIC().STATIC().build(), false));
    Assertions.assertEquals("abstract readonly", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().ABSTRACT().READONLY().build(), false));
    Assertions.assertEquals("protected static", UMLModifierMill.prettyPrint(UMLModifierMill.modifierBuilder().PROTECTED().STATIC().build(), false));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
