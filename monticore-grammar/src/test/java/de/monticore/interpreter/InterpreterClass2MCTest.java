// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InterpreterClass2MCTest extends InterpreterTestAbstract {

  @ParameterizedTest(name = "[{index}] {0}")
  @MethodSource("nativeJavaCases")
  void testExpressionsNativeJava(String exprStr, Object expectedValue) {
    String modelStr = """
        import de.monticore.interpreter.util.AClass;
        AClass aClass = AClass();
        """
        + exprStr;
    assertEquals(expectedValue, interpretAndCast(modelStr));
  }

  static Stream<Arguments> nativeJavaCases() {
    return Stream.of(
        Arguments.of("aClass.var_boolean", false),
        Arguments.of("aClass.var_byte", 0),
        Arguments.of("aClass.var_short", 0),
        Arguments.of("aClass.var_char", 0),
        Arguments.of("aClass.var_int", 0),
        Arguments.of("aClass.var_long", 0),
        Arguments.of("aClass.var_float", 0.0),
        Arguments.of("aClass.var_double", 0.0),
        Arguments.of("aClass.var_Boolean", false),
        Arguments.of("aClass.var_Byte", 0),
        Arguments.of("aClass.var_Short", 0),
        Arguments.of("aClass.var_Character", 0),
        Arguments.of("aClass.var_Integer", 0),
        Arguments.of("aClass.var_Long", 0),
        Arguments.of("aClass.var_Float", 0.0),
        Arguments.of("aClass.var_Double", 0.0),

        Arguments.of("AClass.var_s_boolean", false),
        Arguments.of("AClass.var_s_byte", 0),
        Arguments.of("AClass.var_s_short", 0),
        Arguments.of("AClass.var_s_char", 0),
        Arguments.of("AClass.var_s_int", 0),
        Arguments.of("AClass.var_s_long", 0),
        Arguments.of("AClass.var_s_float", 0.0),
        Arguments.of("AClass.var_s_double", 0.0),
        Arguments.of("AClass.var_s_Boolean", false),
        Arguments.of("AClass.var_s_Byte", 0),
        Arguments.of("AClass.var_s_Short", 0),
        Arguments.of("AClass.var_s_Character", 0),
        Arguments.of("AClass.var_s_Integer", 0),
        Arguments.of("AClass.var_s_Long", 0),
        Arguments.of("AClass.var_s_Float", 0.0),
        Arguments.of("AClass.var_s_Double", 0.0),

        Arguments.of("aClass.get_var_boolean()", false),
        Arguments.of("aClass.get_var_byte()", 0),
        Arguments.of("aClass.get_var_short()", 0),
        Arguments.of("aClass.get_var_char()", 0),
        Arguments.of("aClass.get_var_int()", 0),
        Arguments.of("aClass.get_var_long()", 0),
        Arguments.of("aClass.get_var_float()", 0.0),
        Arguments.of("aClass.get_var_double()", 0.0),
        Arguments.of("aClass.get_var_Boolean()", false),
        Arguments.of("aClass.get_var_Byte()", 0),
        Arguments.of("aClass.get_var_Short()", 0),
        Arguments.of("aClass.get_var_Character()", 0),
        Arguments.of("aClass.get_var_Integer()", 0),
        Arguments.of("aClass.get_var_Long()", 0),
        Arguments.of("aClass.get_var_Float()", 0.0),
        Arguments.of("aClass.get_var_Double()", 0.0),

        Arguments.of("AClass.get_var_s_boolean()", false),
        Arguments.of("AClass.get_var_s_byte()", 0),
        Arguments.of("AClass.get_var_s_short()", 0),
        Arguments.of("AClass.get_var_s_char()", 0),
        Arguments.of("AClass.get_var_s_int()", 0),
        Arguments.of("AClass.get_var_s_long()", 0),
        Arguments.of("AClass.get_var_s_float()", 0.0),
        Arguments.of("AClass.get_var_s_double()", 0.0),
        Arguments.of("AClass.get_var_s_Boolean()", false),
        Arguments.of("AClass.get_var_s_Byte()", 0),
        Arguments.of("AClass.get_var_s_Short()", 0),
        Arguments.of("AClass.get_var_s_Character()", 0),
        Arguments.of("AClass.get_var_s_Integer()", 0),
        Arguments.of("AClass.get_var_s_Long()", 0),
        Arguments.of("AClass.get_var_s_Float()", 0.0),
        Arguments.of("AClass.get_var_s_Double()", 0.0),

        Arguments.of("aClass.set_var_boolean(true); aClass.var_boolean", true),
        Arguments.of("aClass.set_var_byte((byte)1); aClass.var_byte", 1),
        Arguments.of("aClass.set_var_short((short)2); aClass.var_short", 2),
        Arguments.of("aClass.set_var_char('R'); aClass.var_char", (int) 'R'),
        Arguments.of("aClass.set_var_int(3); aClass.var_int", 3),
        Arguments.of("aClass.set_var_long(4L); aClass.var_long", 4),
        Arguments.of("aClass.set_var_float(5.0f); aClass.var_float", 5.0),
        Arguments.of("aClass.set_var_double(6.0); aClass.var_double", 6.0),
        Arguments.of("aClass.set_var_Boolean(true); aClass.var_Boolean", true),
        Arguments.of("aClass.set_var_Byte((byte)7); aClass.var_Byte", 7),
        Arguments.of("aClass.set_var_Short((short)8); aClass.var_Short", 8),
        Arguments.of("aClass.set_var_Character('E'); aClass.var_Character", (int) 'E'),
        Arguments.of("aClass.set_var_Integer(9); aClass.var_Integer", 9),
        Arguments.of("aClass.set_var_Long(10L); aClass.var_Long", 10),
        Arguments.of("aClass.set_var_Float(11.0f); aClass.var_Float", 11.0),
        Arguments.of("aClass.set_var_Double(12.0); aClass.var_Double", 12.0),

        Arguments.of("AClass.set_var_s_boolean(true); AClass.var_s_boolean", true),
        Arguments.of("AClass.set_var_s_byte((byte)13); AClass.var_s_byte", 13),
        Arguments.of("AClass.set_var_s_short((short)14); AClass.var_s_short", 14),
        Arguments.of("AClass.set_var_s_char('P'); AClass.var_s_char", (int) 'P'),
        Arguments.of("AClass.set_var_s_int(15); AClass.var_s_int", 15),
        Arguments.of("AClass.set_var_s_long(16L); AClass.var_s_long", 16),
        Arguments.of("AClass.set_var_s_float(17.0f); AClass.var_s_float", 17.0),
        Arguments.of("AClass.set_var_s_double(18.0); AClass.var_s_double", 18.0),
        Arguments.of("AClass.set_var_s_Boolean(true); AClass.var_s_Boolean", true),
        Arguments.of("AClass.set_var_s_Byte((byte)19); AClass.var_s_Byte", 19),
        Arguments.of("AClass.set_var_s_Short((short)20); AClass.var_s_Short", 20),
        Arguments.of("AClass.set_var_s_Character('L'); AClass.var_s_Character", (int) 'L'),
        Arguments.of("AClass.set_var_s_Integer(21); AClass.var_s_Integer", 21),
        Arguments.of("AClass.set_var_s_Long(22L); AClass.var_s_Long", 22),
        Arguments.of("AClass.set_var_s_Float(23.0f); AClass.var_s_Float", 23.0),
        Arguments.of("AClass.set_var_s_Double(24.0); AClass.var_s_Double", 24.0)
    );
  }
}
