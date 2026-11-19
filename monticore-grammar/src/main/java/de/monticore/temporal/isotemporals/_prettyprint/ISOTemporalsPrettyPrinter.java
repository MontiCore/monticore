/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.temporal.isotemporals._ast.*;

/**
 * This hand-written pretty printer prints ISO temporals as they were written.
 * The generated one does not support the enum Sign and inserts spaces inside a
 * literal, e.g. before the unit of a period fraction (PT6.5 H) or a time shift.
 */
public class ISOTemporalsPrettyPrinter extends ISOTemporalsPrettyPrinterTOP {

  public ISOTemporalsPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTCalendarDate node) {
    print(node, node.toRawString() + " ");
  }

  @Override
  public void handle(ASTOrdinalDate node) {
    print(node, node.toRawString() + " ");
  }

  @Override
  public void handle(ASTExtendedWeekDate node) {
    StringBuilder result = new StringBuilder();
    if (node.isPresentSign()) {
      result.append(node.getSign() == ASTSign.PLUS ? "+" : "-");
    }
    result.append(node.getYearSource()).append("-").append(node.getWeekSource().getSource());
    if (node.isPresentDayOfWeekSource()) {
      result.append("-").append(node.getDayOfWeekSource());
    }
    print(node, result + " ");
  }

  @Override
  public void handle(ASTISOTime node) {
    print(node, node.toRawString() + " ");
  }

  @Override
  public void handle(ASTTimeShift node) {
    print(node, node.toRawString() + " ");
  }

  @Override
  public void handle(ASTFullPeriod node) {
    print(node, node.toRawString() + " ");
  }

  protected void print(ASTNode node, String text) {
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
    }
    getPrinter().print(text);
    if (this.isPrintComments()) {
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }
}
