/*(c) https://github.com/MontiCore/monticore*/

package de.monticore.umlmodifier;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.umlmodifier._ast.ASTModifier;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestWithMCLanguage(UMLModifierMill.class)
public class ModifierPrettyPrintTest {
  
  static Stream<Arguments> testLongFormsIndividualParams() {
    return Stream.of(
        Arguments.of("public", UMLModifierMill.modifierBuilder().PUBLIC().build()),
        Arguments.of("private", UMLModifierMill.modifierBuilder().PRIVATE().build()),
        Arguments.of("protected", UMLModifierMill.modifierBuilder().PROTECTED().build()),
        Arguments.of("final", UMLModifierMill.modifierBuilder().FINAL().build()),
        Arguments.of("abstract", UMLModifierMill.modifierBuilder().ABSTRACT().build()),
        Arguments.of("local", UMLModifierMill.modifierBuilder().LOCAL().build()),
        Arguments.of("derived", UMLModifierMill.modifierBuilder().DERIVED().build()),
        Arguments.of("readonly", UMLModifierMill.modifierBuilder().READONLY().build()),
        Arguments.of("static", UMLModifierMill.modifierBuilder().STATIC().build()));
  }
  
  @ParameterizedTest
  @MethodSource("testLongFormsIndividualParams")
  public void testLongFormsIndividual(String str, ASTModifier modifier) {
    assertEquals(str, UMLModifierMill.prettyPrint(modifier, false));
  }
  
  static Stream<Arguments> testLongParams() {
    return Stream.of(
        Arguments.of("public static", UMLModifierMill.modifierBuilder().PUBLIC().STATIC().build()),
        Arguments.of("abstract readonly",
            UMLModifierMill.modifierBuilder().ABSTRACT().READONLY().build()),
        Arguments.of("protected static",
            UMLModifierMill.modifierBuilder().PROTECTED().STATIC().build()));
  }
  
  @ParameterizedTest
  @MethodSource("testLongParams")
  public void testLong(String str, ASTModifier modifier) {
    assertEquals(str, UMLModifierMill.prettyPrint(modifier, false));
  }
  
}
