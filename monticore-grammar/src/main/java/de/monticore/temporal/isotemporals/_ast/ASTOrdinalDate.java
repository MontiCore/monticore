/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals._ast;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

public class ASTOrdinalDate extends ASTOrdinalDateTOP {
  
  /**
   * @return a string representing the OrdinalDate that can be processed by the second parser.
   * Methods with this purpose are uniformly called <code>toRawString</code>.
   */
  public String toRawString() {
    StringBuilder result = new StringBuilder();
    if (isPresentSign()) {
      if (getSign() == ASTSign.PLUS) {
        result.append("+");
      } else {
        result.append("-");
      }
    }
    result.append(getPre());
    if (isPresentPost()) {
      result.append("-");
      result.append(getPost());
    }
    return result.toString();
  }
  
  /**
   * Checks whether the specified temporal field is supported.
   * <p>
   * Only {@link ChronoField#YEAR} and {@link ChronoField#DAY_OF_YEAR} are supported.
   *
   * @param field the temporal field to check
   * @return {@code true} if the field is supported, otherwise {@code false}
   */
  @Override
  public boolean isSupported(TemporalField field) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      switch (f) {
        case YEAR:
        case DAY_OF_YEAR:
          return true;
      }
    }
    return false;
  }
  
  /**
   * Returns the value of the specified temporal field.
   *
   * @param field the temporal field to query
   * @return the value of the requested field
   * @throws UnsupportedTemporalTypeException if the field is not supported
   */
  @Override
  public long getLong(TemporalField field) {
    if (field instanceof ChronoField) {
      ChronoField f = (ChronoField) field;
      switch (f) {
        case YEAR:
          return getYear();
        case DAY_OF_YEAR:
          return getDayOfYear();
      }
    }
    
    throw new UnsupportedTemporalTypeException(field.toString());
  }
}
