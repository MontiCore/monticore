/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions.types3;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;
import de.monticore.types3.util.TypeContextCalculator;

import java.util.List;
import java.util.Optional;

/**
 * This visitor allows the use of type identifiers "as" the types constructors,
 * e.g., pack.age.Foo(1) is accepted if the constructor
 * pack.age.Foo::Foo(int) exists.
 *
 * @deprecated use {@link CommonExpressionsTypeIdAsConstructorCTTIVisitor}
 */
@Deprecated
public class CommonExpressionsTypeIdAsConstructorTypeVisitor extends
    CommonExpressionsTypeVisitor {

}
