/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

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
   * Converts model types in a Java compatible way.
   */
  public static String convert2JavaType(SymTypeExpression modelType) {
    return getDelegate()._convert2JavaType(modelType);
  }

  protected String _convert2JavaType(SymTypeExpression modelType) {
    return javaTypePrinterVisitor.calculate(modelType);
  }

  /**
   * Converts model types in a boxed Java compatible way.
   */
  public static String convert2BoxedJavaType(SymTypeExpression modelType) {
    return getDelegate()._convert2BoxedJavaType(modelType);
  }

  protected String _convert2BoxedJavaType(SymTypeExpression modelType) {
    return javaBoxedTypePrinterVisitor.calculate(modelType);
  }

  /**
   * Converts model types in a Java compatible way with type erasure.
   */
  public static String convert2TypeErasedJavaType(SymTypeExpression modelType) {
    return getDelegate()._convert2TypeErasedJavaType(modelType);
  }

  protected String _convert2TypeErasedJavaType(SymTypeExpression modelType) {
    return javaTypeErasedPrinterVisitor.calculate(modelType);
  }

  // Convenience methods

  /**
   * Converts model types in java compatible way and returns their type constructor
   */
  public static String getJavaTypeConstructor(SymTypeExpression modelType) {
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
