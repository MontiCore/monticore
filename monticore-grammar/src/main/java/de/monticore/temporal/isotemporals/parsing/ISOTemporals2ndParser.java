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
import de.monticore.ast.ASTNode;
import de.monticore.temporal.parsing.isotemporals4parsing._parser.ISOTemporals4ParsingAntlrParser;
import de.se_rwth.commons.logging.Log;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ISOTemporals2ndParser implements ISOTemporalsVisitor2 {
  
  public static final String CALENDAR_DATE_ERROR_CODE = "0xD0069";
  
  public static final String ORDINAL_DATE_ERROR_CODE = "0x4CD96";
  
  public static final String WEEK_DATE_ERROR_CODE = "0xE9EFC";
  
  public static final String ISO_TIME_ERROR_CODE = "0x17579";
  
  public static final String ISO_DATE_TIME_ERROR_CODE = "0xA15DF";
  
  public static final String FULL_PERIOD_ERROR_CODE = "0x116F2";
  
  public static final String WEEK_PERIOD_ERROR_CODE = "0x95FB5";
  
  public static final String SECOND_PARSE_ERROR_MESSAGE = "%s: Expected %s but got '%s' (%s)";
  
  protected SilentParser parser;
  
  public ISOTemporals2ndParser() {
    parser = new SilentParser();
  }
  
  public static void doParse(ASTISOTemporalsNode node) {
    ISOTemporals2ndParser parserVisitor = new ISOTemporals2ndParser();
    ISOTemporalsTraverser traverser = ISOTemporalsMill.traverser();
    traverser.add4ISOTemporals(parserVisitor);
    node.accept(traverser);
  }
  
  @Override
  public void visit(ASTCalendarDate node) {
    Optional<ASTCalendarDate4P> result = parse(parser::parse_StringCalendarDate4P, node, node.toRawString(),
        CALENDAR_DATE_ERROR_CODE, "a calendar date");
    if (result.isEmpty()) {
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
    Optional<ASTOrdinalDate4P> result = parse(parser::parse_StringOrdinalDate4P, node, node.toRawString(),
        ORDINAL_DATE_ERROR_CODE, "an ordinal date");
    if (result.isEmpty()) {
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
    Optional<ASTWeekDate4P> result = parse(parser::parse_StringWeekDate4P, node, node.toRawString(),
        WEEK_DATE_ERROR_CODE, "a week date");
    if (result.isEmpty()) {
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
    Optional<ASTISOTime4P> result = parse(parser::parse_StringISOTime4P, node, node.toRawString(),
        ISO_TIME_ERROR_CODE, "an ISO time");
    if (result.isEmpty()) {
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
    Optional<ASTISODateTime4P> result = parse(parser::parse_StringISODateTime4P, node, node.toRawString(),
        ISO_DATE_TIME_ERROR_CODE, "an ISO date-time");
    if (result.isEmpty()) {
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
    
    Optional<ASTFullPeriod4P> result = parse(parser::parse_StringFullPeriod4P, node, node.toRawString(),
        FULL_PERIOD_ERROR_CODE, "an ISO period");
    if (result.isEmpty()) {
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
    Optional<ASTWeekPeriod4P> result = parse(parser::parse_StringWeekPeriod4P, node, node.toRawString(),
        WEEK_PERIOD_ERROR_CODE, "an ISO week period");
    if (result.isEmpty()) {
      return;
    }
    ASTWeekPeriod4P parsed = result.get();
    
    node.setWeeks(parsed.getWeeks().getValue());
  }

  /**
   * Parses the raw string of a node with a rule of ISOTemporals4Parsing.
   * If this fails, exactly one error is logged at the position of the node;
   * the node keeps its default values.
   */
  protected <T> Optional<T> parse(RuleParser<T> ruleParser, ASTNode node, String input,
                                  String errorCode, String expected) {
    Optional<T> result;
    String detail;
    try {
      result = ruleParser.parse(input);
      detail = String.join("; ", parser.syntaxErrors);
    } catch (IOException e) {
      result = Optional.empty();
      detail = e.getMessage();
    }
    if (result.isEmpty()) {
      Log.error(String.format(SECOND_PARSE_ERROR_MESSAGE, errorCode, expected, input, detail),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
    return result;
  }
  
  @FunctionalInterface
  protected interface RuleParser<T> {
    Optional<T> parse(String input) throws IOException;
  }
  
  /**
   * Collects the syntax errors of the second parse step instead of logging them.
   * Their positions refer to the raw string, not to the model,
   * so {@link #parse} reports them as part of a single error at the position of the node.
   */
  protected static class SilentParser extends ISOTemporals4ParsingParser {
    protected List<String> syntaxErrors = new ArrayList<>();
    
    @Override
    protected ISOTemporals4ParsingAntlrParser create(Reader reader) throws IOException {
      ISOTemporals4ParsingAntlrParser antlrParser = super.create(reader);
      syntaxErrors.clear();
      BaseErrorListener listener = new BaseErrorListener() {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                int charPositionInLine, String msg, RecognitionException e) {
          syntaxErrors.add(msg);
          antlrParser.setErrors(true);
        }
      };
      antlrParser.removeErrorListeners();
      antlrParser.addErrorListener(listener);
      Lexer lexer = (Lexer) antlrParser.getTokenStream().getTokenSource();
      lexer.removeErrorListeners();
      lexer.addErrorListener(listener);
      return antlrParser;
    }
  }
}
