/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.timetriggerconditions._cocos;

import de.se_rwth.commons.logging.Log;
import de.monticore.temporal.timetriggerconditions._ast.ASTCronAtom;
import de.monticore.temporal.timetriggerconditions._ast.ASTCronCondition;
import de.monticore.temporal.timetriggerconditions._ast.ASTCronElement;
import de.monticore.temporal.timetriggerconditions._ast.ASTCronExpression;
import de.monticore.temporal.timetriggerconditions._ast.ASTCronField;
import de.monticore.temporal.timetriggerconditions._ast.ASTCronName;
import de.monticore.temporal.timetriggerconditions._ast.ASTCronNumber;
import de.monticore.temporal.timetriggerconditions._ast.ASTCronRange;
import de.monticore.temporal.timetriggerconditions._ast.ASTCronValue;
import de.monticore.temporal.timetriggerconditions._ast.ASTCronWildcard;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

/**
 * Checks that a Cron condition uses the supported Crontab.guru five-field syntax.
 */
public class CronExpressionIsValid implements TimeTriggerConditionsASTCronConditionCoCo {
  
  /** Error code for invalid field values, names, or ranges. */
  public static final String INVALID_FIELD_VALUE = "0xF0012";
  /** Error code for a zero or unrepresentable step. */
  public static final String INVALID_STEP = "0xF0013";
  /** Warning code for month/day combinations without a Gregorian occurrence. */
  public static final String IMPOSSIBLE_DATE = "0xF0014";
  
  /** Maximum possible Gregorian day for each month, including leap-day February. */
  private static final int[] MAXIMUM_DAY_BY_MONTH = { 0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30,
      31 };
  
  /** Case-insensitive Cron month names mapped to their numeric values. */
  protected static final Map<String, Integer> MONTH_NAMES = Map.ofEntries(Map.entry("JAN", 1), Map
      .entry("FEB", 2), Map.entry("MAR", 3), Map.entry("APR", 4), Map.entry("MAY", 5), Map.entry(
          "JUN", 6), Map.entry("JUL", 7), Map.entry("AUG", 8), Map.entry("SEP", 9), Map.entry("OCT",
              10), Map.entry("NOV", 11), Map.entry("DEC", 12));
  
  /** Case-insensitive Cron weekday names mapped to Sunday-based numeric values. */
  protected static final Map<String, Integer> WEEKDAY_NAMES = Map.of("SUN", 0, "MON", 1, "TUE", 2,
      "WED", 3, "THU", 4, "FRI", 5, "SAT", 6);
  
  /** Creates the stateless Cron validation CoCo. */
  public CronExpressionIsValid() {
  }
  
  @Override
  public void check(ASTCronCondition node) {
    ASTCronExpression expression = node.getExpression();
    
    boolean valid = checkField(expression.getMinute(), 0, 59, "minute", Map.of());
    valid &= checkField(expression.getHour(), 0, 23, "hour", Map.of());
    valid &= checkField(expression.getDayOfMonth(), 1, 31, "day-of-month", Map.of());
    valid &= checkField(expression.getMonth(), 1, 12, "month", MONTH_NAMES);
    valid &= checkField(expression.getDayOfWeek(), 0, 7, "day-of-week", WEEKDAY_NAMES);
    
    if (valid) {
      warnIfCalendarImpossible(expression);
    }
  }
  
  /** Validates every value, range, name, and step in one Cron field. */
  protected boolean checkField(ASTCronField field, int minimum, int maximum, String fieldName,
      Map<String, Integer> names) {
    for (ASTCronElement element : field.getElementsList()) {
      if (!hasValidValue(element, minimum, maximum, names)) {
        Log.error(INVALID_FIELD_VALUE + " Cron " + fieldName + " must use values from " + minimum
            + " to " + maximum + ", valid names, and ascending ranges.", element
                .get_SourcePositionStart());
        return false;
      }
      if (!hasValidStep(element)) {
        Log.error(INVALID_STEP + " Cron " + fieldName
            + " step must be a positive representable integer.", element.get_SourcePositionStart());
        return false;
      }
    }
    return true;
  }
  
  /**
   * Reports schedules whose selected months cannot contain any selected day-of-month.
   * February is evaluated with 29 days because the dialect has no year field and leap
   * years therefore provide valid occurrences. A restricted day-of-week can rescue a
   * restricted day-of-month through the POSIX OR rule; in that case no warning is issued.
   */
  protected void warnIfCalendarImpossible(ASTCronExpression expression) {
    if (!startsWithWildcard(expression.getDayOfMonth()) && !startsWithWildcard(expression
        .getDayOfWeek())) {
      return;
    }
    
    Set<Integer> days = expand(expression.getDayOfMonth(), 1, 31, Map.of());
    Set<Integer> months = expand(expression.getMonth(), 1, 12, MONTH_NAMES);
    boolean possible = months.stream().anyMatch(month -> days.stream().anyMatch(day -> day
        <= MAXIMUM_DAY_BY_MONTH[month]));
    
    if (!possible) {
      Log.warn(IMPOSSIBLE_DATE
          + " Cron day-of-month and month fields cannot match a Gregorian date.", expression
              .getDayOfMonth().get_SourcePositionStart());
    }
  }
  
  /**
   * Returns whether a field begins with a wildcard value. Vixie Cron decides between the
   * intersection and the union of the two day fields by looking at the first element only,
   * so {@code *,MON} forms an intersection while {@code MON,*} forms a union. This test is
   * textual on purpose and must not be widened to the whole field.
   */
  protected boolean startsWithWildcard(ASTCronField field) {
    if (field.getElementsList().isEmpty()) {
      return false;
    }
    ASTCronElement first = field.getElements(0);
    return first instanceof ASTCronValue value && value.getValue() instanceof ASTCronWildcard;
  }
  
  /** Expands a previously validated field to its selected numeric set. */
  protected Set<Integer> expand(ASTCronField field, int minimum, int maximum,
      Map<String, Integer> names) {
    Set<Integer> values = new LinkedHashSet<>();
    for (ASTCronElement element : field.getElementsList()) {
      int start;
      int end;
      int step = 1;
      if (element instanceof ASTCronValue value) {
        if (value.getValue() instanceof ASTCronWildcard) {
          start = minimum;
          end = maximum;
        }
        else {
          start = resolve(value.getValue(), names).orElseThrow();
          end = value.isPresentStep() ? maximum : start;
        }
        if (value.isPresentStep()) {
          step = value.getStep().getValue();
        }
      }
      else if (element instanceof ASTCronRange range) {
        start = resolve(range.getStart(), names).orElseThrow();
        end = resolve(range.getEnd(), names).orElseThrow();
        if (range.isPresentStep()) {
          step = range.getStep().getValue();
        }
      }
      else {
        throw new IllegalArgumentException("Unsupported Cron element AST type: " + element
            .getClass().getName());
      }
      
      for (int value = start; value <= end; value += step) {
        values.add(value);
        if (value > end - step) {
          break;
        }
      }
    }
    return values;
  }
  
  /** Validates the atom or inclusive range represented by one Cron element. */
  protected boolean hasValidValue(ASTCronElement element, int minimum, int maximum,
      Map<String, Integer> names) {
    if (element instanceof ASTCronValue) {
      ASTCronAtom atom = ((ASTCronValue) element).getValue();
      if (atom instanceof ASTCronWildcard) {
        return true;
      }
      OptionalInt value = resolve(atom, names);
      return value.isPresent() && isInRange(value.getAsInt(), minimum, maximum);
    }
    if (element instanceof ASTCronRange) {
      ASTCronRange range = (ASTCronRange) element;
      if (range.getStart() instanceof ASTCronWildcard || range
          .getEnd() instanceof ASTCronWildcard) {
        return false;
      }
      OptionalInt start = resolve(range.getStart(), names);
      OptionalInt end = resolve(range.getEnd(), names);
      return start.isPresent() && end.isPresent() && isInRange(start.getAsInt(), minimum, maximum)
          && isInRange(end.getAsInt(), minimum, maximum) && start.getAsInt() <= end.getAsInt();
    }
    return false;
  }
  
  /** Validates an optional step attached to a value or range. */
  protected boolean hasValidStep(ASTCronElement element) {
    if (element instanceof ASTCronValue && ((ASTCronValue) element).isPresentStep()) {
      return isPositive(((ASTCronValue) element).getStep());
    }
    if (element instanceof ASTCronRange && ((ASTCronRange) element).isPresentStep()) {
      return isPositive(((ASTCronRange) element).getStep());
    }
    return true;
  }
  
  /** Resolves a numeric or named atom to its field-specific integer value. */
  protected OptionalInt resolve(ASTCronAtom atom, Map<String, Integer> names) {
    if (atom instanceof ASTCronNumber) {
      return getValue(((ASTCronNumber) atom).getValue());
    }
    if (atom instanceof ASTCronName) {
      Integer value = names.get(((ASTCronName) atom).getValue().toUpperCase(Locale.ROOT));
      return value == null ? OptionalInt.empty() : OptionalInt.of(value);
    }
    return OptionalInt.empty();
  }
  
  /** Returns whether a step literal is representable and strictly positive. */
  protected boolean isPositive(de.monticore.literals.mccommonliterals._ast.ASTNatLiteral literal) {
    OptionalInt value = getValue(literal);
    return value.isPresent() && value.getAsInt() > 0;
  }
  
  /** Safely reads a natural-number literal without propagating overflow errors. */
  protected OptionalInt getValue(
      de.monticore.literals.mccommonliterals._ast.ASTNatLiteral literal) {
    try {
      return OptionalInt.of(literal.getValue());
    }
    catch (NumberFormatException exception) {
      return OptionalInt.empty();
    }
  }
  
  /** Returns whether a value lies inside an inclusive field range. */
  protected boolean isInRange(int value, int minimum, int maximum) {
    return value >= minimum && value <= maximum;
  }
  
}
