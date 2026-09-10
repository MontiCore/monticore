/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types3.ISymTypeVisitor;
import de.monticore.types3.util.*;
import de.se_rwth.commons.logging.Log;

/**
 * SymTypeExpression is the superclass for all typeexpressions, such as
 * TypeConstants, TypeVariables and applications of Type-Constructors.
 * It shares common functionality
 * (such as comparison, printing)
 */
public abstract class SymTypeExpression
    implements Comparable<SymTypeExpression> {

  protected static final String LOG_NAME = "SymTypeExpression";

  /**
   * print: Conversion to a compact string, such as {@code "int"}, {@code "Person"}, {@code "List< A >"}
   */
  public String print() {
    return new SymTypePrintVisitor().calculate(this);
  }

  /**
   * printFullName: prints the full name of the symbol, such as {@code "java.util.List<java.lang.String>"}
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
   * Am I primitive? (such as "int")
   * (default: no)
   */
  public boolean isPrimitive() {
    return false;
  }

  public SymTypePrimitive asPrimitive() {
    throw new UnsupportedOperationException("0xFDAA0 internal error: "
        + "tried to convert non-primitive to a primitive."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I a generic type? (such as {@code "List<Integer>"})
   */
  public boolean isGenericType() {
    return false;
  }

  public SymTypeOfGenerics asGenericType() {
    throw new UnsupportedOperationException("0xFDAA1 internal error: "
        + "tried to convert non-generic to a generic."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I a bound type variable?
   */
  public boolean isTypeVariable() {
    return false;
  }

  public SymTypeVariable asTypeVariable() {
    throw new UnsupportedOperationException("0xFDAA2 internal error: "
        + "tried to convert non-bound-type-variable to a bound-type-variable."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I a free type variable?
   */
  public boolean isInferenceVariable() {
    return false;
  }

  public SymTypeInferenceVariable asInferenceVariable() {
    throw new UnsupportedOperationException("0xFDAAF internal error: "
        + "tried to convert non-inference-variable to an inference-variable."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I an array?
   */
  public boolean isArrayType() {
    return false;
  }

  public SymTypeArray asArrayType() {
    throw new UnsupportedOperationException("0xFDAA3 internal error: "
        + "tried to convert non-array to an array."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I of void type?
   */
  public boolean isVoidType() {
    return false;
  }

  public SymTypeVoid asVoidType() {
    throw new UnsupportedOperationException("0xFDAA4 internal error: "
        + "tried to convert non-void-type to a void type."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I of null type?
   */
  public boolean isNullType() {
    return false;
  }

  public SymTypeOfNull asNullType() {
    throw new UnsupportedOperationException("0xFDAA5 internal error: "
        + "tried to convert non-null-type to a null-type."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I an object type? (e.g. "String", "Person")
   */
  public boolean isObjectType() {
    return false;
  }

  public SymTypeOfObject asObjectType() {
    throw new UnsupportedOperationException("0xFDAA6 internal error: "
        + "tried to convert non-object-type to an object-type."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I a regex type (e.g. 'R"rege(x(es)?|xps?)"')
   */
  public boolean isRegExType() {
    return false;
  }

  public SymTypeOfRegEx asRegExType() {
    throw new UnsupportedOperationException("0xFDAAC internal error: "
        + "tried to convert non-regex-type to a regex type."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I a function type (e.g. {@code "String -> Integer"})
   */
  public boolean isFunctionType() {
    return false;
  }

  public SymTypeOfFunction asFunctionType() {
    throw new UnsupportedOperationException("0xFDAA7 internal error: "
        + "tried to convert non-function-type to a function type."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I an SIUnit type (e.g., "[km/h]")
   */
  public boolean isSIUnitType() {
    return false;
  }

  public SymTypeOfSIUnit asSIUnitType() {
    throw new UnsupportedOperationException("0xFDAAC internal error: "
        + "tried to convert non-SIUnit type to a SIUnit type."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I a numeric with SIUnit type (e.g., {@code "[km/h]<float>"})
   */
  public boolean isNumericWithSIUnitType() {
    return false;
  }

  public SymTypeOfNumericWithSIUnit asNumericWithSIUnitType() {
    throw new UnsupportedOperationException("0xFDAAD internal error: "
        + "tried to convert non-numeric-with-SIUnit type "
        + "to a numeric-with-SIUnit type."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I a tuple type (e.g. "(String, int)")
   */
  public boolean isTupleType() {
    return false;
  }

  public SymTypeOfTuple asTupleType() {
    throw new UnsupportedOperationException("0xFDAAE internal error: "
        + "tried to convert non-tuple-type to a tuple type."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I an union type (e.g. "(A|B)")?
   */
  public boolean isUnionType() {
    return false;
  }

  public SymTypeOfUnion asUnionType() {
    throw new UnsupportedOperationException("0xFDAA8 internal error: "
        + "tried to convert non-union-type to a union-type."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I an intersection type (e.g. {@code "(A&B)"})
   */
  public boolean isIntersectionType() {
    return false;
  }

  public SymTypeOfIntersection asIntersectionType() {
    throw new UnsupportedOperationException("0xFDAA9 internal error: "
        + "tried to convert non-intersection-type to an intersection-type."
        + " Actual: " + this.printFullName());
  }

  /**
   * Can I not have a type derived from (e.g. "1 - student")?
   */
  public boolean isObscureType() {
    return false;
  }

  public SymTypeObscure asObscureType() {
    throw new UnsupportedOperationException("0xFDAAA internal error: "
        + "tried to convert non-obscure-type to an obscure-type."
        + " Actual: " + this.printFullName());
  }

  /**
   * Am I a wildcard (s. generics)?
   */
  public boolean isWildcard() {
    return false;
  }

  public SymTypeOfWildcard asWildcard() {
    throw new UnsupportedOperationException("0xFDAAB internal error: "
        + "tried to convert non-wildcard-type to a wildcard-type."
        + " Actual: " + this.printFullName());
  }

  public SymTypeExpression deepClone() {
    return new SymTypeDeepCloneVisitor().calculate(this);
  }

  public abstract boolean deepEquals(SymTypeExpression sym);

  @Override
  public int compareTo(SymTypeExpression o) {
    return SymTypeExpressionComparator.compareSymTypeExpressions(this, o);
  }

  /**
   * @deprecated TypeSymbols are to be found in the corresponding subclasses,
   * however, not every subclass will have a type symbol
   */
  @Deprecated(forRemoval = true)
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
    throw new UnsupportedOperationException(
        "0xFDFDF internal error: getTypeInfo called"
        + ", but no typeinfo available. Presumably hasTypeInfo() missing?"
        + " Type: " + printFullName()
    );
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
