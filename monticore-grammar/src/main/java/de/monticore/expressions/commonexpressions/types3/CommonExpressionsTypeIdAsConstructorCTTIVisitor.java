/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions.types3;

/**
 * This visitor allows the use of type identifiers "as" the types constructors,
 * e.g., pack.age.Foo(1) is accepted if the constructor
 * pack.age.Foo::Foo(int) exists.
 *
 * @deprecated feature is now part of the super class (if enabled)
 */
@Deprecated(forRemoval = true)
public class CommonExpressionsTypeIdAsConstructorCTTIVisitor extends
    CommonExpressionsCTTIVisitor {

}
