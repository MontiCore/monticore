/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTConditionalExpression;
import de.monticore.expressions.commonexpressions._ast.ASTMinusExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.temporal.isotemporals._ast.ASTISODateTime;
import de.monticore.temporal.isotemporals._ast.ASTISOTime;
import de.monticore.temporal.timedactions.TimedActionsMill;
import de.monticore.temporal.timedactions._ast.ASTTimedAction;
import de.monticore.temporal.timedactions._parser.TimedActionsParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import static java.time.temporal.ChronoField.*;

/**
 * Negative time shifts with minutes and digits around ':' and '-' in expressions
 * of a host language.
 */
@TestWithMCLanguage(TimedActionsMill.class)
public class TimedActionsTimeShiftTest {

  protected TimedActionsParser parser;

  @BeforeEach
  void setup() {
    parser = TimedActionsMill.parser();
  }

  protected ASTTimedAction parse(String input) throws IOException {
    return parser.parse_StringTimedAction(input)
        .orElseGet(MCAssertions::failAndPrintFindings);
  }

  @Test
  public void testTimeWithNegativeTimeShift() throws IOException {
    ASTISOTime time = Assertions.assertInstanceOf(ASTISOTime.class,
        parse("on 12:00-05:30 do ping();").getInstant());

    Assertions.assertEquals(12, time.get(HOUR_OF_DAY));
    Assertions.assertEquals(0, time.get(MINUTE_OF_HOUR));
    Assertions.assertEquals(ZoneOffset.ofHoursMinutes(-5, -30).getTotalSeconds(),
        time.get(OFFSET_SECONDS));
  }

  @Test
  public void testTimeWithDesignatorAndNegativeTimeShift() throws IOException {
    ASTISOTime time = Assertions.assertInstanceOf(ASTISOTime.class,
        parse("on T12:30:01-05:00 do ping();").getInstant());

    Assertions.assertEquals(OffsetTime.of(LocalTime.of(12, 30, 1), ZoneOffset.ofHours(-5)),
        time.toOffsetTime());
  }

  @Test
  public void testDateTimeWithNegativeTimeShift() throws IOException {
    ASTISODateTime dateTime = Assertions.assertInstanceOf(ASTISODateTime.class,
        parse("on 2017-12-04T12:30:01-05:30 do ping();").getInstant());

    Assertions.assertEquals(OffsetDateTime.of(LocalDate.of(2017, 12, 4), LocalTime.of(12, 30, 1),
        ZoneOffset.ofHoursMinutes(-5, -30)), dateTime.toOffsetDateTime());
  }

  @Test
  public void testConditionalWithNumbers() throws IOException {
    ASTConditionalExpression conditional = conditionalArgument(
        parse("on 2017-12-04 do f(b ? 100:20);"));

    Assertions.assertEquals(100, natLiteral(conditional.getTrueExpression()).getValue());
    Assertions.assertEquals(20, natLiteral(conditional.getFalseExpression()).getValue());
  }

  @Test
  public void testConditionalWithSubtraction() throws IOException {
    ASTConditionalExpression conditional = conditionalArgument(
        parse("on 2017-12-04 do f(b ? 10-05:20);"));

    ASTMinusExpression minus = Assertions.assertInstanceOf(ASTMinusExpression.class,
        conditional.getTrueExpression());
    Assertions.assertEquals(10, natLiteral(minus.getLeft()).getValue());
    Assertions.assertEquals(5, natLiteral(minus.getRight()).getValue());
    Assertions.assertEquals(20, natLiteral(conditional.getFalseExpression()).getValue());
  }

  protected ASTConditionalExpression conditionalArgument(ASTTimedAction action) {
    ASTExpressionStatement statement = Assertions.assertInstanceOf(ASTExpressionStatement.class,
        action.getAction().getMCStatement());
    ASTCallExpression call = Assertions.assertInstanceOf(ASTCallExpression.class,
        statement.getExpression());
    Assertions.assertEquals(1, call.getArguments().sizeExpressions());
    return Assertions.assertInstanceOf(ASTConditionalExpression.class,
        call.getArguments().getExpression(0));
  }

  protected ASTNatLiteral natLiteral(ASTExpression expression) {
    ASTLiteralExpression literal = Assertions.assertInstanceOf(ASTLiteralExpression.class,
        expression);
    return Assertions.assertInstanceOf(ASTNatLiteral.class, literal.getLiteral());
  }
}
