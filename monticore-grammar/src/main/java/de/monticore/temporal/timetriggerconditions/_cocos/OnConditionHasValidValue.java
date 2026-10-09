/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.timetriggerconditions._cocos;

import de.monticore.temporal.isotemporals._ast.ASTCalendarDate;
import de.monticore.temporal.isotemporals._ast.ASTISODateTime;
import de.monticore.temporal.isotemporals._ast.ASTISOTime;
import de.se_rwth.commons.logging.Log;

import java.time.format.DateTimeParseException;

import de.monticore.temporal.timetriggerconditions._ast.ASTOnCondition;
import de.monticore.temporal.isotemporals.util.ISOTemporalsConversions;

/**
 * Checks that an on condition denotes a valid date-time, date, or time.
 * Date-times and times require an explicit UTC offset; dates denote midnight UTC.
 */
public class OnConditionHasValidValue implements TimeTriggerConditionsASTOnConditionCoCo {
  
  public static final String INVALID_DATE_TIME = "0xF0004";
  public static final String INVALID_DATE = "0xF0006";
  public static final String INVALID_TIME = "0xF0007";
  
  @Override
  public void check(ASTOnCondition node) {
    if (node.isPresentIsoDateTime()) {
      checkDateTime(node.getIsoDateTime());
      return;
    }
    if (node.isPresentDateOnly()) {
      checkDate(node.getDateOnly());
      return;
    }
    checkTime(node.getTimeOnly());
  }
  
  private void checkDateTime(ASTISODateTime node) {
    String dateTime = node.toRawString();
    
    try {
      ISOTemporalsConversions.toOffsetDateTime(node);
    }
    catch (DateTimeParseException exception) {
      Log.error(INVALID_DATE_TIME + " ISO 8601 date-time '" + dateTime
          + "' is not a valid UTC instant: " + exception.getMessage() + ".", node
              .get_SourcePositionStart());
    }
  }
  
  private void checkDate(ASTCalendarDate node) {
    String date = node.toRawString();
    
    try {
      ISOTemporalsConversions.toLocalDate(node);
    }
    catch (DateTimeParseException exception) {
      Log.error(INVALID_DATE + " ISO 8601 date '" + date + "' is not a valid date: " + exception
          .getMessage() + ".", node.get_SourcePositionStart());
    }
  }
  
  private void checkTime(ASTISOTime node) {
    String time = node.toRawString();
    
    if (!node.isPresentTimeShift()) {
      Log.error(INVALID_TIME + " ISO 8601 time '" + time
          + "' requires an explicit UTC offset ('Z' or a numeric offset).", node
              .get_SourcePositionStart());
      return;
    }
    
    try {
      ISOTemporalsConversions.toOffsetTime(node);
    }
    catch (DateTimeParseException exception) {
      Log.error(INVALID_TIME + " ISO 8601 time '" + time + "' is not a valid UTC time: " + exception
          .getMessage() + ".", node.get_SourcePositionStart());
    }
  }
  
}
