package de.monticore.temporal.isotemporals.parsing;

import de.monticore.temporal.isotemporals.ISOTemporalsMill;
import de.monticore.temporal.isotemporals._ast.*;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsTraverser;
import de.monticore.temporal.isotemporals._visitor.ISOTemporalsVisitor2;
import de.monticore.temporal.parsing.isotemporals4parsing._ast.ASTSign4P;
import de.monticore.temporal.parsing.isotemporals4parsing.ISOTemporals4ParsingMill;
import de.monticore.temporal.parsing.isotemporals4parsing._ast.*;
import de.monticore.temporal.parsing.isotemporals4parsing._parser.ISOTemporals4ParsingParser;
import de.monticore.temporal.parsing.isotemporals4parsing._visitor.ISOTemporals4ParsingTraverser;
import de.monticore.temporal.isotemporals.cocos.PeriodIsNonemptyCoCo;
import de.monticore.temporal.parsing.isotemporals4parsing.convert.ConvertToISOTemporals;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;

public class ISOTemporals2ndParser implements ISOTemporalsVisitor2 {
  protected ISOTemporals4ParsingParser parser;
  
  public ISOTemporals2ndParser() {
    ISOTemporals4ParsingMill.init();
    parser = ISOTemporals4ParsingMill.parser();
  }
  
  public static void doParse(ASTISOTemporalsNode node) {
    ISOTemporals2ndParser parserVisitor = new ISOTemporals2ndParser();
    ISOTemporalsTraverser traverser = ISOTemporalsMill.traverser();
    traverser.add4ISOTemporals(parserVisitor);
    node.accept(traverser);
  }
  
  @Override
  public void visit(ASTCalendarDate node) {
    ASTCalendarDate4P parsed;
    try {
      parsed = parser.parse_StringCalendarDate4P(node.toRawString()).orElseThrow();
    } catch (Exception e) {
      Log.error("Expected a calendar date but got " + node.toRawString(), e);
      return;
    }
    String dateExtension = "";
    if (parsed.isPresentDateExtension()) {
      dateExtension = parsed.getDateExtension().getRawString();
    }
    if (parsed.isPresentCentury()) {
      node.setCentury(Integer.parseInt(dateExtension + parsed.getCentury().getSource()));
    }
    if (parsed.isPresentDecade()) {
      node.setDecade(Integer.parseInt(dateExtension + parsed.getDecade().getSource()));
    }
    if (parsed.isPresentYear()) {
      node.setYear(Integer.parseInt(dateExtension + parsed.getYear().getSource()));
    }
    if (parsed.isPresentMonth()) {
      node.setMonth(parsed.getMonth().getValue());
    }
    if (parsed.isPresentDay()) {
      node.setDay(parsed.getDay().getValue());
    }
  }
  
  @Override
  public void visit(ASTOrdinalDate node) {
    ASTOrdinalDate4P parsed;
    try {
      parsed = parser.parse_StringOrdinalDate4P(node.toRawString()).orElseThrow();
    } catch (IOException e) {
      Log.error("Expected an ordinal date but got " + node.toRawString(), e);
      return;
    }
    String dateExtension = "";
    if (parsed.isPresentDateExtension()) {
      dateExtension = parsed.getDateExtension().getRawString();
    }
    node.setYear(Integer.parseInt(dateExtension + parsed.getYear().getSource()));
    node.setDayOfYear(parsed.getDayOfYear().getValue());
  }
  
  @Override
  public void visit(ASTBasicWeekDate node) {
    ASTWeekDate4P parsed;
    try {
      parsed = parser.parse_StringWeekDate4P(node.toRawString()).orElseThrow();
    } catch (IOException e) {
      Log.error("Expected a week date but got " + node.toRawString(), e);
      return;
    }
    String dateExtension = "";
    if (parsed.isPresentDateExtension()) {
      dateExtension = parsed.getDateExtension().getRawString();
    }
    node.setYear(Integer.parseInt(dateExtension + parsed.getYear().getSource()));
    node.setWeek(parsed.getWeek().getValue());
    if (parsed.isPresentDayOfWeek()) {
      node.setDayOfWeekInternal(parsed.getDayOfWeek().getValue());
    }
  }
  
  @Override
  public void visit(ASTISOTime node) {
    ASTISOTime4P parsed;
    try {
      parsed = parser.parse_StringISOTime4P(node.toRawString()).orElseThrow();
    } catch (IOException e) {
      Log.error("Expected an ISO time but got " + node.toRawString(), e);
      return;
    }
    node.setHour(parsed.getHour().getValue());
    if (parsed.isPresentMinute()) {
      node.setMinute(parsed.getMinute().getValue());
    }
    if (parsed.isPresentSecond()) {
      node.setSecond(parsed.getSecond().getValue());
    }
    if (parsed.isPresentFraction()) {
      node.setDecimalDigits(parsed.getFraction().getDigitVar().getSource());
    }
    if (parsed.isPresentTimeShift()) {
      if (parsed.getTimeShift().isUtc()) {
        node.setTimeShift(0);
      } else {
        if (parsed.getTimeShift().isPresentMinute() && parsed.getTimeShift().getMinute().getValue() != 0) {
          Log.error("The minute component of time shift has to be '00' or absent!", node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        }
        
        int timeShift = parsed.getTimeShift().getHour().getValue();
        if (parsed.getTimeShift().getSign() == ASTSign4P.MINUS) {
          timeShift = -timeShift;
        }
        node.setTimeShift(timeShift);
      }
    }
  }
  
  @Override
  public void visit(ASTISODateTime node) {
    ASTISODateTime4P parsed;
    try {
      parsed = parser.parse_StringISODateTime4P(node.toRawString()).orElseThrow();
    } catch (IOException e) {
      Log.error("Expected an ISO time but got " + node.toRawString(), e);
      return;
    }
    
    ConvertToISOTemporals converter = new ConvertToISOTemporals();
    ISOTemporals4ParsingTraverser traverser = ISOTemporals4ParsingMill.traverser();
    converter.setTraverser(traverser);
    traverser.setISOTemporals4ParsingHandler(converter);
    traverser.add4ISOTemporals4Parsing(converter);
    
    parsed.getDate().accept(traverser);
    node.setDate((ASTISODate) converter.getResult());
    parsed.getTime().accept(traverser);
    node.setTime((ASTISOTime) converter.getResult());
  }
  
  @Override
  public void visit(ASTFullPeriod node) {
    PeriodIsNonemptyCoCo.doCheck(node);
    
    ASTFullPeriod4P parsed;
    try {
      parsed = parser.parse_StringFullPeriod4P(node.toRawString()).orElseThrow();
    } catch (Exception e) {
      Log.error("Expected an ISO period but got " + node.toRawString(), e);
      return;
    }
    
    if (parsed.isPresentYears()) {
      node.setYears(parsed.getYears().getValue());
    }
    if (parsed.isPresentMonths()) {
      node.setMonths(parsed.getMonths().getValue());
    }
    if (parsed.isPresentDays()) {
      node.setDays(parsed.getDays().getValue());
    }
    if (parsed.isPresentHours()) {
      node.setHours(parsed.getHours().getValue());
    }
    if (parsed.isPresentMinutes()) {
      node.setMinutes(parsed.getMinutes().getValue());
    }
    if (parsed.isPresentSeconds()) {
      node.setSeconds(parsed.getSeconds().getValue());
    }
    if (!parsed.isEmptyFraction()) {
      // There is guaranteed to be at most one fraction, due to the first parse step
      node.setDecimalDigits(parsed.getFraction(0).getDigitVar().getSource());
    }
  }
  
  @Override
  public void visit(ASTWeekPeriod node) {
    ASTWeekPeriod4P parsed;
    try {
      parsed = parser.parse_StringWeekPeriod4P(node.toRawString()).orElseThrow();
    } catch (Exception e) {
      Log.error("Expected an ISO week period but got " + node.toRawString(), e);
      return;
    }
    
    node.setWeeks(parsed.getWeeks().getValue());
  }
}
