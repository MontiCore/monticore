package de.monticore.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getTypeErasedJavaTypePrint;

/**
 * SymTypeRelations, specifically for Java code generation.
 * <p>
 * Note: This file mostly would not need to exist,
 * but as we have to (currently (7.9.0)) generate to Strings directly,
 * we provide some checks on the model types
 * that will be generated Java types from,
 * instead of using Java-SymTypeExpressions.
 */
public class JavaGenSymTypeRelations {

  protected static JavaGenSymTypeRelations delegate;

  protected Set<String> javaPrimitiveTypes;
  protected Set<String> javaNumericTypes;

  protected JavaGenSymTypeRelations() {
    Set<String> javaPrimitiveNumericTypes = new HashSet<>();
    javaPrimitiveNumericTypes.add(BasicSymbolsMill.BYTE);
    javaPrimitiveNumericTypes.add(BasicSymbolsMill.CHAR);
    javaPrimitiveNumericTypes.add(BasicSymbolsMill.DOUBLE);
    javaPrimitiveNumericTypes.add(BasicSymbolsMill.FLOAT);
    javaPrimitiveNumericTypes.add(BasicSymbolsMill.INT);
    javaPrimitiveNumericTypes.add(BasicSymbolsMill.LONG);
    javaPrimitiveNumericTypes.add(BasicSymbolsMill.SHORT);

    Set<String> javaPrimitiveTypes_temp = new HashSet<>();
    javaPrimitiveTypes_temp.addAll(javaPrimitiveNumericTypes);
    javaPrimitiveTypes_temp.add(BasicSymbolsMill.BOOLEAN);
    javaPrimitiveTypes = Collections.unmodifiableSet(javaPrimitiveTypes_temp);

    Set<String> javaNumericTypes_temp = new HashSet<>();
    javaNumericTypes_temp.addAll(javaPrimitiveNumericTypes);
    javaNumericTypes_temp.add("java.lang.Byte");
    javaNumericTypes_temp.add("java.lang.Character");
    javaNumericTypes_temp.add("java.lang.Double");
    javaNumericTypes_temp.add("java.lang.Float");
    javaNumericTypes_temp.add("java.lang.Integer");
    javaNumericTypes_temp.add("java.lang.Long");
    javaNumericTypes_temp.add("java.lang.Short");
    javaNumericTypes = Collections.unmodifiableSet(javaNumericTypes_temp);
  }

  /**
   * Returns true iff the provided type generates to a java primitive
   */
  public static boolean generatesToJavaPrimitive(SymTypeExpression type) {
    return getDelegate()._generatesToJavaPrimitive(type);
  }

  protected boolean _generatesToJavaPrimitive(SymTypeExpression type) {
    return javaPrimitiveTypes.contains(SymTypeExpression2JavaConverter.getJavaTypePrint(type));
  }

  /**
   * Returns true iff the provided type generates to a java numeric type (boxed or unboxed)
   */
  public static boolean generatesToJavaNumeric(SymTypeExpression type) {
    return getDelegate()._generatesToJavaNumeric(type);
  }

  protected boolean _generatesToJavaNumeric(SymTypeExpression type) {
    return javaNumericTypes.contains(SymTypeExpression2JavaConverter.getJavaTypePrint(type));
  }

  /**
   * checks if {@code instanceof} can be used with the type
   * in such a way that the instance is guaranteed to be of the type.
   * This is not always the case due to type erasure.
   * Example: {code f instanceof int -> void} would generate
   * {@code Action1<Integer>} for the function.
   * Due to type erasure, this is effectively {@code Action1<?>} at runtime,
   * which cannot be distinguished from, e.g., {@code boolean -> void}.
   * <p>
   * We do not allow for non-identifiable types for {@code instanceof}
   * and simmilar expressions.
   *
   * @param type the type to check for runtime instanceof checks.
   * @return iff the type can be used for runtime instanceof checks.
   */
  public static boolean generatesToJavaRuntimeIdentifiableType(SymTypeExpression type) {
    return getDelegate()._generatesToJavaRuntimeIdentifiableType(type);
  }

  protected boolean _generatesToJavaRuntimeIdentifiableType(SymTypeExpression type) {
    return !getTypeErasedJavaTypePrint(type).contains("?");
  }

  // static delegate

  public static void init() {
    Log.trace("init default JavaGenSymTypeRelations", "CodeGen setup");
    setDelegate(new JavaGenSymTypeRelations());
  }

  public static void reset() {
    JavaGenSymTypeRelations.delegate = null;
  }

  protected static void setDelegate(JavaGenSymTypeRelations newDelegate) {
    JavaGenSymTypeRelations.delegate =
        Preconditions.checkNotNull(newDelegate);
  }

  protected static JavaGenSymTypeRelations getDelegate() {
    if (JavaGenSymTypeRelations.delegate == null) {
      init();
    }
    return JavaGenSymTypeRelations.delegate;
  }
}
