// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMinusPrefixExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;

import java.util.ArrayList;
import java.util.List;

/**
 * This visitor attempts to find "noSpace(...)" calls and collects the integer literals used for arguments.
 * <p>
 * - noSpace() will target the previous token/rule component
 * - noSpace(n) will target the nth token/rule component to the right of it
 * - noSpace(-n) will target the nth token/rule component to the left of it
 * - {noSpace(2)}? "-" "-" only accepts "--"
 * <p>
 * An alternative to noSpace is the splittoken control directive (4.3 of the handbook),
 * which the pretty printer needs not be aware of.
 */
public class NoSpacePredicateVisitor implements CommonExpressionsVisitor2, ExpressionsBasisVisitor2, MCCommonLiteralsVisitor2 {

  /**
   * The indexes of tokens which were targeted by the noSpace method
   */
  protected List<Integer> tokenIndexes;

  /**
   * Whether a CallExpression was found
   */
  protected boolean isCallExpression;

  /**
   * Whether the NameExpression of the CallExpression calls the "noSpace" directive
   */
  protected boolean isNoSpace;

  /**
   * Whether arguments are being collecting
   */
  protected boolean isLookingForArgs;


  public List<Integer> getTokenIndexes() {
    return tokenIndexes;
  }

  public boolean isNoSpaceDirective() {
    return this.isNoSpace;
  }

  public void reset() {
    this.tokenIndexes = new ArrayList<>();
    this.isNoSpace = false;
    this.isLookingForArgs = false;
    this.isCallExpression = false;
  }


  @Override
  public void visit(ASTCallExpression node) {
    this.isCallExpression = true;
  }

  // Negative numbers may be parsed as a minus prefix expression
  protected int minus = 1;

  @Override
  public void visit(ASTMinusPrefixExpression node) {
    this.minus = -1;
  }

  @Override
  public void endVisit(ASTMinusPrefixExpression node) {
    this.minus = 1;
  }

  @Override
  public void visit(ASTNatLiteral node) {
    if (this.isLookingForArgs)
      this.tokenIndexes.add(this.minus * node.getValue());
  }

  @Override
  public void visit(ASTSignedNatLiteral node) {
    if (this.isLookingForArgs)
      this.tokenIndexes.add(this.minus * (node.isNegative() ? -1 : 1) * node.getValue());
  }

  @Override
  public void visit(ASTNameExpression node) {
    if (this.isCallExpression && node.getName().equals("noSpace")) {
      this.isNoSpace = true;
      this.isLookingForArgs = true;
    }
  }


  @Override
  public void endVisit(ASTCallExpression node) {
    this.isCallExpression = false;
    this.isLookingForArgs = false;
  }

}
