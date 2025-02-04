// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.expressionsbasis.types3.util;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

/**
 * Tests whether an expression can be considered an LValue
 * for corresponding assignment CoCos.
 * Expressions have a type and a value category.
 * In our type systems values can be categorised by whether they have
 * an identity, i.e., an address in memory, and thus can be assigned to.
 * Values that can be assigned to are lvalues,
 * examples include variables, e.g.,
 * int i = 0; // i is a variable and thus a lvalue
 * int[] is = new int[3]; is[0] = 0; // is[0] is a variable and thus a lvalue
 * Note, not every lvalue can be assigned to,
 * e.g., a final variable that has been assigned to already.
 */
public interface ILValueRelations {

  boolean isLValue(ASTExpression expression);

}