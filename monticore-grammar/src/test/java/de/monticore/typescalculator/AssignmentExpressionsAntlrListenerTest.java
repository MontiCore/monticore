package de.monticore.typescalculator;

import de.monticore.typescalculator.testassignmentexpressions._parser.TestAssignmentExpressionsAntlrListener;
import de.monticore.typescalculator.testassignmentexpressions._parser.TestAssignmentExpressionsAntlrParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public abstract class AssignmentExpressionsAntlrListenerTest implements TestAssignmentExpressionsAntlrListener {
  @Override
  public void enterAssignmentExpression(TestAssignmentExpressionsAntlrParser.AssignmentExpressionContext ctx) {
    System.out.println("Enter AssignmentExpression");
  }

  @Override
  public void exitAssignmentExpression(TestAssignmentExpressionsAntlrParser.AssignmentExpressionContext ctx) {
    System.out.println("Exit AssignmentExpression");
  }

  @Override
  public void enterAssignmentExpression_eof(TestAssignmentExpressionsAntlrParser.AssignmentExpression_eofContext ctx) {
    System.out.println("Enter AssignmentExpression eof");
  }

  @Override
  public void exitAssignmentExpression_eof(TestAssignmentExpressionsAntlrParser.AssignmentExpression_eofContext ctx) {
    System.out.println("Exit AssignmentExpression eof");

  }

  @Override
  public void enterNameExpression_eof(TestAssignmentExpressionsAntlrParser.NameExpression_eofContext ctx) {
    System.out.println("Enter NameExpression eof");

  }

  @Override
  public void exitNameExpression_eof(TestAssignmentExpressionsAntlrParser.NameExpression_eofContext ctx) {
    System.out.println("Exit NameExpression eof");

  }

  @Override
  public void enterNameExpression(TestAssignmentExpressionsAntlrParser.NameExpressionContext ctx) {
    System.out.println("Enter NameExpression");

  }

  @Override
  public void exitNameExpression(TestAssignmentExpressionsAntlrParser.NameExpressionContext ctx) {
    System.out.println("Exit NameExpression");

  }

  @Override
  public void enterLiteral_eof(TestAssignmentExpressionsAntlrParser.Literal_eofContext ctx) {
    System.out.println("Enter Literal eof");

  }

  @Override
  public void exitLiteral_eof(TestAssignmentExpressionsAntlrParser.Literal_eofContext ctx) {
    System.out.println("Exit Literal eof");

  }

  @Override
  public void enterLiteral(TestAssignmentExpressionsAntlrParser.LiteralContext ctx) {
    System.out.println("Enter Literal");

  }

  @Override
  public void exitLiteral(TestAssignmentExpressionsAntlrParser.LiteralContext ctx) {
    System.out.println("Exit Literal");

  }

  @Override
  public void enterSignedLiteral_eof(TestAssignmentExpressionsAntlrParser.SignedLiteral_eofContext ctx) {
    System.out.println("Enter SignedLiteral eof");

  }

  @Override
  public void exitSignedLiteral_eof(TestAssignmentExpressionsAntlrParser.SignedLiteral_eofContext ctx) {
    System.out.println("Exit SignedLiteral eof");

  }

  @Override
  public void enterSignedLiteral(TestAssignmentExpressionsAntlrParser.SignedLiteralContext ctx) {
    System.out.println("Enter SignedLiteral");

  }

  @Override
  public void exitSignedLiteral(TestAssignmentExpressionsAntlrParser.SignedLiteralContext ctx) {
    System.out.println("Exit SignedLiteral");

  }

  @Override
  public void enterNumericLiteral_eof(TestAssignmentExpressionsAntlrParser.NumericLiteral_eofContext ctx) {
    System.out.println("Enter NumericLiteral eof");

  }

  @Override
  public void exitNumericLiteral_eof(TestAssignmentExpressionsAntlrParser.NumericLiteral_eofContext ctx) {
    System.out.println("Exit NumericLiteral eof");

  }

  @Override
  public void enterNumericLiteral(TestAssignmentExpressionsAntlrParser.NumericLiteralContext ctx) {
    System.out.println("Enter NumericLiteral");

  }

  @Override
  public void exitNumericLiteral(TestAssignmentExpressionsAntlrParser.NumericLiteralContext ctx) {
    System.out.println("Exit NumericLiteral");

  }

  @Override
  public void enterSignedNumericLiteral_eof(TestAssignmentExpressionsAntlrParser.SignedNumericLiteral_eofContext ctx) {
    System.out.println("Enter SignedNumericLiteral eof");

  }

  @Override
  public void exitSignedNumericLiteral_eof(TestAssignmentExpressionsAntlrParser.SignedNumericLiteral_eofContext ctx) {
    System.out.println("Exit SignedNumericLiteral eof");

  }

  @Override
  public void enterSignedNumericLiteral(TestAssignmentExpressionsAntlrParser.SignedNumericLiteralContext ctx) {
    System.out.println("Enter SignedNumericLiteral");

  }

  @Override
  public void exitSignedNumericLiteral(TestAssignmentExpressionsAntlrParser.SignedNumericLiteralContext ctx) {
    System.out.println("Exit SignedNumericLiteral");

  }

  @Override
  public void enterExpression_eof(TestAssignmentExpressionsAntlrParser.Expression_eofContext ctx) {
    System.out.println("Enter Expression eof");

  }

  @Override
  public void exitExpression_eof(TestAssignmentExpressionsAntlrParser.Expression_eofContext ctx) {
    System.out.println("Exit Expression eof");

  }

  @Override
  public void enterExpression(TestAssignmentExpressionsAntlrParser.ExpressionContext ctx) {
    System.out.println("Enter Expression");

  }

  @Override
  public void exitExpression(TestAssignmentExpressionsAntlrParser.ExpressionContext ctx) {
    System.out.println("Exit Expression");

  }

  @Override
  public void enterBooleanLiteral(TestAssignmentExpressionsAntlrParser.BooleanLiteralContext ctx) {
    System.out.println("Enter BooleanLiteral");

  }

  @Override
  public void exitBooleanLiteral(TestAssignmentExpressionsAntlrParser.BooleanLiteralContext ctx) {
    System.out.println("Exit BooleanLiteral");

  }

  @Override
  public void enterNullLiteral_eof(TestAssignmentExpressionsAntlrParser.NullLiteral_eofContext ctx) {
    System.out.println("Enter NullLiteral eof");

  }

  @Override
  public void exitNullLiteral_eof(TestAssignmentExpressionsAntlrParser.NullLiteral_eofContext ctx) {
    System.out.println("Exit NullLiteral eof");

  }

  @Override
  public void enterNullLiteral(TestAssignmentExpressionsAntlrParser.NullLiteralContext ctx) {
    System.out.println("Enter NullLiteral");

  }

  @Override
  public void exitNullLiteral(TestAssignmentExpressionsAntlrParser.NullLiteralContext ctx) {
    System.out.println("Exit NullLiteral");

  }

  @Override
  public void enterBooleanLiteral_eof(TestAssignmentExpressionsAntlrParser.BooleanLiteral_eofContext ctx) {
    System.out.println("Enter BooleanLiteral eof");

  }

  @Override
  public void exitBooleanLiteral_eof(TestAssignmentExpressionsAntlrParser.BooleanLiteral_eofContext ctx) {
    System.out.println("Exit BooleanLiteral eof");

  }

  @Override
  public void enterCharLiteral(TestAssignmentExpressionsAntlrParser.CharLiteralContext ctx) {
    System.out.println("Enter CharLiteral");

  }

  @Override
  public void exitCharLiteral(TestAssignmentExpressionsAntlrParser.CharLiteralContext ctx) {
    System.out.println("Exit CharLiteral");

  }

  @Override
  public void enterStringLiteral_eof(TestAssignmentExpressionsAntlrParser.StringLiteral_eofContext ctx) {
    System.out.println("Enter StringLiteral eof");

  }

  @Override
  public void exitStringLiteral_eof(TestAssignmentExpressionsAntlrParser.StringLiteral_eofContext ctx) {
    System.out.println("Exit StringLiteral eof");

  }

  @Override
  public void enterStringLiteral(TestAssignmentExpressionsAntlrParser.StringLiteralContext ctx) {
    System.out.println("Enter StringLiteral");

  }

  @Override
  public void exitStringLiteral(TestAssignmentExpressionsAntlrParser.StringLiteralContext ctx) {
    System.out.println("Exit StringLiteral");

  }

  @Override
  public void enterNatLiteral_eof(TestAssignmentExpressionsAntlrParser.NatLiteral_eofContext ctx) {
    System.out.println("Enter NatLiteral eof");

  }

  @Override
  public void exitNatLiteral_eof(TestAssignmentExpressionsAntlrParser.NatLiteral_eofContext ctx) {
    System.out.println("Exit NatLiteral eof");

  }

  @Override
  public void enterNatLiteral(TestAssignmentExpressionsAntlrParser.NatLiteralContext ctx) {
    System.out.println("Enter NatLiteral");

  }

  @Override
  public void exitNatLiteral(TestAssignmentExpressionsAntlrParser.NatLiteralContext ctx) {
    System.out.println("Exit NatLiteral");

  }

  @Override
  public void enterSignedNatLiteral_eof(TestAssignmentExpressionsAntlrParser.SignedNatLiteral_eofContext ctx) {
    System.out.println("Enter SignedNatLiteral eof");

  }

  @Override
  public void exitSignedNatLiteral_eof(TestAssignmentExpressionsAntlrParser.SignedNatLiteral_eofContext ctx) {
    System.out.println("Exit SignedNatLiteral eof");

  }

  @Override
  public void enterSignedNatLiteral(TestAssignmentExpressionsAntlrParser.SignedNatLiteralContext ctx) {
    System.out.println("Enter SignedNatLiteral");

  }

  @Override
  public void exitSignedNatLiteral(TestAssignmentExpressionsAntlrParser.SignedNatLiteralContext ctx) {
    System.out.println("Exit SignedNatLiteral");

  }

  @Override
  public void enterEMethod_eof(TestAssignmentExpressionsAntlrParser.EMethod_eofContext ctx) {
    System.out.println("Enter EMethod eof");

  }

  @Override
  public void exitEMethod_eof(TestAssignmentExpressionsAntlrParser.EMethod_eofContext ctx) {
    System.out.println("Exit EMethod eof");

  }

  @Override
  public void enterEMethod(TestAssignmentExpressionsAntlrParser.EMethodContext ctx) {
    System.out.println("Enter EMethod");

  }

  @Override
  public void exitEMethod(TestAssignmentExpressionsAntlrParser.EMethodContext ctx) {
    System.out.println("Exit EMethod");

  }

  @Override
  public void enterEVariable_eof(TestAssignmentExpressionsAntlrParser.EVariable_eofContext ctx) {
    System.out.println("Enter EVariable eof");

  }

  @Override
  public void exitEVariable_eof(TestAssignmentExpressionsAntlrParser.EVariable_eofContext ctx) {
    System.out.println("Exit EVariable eof");

  }

  @Override
  public void enterEVariable(TestAssignmentExpressionsAntlrParser.EVariableContext ctx) {
    System.out.println("Enter EVariable");

  }

  @Override
  public void exitEVariable(TestAssignmentExpressionsAntlrParser.EVariableContext ctx) {
    System.out.println("Exit EVariable");

  }

  @Override
  public void enterEType_eof(TestAssignmentExpressionsAntlrParser.EType_eofContext ctx) {
    System.out.println("Enter EType eof");

  }

  @Override
  public void exitEType_eof(TestAssignmentExpressionsAntlrParser.EType_eofContext ctx) {
    System.out.println("Exit EType eof");

  }

  @Override
  public void enterEType(TestAssignmentExpressionsAntlrParser.ETypeContext ctx) {
    System.out.println("Enter EType");

  }

  @Override
  public void exitEType(TestAssignmentExpressionsAntlrParser.ETypeContext ctx) {
    System.out.println("Exit EType");

  }

  @Override
  public void enterPlusPrefixExpression_eof(TestAssignmentExpressionsAntlrParser.PlusPrefixExpression_eofContext ctx) {
    System.out.println("Enter PlusPrefixExpression eof");

  }

  @Override
  public void exitPlusPrefixExpression_eof(TestAssignmentExpressionsAntlrParser.PlusPrefixExpression_eofContext ctx) {
    System.out.println("Exit PlusPrefixExpression eof");

  }

  @Override
  public void enterPlusPrefixExpression(TestAssignmentExpressionsAntlrParser.PlusPrefixExpressionContext ctx) {
    System.out.println("Enter PlusPrefixExpression");

  }

  @Override
  public void exitPlusPrefixExpression(TestAssignmentExpressionsAntlrParser.PlusPrefixExpressionContext ctx) {
    System.out.println("Exit PlufPrefixExpression");

  }

  @Override
  public void enterMinusPrefixExpression_eof(TestAssignmentExpressionsAntlrParser.MinusPrefixExpression_eofContext ctx) {
    System.out.println("Enter MinusPrefixExpression eof");

  }

  @Override
  public void exitMinusPrefixExpression_eof(TestAssignmentExpressionsAntlrParser.MinusPrefixExpression_eofContext ctx) {
    System.out.println("Exit MinusPrefixExpression eof");

  }

  @Override
  public void enterMinusPrefixExpression(TestAssignmentExpressionsAntlrParser.MinusPrefixExpressionContext ctx) {
    System.out.println("Enter MinusPrefixExpression");

  }

  @Override
  public void exitMinusPrefixExpression(TestAssignmentExpressionsAntlrParser.MinusPrefixExpressionContext ctx) {
    System.out.println("Exit MinusPrefixExpression");

  }

  @Override
  public void enterIncPrefixExpression_eof(TestAssignmentExpressionsAntlrParser.IncPrefixExpression_eofContext ctx) {
    System.out.println("Enter IncPrefixExpression eof");

  }

  @Override
  public void exitIncPrefixExpression_eof(TestAssignmentExpressionsAntlrParser.IncPrefixExpression_eofContext ctx) {
    System.out.println("Exit IncPrefixExpression eof");

  }

  @Override
  public void enterIncPrefixExpression(TestAssignmentExpressionsAntlrParser.IncPrefixExpressionContext ctx) {
    System.out.println("Enter IncPrefixExpression");

  }

  @Override
  public void exitIncPrefixExpression(TestAssignmentExpressionsAntlrParser.IncPrefixExpressionContext ctx) {
    System.out.println("Exit IncPrefixExpression");

  }

  @Override
  public void enterCharLiteral_eof(TestAssignmentExpressionsAntlrParser.CharLiteral_eofContext ctx) {
    System.out.println("Enter CharLiteral eof");

  }

  @Override
  public void exitCharLiteral_eof(TestAssignmentExpressionsAntlrParser.CharLiteral_eofContext ctx) {
    System.out.println("Exit CharLiteral eof");

  }

  @Override
  public void enterDecPrefixExpression(TestAssignmentExpressionsAntlrParser.DecPrefixExpressionContext ctx) {
    System.out.println("Enter DecPrefixExpression");

  }

  @Override
  public void exitDecPrefixExpression(TestAssignmentExpressionsAntlrParser.DecPrefixExpressionContext ctx) {
    System.out.println("Exit DecPrefixExpression");

  }


  @Override
  public void enterDecPrefixExpression_eof(TestAssignmentExpressionsAntlrParser.DecPrefixExpression_eofContext ctx) {
    System.out.println("Enter DecPrefixExpression eof");

  }

  @Override
  public void exitDecPrefixExpression_eof(TestAssignmentExpressionsAntlrParser.DecPrefixExpression_eofContext ctx) {
    System.out.println("exit DecPrefixExpression eof");

  }

  @Override
  public void visitTerminal(TerminalNode terminalNode) {
    System.out.println("visit Terminal");

  }

  @Override
  public void visitErrorNode(ErrorNode errorNode) {
    System.out.println("visit ErrorNode");

  }

  @Override
  public void enterEveryRule(ParserRuleContext parserRuleContext) {
    System.out.println("Enter EveryRule");

  }

  @Override
  public void exitEveryRule(ParserRuleContext parserRuleContext) {
    System.out.println("Exit EveryRule");
  }
}
