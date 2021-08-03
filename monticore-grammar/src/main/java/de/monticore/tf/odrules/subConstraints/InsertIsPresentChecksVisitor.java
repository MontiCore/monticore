/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.subConstraints;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.tf.odrulegeneration._ast.ASTMatchingObject;
import de.monticore.tf.odrules.HierarchyHelper;
import de.monticore.tf.odrules.ODRulesMill;
import de.monticore.tf.odrules._visitor.ODRulesTraverser;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Alexander Wilts on 16.01.2017.
 * <p>
 * This visitor inserts isPresent()-checks into expressions that contain optional variables.
 * <p>
 * Let $O be an optional variable in this expression: isValid($O).
 * In case that no value is present for $O, the evaluation of this expression would produce a NoSuchElementException.
 * In order to prevent this we add an isPresent()-check to the expression like this: !$O.isPresent || isValid($O)
 * <p>
 * Additionally we have to consider the context of the optional.
 * The isPresent-Check has to be implemented differently depending on whether the optional is used inside an OR-Expression
 * or an AND-Expression.
 * <p>
 * Examples for normal variable $A and optional variable $O:
 * <p>
 * $A && $O results in $A && (!$O.isPresent || isValid($O))
 * $A || $O results in $A || ($O.isPresent && isValid($O))
 */
public class InsertIsPresentChecksVisitor implements CommonExpressionsVisitor2 {

    public boolean optionalInOrPresent;

    protected List<ASTMatchingObject> lhsObjects;
    protected HierarchyHelper hierarchyHelper;
    public ASTExpression subConstraint;


    public InsertIsPresentChecksVisitor(List<ASTMatchingObject> lhsObjects, HierarchyHelper hierarchyHelper, ASTExpression subConstraint) {
        super();
        this.hierarchyHelper = hierarchyHelper;
        optionalInOrPresent = false;
        this.lhsObjects = lhsObjects;
        this.subConstraint = subConstraint;
    }



    @Override
    public void visit(ASTCallExpression node) {
        if(node.equals(subConstraint) && checkNode(node)) {
            subConstraint = replaceNode(node);
        }
    }

    @Override
    public void visit(ASTFieldAccessExpression node) {
        if(node.equals(subConstraint) && checkNode(node)) {
            subConstraint = replaceNode(node);
        }
    }

    @Override
    public void visit(ASTBooleanNotExpression node) {
        if(node.equals(subConstraint) && checkNode(node)) {
            subConstraint = replaceNode(node);
        }
    }

    @Override
    public void visit(ASTLogicalNotExpression node) {
        if(node.equals(subConstraint) && checkNode(node)) {
            subConstraint = replaceNode(node);
        }
    }

    @Override
    public void visit(ASTEqualsExpression node) {
        if(node.equals(subConstraint) && checkNode(node)) {
            subConstraint = replaceNode(node);
        }
    }

    @Override
    public void visit(ASTPlusExpression node) {
        if(node.equals(subConstraint) && checkNode(node)) {
            subConstraint = replaceNode(node);
        }
    }

    @Override
    public void visit(ASTBracketExpression node) {
        if(node.equals(subConstraint) && checkNode(node)) {
            subConstraint = replaceNode(node);
        }
    }

    @Override
    public void visit(ASTBooleanAndOpExpression node) {
        if(checkNode(node.getRight())) {
            node.setRight(replaceNode(node.getRight()));
        }
        if(checkNode(node.getLeft())) {
            node.setLeft(replaceNode(node.getLeft()));
        }
    }

    @Override
    public void visit(ASTBooleanOrOpExpression node) {
        if(checkNode(node.getRight())) {
            node.setRight(replaceNode(node.getRight()));
        }
        if(checkNode(node.getLeft())) {
            node.setLeft(replaceNode(node.getLeft()));
        }
    }


    private boolean checkNode(ASTExpression node) {
        FindSubExpressionVisitor subExprVisitor = new FindSubExpressionVisitor();
        CommonExpressionsTraverser t = CommonExpressionsMill.inheritanceTraverser();
        t.add4CommonExpressions(subExprVisitor);
        node.accept(t);

        if(!subExprVisitor.subExpressionIsPresent) {
            return true;
        }

        return false;
    }

    public ASTExpression replaceNode(ASTExpression node) {
        CommonExpressionsTraverser findOptsVisitorTraverser = CommonExpressionsMill.inheritanceTraverser();
        FindOptionalsVisitor findOptsVisitor = new FindOptionalsVisitor(lhsObjects, hierarchyHelper);
        findOptsVisitorTraverser.add4ExpressionsBasis(findOptsVisitor);
        node.accept(findOptsVisitorTraverser);
        if(!findOptsVisitor.optVars.isEmpty()) {
            CommonExpressionsTraverser contextVisitorTraverser = CommonExpressionsMill.inheritanceTraverser();
            CalculateContextVisitor contextVisitor = new CalculateContextVisitor(node);
            contextVisitorTraverser.add4CommonExpressions(contextVisitor);
            subConstraint.accept(contextVisitorTraverser);
            if (contextVisitor.inOrContext) {
                optionalInOrPresent = true;
                return insertIsPresentCheckForOr(node, findOptsVisitor.optVars);
            } else {
                return insertDefaultIsPresentCheck(node, findOptsVisitor.optVars);
            }
        }


        return node;
    }


    private ASTBracketExpression insertDefaultIsPresentCheck(ASTExpression node, Set<ASTMatchingObject> optVars) {
        ODRulesTraverser t = ODRulesMill.inheritanceTraverser();
        AddSuffixToOptionalsVisitor suffixVisitor = new AddSuffixToOptionalsVisitor(lhsObjects, hierarchyHelper);
        t.add4ExpressionsBasis(suffixVisitor);
        t.add4CommonExpressions(suffixVisitor);
        node.accept(t);

        ASTBooleanOrOpExpression mainOrExpr = CommonExpressionsMill.booleanOrOpExpressionBuilder().uncheckedBuild();
        mainOrExpr.setRight(node);

        ASTBracketExpression bracketExpr = CommonExpressionsMill.bracketExpressionBuilder()
                .setExpression(mainOrExpr)
                .build();

        Iterator<ASTMatchingObject> i = optVars.iterator();
        while (i.hasNext()) {
            ASTMatchingObject o = i.next();


            ASTCallExpression callExpr = buildIsPresentCheck(o);


            ASTLogicalNotExpression notExpr = CommonExpressionsMill.logicalNotExpressionBuilder()
                    .setExpression(callExpr)
                    .build();


            if (i.hasNext()) {
                ASTBooleanOrOpExpression orExpr = CommonExpressionsMill.booleanOrOpExpressionBuilder().uncheckedBuild();
                orExpr.setRight(notExpr);
                mainOrExpr.setLeft(orExpr);
                mainOrExpr = orExpr;
            } else {
                mainOrExpr.setLeft(notExpr);
            }
        }

        return bracketExpr;
    }


    private ASTBracketExpression insertIsPresentCheckForOr(ASTExpression node, Set<ASTMatchingObject> optVars) {
        ODRulesTraverser t = ODRulesMill.inheritanceTraverser();
        AddSuffixToOptionalsVisitor suffixVisitor = new AddSuffixToOptionalsVisitor(lhsObjects, hierarchyHelper);
        t.add4ExpressionsBasis(suffixVisitor);
        t.add4CommonExpressions(suffixVisitor);
        node.accept(t);

        ASTBooleanAndOpExpression mainAndExpr = CommonExpressionsMill.booleanAndOpExpressionBuilder().uncheckedBuild();
        mainAndExpr.setRight(node);

        ASTBracketExpression bracketExpr = CommonExpressionsMill.bracketExpressionBuilder()
                .setExpression(mainAndExpr)
                .build();

        Iterator<ASTMatchingObject> i = optVars.iterator();
        while (i.hasNext()) {
            ASTMatchingObject o = i.next();


            ASTCallExpression callExpr = buildIsPresentCheck(o);


            if (i.hasNext()) {
                ASTBooleanAndOpExpression orExpr = CommonExpressionsMill.booleanAndOpExpressionBuilder()
                        .setRight(callExpr)
                        .build();
                mainAndExpr.setLeft(orExpr);
                mainAndExpr = orExpr;
            } else {
                mainAndExpr.setLeft(callExpr);
            }
        }

        return bracketExpr;
    }

    private ASTCallExpression buildIsPresentCheck(ASTMatchingObject o) {
        ASTNameExpression optExpr = CommonExpressionsMill.nameExpressionBuilder()
                .setName(o.getObjectName() + "_candAsOptional")
                .build();

        ASTFieldAccessExpression isPresentExpr = CommonExpressionsMill.fieldAccessExpressionBuilder()
                .setName("isPresent")
                .setExpression(optExpr)
                .build();

        return CommonExpressionsMill.callExpressionBuilder()
                .setExpression(isPresentExpr)
                .setArguments(CommonExpressionsMill.argumentsBuilder().uncheckedBuild())
                .setName("")
                .build();
    }

}
