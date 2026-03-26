package de.monticore.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class JavaSymTypeRelations {

  protected static JavaSymTypeRelations delegate;

  protected Set<String> javaPrimitiveTypes;
  protected Set<String> javaNumericTypes;


  public JavaSymTypeRelations() {
    Set<String> javaPrimitiveTypes_temp = new HashSet<>();
    javaPrimitiveTypes_temp.add("boolean");
    javaPrimitiveTypes_temp.add("byte");
    javaPrimitiveTypes_temp.add("char");
    javaPrimitiveTypes_temp.add("double");
    javaPrimitiveTypes_temp.add("float");
    javaPrimitiveTypes_temp.add("int");
    javaPrimitiveTypes_temp.add("long");
    javaPrimitiveTypes_temp.add("short");
    javaPrimitiveTypes = Collections.unmodifiableSet(javaPrimitiveTypes_temp);

    Set<String> javaNumericTypes_temp = new HashSet<>();
    javaNumericTypes_temp.add("byte");
    javaNumericTypes_temp.add("java.lang.Byte");
    javaNumericTypes_temp.add("char");
    javaNumericTypes_temp.add("java.lang.Character");
    javaNumericTypes_temp.add("double");
    javaNumericTypes_temp.add("java.lang.Double");
    javaNumericTypes_temp.add("float");
    javaNumericTypes_temp.add("java.lang.Float");
    javaNumericTypes_temp.add("int");
    javaNumericTypes_temp.add("java.lang.Integer");
    javaNumericTypes_temp.add("long");
    javaNumericTypes_temp.add("java.lang.Long");
    javaNumericTypes_temp.add("short");
    javaNumericTypes_temp.add("java.lang.Short");
    javaNumericTypes = Collections.unmodifiableSet(javaNumericTypes_temp);
  }

  public static boolean isJavaPrimitive(SymTypeExpression type) {
    return getDelegate()._isJavaPrimitive(type);
  }

  public boolean _isJavaPrimitive(SymTypeExpression type) {
    return javaPrimitiveTypes.contains(SymTypeExpression2JavaConverter.convert2JavaType(type));
  }

  public static boolean isJavaNumeric(SymTypeExpression type) {
    return getDelegate()._isJavaNumeric(type);
  }

  public boolean _isJavaNumeric(SymTypeExpression type) {
    return javaNumericTypes.contains(SymTypeExpression2JavaConverter.convert2JavaType(type));
  }

  // static delegate

  public static void init() {
    Log.trace("init default JavaSymTypeRelations", "CodeGen setup");
    setDelegate(new JavaSymTypeRelations());
  }

  public static void reset() {
    JavaSymTypeRelations.delegate = null;
  }

  protected static void setDelegate(JavaSymTypeRelations newDelegate) {
    JavaSymTypeRelations.delegate =
      Preconditions.checkNotNull(newDelegate);
  }

  protected static JavaSymTypeRelations getDelegate() {
    if (JavaSymTypeRelations.delegate == null) {
      init();
    }
    return JavaSymTypeRelations.delegate;
  }
}
