package de.monticore.expressions.prettyprint;

import de.monticore.expressions.javaclassexpressions.JavaClassExpressionsMill;
import de.monticore.expressions.javaclassexpressions._ast.ASTJavaClassExpressionsNode;
import de.monticore.expressions.javaclassexpressions._visitor.JavaClassExpressionsTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;

public class JavaClassExpressionsFullPrettyPrinter extends CommonExpressionsFullPrettyPrinter{

    private JavaClassExpressionsTraverser traverser;

    @Override
    public JavaClassExpressionsTraverser getTraverser() {
        return traverser;
    }

    public void setTraverser(JavaClassExpressionsTraverser traverser) {
        super.setTraverser(traverser);
    }

    public JavaClassExpressionsFullPrettyPrinter(IndentPrinter printer) {
        super(printer);
        this.traverser = JavaClassExpressionsMill.traverser();
        CommonExpressionsPrettyPrinter commonExpression = new CommonExpressionsPrettyPrinter(printer);
        traverser.setCommonExpressionsHandler(commonExpression);
        traverser.addCommonExpressionsVisitor(commonExpression);
        ExpressionsBasisPrettyPrinter expressionBasis = new ExpressionsBasisPrettyPrinter(printer);
        traverser.setExpressionsBasisHandler(expressionBasis);
        traverser.addExpressionsBasisVisitor(expressionBasis);
        JavaClassExpressionsPrettyPrinter javaClassExpression = new JavaClassExpressionsPrettyPrinter(printer);
        traverser.setJavaClassExpressionsHandler(javaClassExpression);
        traverser.addJavaClassExpressionsVisitor(javaClassExpression);
        MCBasicsPrettyPrinter basic = new MCBasicsPrettyPrinter(printer);
        traverser.addMCBasicsVisitor(basic);
    }
    /**
     * This method prettyprints a given node from type grammar.
     *
     * @param a A node from type grammar.
     * @return String representation.
     */
    public String prettyprint(ASTJavaClassExpressionsNode a) {
        getPrinter().clearBuffer();
        a.accept(getTraverser());
        return getPrinter().getContent();
    }
}
