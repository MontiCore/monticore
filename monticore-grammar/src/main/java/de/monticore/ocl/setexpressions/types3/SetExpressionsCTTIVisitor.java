/* (c) https://github.com/MontiCore/monticore */
package de.monticore.ocl.setexpressions.types3;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.setexpressions._ast.ASTSetEnumeration;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsHandler;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsTraverser;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory;
import de.monticore.types3.generics.util.CompileTimeTypeCalculator;

import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypeExpressionFactory.createBottomType;
import static de.monticore.types.check.SymTypeExpressionFactory.createFunction;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createTopType;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeVariable;

public class SetExpressionsCTTIVisitor extends SetExpressionsTypeVisitor
    implements SetExpressionsHandler {

  protected SetExpressionsTraverser traverser;

  @Override
  public SetExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(SetExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  // these functions types are used for the CTTI in SetEnumerations
  // they are initialized on demand, as they require symbols for Set, List

  /** {@code <T> (T...) -> Set<T>} */
  protected SymTypeOfFunction setEnumerationFunc;

  /** {@code <T> (T...) -> List<T>} */
  protected SymTypeOfFunction listEnumerationFunc;

  protected SymTypeOfFunction getSetEnumerationFunc() {
    if (setEnumerationFunc == null) {
      SymTypeVariable typeVar = createTypeVariable(
          createBottomType(),
          createTopType()
      );
      setEnumerationFunc = createFunction(
          MCCollectionSymTypeFactory.createSet(typeVar),
          List.of(typeVar),
          true
      );
    }
    return setEnumerationFunc;
  }

  protected SymTypeOfFunction getListEnumerationFunc() {
    if (listEnumerationFunc == null) {
      SymTypeVariable typeVar = createTypeVariable(
          createBottomType(),
          createTopType()
      );
      listEnumerationFunc = createFunction(
          MCCollectionSymTypeFactory.createList(typeVar),
          List.of(typeVar),
          true
      );
    }
    return listEnumerationFunc;
  }

  @Override
  public void handle(ASTSetEnumeration expr) {
    Optional<List<ASTExpression>> containedExprs =
        getContainedExpressions(expr);

    if (containedExprs.isEmpty()) {
      getType4Ast().setTypeOfExpression(expr, createObscureType());
    }
    else {
      SymTypeOfFunction exprFunc;
      if (expr.isSet()) {
        exprFunc = getSetEnumerationFunc();
      }
      else {
        exprFunc = getListEnumerationFunc();
      }
      CompileTimeTypeCalculator.handleCall(
          expr,
          exprFunc.getWithFixedArity(containedExprs.get().size()),
          containedExprs.get(),
          getTraverser(), getType4Ast(), getInferenceContext4Ast()
      );
    }
    if (getType4Ast().hasPartialTypeOfExpression(expr) &&
        !getType4Ast().getPartialTypeOfExpr(expr).isObscureType()
    ) {
      visit(expr);
      traverse(expr);
      endVisit(expr);
    }
  }

}
