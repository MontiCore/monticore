/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.subConstraints;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTArguments;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.tf.odrulegeneration._ast.ASTMatchingObject;
import de.monticore.tf.odrules.HierarchyHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Alexander Wilts on 16.01.2017.
 * <p>
 * This visitor replaces elements with stereotype 'not' or 'optional' with a new variable with suffix '_candAsOptional'.
 * Additionally the visitor adds a '.get()' call behind that variable.
 */
public class AddSuffixToOptionalsVisitor implements
        ExpressionsBasisVisitor2, CommonExpressionsVisitor2 {

    protected List<ASTMatchingObject> lhsObjects;
    protected HierarchyHelper hierarchyHelper;
    public Set<Integer> handledNodes;

    public AddSuffixToOptionalsVisitor(List<ASTMatchingObject> lhsObjects, HierarchyHelper hierarchyHelper) {
        super();
        this.hierarchyHelper = hierarchyHelper;
        this.lhsObjects = lhsObjects;
        handledNodes = new HashSet<>();
    }

    @Override
    public void visit(ASTArguments node) {
        List<ASTExpression> newExpressions = new ArrayList<>();
        for(ASTExpression expr : node.getExpressionList()) {
            if(expr instanceof ASTNameExpression) {
                ASTNameExpression nameExpr = (ASTNameExpression) expr;
                newExpressions.add(replaceNode(nameExpr));
            } else {
                newExpressions.add(expr);
            }
        }
        node.setExpressionList(newExpressions);
    }

    @Override
    public void visit(ASTFieldAccessExpression node) {
        if (node.getExpression() instanceof ASTNameExpression) {
            node.setExpression(replaceNode((ASTNameExpression) node.getExpression()));
        }
    }

    @Override
    public void visit(ASTBooleanNotExpression node) {
        if (node.getExpression() instanceof ASTNameExpression) {
            node.setExpression(replaceNode((ASTNameExpression) node.getExpression()));
        }
    }

    @Override
    public void visit(ASTLogicalNotExpression node) {
        if (node.getExpression() instanceof ASTNameExpression) {
            node.setExpression(replaceNode((ASTNameExpression) node.getExpression()));
        }
    }

    @Override
    public void visit(ASTEqualsExpression node) {
        if (node.getLeft() instanceof ASTNameExpression) {
            node.setLeft(replaceNode((ASTNameExpression) node.getLeft()));
        }
        if (node.getRight() instanceof ASTNameExpression) {
            node.setRight(replaceNode((ASTNameExpression) node.getRight()));
        }
    }

    @Override
    public void visit(ASTBooleanAndOpExpression node) {
        if (node.getLeft() instanceof ASTNameExpression) {
            node.setLeft(replaceNode((ASTNameExpression) node.getLeft()));
        }
        if (node.getRight() instanceof ASTNameExpression) {
            node.setRight(replaceNode((ASTNameExpression) node.getRight()));
        }
    }

    @Override
    public void visit(ASTBooleanOrOpExpression node) {
        if (node.getLeft() instanceof ASTNameExpression) {
            node.setLeft(replaceNode((ASTNameExpression) node.getLeft()));
        }
        if (node.getRight() instanceof ASTNameExpression) {
            node.setRight(replaceNode((ASTNameExpression) node.getRight()));
        }
    }

    @Override
    public void visit(ASTPlusExpression node) {
        if (node.getLeft() instanceof ASTNameExpression) {
            node.setLeft(replaceNode((ASTNameExpression) node.getLeft()));
        }
        if (node.getRight() instanceof ASTNameExpression) {
            node.setRight(replaceNode((ASTNameExpression) node.getRight()));
        }
    }

    @Override
    public void visit(ASTBracketExpression node) {
        if (node.getExpression() instanceof ASTNameExpression) {
            node.setExpression(replaceNode((ASTNameExpression) node.getExpression()));
        }
    }

    private ASTExpression replaceNode(ASTNameExpression node) {
        for (ASTMatchingObject o : lhsObjects) {
            if (node.getName().equals(o.getObjectName())) {
                if (hierarchyHelper.isWithinOptionalStructure(o.getObjectName())
                        || hierarchyHelper.isWithinNegativeStructure(o.getObjectName())) {
                    return buildNode(node);
                }
                break;
            }
        }

        return node;
    }

    private ASTExpression buildNode(ASTNameExpression node) {

        ASTNameExpression nameExpr = ExpressionsBasisMill.nameExpressionBuilder()
                .setName(node.getName() + "_candAsOptional")
                .build();

        ASTFieldAccessExpression getExpr = CommonExpressionsMill.fieldAccessExpressionBuilder()
                .setExpression(nameExpr)
                .setName("get")
                .build();

        ASTCallExpression callExpr = CommonExpressionsMill.callExpressionBuilder()
                .setExpression(getExpr)
                .setArguments(CommonExpressionsMill.argumentsBuilder().build())
                .build();

        return callExpr;
    }
}
