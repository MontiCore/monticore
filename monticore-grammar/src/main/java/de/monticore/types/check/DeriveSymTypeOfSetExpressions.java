/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.setexpressions._ast.ASTIntersectionExpressionInfix;
import de.monticore.expressions.setexpressions._ast.ASTIsInExpression;
import de.monticore.expressions.setexpressions._ast.ASTSetInExpression;
import de.monticore.expressions.setexpressions._ast.ASTUnionExpressionInfix;
import de.monticore.expressions.setexpressions._visitor.SetExpressionsVisitor;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;

import java.util.List;
import java.util.Optional;

import static de.monticore.types.check.SymTypeConstant.unbox;
import static de.monticore.types.check.TypeCheck.compatible;
import static de.monticore.types.check.TypeCheck.isSubtypeOf;

/**
 * This Visitor can calculate a SymTypeExpression (type) for the expressions in SetExpressions
 * It can be combined with other expressions in your language by creating a DelegatorVisitor
 */
public class DeriveSymTypeOfSetExpressions extends DeriveSymTypeOfExpression implements SetExpressionsVisitor {

    private SetExpressionsVisitor realThis;

    protected final List<String> collections = Lists.newArrayList("List", "Set");

    public DeriveSymTypeOfSetExpressions() {
        this.realThis = this;
    }

    @Override
    public void setRealThis(SetExpressionsVisitor realThis) {
        this.realThis = realThis;
    }

    @Override
    public SetExpressionsVisitor getRealThis() {
        return realThis;
    }

    @Override
    public void traverse(ASTIsInExpression node) {
        //e isin E checks whether e is an Element in Set E and can only be calculated if e is a subtype or the same type as the set type
        Optional<SymTypeExpression> wholeResult = calculateIsInExpression(node);
        storeResultOrLogError(wholeResult, node, "0xA0288");
    }

    protected Optional<SymTypeExpression> calculateIsInExpression(ASTIsInExpression node) {
        SymTypeExpression elemResult = acceptThisAndReturnSymTypeExpressionOrLogError(node.getElem(), "0xA0286");
        SymTypeExpression setResult = acceptThisAndReturnSymTypeExpressionOrLogError(node.getSet(), "0xA0287");
        ;
        Optional<SymTypeExpression> wholeResult = Optional.empty();

        boolean correct = false;
        for (String s : collections) {
            if (setResult.isGenericType() && setResult.getTypeInfo().getName().equals(s)) {
                correct = true;
            }
        }
        if (correct) {
            SymTypeOfGenerics genericResult = (SymTypeOfGenerics) setResult;
            if (unbox(elemResult.print()).equals(unbox(genericResult.getArgument(0).print()))) {
                wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
            }else if (isSubtypeOf(elemResult, genericResult.getArgument(0))) {
                wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
            }else if(elemResult.deepEquals(genericResult.getArgument(0))) {
                wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
            }
        }
        return wholeResult;
    }

    @Override
    public void traverse(ASTSetInExpression node) {
        Optional<SymTypeExpression> wholeResult = calculateSetInExpression(node);
        storeResultOrLogError(wholeResult, node, "0xA0291");
    }

    protected Optional<SymTypeExpression> calculateSetInExpression(ASTSetInExpression node) {
        SymTypeExpression elemResult = acceptThisAndReturnSymTypeExpressionOrLogError(node.getElem(), "0xA0289");
        SymTypeExpression setResult = acceptThisAndReturnSymTypeExpressionOrLogError(node.getSet(), "0xA0290");
        Optional<SymTypeExpression> wholeResult = Optional.empty();

        boolean correct = false;
        for (String s : collections) {
            if (setResult.isGenericType() && setResult.getTypeInfo().getName().equals(s)) {
                correct = true;
            }
        }
        if (correct) {
            SymTypeOfGenerics genericResult = (SymTypeOfGenerics) setResult;
            if (compatible(genericResult.getArgument(0), elemResult)) {
                wholeResult = Optional.of(genericResult.getArgument(0).deepClone());
            }
        }
        return wholeResult;
    }

    @Override
    public void traverse(ASTUnionExpressionInfix node) {
        //union of two sets -> both sets need to have the same type or their types need to be sub/super types
        Optional<SymTypeExpression> wholeResult = calculateUnionExpressionInfix(node);
        storeResultOrLogError(wholeResult, node, "0xA0292");
    }

    protected Optional<SymTypeExpression> calculateUnionExpressionInfix(ASTUnionExpressionInfix node) {
        return calculateUnionAndIntersectionInfix(node, node.getLeft(), node.getRight());
    }

    @Override
    public void traverse(ASTIntersectionExpressionInfix node) {
        //intersection of two sets -> both sets need to have the same type or their types need to be sub/super types
        Optional<SymTypeExpression> wholeResult = calculateIntersectionExpressionInfix(node);
        storeResultOrLogError(wholeResult, node, "0xA0293");
    }

    protected Optional<SymTypeExpression> calculateIntersectionExpressionInfix(ASTIntersectionExpressionInfix node) {
        return calculateUnionAndIntersectionInfix(node, node.getLeft(), node.getRight());
    }

    public Optional<SymTypeExpression> calculateUnionAndIntersectionInfix(ASTExpression expr, ASTExpression leftExpr, ASTExpression rightExpr) {
        SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(leftExpr, "0xA0294");
        SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(rightExpr, "0xA0295");
        Optional<SymTypeExpression> wholeResult = Optional.empty();

    if(rightResult.isGenericType()&&leftResult.isGenericType()){
      SymTypeOfGenerics leftGeneric = (SymTypeOfGenerics) leftResult;
      SymTypeOfGenerics rightGeneric = (SymTypeOfGenerics) rightResult;
      String left = leftGeneric.getTypeInfo().getName();
      String right = rightGeneric.getTypeInfo().getName();
      if(collections.contains(left) && unbox(left).equals(unbox(right))) {
        if (compatible(leftGeneric.getArgument(0), rightGeneric.getArgument(0))) {
            OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate(left);
            loader.setEnclosingScope(getScope(expr.getEnclosingScope()));
            wholeResult = Optional.of(SymTypeExpressionFactory.createGenerics(loader,leftGeneric.getArgument(0).deepClone()));
        } else if(compatible(rightGeneric.getArgument(0), leftGeneric.getArgument(0))) {
            OOTypeSymbolSurrogate loader = new OOTypeSymbolSurrogate(right);
            loader.setEnclosingScope(getScope(expr.getEnclosingScope()));
            wholeResult = Optional.of(SymTypeExpressionFactory.createGenerics(loader,rightGeneric.getArgument(0).deepClone()));
        }
      }
    }
    return wholeResult;
  }
}
