/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory;
import de.monticore.types3.util.DefsVariablesForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.monticore.types3.util.DefsTypesForTests._BooleanSymType;
import static de.monticore.types3.util.DefsTypesForTests._booleanSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedListSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedSetSymType;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.variable;
import static de.monticore.types3.util.DefsVariablesForTests._intUnboxedListVarSym;
import static de.monticore.types3.util.DefsVariablesForTests._intUnboxedSetVarSym;

public class SetExpressionsTypeVisitorTest extends AbstractTypeVisitorTest {

  @BeforeEach
  public void setup() {
    MCCollectionSymTypeRelations.init();
    DefsVariablesForTests.setup();
    addFurtherVariables();
  }

  protected void addFurtherVariables() {
    // adds variables with further types, e.g.,
    // Set<boolean>, List<Set<int>>
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    inScope(gs, variable("varbooleanSet",
        MCCollectionSymTypeFactory.createSet(_booleanSymType)
    ));
    inScope(gs, variable("varbooleanList",
        MCCollectionSymTypeFactory.createList(_booleanSymType)
    ));
    inScope(gs, variable("varBooleanSet",
        MCCollectionSymTypeFactory.createSet(_BooleanSymType)
    ));
    inScope(gs, variable("varBooleanList",
        MCCollectionSymTypeFactory.createList(_BooleanSymType)
    ));

    inScope(gs, variable("varintSetSet",
        SymTypeExpressionFactory.createGenerics(
            _unboxedSetSymType.getTypeInfo(),
            _intUnboxedSetVarSym.getType()
        )
    ));
    inScope(gs, variable("varintListSet",
        SymTypeExpressionFactory.createGenerics(
            _unboxedListSymType.getTypeInfo(),
            _intUnboxedSetVarSym.getType()
        )
    ));
    inScope(gs, variable("varintSetList",
        SymTypeExpressionFactory.createGenerics(
            _unboxedSetSymType.getTypeInfo(),
            _intUnboxedListVarSym.getType()
        )
    ));
    inScope(gs, variable("varintListList",
        SymTypeExpressionFactory.createGenerics(
            _unboxedListSymType.getTypeInfo(),
            _intUnboxedListVarSym.getType()
        )
    ));
  }

  @Test
  public void deriveFromSetInExpressionsTest() throws IOException {
    checkExpr("2 isin varintSet", "boolean");
    checkExpr("2 isin varintList", "boolean");
    checkExpr("2 isin varintBoxedSet", "boolean");
    checkExpr("2 isin varintBoxedList", "boolean");
  }

  @Test
  public void invalidSetInExpressionsTest() throws IOException {
    // not an accepted collection type
    checkErrorExpr("2 isin varintMap", "0xFD542");
    checkErrorExpr("2 isin varintOptional", "0xFD542");
    checkErrorExpr("2 isin varintBoxedMap", "0xFD542");
    checkErrorExpr("2 isin varintBoxedOptional", "0xFD542");
    // not a type that can be in the collection
    checkErrorExpr("true isin varintSet", "0xFD541");
    checkErrorExpr("varintSet isin varintSet", "0xFD541");
    // not a collection
    checkErrorExpr("true isin 2", "0xFD542");
  }

  @Test
  public void deriveFromSetNotInExpressionsTest() throws IOException {
    checkExpr("2 notin varintSet", "boolean");
    checkExpr("2 notin varintList", "boolean");
    checkExpr("2 notin varintBoxedSet", "boolean");
    checkExpr("2 notin varintBoxedList", "boolean");

  }

  @Test
  public void invalidSetNotInExpressionsTest() throws IOException {
    // not an accepted collection type
    checkErrorExpr("2 notin varintMap", "0xFD542");
    checkErrorExpr("2 notin varintOptional", "0xFD542");
    checkErrorExpr("2 notin varintBoxedMap", "0xFD542");
    checkErrorExpr("2 notin varintBoxedOptional", "0xFD542");
    // not a type that can be in the collection
    checkErrorExpr("true notin varintSet", "0xFD541");
    checkErrorExpr("varintSet notin varintSet", "0xFD541");
    // not a collection
    checkErrorExpr("true notin 2", "0xFD542");
  }

  @Test
  public void deriveFromUnionExpressionsTest() throws IOException {
    checkExpr("varintSet union varintSet", "Set<int>");
    checkExpr("varintList union varintList", "List<int>");
    checkExpr("varintBoxedSet union varintBoxedSet", "java.util.Set<int>");
    checkExpr("varintBoxedList union varintBoxedList", "java.util.List<int>");

  }

  @Test
  public void invalidUnionExpressionsTest() throws IOException {
    // s.a. OCL spec 2.4
    checkErrorExpr("varintSet union varintList", "0xFD543");
    checkErrorExpr("varintList union varintSet", "0xFD543");
    // not an acceptable collection type
    checkErrorExpr("varintMap union varintMap", "0xFD544");
    checkErrorExpr("varintOptional union varintOptional", "0xFD544");
    checkErrorExpr("varintBoxedOptional union varintBoxedOptional", "0xFD544");
  }

  @Test
  public void deriveFromIntersectionExpressionsTest() throws IOException {
    checkExpr("varintSet intersect varintSet", "Set<int>");
    checkExpr("varintBoxedSet intersect varintBoxedSet", "java.util.Set<int>");
  }

  @Test
  public void invalidIntersectionExpressionsTest() throws IOException {
    // s.a. OCL spec 2.4
    checkErrorExpr("varintList \\ varintList", "0xFD546");
    checkErrorExpr("varintBoxedList \\ varintBoxedList", "0xFD546");
    checkErrorExpr("varintSet intersect varintList", "0xFD546");
    checkErrorExpr("varintList intersect varintSet", "0xFD546");
    checkErrorExpr("varintMap intersect varintMap", "0xFD546");
    checkErrorExpr("varintOptional intersect varintOptional", "0xFD546");
    checkErrorExpr("varintBoxedOptional intersect varintBoxedOptional", "0xFD546");
  }

  @Test
  public void deriveFromSetMinusExpressionsTest() throws IOException {
    checkExpr("varintSet \\ varintSet", "Set<int>");
    checkExpr("varintBoxedSet \\ varintBoxedSet", "java.util.Set<int>");
  }

  @Test
  public void invalidSetMinusExpressionsTest() throws IOException {
    // s.a. OCL spec 2.4
    checkErrorExpr("varintList \\ varintList", "0xFD546");
    checkErrorExpr("varintBoxedList \\ varintBoxedList", "0xFD546");
    checkErrorExpr("varintSet \\ varintList", "0xFD546");
    checkErrorExpr("varintList \\ varintSet", "0xFD546");
    checkErrorExpr("varintMap \\ varintMap", "0xFD546");
    checkErrorExpr("varintOptional \\ varintOptional", "0xFD546");
    checkErrorExpr("varintBoxedOptional \\ varintBoxedOptional", "0xFD546");
  }

  @Test
  public void deriveFromSetUnionExpression() throws IOException {
    checkExpr("union varintSetSet", "Set<int>");
    checkExpr("union varintSetList", "List<int>");
    checkExpr("union varintListSet", "Set<int>");
    checkExpr("union varintListList", "List<int>");
  }

  @Test
  public void invalidSetUnionExpressionsTest() throws IOException {
    checkErrorExpr("union varint", "0xFD530");
    checkErrorExpr("union varintSet", "0xFD544");
    checkErrorExpr("union varintList", "0xFD544");
  }

  @Test
  public void deriveFromSetIntersectionExpression() throws IOException {
    checkExpr("intersect varintSetSet", "Set<int>");
    checkExpr("intersect varintListSet", "Set<int>");
  }

  @Test
  public void invalidSetIntersectionExpressionsTest() throws IOException {
    checkErrorExpr("intersect varintSetList", "0xFD546");
    checkErrorExpr("intersect varintListList", "0xFD546");
    checkErrorExpr("intersect varint", "0xFD531");
    checkErrorExpr("intersect varintSet", "0xFD546");
    checkErrorExpr("intersect varintList", "0xFD546");
  }

  @Test
  public void deriveFromSetAndExpression() throws IOException {
    checkExpr("setand varbooleanSet", "boolean");
    checkExpr("setand varbooleanList", "boolean");
    checkExpr("setand varBooleanSet", "boolean");
    checkExpr("setand varBooleanList", "boolean");
  }

  @Test
  public void invalidSetAndExpressionsTest() throws IOException {
    checkErrorExpr("setand varintSet", "0xFD545");
    checkErrorExpr("setand varintList", "0xFD545");
    checkErrorExpr("setand varboolean", "0xFD545");
  }

  @Test
  public void deriveFromSetOrExpression() throws IOException {
    checkExpr("setor varbooleanSet", "boolean");
    checkExpr("setor varbooleanList", "boolean");
    checkExpr("setor varBooleanSet", "boolean");
    checkExpr("setor varBooleanList", "boolean");
  }

  @Test
  public void invalidSetOrExpressionsTest() throws IOException {
    checkErrorExpr("setor varintSet", "0xFD545");
    checkErrorExpr("setor varintList", "0xFD545");
    checkErrorExpr("setor varboolean", "0xFD545");
  }

  @Test
  public void deriveFromSetComprehension() throws IOException {
    // check mostly left side of "|"
    // expression left side
    checkExpr("{x | x in varintSet}", "Set<int>");
    checkExpr("{x*x | x in varintSet}", "Set<int>");
    // generator left side
    checkExpr("{x in varintSet | x % 2 == 0}", "Set<int>");
    // typed left side
    checkExpr("{float x | x in varintSet}", "Set<float>");

    // check right side of "|"
    // generator only
    checkExpr("{x | int x in varintSet}", "Set<int>");
    checkExpr("{x | java.lang.Integer x in varintSet}", "Set<java.lang.Integer>");
    checkExpr("{x | x in varintSet, y in varintSet}", "Set<int>");
    // variable declaration
    checkExpr("{z | x in varintSet, int z = 1 + x}", "Set<int>");
    checkExpr("{z | x in varintSet, int y = x - 2, int z = y + x}", "Set<int>");
    // filter expression
    checkExpr("{x | x in varintSet, x % 2 == 0}", "Set<int>");

    // examples using List
    checkExpr("[x | x in varintSet]", "List<int>");
    checkExpr("[x*x | x in varintSet]", "List<int>");

    // more complex examples
    checkExpr(
        "{y | x in varintSet, y in {x*z | z in varintSet}}",
        "Set<int>"
    );
    checkExpr(
        "[z + \"?\" |"
            + "x in [\"Spiel\", \"Feuer\", \"Flug\"], "
            + "y in [\"zeug\", \"platz\"],"
            + "String z = x + y, "
            + "z != \"Feuerplatz\"]",
        "List<String>"
    );

  }

  @Test
  public void invalidSetComprehensionTest() throws IOException {
    // Note, that most invalid ordering of variable declaration and usage
    // is handled by the corresponding CoCo
    // generator
    checkErrorExpr("{Person x in varintSet | true}", "0xFD549");
    checkErrorExpr("{x | Person x in varintSet}", "0xFD549");
    // variable
    checkErrorExpr("{int x = 2 | true}", "0xFD536");
    checkErrorExpr("{z | int x in varintSet, byte z = x}", "0xFD547");
    // non-boolean filter
    checkErrorExpr("{x | x in varintSet, x+x}", "0xFD554");
  }

  @Test
  public void deriveFromSetEnumeration() throws IOException {
    // examples with int
    checkExpr("{1}", "Set<int>");
    checkExpr("[1]", "List<int>");
    checkExpr("{1,2}", "Set<int>");
    checkExpr("[1,2]", "List<int>");
    checkExpr("{1,1,1,1,1}", "Set<int>");
    checkExpr("[1,1,1,1,1]", "List<int>");
    checkExpr("{1..1}", "Set<int>");
    checkExpr("[1..1]", "List<int>");
    checkExpr("{1..5}", "Set<int>");
    checkExpr("[1..5]", "List<int>");
    checkExpr("{1..(2+3)}", "Set<int>");
    checkExpr("[1..(2+3)]", "List<int>");
    checkExpr("{1..true?1:2}", "Set<int>");
    checkExpr("[1..true?1:2]", "List<int>");
    checkExpr("{1..(short)2}", "Set<int>");
    checkExpr("[1..(short)2]", "List<int>");
    checkExpr("{1, 1..2, 2..3, 4}", "Set<int>");
    checkExpr("[1, 1..2, 2..3, 4]", "List<int>");
    // examples with char
    checkExpr("{'a'..'z'}", "Set<char>");
    checkExpr("['a'..'z']", "List<char>");
    // examples combining numeric types
    checkExpr("{1.2, 1}", "Set<double>");
    checkExpr("[1.2, 1]", "List<double>");
    checkExpr("{1, 1.2f}", "Set<float>");
    checkExpr("[1, 1.2f]", "List<float>");
    checkExpr("{(char)1, (byte)1, (short)1, (int)1, (float)1}", "Set<float>");
    checkExpr("[(char)1, (byte)1, (short)1, (int)1, (float)1]", "List<float>");
    // examples combining non-numeric types
    checkExpr("{\"1\", 1}", "Set<(String | int)>");
    checkExpr("{\"1\", varPerson}", "Set<(Person | String)>");
  }

  @Test
  public void invalidSetEnumerationTest() throws IOException {
    // only integral ranges are allowed
    checkErrorExpr("{1.0f .. 1}", "0xFD551");
    checkErrorExpr("{1 .. 1.0f}", "0xFD551");
    checkErrorExpr("{1.0f .. 1.0f}", "0xFD551");
  }

  @Test
  public void deriveFromSetEnumerationWithTargetType() throws IOException {
    checkExpr("{}", "Set<int>", "Set<int>");
    checkExpr("{}", "Set<? extends Person>", "Set<Person>");
    checkExpr("{}", "Set<? super Person>", "Set<Person>");
    checkExpr("[]", "List<int>", "List<int>");
    checkExpr("{{}}", "Set<Set<Set<int>>>", "Set<Set<Set<int>>>");
    checkExpr("{[]}", "Set<List<Set<int>>>", "Set<List<Set<int>>>");
    checkExpr("{[],[]}", "Set<List<Set<int>>>", "Set<List<Set<int>>>");
    checkExpr("[[{[],[]},{}]]",
        "List<List<Set<List<Set<int>>>>>",
        "List<List<Set<List<Set<int>>>>>"
    );
  }

}
