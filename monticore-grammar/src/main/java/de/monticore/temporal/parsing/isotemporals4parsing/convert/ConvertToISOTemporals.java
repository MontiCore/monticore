/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.parsing.isotemporals4parsing.convert;

import de.monticore.ast.ASTNode;
import de.monticore.temporal.isotemporals.ISOTemporalsMill;
import de.monticore.temporal.isotemporals._ast.*;
import de.monticore.temporal.parsing.isotemporals4parsing._ast.ASTSign4P;
import de.monticore.temporal.isotemporals._ast.ASTSign;
import de.monticore.temporal.parsing.isotemporals4parsing._ast.*;
import de.monticore.temporal.parsing.isotemporals4parsing._visitor.ISOTemporals4ParsingHandler;
import de.monticore.temporal.parsing.isotemporals4parsing._visitor.ISOTemporals4ParsingTraverser;
import de.monticore.temporal.parsing.isotemporals4parsing._visitor.ISOTemporals4ParsingVisitor2;

public class ConvertToISOTemporals implements ISOTemporals4ParsingVisitor2,
    ISOTemporals4ParsingHandler {
  protected ASTNode result;
  protected ASTSign currentSign;
  protected ISOTemporals4ParsingTraverser traverser;
  
  @Override
  public ISOTemporals4ParsingTraverser getTraverser() {
    return traverser;
  }
  
  @Override
  public void setTraverser(ISOTemporals4ParsingTraverser traverser) {
    this.traverser = traverser;
  }
  
  public ASTNode getResult() {
    return result;
  }
  
  @Override
  public void handle(ASTCalendarDate4P node) {
    ASTCalendarDateBuilder builder = ISOTemporalsMill.calendarDateBuilder();
    String pre = "";
    String mid = "";
    String post = "";
    if (node.isPresentDateExtension()) { // True only if there is a sign
      visit(node.getDateExtension().getSign());
      builder.setSign(currentSign);
      pre += node.getDateExtension().getLeadingDigits().getSource();
    }
    if (node.isPresentCentury()) {
      pre += node.getCentury().getSource();
    }
    if (node.isPresentDecade()) {
      pre += node.getDecade().getSource();
    }
    if (node.isPresentYear()) {
      pre += node.getYear().getSource();
    }
    if (node.isEmptyX()) {
      if (node.isPresentMonth()) {
        pre += node.getMonth().getSource();
      }
      if (node.isPresentDay()) {
        pre += node.getDay().getSource();
      }
    } else {
      if (node.isPresentMonth()) {
        mid = node.getMonth().getSource();
      }
      if (node.isPresentDay()) {
        post = node.getDay().getSource();
      }
    }
    builder.setPre(pre);
    if (!mid.isEmpty()) {
      builder.setMid(mid);
    }
    if (!post.isEmpty()) {
      builder.setPost(post);
    }
    result = builder.build();
  }
  
  @Override
  public void handle(ASTOrdinalDate4P node) {
    ASTOrdinalDateBuilder builder = ISOTemporalsMill.ordinalDateBuilder();
    String pre = "";
    String post = "";
    if (node.isPresentDateExtension()) {
      visit(node.getDateExtension().getSign());
      builder.setSign(currentSign);
      pre += node.getDateExtension().getLeadingDigits().getSource();
    }
    pre += node.getYear().getSource();
    if (node.isPresentX()) {
      post = node.getDayOfYear().getSource();
    } else {
      pre += node.getDayOfYear().getSource();
    }
    builder.setPre(pre);
    if (!post.isEmpty()) {
      builder.setPost(post);
    }
    result = builder.build();
  }
  
  @Override
  public void handle(ASTWeekDate4P node) {
    if (node.isEmptyX()) {
      result = convertBasicWeekDate(node);
    } else {
      result = convertExtendedWeekDate(node);
    }
  }
  
  private ASTBasicWeekDate convertBasicWeekDate(ASTWeekDate4P node) {
    ASTBasicWeekDateBuilder builder = ISOTemporalsMill.basicWeekDateBuilder();
    String raw = "";
    if (node.isPresentDateExtension()) {
      raw += node.getDateExtension().getSign() == ASTSign4P.PLUS ? "+" : "-";
      raw += node.getDateExtension().getLeadingDigits().getSource();
    }
    raw += node.getYear().getSource();
    raw += "W";
    raw += node.getWeek().getSource();
    if (node.isPresentDayOfWeek()) {
      raw += node.getDayOfWeek().getSource();
    }
    builder.setSource(raw);
    return builder.build();
  }
  
  private ASTExtendedWeekDate convertExtendedWeekDate(ASTWeekDate4P node) {
    ASTExtendedWeekDateBuilder builder = ISOTemporalsMill.extendedWeekDateBuilder();
    String year = node.getYear().getSource();
    if (node.isPresentDateExtension()) {
      visit(node.getDateExtension().getSign());
      builder.setSign(currentSign);
      year = node.getDateExtension().getLeadingDigits().getSource() + year;
    }
    builder.setYearSource(year);
    String week = "W" + node.getWeek().getSource();
    builder.setWeekSource(ISOTemporalsMill.wDigitsBuilder().setSource(week).build());
    if (node.isPresentDayOfWeek()) {
      builder.setDayOfWeekSource(node.getDayOfWeek().getSource());
    }
    return builder.build();
  }
  
  @Override
  public void handle(ASTTimeOfDate node) {
    ASTISOTimeBuilder builder = ISOTemporalsMill.iSOTimeBuilder();
    String pre = "T"; // As this is a time of date, the time designator "T" MUST be present.
    String mid = "";
    String post = "";
    pre += node.getHour().getSource();
    if (node.isEmptyX()) {
      if (node.isPresentMinute()) {
        pre += node.getMinute().getSource();
      }
      if (node.isPresentSecond()) {
        pre += node.getSecond().getSource();
      }
    } else {
      if (node.isPresentMinute()) {
        mid = node.getMinute().getSource();
      }
      if (node.isPresentSecond()) {
        post = node.getSecond().getSource();
      }
    }
    ASTTDigits preWithT = ISOTemporalsMill.tDigitsBuilder().setSource(pre).build();
    builder.setPreWithT(preWithT);
    if (!mid.isEmpty()) {
      builder.setMid(mid);
    }
    if (!post.isEmpty()) {
      builder.setPost(post);
    }
    if (node.isPresentFraction()) {
      node.getFraction().accept(getTraverser());
      builder.setFraction((ASTFraction) result);
    }
    if (node.isPresentTimeShift()) {
      node.getTimeShift().accept(getTraverser());
      builder.setTimeShiftSource((ASTTimeShift) result);
    }
    result = builder.build();
  }
  
  @Override
  public void visit(ASTSign4P node) {
    if (node == ASTSign4P.PLUS) {
      currentSign = ASTSign.PLUS;
    } else {
      currentSign = ASTSign.MINUS;
    }
  }
  
  @Override
  public void visit(ASTFraction4P node) {
    ASTFractionBuilder builder = ISOTemporalsMill.fractionBuilder();
    if (node.isPresentPeriod()) {
      builder.setPeriod(".");
    } else {
      builder.setComma(",");
    }
    builder.setDigits(node.getDigitVar().getSource());
    result = builder.build();
  }
  
  @Override
  public void visit(ASTBasicTimeShift4P node) {
    ASTTimeShiftBuilder builder = ISOTemporalsMill.timeShiftBuilder();
    if (!node.isUtc()) {
      if (node.getSign() == ASTSign4P.PLUS) {
        builder.setSign(ASTSign.PLUS);
      } else {
        builder.setSign(ASTSign.MINUS);
      }
      String pre = node.getHour().getSource();
      if (node.isPresentMinute()) {
        pre += node.getMinute().getSource();
      }
      builder.setPre(pre);
    }
    result = builder.build();
  }
  
  @Override
  public void visit(ASTExtendedTimeShift4P node) {
    ASTTimeShiftBuilder builder = ISOTemporalsMill.timeShiftBuilder();
    if (!node.isUtc()) {
      if (node.getSign() == ASTSign4P.PLUS) {
        builder.setSign(ASTSign.PLUS);
      } else {
        builder.setSign(ASTSign.MINUS);
      }
      builder.setPre(node.getHour().getSource());
      if (node.isPresentMinute()) {
        builder.setPost(node.getMinute().getSource());
      }
    }
    result = builder.build();
  }
  
}
