/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

/**
 * Converts model {@link SymTypeExpression}s to Java equivalents
 * for code generation.
 * <p>
 * Note: This _ought_ to create new SymTypeExpressions,
 * but due to technical limitations regarding global scopes,
 * creates Strings instead.
 */
public class SymTypeExpression2JavaConverter {

  protected static SymTypeExpression2JavaConverter delegate;

  protected SymTypeExpressionJavaPrinterVisitor javaTypePrinterVisitor =
      new SymTypeExpressionJavaPrinterVisitor();

  protected SymTypeExpressionBoxedJavaPrinterVisitor javaBoxedTypePrinterVisitor =
      new SymTypeExpressionBoxedJavaPrinterVisitor();

  protected SymTypeExpressionTypeErasedJavaPrinterVisitor javaTypeErasedPrinterVisitor =
      new SymTypeExpressionTypeErasedJavaPrinterVisitor();

  // methods

  /**
   * Converts a model type into a Java type.
   */
  public static String convert2JavaType(SymTypeExpression modelType) {
    return getDelegate()._convert2JavaType(modelType);
  }

  protected String _convert2JavaType(SymTypeExpression modelType) {
    return javaTypePrinterVisitor.calculate(modelType);
  }

  /**
   * Converts a model type into a (boxed) Java type.
   * To be used, e.g., for type parameters of generics.
   */
  public static String convert2BoxedJavaType(SymTypeExpression modelType) {
    return getDelegate()._convert2BoxedJavaType(modelType);
  }

  protected String _convert2BoxedJavaType(SymTypeExpression modelType) {
    return javaBoxedTypePrinterVisitor.calculate(modelType);
  }

  /**
   * Converts a model type into a Java type after type erasure, e.g.,
   * {@code List<String>} becomes {@code java.util.List<?>}
   * for, e.g., {@code instanceof}.
   */
  public static String convert2TypeErasedJavaType(SymTypeExpression modelType) {
    return getDelegate()._convert2TypeErasedJavaType(modelType);
  }

  protected String _convert2TypeErasedJavaType(SymTypeExpression modelType) {
    return javaTypeErasedPrinterVisitor.calculate(modelType);
  }

  // Convenience methods

  /**
   * Provides the (qualified) name of the Java type
   * corresponding to the model type.
   * This can be used, e.g., as constructor.
   */
  public static String convert2JavaQName(SymTypeExpression modelType) {
    return convert2JavaType(modelType).split("<")[0];
  }

  // static delegate

  public static void init() {
    Log.trace("init default SymTypeExpression2JavaConverter", "CodeGen setup");
    setDelegate(new SymTypeExpression2JavaConverter());
  }

  public static void reset() {
    SymTypeExpression2JavaConverter.delegate = null;
  }

  protected static void setDelegate(SymTypeExpression2JavaConverter newDelegate) {
    SymTypeExpression2JavaConverter.delegate =
        Preconditions.checkNotNull(newDelegate);
  }

  protected static SymTypeExpression2JavaConverter getDelegate() {
    if (SymTypeExpression2JavaConverter.delegate == null) {
      init();
    }
    return SymTypeExpression2JavaConverter.delegate;
  }

}
