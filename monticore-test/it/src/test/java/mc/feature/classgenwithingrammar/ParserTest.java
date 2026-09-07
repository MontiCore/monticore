/* (c) https://github.com/MontiCore/monticore */

package mc.feature.classgenwithingrammar;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.classgenwithingrammar.type.TypeMill;
import mc.feature.classgenwithingrammar.type._parser.TypeParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TypeMill.class)
public class ParserTest {
  
  @ParameterizedTest
  @ValueSource(strings = {
      // Tests that String is ok
      "Hallo Hallo Hallo "
  })
  public void testTypeValid(String value) throws IOException {
    boolean hasError = parse(value);
    assertFalse(hasError);
  }
  
  static Stream<Arguments> testTypeInvalidArgs() {
    return Stream.of(
        // Test that one Welt is too much
        Arguments.of("Hallo Hallo Hallo Welt ", "Expected EOF but found token"),
        // Test that the last Hallo is too much
        Arguments.of("Hallo Hallo Hallo Hallo ",
            "0xA7018x298 Invalid maximal occurence for sub in rule Type : Should be 3 but is 4!"),
        // Tests that one hallo is missing
        Arguments.of("Hallo ",
            "0xA7017x298 Invalid minimal occurence for sub in rule Type : Should be 2 but is 1!")
    );
  }
  
  @ParameterizedTest
  @MethodSource("testTypeInvalidArgs")
  public void testTypeInvalid(String value, String expectedError) throws IOException {
    boolean hasError = parse(value);
    assertTrue(hasError);
    MCAssertions.assertHasFindingStartingWith(expectedError);
  }
  
  @ParameterizedTest
  @ValueSource(strings = {
      // Tests that String is ok
      "Hall Hall Hall "
  })
  public void testType2Valid(String value) throws IOException {
    boolean hasError = parse2(value);
    assertFalse(hasError);
  }
  
  static Stream<Arguments> testType2InvalidArgs() {
    return Stream.of(
        // Test that one Welt is too much
        Arguments.of("Hall Hall Hall \"Wel\" ", "Expected EOF but found token"),
        // Test that too many Hallo and Welt are detected in one go
        Arguments.of("Hall Hall Hall Hall \"Wel\" ",
            "0xA7018x288 Invalid maximal occurence for name in rule Type2 : Should be 3 but is 4!")
    );
  }
  
  @ParameterizedTest
  @MethodSource("testType2InvalidArgs")
  public void testType2Invalid(String value, String expectedError) throws IOException {
    boolean hasError = parse2(value);
    assertTrue(hasError);
    MCAssertions.assertHasFindingStartingWith(expectedError);
  }

  private boolean parse(String input) throws IOException {
    TypeParser parser = TypeMill.parser();
            
    parser.parse_StringType(input);
    return parser.hasErrors();
  }
  
  private boolean parse2(String input) throws IOException {
    TypeParser parser = TypeMill.parser();
    
    parser.parse_StringType2(input);
    return parser.hasErrors();
  }
}
