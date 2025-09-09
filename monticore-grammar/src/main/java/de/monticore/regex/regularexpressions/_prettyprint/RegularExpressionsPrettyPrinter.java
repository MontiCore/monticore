/* (c) https://github.com/MontiCore/monticore */
package de.monticore.regex.regularexpressions._prettyprint;

import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.regex.regularexpressions._ast.*;

/**
 * handwritten pretty printer,
 * as the auto-generated one inserts too many whitespaces
 */
public class RegularExpressionsPrettyPrinter
    extends RegularExpressionsPrettyPrinterTOP {

  public RegularExpressionsPrettyPrinter(
      IndentPrinter printer,
      boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTRegExLiteral node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getRegExStartToken());
    node.getRegularExpression().accept(getTraverser());
    getPrinter().print(node.getRegExEndToken());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTRegularExpression regex) {
    CommentPrettyPrinter.printPreComments(regex, getPrinter());
    if (regex.isPresentPipe()) {
      regex.getLeft().accept(getTraverser());
      getPrinter().print("|");
      regex.getRight().accept(getTraverser());
    }
    else {
      for (ASTRegExItem item : regex.getRegExItemList()) {
        item.accept(getTraverser());
      }
    }
    CommentPrettyPrinter.printPostComments(regex, getPrinter());
  }

  @Override
  public void handle(ASTBracketRegEx node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("[");
    if (node.isPresentRoof()) {
      getPrinter().print("^");
    }
    for (ASTBracketRegExItem item : node.getBracketRegExItemList()) {
      item.accept(getTraverser());
    }
    getPrinter().print("]");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTCharOption node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getCharacterInCharacterElement());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTSpecialCharOption node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentPoint()) {
      getPrinter().print(node.getPoint());
    }
    if (node.isPresentStar()) {
      getPrinter().print(node.getStar());
    }
    if (node.isPresentPlus()) {
      getPrinter().print(node.getPlus());
    }
    if (node.isPresentComma()) {
      getPrinter().print(node.getComma());
    }
    if (node.isPresentRoof()) {
      getPrinter().print(node.getRoof());
    }
    if (node.isPresentPipe()) {
      getPrinter().print(node.getPipe());
    }
    if (node.isPresentBackslash()) {
      getPrinter().print(node.getBackslash());
    }
    if (node.isPresentLCurly()) {
      getPrinter().print(node.getLCurly());
    }
    if (node.isPresentRCurly()) {
      getPrinter().print(node.getRCurly());
    }
    if (node.isPresentLBrack()) {
      getPrinter().print(node.getLBrack());
    }
    if (node.isPresentDollar()) {
      getPrinter().print(node.getDollar());
    }
    if (node.isPresentLParen()) {
      getPrinter().print(node.getLParen());
    }
    if (node.isPresentRParen()) {
      getPrinter().print(node.getRParen());
    }
    if (node.isPresentQuestion()) {
      getPrinter().print(node.getQuestion());
    }
    if (node.isPresentSingleDigit()) {
      getPrinter().print(node.getSingleDigit());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTCharRange node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getRange());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTCapturingGroup node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print("(");
    node.getRegularExpression().accept(getTraverser());
    getPrinter().print(")");
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTNamedCapturingGroup node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getNamedCapturingGroupStart());
    getPrinter().print(node.getName());
    getPrinter().print(node.getNamedCapturingGroupEnd());
    node.getRegularExpression().accept(getTraverser());
    getPrinter().print(node.getRParen());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTNonCapturingGroup node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getNonCapturingGroupStart());
    node.getRegularExpression().accept(getTraverser());
    getPrinter().print(node.getRParen());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTBackReference node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getBackslash());
    if (node.isPresentSingleDigit()) {
      getPrinter().print(node.getSingleDigit());
    }
    else {
      getPrinter().print(node.getBackReferenceStart());
      getPrinter().print(node.getName());
      getPrinter().print(node.getNamedCapturingGroupEnd());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTRegExChar node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getCharacterInCharacterElement());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTRegExPoint node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getPoint());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTRegExDigit node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getSingleDigit());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTStartOfLine node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getRoof());
    node.getRegularExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTQualifiedRegEx node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getRegExItem().accept(getTraverser());
    node.getQualification().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTRegExQualification node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentStar()) {
      getPrinter().print(node.getStar());
    }
    else if (node.isPresentPlus()) {
      getPrinter().print(node.getPlus());
    }
    else if (node.isPresentQuestion()) {
      getPrinter().print(node.getQuestion());
    }
    else if (node.isPresentDollar()) {
      getPrinter().print(node.getDollar());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTRangeQualification node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getLCurly());
    for (String s : node.getLowerBoundList()) {
      getPrinter().print(s);
    }
    getPrinter().print(node.getComma());
    for (String s : node.getUpperBoundList()) {
      getPrinter().print(s);
    }
    getPrinter().print(node.getRCurly());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTNumberQualification node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getLCurly());
    for (String s : node.getSingleDigitList()) {
      getPrinter().print(s);
    }
    getPrinter().print(node.getRCurly());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTSpecificChars node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    getPrinter().print(node.getSpecificCharsStart());
    getPrinter().print(node.getSpecificCharsName());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTRegExEscapeChar node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.isPresentAlphaNumCharsWithUnderscoreToken()) {
      getPrinter().print(node.getAlphaNumCharsWithUnderscoreToken());
    }
    else if (node.isPresentNonWordCharsToken()) {
      getPrinter().print(node.getNonWordCharsToken());
    }
    else if (node.isPresentWordBoundariesToken()) {
      getPrinter().print(node.getWordBoundariesToken());
    }
    else if (node.isPresentNonWordBoundariesToken()) {
      getPrinter().print(node.getNonWordBoundariesToken());
    }
    else if (node.isPresentDigitCharsToken()) {
      getPrinter().print(node.getDigitCharsToken());
    }
    else if (node.isPresentNonDigitCharsToken()) {
      getPrinter().print(node.getNonDigitCharsToken());
    }
    else if (node.isPresentWhitespaceCharsToken()) {
      getPrinter().print(node.getWhitespaceCharsToken());
    }
    else if (node.isPresentNonWhitespaceCharsToken()) {
      getPrinter().print(node.getNonWhitespaceCharsToken());
    }
    else if (node.isPresentBackslash()) {
      getPrinter().print(node.getBackslash());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
}