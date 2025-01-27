/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types3.ISymTypeVisitor;
import de.monticore.types3.util.SymTypeDeepCloneVisitor;
import de.monticore.types3.util.SymTypePrintFullNameVisitor;
import de.monticore.types3.util.SymTypePrintVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.*;

/**
 * SymTypeExpression is the superclass for all typeexpressions, such as
 * TypeConstants, TypeVariables and applications of Type-Constructors.
 * It shares common functionality
 * (such as comparison, printing)
 */
public abstract class SymTypeExpression {

  protected static final String LOG_NAME = "SymTypeExpression";

  /**
   * print: Conversion to a compact string, such as "int", "Person", "List< A >"
   */
  public String print() {
    return new SymTypePrintVisitor().calculate(this);
  }

  /**
   * printFullName: prints the full name of the symbol, such as "java.util.List<java.lang.String>"
   * @return
   */
  public String printFullName() {
    return new SymTypePrintFullNameVisitor().calculate(this);
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    return SymTypeExpressionDeSer.getInstance().serialize(this);
  }

  /**
   * Am I a not valid type?
   * e.g. unknown type,
   * not all type variables set,
   * pseudo types like typeVariables
   * @deprecated not well-thought-out and unused
   */
  @Deprecated
  public boolean isValidType() {
    return true;
  }

  /**
   * Am I primitive? (such as "int")
   * (default: no)
   */
  public boolean isPrimitive() {
    return false;
  }

  public SymTypePrimitive asPrimitive() {
    Log.error("0xFDAA0 internal error: "
        + "tried to convert non-primitive to a primitive");
    return null;
  }

  /**
   * Am I a generic type? (such as "List<Integer>")
   */
  public boolean isGenericType() {
    return false;
  }

  public SymTypeOfGenerics asGenericType() {
    Log.error("0xFDAA1 internal error: "
        + "tried to convert non-generic to a generic");
    return null;
  }

  /**
   * Am I a bound type variable?
   */
  public boolean isTypeVariable() {
    return false;
  }

  public SymTypeVariable asTypeVariable() {
    Log.error("0xFDAA2 internal error: "
        + "tried to convert non-bound-type-variable to a bound-type-variable");
    return null;
  }

  /**
   * Am I a free type variable?
   */
  public boolean isInferenceVariable() {
    return false;
  }

  public SymTypeInferenceVariable asInferenceVariable() {
    Log.error("0xFDAAF internal error: "
        + "tried to convert non-inference-variable to an inference-variable");
    return null;
  }

  /**
   * Am I an array?
   */
  public boolean isArrayType() {
    return false;
  }

  public SymTypeArray asArrayType() {
    Log.error("0xFDAA3 internal error: "
        + "tried to convert non-array to an array");
    return null;
  }

  /**
   * Am I of void type?
   */
  public boolean isVoidType() {
    return false;
  }

  public SymTypeVoid asVoidType() {
    Log.error("0xFDAA4 internal error: "
        + "tried to convert non-void-type to a void type");
    return null;
  }

  /**
   * Am I of null type?
   */
  public boolean isNullType() {
    return false;
  }

  public SymTypeOfNull asNullType() {
    Log.error("0xFDAA5 internal error: "
        + "tried to convert non-null-type to a null-type");
    return null;
  }

  /**
   * Am I an object type? (e.g. "String", "Person")
   */
  public boolean isObjectType() {
    return false;
  }

  public SymTypeOfObject asObjectType() {
    Log.error("0xFDAA6 internal error: "
        + "tried to convert non-object-type to an object-type");
    return null;
  }

  /**
   * Am I a regex type (e.g. 'R"rege(x(es)?|xps?)"')
   */
  public boolean isRegExType() {
    return false;
  }

  public SymTypeOfRegEx asRegExType() {
    Log.error("0xFDAAC internal error: "
        + "tried to convert non-regex-type to a regex type");
    return null;
  }

  /**
   * Am I a function type (e.g. "String -> Integer")
   */
  public boolean isFunctionType() {
    return false;
  }

  public SymTypeOfFunction asFunctionType() {
    Log.error("0xFDAA7 internal error: "
        + "tried to convert non-function-type to a function type");
    return null;
  }

  // Am I an SIUnit type (e.g., "km/h")
  public boolean isSIUnitType() {
    return false;
  }

  public SymTypeOfSIUnit asSIUnitType() {
    Log.error("0xFDAAC internal error: "
        + "tried to convert non-SIUnit type to a SIUnit type");
    return null;
  }

  // Am I a numeric with SIUnit type (e.g., "km/h<float>")
  public boolean isNumericWithSIUnitType() {
    return false;
  }

  public SymTypeOfNumericWithSIUnit asNumericWithSIUnitType() {
    Log.error("0xFDAAD internal error: "
        + "tried to convert non-numeric-with-SIUnit type "
        + "to a numeric-with-SIUnit type"
    );
    return null;
  }

  /**
   * Am I a tuple type (e.g. "(String, int)")
   */
  public boolean isTupleType() {
    return false;
  }

  public SymTypeOfTuple asTupleType() {
    Log.error("0xFDAAE internal error: "
        + "tried to convert non-tuple-type to a tuple type");
    return null;
  }

  /**
   * Am I an union type (e.g. "(A|B)")?
   */
  public boolean isUnionType() {
    return false;
  }

  public SymTypeOfUnion asUnionType() {
    Log.error("0xFDAA8 internal error: "
        + "tried to convert non-union-type to a union-type");
    return null;
  }

  /**
   * Am I an intersection type (e.g. "(A&B)")
   */
  public boolean isIntersectionType() {
    return false;
  }

  public SymTypeOfIntersection asIntersectionType() {
    Log.error("0xFDAA9 internal error: "
        + "tried to convert non-intersection-type to an intersection-type");
    return null;
  }

  /**
   * Can I not have a type derived from (e.g. "1 - student")?
   */
  public boolean isObscureType() {
    return false;
  }

  public SymTypeObscure asObscureType() {
    Log.error("0xFDAAA internal error: "
        + "tried to convert non-obscure-type to an obscure-type");
    return null;
  }

  /**
   * Am I a wildcard (s. generics)?
   */
  public boolean isWildcard() {
    return false;
  }

  public SymTypeOfWildcard asWildcard() {
    Log.error("0xFDAAB internal error: "
        + "tried to convert non-wildcard-type to a wildcard-type");
    return null;
  }

  public SymTypeExpression deepClone() {
    return new SymTypeDeepCloneVisitor().calculate(this);
  }

  public abstract boolean deepEquals(SymTypeExpression sym);

  // the following methods are deprecated, due to (very) broken behavior
  // combined with a suboptimal interface
  // s. DeprecatedSymTypeExpressionSymbolResolver for more details

  @Deprecated(forRemoval = true)
  protected List<FunctionSymbol> functionList = new ArrayList<>();

@Deprecated
public List<FunctionSymbol> getMethodList(String methodName, boolean abstractTc) {
  return DeprecatedSymTypeExpressionSymbolResolver.getMethodList(this, methodName, abstractTc, AccessModifier.ALL_INCLUSION);
}

  @Deprecated
  public List<FunctionSymbol> getMethodList(String methodname, boolean abstractTc, AccessModifier modifier){
    return DeprecatedSymTypeExpressionSymbolResolver.getMethodList(this, methodname, abstractTc, modifier);
  }

@Deprecated
public List<FunctionSymbol> getCorrectMethods(String methodName, boolean outerIsType, boolean abstractTc) {
  return DeprecatedSymTypeExpressionSymbolResolver.getCorrectMethods(this, methodName, outerIsType, abstractTc, AccessModifier.ALL_INCLUSION);
}

  @Deprecated
  protected List<FunctionSymbol> getCorrectMethods(String methodName, 
                    boolean outerIsType, boolean abstractTc, AccessModifier modifier){
    return DeprecatedSymTypeExpressionSymbolResolver.getCorrectMethods(this, methodName, outerIsType, abstractTc, modifier);
  }

  @Deprecated
  protected List<FunctionSymbol> transformMethodList(String methodName, List<FunctionSymbol> functions){
   return DeprecatedSymTypeExpressionSymbolResolver.transformMethodList(this, methodName, functions, functionList);
  }

  @Deprecated
  public void replaceTypeVariables(Map<TypeVarSymbol, SymTypeExpression> replaceMap){
    DeprecatedSymTypeExpressionSymbolResolver.replaceTypeVariables(this, replaceMap);
  }

@Deprecated
public List<FunctionSymbol> getMethodList(String methodName, boolean outerIsType, boolean abstractTc) {
  return getMethodList(methodName, outerIsType, abstractTc, AccessModifier.ALL_INCLUSION);
}

  @Deprecated
  public List<FunctionSymbol> getMethodList(String methodName,
                                            boolean outerIsType, boolean abstractTc, AccessModifier modifier) {
    return DeprecatedSymTypeExpressionSymbolResolver.getMethodList(this, methodName, outerIsType, abstractTc, modifier);
  }

@Deprecated
public List<VariableSymbol> getFieldList(String fieldName, boolean abstractTc){
  return getFieldList(fieldName, abstractTc, AccessModifier.ALL_INCLUSION);
}

  @Deprecated
  public List<VariableSymbol> getFieldList(String fieldName, boolean abstractTc, AccessModifier modifier){
    return DeprecatedSymTypeExpressionSymbolResolver.getFieldList(this, fieldName, abstractTc, modifier);
  }

@Deprecated
public List<VariableSymbol> getFieldList(String fieldName, boolean outerIsType, boolean abstractTc){
  return getFieldList(fieldName, outerIsType, abstractTc, AccessModifier.ALL_INCLUSION);
}

  @Deprecated
  public List<VariableSymbol> getFieldList(String fieldName, 
                    boolean outerIsType, boolean abstractTc, AccessModifier modifier) {
    return DeprecatedSymTypeExpressionSymbolResolver.getFieldList(this, fieldName, outerIsType, abstractTc, modifier);
  }

  @Deprecated
public List<VariableSymbol> getCorrectFields(String fieldName, boolean outerIsType, boolean abstractTc){
  return getCorrectFields(fieldName, outerIsType, abstractTc, AccessModifier.ALL_INCLUSION);
}

  @Deprecated
  protected List<VariableSymbol> getCorrectFields(String fieldName, 
                        boolean outerIsType, boolean abstractTc, AccessModifier modifier) {
    return DeprecatedSymTypeExpressionSymbolResolver.getCorrectFields(this, fieldName, outerIsType, abstractTc, modifier);
  }

  @Deprecated
  protected List<VariableSymbol> transformFieldList(String fieldName, 
                                          List<VariableSymbol> fields) {
    return DeprecatedSymTypeExpressionSymbolResolver.transformFieldList(this, fieldName, fields);
  }

  /**
   * @deprecated TypeSymbols are to be found in the corresponding subclasses,
   * however, not every subclass will have a type symbol
   */
  @Deprecated
  protected TypeSymbol typeSymbol;

  /**
   * Whether we can call getTypeInfo
   */
  public boolean hasTypeInfo() {
    return false;
  }

  /**
   * Returns an TypeSymbol representing the type
   * Only to be called according to {@link #hasTypeInfo()}
   * <p>
   * As most SymTypeExpressions do not have a TypeSymbol (this is legacy),
   * this method will log a warning and is expected to be overridden,
   * if a TypeSymbol exists.
   */
  public TypeSymbol getTypeInfo() {
    Log.debug("0xFDFDE internal error: getTypeInfo called"
            + ", but the current SymTypeExpression should never have"
            + " a TypeSymbol in the first place."
            + " (This will be an error in the future)",
        LOG_NAME
    );
    //support deprecated behaviour
    if(typeSymbol != null) {
      return typeSymbol;
    }
    Log.error("0xFDFDF internal error: getTypeInfo called,"
        + "but no typeinfo available");
    return null;
  }

  protected SymTypeSourceInfo sourceInfo = new SymTypeSourceInfo();

  /**
   * Contains information where this SymTypeExpression comes from.
   * Used in CoCos, code-generation, etc.
   * <p>
   * not considered during {@link #deepEquals(SymTypeExpression)}.
   */
  public SymTypeSourceInfo getSourceInfo() {
    return this.sourceInfo;
  }

  /**
   * used during deep-cloning, clones the SymTypeSourceInfo
   */
  public void _internal_setSourceInfo(SymTypeSourceInfo sourceInfo) {
    this.sourceInfo = new SymTypeSourceInfo(sourceInfo);
  }

  public void accept(ISymTypeVisitor visitor) {
    // not abstract to support legacy typecheck subclasses
  }
}
