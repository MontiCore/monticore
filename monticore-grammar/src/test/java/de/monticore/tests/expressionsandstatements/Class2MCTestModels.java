// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements;

import de.monticore.tests.expressionsandstatements.rte.AClass;
import de.monticore.tests.expressionsandstatements.rte.Person;
import de.monticore.tests.expressionsandstatements.rte.Student;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Contains test data for Class2MC test
 * containing behavior and expected results.
 * For, e.g., interpreter/code generator
 */
public class Class2MCTestModels {

  static public Stream<Arguments> getNativeJavaCases() {
    return Stream.of(
        Arguments.of("java.lang.Integer.MAX_VALUE", Integer.MAX_VALUE),
        Arguments.of("java.lang.Math.abs(-2)", 2),
        Arguments.of("((java.lang.Integer) 5) + 2.0", 7.0),
        Arguments.of("((java.lang.Integer) 5) + (java.lang.Double) 2", 7.0),
        Arguments.of("ArrayList()", new ArrayList<>())
    );
  }

  /**
   * To be added to each model.
   * Deliberately kept separately to simplify test naming.
   *
   * @return the prefix
   */
  static public String getModelPrefix() {
    return "import " + AClass.class.getPackageName() + ".*;"
        + System.lineSeparator()
        + "AClass aClass = AClass();" + System.lineSeparator()
        + "Person person = Person(\"Sage\", 23);" + System.lineSeparator()
        + "Student student = Student(\"Quinn\", 19, 8243721);"
        + System.lineSeparator();
  }

  static public Stream<Arguments> getAClassCases() {
    return Stream.of(
        Arguments.of("aClass.var_boolean", false),
        Arguments.of("aClass.var_byte", (byte) 0),
        Arguments.of("aClass.var_short", (short) 0),
        Arguments.of("aClass.var_char", (char) 0),
        Arguments.of("aClass.var_int", 0),
        Arguments.of("aClass.var_long", 0L),
        Arguments.of("aClass.var_float", 0.0f),
        Arguments.of("aClass.var_double", 0.0d),
        Arguments.of("aClass.var_Boolean", false),
        Arguments.of("aClass.var_Byte", (byte) 0),
        Arguments.of("aClass.var_Short", (short) 0),
        Arguments.of("aClass.var_Character", (char) 0),
        Arguments.of("aClass.var_Integer", 0),
        Arguments.of("aClass.var_Long", 0L),
        Arguments.of("aClass.var_Float", 0.0f),
        Arguments.of("aClass.var_Double", 0.0d),

        Arguments.of("AClass.var_s_boolean", false),
        Arguments.of("AClass.var_s_byte", (byte) 0),
        Arguments.of("AClass.var_s_short", (short) 0),
        Arguments.of("AClass.var_s_char", (char) 0),
        Arguments.of("AClass.var_s_int", 0),
        Arguments.of("AClass.var_s_long", 0L),
        Arguments.of("AClass.var_s_float", 0.0f),
        Arguments.of("AClass.var_s_double", 0.0d),
        Arguments.of("AClass.var_s_Boolean", false),
        Arguments.of("AClass.var_s_Byte", (byte) 0),
        Arguments.of("AClass.var_s_Short", (short) 0),
        Arguments.of("AClass.var_s_Character", (char) 0),
        Arguments.of("AClass.var_s_Integer", 0),
        Arguments.of("AClass.var_s_Long", 0L),
        Arguments.of("AClass.var_s_Float", 0.0f),
        Arguments.of("AClass.var_s_Double", 0.0d),

        Arguments.of("aClass.get_var_boolean()", false),
        Arguments.of("aClass.get_var_byte()", (byte) 0),
        Arguments.of("aClass.get_var_short()", (short) 0),
        Arguments.of("aClass.get_var_char()", (char) 0),
        Arguments.of("aClass.get_var_int()", 0),
        Arguments.of("aClass.get_var_long()", 0L),
        Arguments.of("aClass.get_var_float()", 0.0f),
        Arguments.of("aClass.get_var_double()", 0.0d),
        Arguments.of("aClass.get_var_Boolean()", false),
        Arguments.of("aClass.get_var_Byte()", (byte) 0),
        Arguments.of("aClass.get_var_Short()", (short) 0),
        Arguments.of("aClass.get_var_Character()", (char) 0),
        Arguments.of("aClass.get_var_Integer()", 0),
        Arguments.of("aClass.get_var_Long()", 0L),
        Arguments.of("aClass.get_var_Float()", 0.0f),
        Arguments.of("aClass.get_var_Double()", 0.0d),

        Arguments.of("AClass.get_var_s_boolean()", false),
        Arguments.of("AClass.get_var_s_byte()", (byte) 0),
        Arguments.of("AClass.get_var_s_short()", (short) 0),
        Arguments.of("AClass.get_var_s_char()", (char) 0),
        Arguments.of("AClass.get_var_s_int()", 0),
        Arguments.of("AClass.get_var_s_long()", 0L),
        Arguments.of("AClass.get_var_s_float()", 0.0f),
        Arguments.of("AClass.get_var_s_double()", 0.0d),
        Arguments.of("AClass.get_var_s_Boolean()", false),
        Arguments.of("AClass.get_var_s_Byte()", (byte) 0),
        Arguments.of("AClass.get_var_s_Short()", (short) 0),
        Arguments.of("AClass.get_var_s_Character()", (char) 0),
        Arguments.of("AClass.get_var_s_Integer()", 0),
        Arguments.of("AClass.get_var_s_Long()", 0L),
        Arguments.of("AClass.get_var_s_Float()", 0.0f),
        Arguments.of("AClass.get_var_s_Double()", 0.0d),

        Arguments.of("aClass.set_var_boolean(true); aClass.var_boolean", true),
        Arguments.of("aClass.set_var_byte((byte)1); aClass.var_byte", (byte) 1),
        Arguments.of("aClass.set_var_short((short)2); aClass.var_short", (short) 2),
        Arguments.of("aClass.set_var_char('R'); aClass.var_char", 'R'),
        Arguments.of("aClass.set_var_int(3); aClass.var_int", 3),
        Arguments.of("aClass.set_var_long(4L); aClass.var_long", 4L),
        Arguments.of("aClass.set_var_float(5.0f); aClass.var_float", 5.0f),
        Arguments.of("aClass.set_var_double(6.0); aClass.var_double", 6.0d),
        Arguments.of("aClass.set_var_Boolean(true); aClass.var_Boolean", true),
        Arguments.of("aClass.set_var_Byte((byte)7); aClass.var_Byte", (byte) 7),
        Arguments.of("aClass.set_var_Short((short)8); aClass.var_Short", (short) 8),
        Arguments.of("aClass.set_var_Character('E'); aClass.var_Character", 'E'),
        Arguments.of("aClass.set_var_Integer(9); aClass.var_Integer", 9),
        Arguments.of("aClass.set_var_Long(10L); aClass.var_Long", 10L),
        Arguments.of("aClass.set_var_Float(11.0f); aClass.var_Float", 11.0f),
        Arguments.of("aClass.set_var_Double(12.0); aClass.var_Double", 12.0d),

        Arguments.of("AClass.set_var_s_boolean(true); AClass.var_s_boolean", true),
        Arguments.of("AClass.set_var_s_byte((byte)13); AClass.var_s_byte", (byte) 13),
        Arguments.of("AClass.set_var_s_short((short)14); AClass.var_s_short", (short) 14),
        Arguments.of("AClass.set_var_s_char('P'); AClass.var_s_char", 'P'),
        Arguments.of("AClass.set_var_s_int(15); AClass.var_s_int", 15),
        Arguments.of("AClass.set_var_s_long(16L); AClass.var_s_long", 16L),
        Arguments.of("AClass.set_var_s_float(17.0f); AClass.var_s_float", 17.0f),
        Arguments.of("AClass.set_var_s_double(18.0); AClass.var_s_double", 18.0d),
        Arguments.of("AClass.set_var_s_Boolean(true); AClass.var_s_Boolean", true),
        Arguments.of("AClass.set_var_s_Byte((byte)19); AClass.var_s_Byte", (byte) 19),
        Arguments.of("AClass.set_var_s_Short((short)20); AClass.var_s_Short", (short) 20),
        Arguments.of("AClass.set_var_s_Character('L'); AClass.var_s_Character", 'L'),
        Arguments.of("AClass.set_var_s_Integer(21); AClass.var_s_Integer", 21),
        Arguments.of("AClass.set_var_s_Long(22L); AClass.var_s_Long", 22L),
        Arguments.of("AClass.set_var_s_Float(23.0f); AClass.var_s_Float", 23.0f),
        Arguments.of("AClass.set_var_s_Double(24.0); AClass.var_s_Double", 24.0d)
    );
  }

  static public Stream<Arguments> getInstanceOfCases() {
    return Stream.of(
        Arguments.of("person instanceof Student", false),
        Arguments.of(
            "Person s = student; s instanceof Student",
            true
        ),
        Arguments.of(
            " typeif person instanceof Student then true else false",
            false
        ),
        Arguments.of(
            "Person s = student;"
                + " typeif s instanceof Student then s.getStudentID() else -1",
            8243721
        )
    );
  }

  static public Stream<Arguments> getCreatorExpressionCases() {
    return Stream.of(
        Arguments.of("new Person(\"Rowan\", 33).getAge()", 33),
        Arguments.of(
            "new Person(\"Rowan\", 33)",
            new Person("Rowan", 33)
        ),
        Arguments.of(
            "new Student(\"Quinn\", 21, 42)",
            new Student("Quinn", 21, 42)
        ),
        Arguments.of("(new Person[1])[0]", null),
        Arguments.of("(new Person[1][2][][])[0][0]", null)
    );
  }

}
