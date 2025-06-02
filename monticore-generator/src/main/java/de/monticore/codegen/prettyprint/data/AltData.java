// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint.data;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._ast.ASTConstantsMCCommonLiterals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AltData implements Comparable<AltData> {

  /**
   * Data for inner rule components
   */
  protected final List<PPGuardComponent> componentList = new ArrayList<>();

  /**
   * Number of required referenced NonTerminals (Default and Plus)
   */
  protected int required = 0;

  /**
   * Number of optional referenced NonTerminals (Optional and Star)
   */
  protected int optional = 0;

  /**
   * Guarding expressions (such as isPresent, isEmpty, ...) for this alt
   */
  protected final List<ASTExpression> expressionList = new ArrayList<>();

  /**
   * Marks that iterator-based list access is used
   */
  protected boolean isListReady;

  /**
   * Indexes of tokens targeted by a noSpace control directive
   */
  protected List<Integer> noSpaceTokens = new ArrayList<>();

  public List<PPGuardComponent> getComponentList() {
    return componentList;
  }

  public List<Integer> getNoSpaceTokens() {
    return noSpaceTokens;
  }

  public int getRequired() {
    return required;
  }

  public int getOptional() {
    return optional;
  }

  public List<ASTExpression> getExpressionList() {
    return expressionList;
  }

  public void setRequired(int required) {
    this.required = required;
  }

  public void setOptional(int optional) {
    this.optional = optional;
  }


  /**
   * Returns the expressions for this Alt in conjunction
   *
   * @return the printed
   */
  public String getExpressionConj() {
    return CD4CodeMill.prettyPrint(reduceToAnd(getExpressionList()), false);
  }

  public boolean isAlwaysTrue() {
    return getExpressionList().stream().allMatch(expression -> expression == TRUE_EXPRESSION);
  }

  /**
   * The comparison of Alts ensures that ones with more nonterminals are checked first.
   * For example, the empty statechart body ";" is ranked below printing the inner elements.
   * {@inheritDoc}
   */
  @Override
  public int compareTo(AltData o) {
    int ret = Integer.compare(this.getRequired(), o.getRequired());
    if (ret == 0)
      return Integer.compare(this.getOptional(), o.getOptional());
    return ret;
  }

  @Override
  public String toString() {
    return "AltData{" +
            "componentList=" + componentList +
            ", required=" + required +
            ", optional=" + optional +
            ", isListReady=" + isListReady +
            ", #expressionList=" + expressionList.size() +
            '}';
  }


  public void markListReady() {
    this.isListReady = true;
  }

  public boolean isListReady() {
    return isListReady;
  }

  public final static ASTExpression TRUE_EXPRESSION = CommonExpressionsMill.literalExpressionBuilder()
          .setLiteral(MCCommonLiteralsMill.booleanLiteralBuilder().setSource(
                  ASTConstantsMCCommonLiterals.TRUE).build()).build();

  public final static ASTExpression FALSE_EXPRESSION = CommonExpressionsMill.literalExpressionBuilder()
          .setLiteral(MCCommonLiteralsMill.booleanLiteralBuilder().setSource(
                  ASTConstantsMCCommonLiterals.FALSE).build()).build();


  public static ASTExpression reduceToAnd(Collection<ASTExpression> expressions) {
    return expressions.stream().reduce(TRUE_EXPRESSION, (expression, expression2) ->
            expression == TRUE_EXPRESSION ? expression2 :
                    (expression2 == TRUE_EXPRESSION ? expression :
                            CommonExpressionsMill.booleanAndOpExpressionBuilder().setLeft(expression).setRight(expression2)
                                    .setOperator("&&")
                                    .build()));
  }

  public static ASTExpression reduceToOr(Collection<ASTExpression> expressions) {
    return expressions.stream().reduce(FALSE_EXPRESSION, (expression, expression2) ->
            expression == FALSE_EXPRESSION ? expression2 :
                    (expression2 == FALSE_EXPRESSION ? expression :
                            CommonExpressionsMill.booleanOrOpExpressionBuilder().setLeft(expression).setRight(expression2)
                                    .setOperator("||")
                                    .build()));
  }
}
