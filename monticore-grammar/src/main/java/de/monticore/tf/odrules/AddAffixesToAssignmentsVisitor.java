/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.tf.odrulegeneration._ast.ASTMatchingObject;

import java.util.List;

/**
 * Created by Alexander Wilts on 16.01.2017.
 * <p>
 * This visitor adds prefix "m." to normal nodes in assignments.
 */
public class AddAffixesToAssignmentsVisitor implements
        ExpressionsBasisVisitor2, CommonExpressionsVisitor2 {

    protected List<ASTMatchingObject> lhsObjects;
    protected HierarchyHelper hierarchyHelper;
    public ASTExpression rootExp;

    public AddAffixesToAssignmentsVisitor(List<ASTMatchingObject> lhsObjects, HierarchyHelper hierarchyHelper, ASTExpression rootExp) {
        super();
        this.hierarchyHelper = hierarchyHelper;
        this.lhsObjects = lhsObjects;
        this.rootExp = rootExp;
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

    @Override
    public void visit(ASTNameExpression node) {
        if(node.equals(rootExp))
            //if(!node.getName().equals("m"))
            rootExp = replaceNode(node);
    }

    /**
     * Prefix "m." is added for nodes.
     * Suffix .get() for optional variables
     */
    private ASTExpression replaceNode(ASTNameExpression node) {
        for (ASTMatchingObject o : lhsObjects) {
            if (node.getName().equals(o.getObjectName())) {

                ASTNameExpression nameExpr = CommonExpressionsMill.nameExpressionBuilder()
                        .setName("m")
                        .build();

                ASTFieldAccessExpression resultExpr = CommonExpressionsMill.fieldAccessExpressionBuilder()
                        .setExpression(nameExpr)
                        .setName(node.getName())
                        .build();

                if(hierarchyHelper.isWithinOptionalStructure(o.getObjectName())
                        || hierarchyHelper.isWithinNegativeStructure(o.getObjectName())) {

                    ASTFieldAccessExpression getExpr = CommonExpressionsMill.fieldAccessExpressionBuilder()
                            .setExpression(resultExpr)
                            .setName("get")
                            .build();

                    ASTCallExpression callExpr = CommonExpressionsMill.callExpressionBuilder()
                            .setExpression(getExpr)
                            .setArguments(CommonExpressionsMill.argumentsBuilder().uncheckedBuild())
                            .build();

                    return callExpr;
                }

                return resultExpr;
            }
        }

        return node;
    }


}
