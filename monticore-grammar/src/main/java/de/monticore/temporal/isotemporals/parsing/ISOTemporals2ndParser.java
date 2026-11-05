/* (c) https://github.com/MontiCore/monticore */
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
import java.util.Optional;

public class ISOTemporals2ndParser implements ISOTemporalsVisitor2 {
  protected ISOTemporals4ParsingParser parser;
  
  public ISOTemporals2ndParser() {
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
    Optional<ASTCalendarDate4P> result;
    try {
      result = parser.parse_StringCalendarDate4P(node.toRawString());
    } catch (IOException e) {
      Log.error("Expected a calendar date but got " + node.toRawString(), e);
      return;
    }
    if (result.isEmpty()) {
      Log.error("Expected a calendar date but got " + node.toRawString());
      return;
    }
    ASTCalendarDate4P parsed = result.get();
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
    Optional<ASTOrdinalDate4P> result;
    try {
      result = parser.parse_StringOrdinalDate4P(node.toRawString());
    } catch (IOException e) {
      Log.error("Expected an ordinal date but got " + node.toRawString(), e);
      return;
    }
    if (result.isEmpty()) {
      Log.error("Expected an ordinal date but got " + node.toRawString());
      return;
    }
    ASTOrdinalDate4P parsed = result.get();
    String dateExtension = "";
    if (parsed.isPresentDateExtension()) {
      dateExtension = parsed.getDateExtension().getRawString();
    }
    node.setYear(Integer.parseInt(dateExtension + parsed.getYear().getSource()));
    node.setDayOfYear(parsed.getDayOfYear().getValue());
  }
  
  @Override
  public void visit(ASTBasicWeekDate node) {
    Optional<ASTWeekDate4P> result;
    try {
      result = parser.parse_StringWeekDate4P(node.toRawString());
    } catch (IOException e) {
      Log.error("Expected a week date but got " + node.toRawString(), e);
      return;
    }
    if (result.isEmpty()) {
      Log.error("Expected a week date but got " + node.toRawString());
      return;
    }
    ASTWeekDate4P parsed = result.get();
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
    Optional<ASTISOTime4P> result;
    try {
      result = parser.parse_StringISOTime4P(node.toRawString());
    } catch (IOException e) {
      Log.error("Expected an ISO time but got " + node.toRawString(), e);
      return;
    }
    if (result.isEmpty()) {
      Log.error("Expected an ISO time but got " + node.toRawString());
      return;
    }
    ASTISOTime4P parsed = result.get();
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
        node.setTimeShiftHour(0);
        node.setTimeShiftMinute(0);
      } else {
        int timeShiftHour = parsed.getTimeShift().getHour().getValue();
        if (parsed.getTimeShift().getSign() == ASTSign4P.MINUS) {
          timeShiftHour = -timeShiftHour;
        }
        node.setTimeShiftHour(timeShiftHour);
        
        int timeShiftMinute;
        if (parsed.getTimeShift().isPresentMinute()) {
          timeShiftMinute = parsed.getTimeShift().getMinute().getValue();
        }
        else {
          timeShiftMinute = 0;
        }
        node.setTimeShiftMinute(timeShiftMinute);
      }
    }
  }
  
  @Override
  public void visit(ASTISODateTime node) {
    Optional<ASTISODateTime4P> result;
    try {
      result = parser.parse_StringISODateTime4P(node.toRawString());
    } catch (IOException e) {
      Log.error("Expected an ISO time but got " + node.toRawString(), e);
      return;
    }
    if (result.isEmpty()) {
      Log.error("Expected an ISO time but got " + node.toRawString());
      return;
    }
    ASTISODateTime4P parsed = result.get();
    
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
    
    Optional<ASTFullPeriod4P> result;
    try {
      result = parser.parse_StringFullPeriod4P(node.toRawString());
    } catch (IOException e) {
      Log.error("Expected an ISO period but got " + node.toRawString(), e);
      return;
    }
    if (result.isEmpty()) {
      Log.error("Expected an ISO period but got " + node.toRawString());
      return;
    }
    ASTFullPeriod4P parsed = result.get();
    
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
    Optional<ASTWeekPeriod4P> result;
    try {
      result = parser.parse_StringWeekPeriod4P(node.toRawString());
    } catch (IOException e) {
      Log.error("Expected an ISO week period but got " + node.toRawString(), e);
      return;
    }
    if (result.isEmpty()) {
      Log.error("Expected an ISO week period but got " + node.toRawString());
      return;
    }
    ASTWeekPeriod4P parsed = result.get();
    
    node.setWeeks(parsed.getWeeks().getValue());
  }
}
