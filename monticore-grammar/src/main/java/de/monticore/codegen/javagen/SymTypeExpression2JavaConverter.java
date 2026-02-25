/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class SymTypeExpression2JavaConverter {

  protected static SymTypeExpression2JavaConverter delegate;

  protected SymTypeExpression2JavaVisitor modelType2JavaTypeVisitor =
      new SymTypeExpression2JavaVisitor();

  protected JavaSymTypeExpressionPrinterVisitor javaTypePrinterVisitor =
      new JavaSymTypeExpressionPrinterVisitor();

  // methods

  /**
   * @deprecated use {@link #getAsJavaType(SymTypeExpression)} instead,
   *     which has a more descriptive name.
   */
  @Deprecated
  public static SymTypeExpression getJavaType(SymTypeExpression modelType) {
    return getAsJavaType(modelType);
  }

  /**
   * converts types of models into Java compatible types
   */
  public static SymTypeExpression getAsJavaType(SymTypeExpression modelType) {
    return getDelegate()._getAsJavaType(modelType);
  }

  protected SymTypeExpression _getAsJavaType(SymTypeExpression modelType) {
    return modelType2JavaTypeVisitor.calculate(modelType);
  }

  /**
   * Prints Java compatible types in a Java compatible way.
   * It is recommended to use {@link #getAsJavaType(SymTypeExpression)}
   * to get a Java compatible type.
   */
  public static String printJavaType(SymTypeExpression javaType) {
    return getDelegate()._printJavaType(javaType);
  }

  protected String _printJavaType(SymTypeExpression javaType) {
    return javaTypePrinterVisitor.calculate(javaType);
  }

  /**
   * applies type erasure to the given Java compatible type
   */
  public static SymTypeExpression applyTypeErasure(SymTypeExpression javaType) {
    return getDelegate()._applyTypeErasure(javaType);
  }

  protected SymTypeExpression _applyTypeErasure(SymTypeExpression javaType) {
    if (javaType.isGenericType()) {
      SymTypeOfGenerics g = javaType.asGenericType();
      List<SymTypeExpression> wildcards = new ArrayList<>();
      for (int i = 0; i < g.sizeArguments(); i++) {
        wildcards.add(SymTypeExpressionFactory.createWildcard());
      }
      return SymTypeExpressionFactory.createGenerics(g.getTypeInfo(), wildcards);
    }
    return javaType;
  }

  // Convenience methods

  public static String printModelTypeAsJavaType(SymTypeExpression modelType) {
    return getDelegate()._printModelTypeAsJavaType(modelType);
  }

  public String _printModelTypeAsJavaType(SymTypeExpression modelType) {
    SymTypeExpression javaType = getAsJavaType(modelType);
    String javaTypeStr = printJavaType(javaType);
    return javaTypeStr;
  }

  public static String printModelTypeAsJavaType(ASTMCType mcType) {
    return getDelegate()._printModelTypeAsJavaType(mcType);
  }

  public String _printModelTypeAsJavaType(ASTMCType mcType) {
    return printModelTypeAsJavaType(TypeCheck3.symTypeFromAST(mcType));
  }

  public static String printModelTypeAsJavaType(ASTExpression expression) {
    return getDelegate()._printModelTypeAsJavaType(expression);
  }

  public String _printModelTypeAsJavaType(ASTExpression expression) {
    return printModelTypeAsJavaType(TypeCheck3.typeOf(expression));
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
