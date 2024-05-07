/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import static de.monticore.types.check.SymTypeExpressionFactory.createGenerics;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeObject;
import static de.monticore.types3.util.DefsTypesForTests.*;

/**
 * creates default variables for type check tests,
 * structure of this class is analoge to {@link DefsTypesForTests},
 * each variable represents one type in {@link DefsTypesForTests}
 * (as far as applicable, e.g., one cannot create a variable of the null-type).
 * The name of the variable is "var[typeArguments][Boxed?][NameOfType]",
 * e.g., vardouble, varDouble, varBoxedString, varintBoxedList;
 * In case of boxed reference types (e.g., String, List),
 * the "Boxed" is added before the name of tne type ("[...]BoxedList")
 * In case of generics with type parameters, "int" is added after "var"
 * and the type arguments are "int", e.g., "varintList, varintBoxedMap"
 * <p>
 * This class is NOT intended to have variables for all tests,
 * if specific variables are needed
 * (e.g., specific generics or two variables of the same type),
 * they ought to be added in the specific test.
 */
public class DefsVariablesForTests {

  /**
   * initializes default variables in the scope
   */
  public static void setup() {
    setup(BasicSymbolsMill.globalScope());
  }

  /**
   * initializes default variables in the scope
   */
  public static void setup(IBasicSymbolsScope scope) {
    set_thePrimitives(scope);
    set_boxedPrimitives(scope);
    set_unboxedObjects(scope);
    set_boxedObjects(scope);
    set_unboxedCollections(scope);
    set_boxedCollections(scope);
    set_objectTypes(scope);
    set_generics(scope);
    set_siUnitsWithNumerics(scope);
  }

  public static VariableSymbol _booleanVarSym;

  public static VariableSymbol _byteVarSym;

  public static VariableSymbol _charVarSym;

  public static VariableSymbol _shortVarSym;

  public static VariableSymbol _intVarSym;

  public static VariableSymbol _longVarSym;

  public static VariableSymbol _floatVarSym;

  public static VariableSymbol _doubleVarSym;

  public static void set_thePrimitives(IBasicSymbolsScope scope) {
    _booleanVarSym = inScope(scope, variable("varboolean", _booleanSymType));
    _byteVarSym = inScope(scope, variable("varbyte", _byteSymType));
    _charVarSym = inScope(scope, variable("varchar", _charSymType));
    _shortVarSym = inScope(scope, variable("varshort", _shortSymType));
    _intVarSym = inScope(scope, variable("varint", _intSymType));
    _longVarSym = inScope(scope, variable("varlong", _longSymType));
    _floatVarSym = inScope(scope, variable("varfloat", _floatSymType));
    _doubleVarSym = inScope(scope, variable("vardouble", _doubleSymType));
  }

  public static VariableSymbol _BooleanVarSym;

  public static VariableSymbol _ByteVarSym;

  public static VariableSymbol _CharacterVarSym;

  public static VariableSymbol _ShortVarSym;

  public static VariableSymbol _IntegerVarSym;

  public static VariableSymbol _LongVarSym;

  public static VariableSymbol _FloatVarSym;

  public static VariableSymbol _DoubleVarSym;

  public static void set_boxedPrimitives(IBasicSymbolsScope scope) {
    _BooleanVarSym = inScope(scope, variable("varBoolean", _BooleanSymType));
    _ByteVarSym = inScope(scope, variable("varByte", _ByteSymType));
    _CharacterVarSym = inScope(scope, variable("varCharacter", _CharacterSymType));
    _ShortVarSym = inScope(scope, variable("varShort", _ShortSymType));
    _IntegerVarSym = inScope(scope, variable("varInteger", _IntegerSymType));
    _LongVarSym = inScope(scope, variable("varLong", _LongSymType));
    _FloatVarSym = inScope(scope, variable("varFloat", _FloatSymType));
    _DoubleVarSym = inScope(scope, variable("varDouble", _DoubleSymType));
  }

  public static VariableSymbol _unboxedStringVarSym;

  public static void set_unboxedObjects(IBasicSymbolsScope scope) {
    _unboxedStringVarSym = inScope(scope, variable("varString", _unboxedString));
  }

  public static VariableSymbol _boxedStringVarSym;

  public static void set_boxedObjects(IBasicSymbolsScope scope) {
    _boxedStringVarSym = inScope(scope, variable("varBoxedString", _boxedString));
  }

  /*
   * These are the predefined Symbol for unboxed Collections like "List"
   */

  public static VariableSymbol _intUnboxedOptionalVarSym;

  public static VariableSymbol _intUnboxedSetVarSym;

  public static VariableSymbol _intUnboxedListVarSym;

  public static VariableSymbol _intUnboxedMapVarSym;

  public static void set_unboxedCollections(IBasicSymbolsScope scope) {
    _intUnboxedOptionalVarSym = inScope(scope, variable("varintOptional",
        createGenerics(_unboxedOptionalSymType.getTypeInfo(), _intSymType)));
    _intUnboxedSetVarSym = inScope(scope, variable("varintSet",
        createGenerics(_unboxedSetSymType.getTypeInfo(), _intSymType)));
    _intUnboxedListVarSym = inScope(scope, variable("varintList",
        createGenerics(_unboxedListSymType.getTypeInfo(), _intSymType)));
    _intUnboxedMapVarSym = inScope(scope, variable("varintMap",
        createGenerics(_unboxedMapSymType.getTypeInfo(), _intSymType, _intSymType)));
  }

  /*
   * These are the predefined Symbol for unboxed Collections like "List"
   */

  public static VariableSymbol _boxedOptionalVarSym;

  public static VariableSymbol _boxedSetVarSym;

  public static VariableSymbol _boxedListVarSym;

  public static VariableSymbol _boxedMapVarSym;

  public static void set_boxedCollections(IBasicSymbolsScope scope) {
    _boxedOptionalVarSym = inScope(scope, variable("varintBoxedOptional",
        createGenerics(_boxedOptionalSymType.getTypeInfo(), _intSymType)));
    _boxedSetVarSym = inScope(scope, variable("varintBoxedSet",
        createGenerics(_boxedSetSymType.getTypeInfo(), _intSymType)));
    _boxedListVarSym = inScope(scope, variable("varintBoxedList",
        createGenerics(_boxedListSymType.getTypeInfo(), _intSymType)));
    _boxedMapVarSym = inScope(scope, variable("varintBoxedMap",
        createGenerics(_boxedMapSymType.getTypeInfo(), _intSymType, _intSymType)));
  }

  /*
   * These are some predefined Symbols for Object Types
   */

  public static VariableSymbol _personVarSym;

  public static VariableSymbol _teachableVarSym;

  public static VariableSymbol _studentVarSym;

  public static VariableSymbol _csStudentVarSym;

  public static VariableSymbol _firstSemesterCsStudentVarSym;

  public static VariableSymbol _childVarSym;

  public static VariableSymbol _teacherVarSym;

  public static VariableSymbol _carVarSym;

  public static VariableSymbol _schoolVarSym;

  public static void set_objectTypes(IBasicSymbolsScope scope) {
    _personVarSym = inScope(scope, variable("varPerson",
        createTypeObject(_personSymType.getTypeInfo())));
    _teachableVarSym = inScope(scope, variable("varTeachable",
        createTypeObject(_teachableSymType.getTypeInfo())));
    _studentVarSym = inScope(scope, variable("varStudent",
        createTypeObject(_studentSymType.getTypeInfo())));
    _csStudentVarSym = inScope(scope, variable("varCsStudent",
        createTypeObject(_csStudentSymType.getTypeInfo())));
    _firstSemesterCsStudentVarSym = inScope(scope, variable("varFirstSemesterCsStudent",
        createTypeObject(_firstSemesterCsStudentSymType.getTypeInfo())));
    _childVarSym = inScope(scope, variable("varChild",
        createTypeObject(_childSymType.getTypeInfo())));
    _teacherVarSym = inScope(scope, variable("varTeacher",
        createTypeObject(_teacherSymType.getTypeInfo())));
    _carVarSym = inScope(scope, variable("varCar",
        createTypeObject(_carSymType.getTypeInfo())));
    _schoolVarSym = inScope(scope, variable("varSchool",
        createTypeObject(_schoolSymType.getTypeInfo())));
  }

  /*
   * These are the predefined Symbols for further generics
   * in most cases, the collections ought to be enough
   */

  public static VariableSymbol _intLinkedListVarSym;

  public static VariableSymbol _intHashMapVarSym;

  public static void set_generics(IBasicSymbolsScope scope) {
    _intLinkedListVarSym = inScope(scope, variable("varintLinkedList",
        createGenerics(_linkedListSymType.getTypeInfo(), _intSymType)));
    _intHashMapVarSym = inScope(scope, variable("varintHashMap",
        createGenerics(_hashMapSymType.getTypeInfo(), _intSymType, _intSymType)));
  }

  /*
   * These are some predefined Symbols for SI units with numerics
   */

  // The seven base units (kg with and without prefix k)
  public static VariableSymbol _s_int_SIVarSym;
  public static VariableSymbol _m_int_SIVarSym;
  public static VariableSymbol _g_int_SIVarSym;
  public static VariableSymbol _A_int_SIVarSym;
  public static VariableSymbol _K_int_SIVarSym;
  public static VariableSymbol _mol_int_SIVarSym;
  public static VariableSymbol _cd_int_SIVarSym;
  public static VariableSymbol _kg_int_SIVarSym;

  public static void set_siUnitsWithNumerics(IBasicSymbolsScope scope) {
    _s_int_SIVarSym = inScope(scope, variable("varintSecond", _s_int_SISymType));
    _m_int_SIVarSym = inScope(scope, variable("varintMetre", _m_int_SISymType));
    _g_int_SIVarSym = inScope(scope, variable("varintGram", _g_int_SISymType));
    _A_int_SIVarSym = inScope(scope, variable("varintAmpere", _A_int_SISymType));
    _K_int_SIVarSym = inScope(scope, variable("varintKelvin", _K_int_SISymType));
    _mol_int_SIVarSym = inScope(scope, variable("varintMole", _mol_int_SISymType));
    _cd_int_SIVarSym = inScope(scope, variable("varintCandela", _cd_int_SISymType));
    _kg_int_SIVarSym = inScope(scope, variable("varintKiloGram", _kg_int_SISymType));
  }
}
