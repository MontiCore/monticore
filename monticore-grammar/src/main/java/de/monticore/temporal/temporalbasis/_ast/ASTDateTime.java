package de.monticore.temporal.temporalbasis._ast;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

@SuppressWarnings("unused") // This interface is part of the public-facing API
public interface ASTDateTime extends ASTDateTimeTOP {
  
  ASTDate getDate();
  
  ASTTime getTime();

  @Override
  default Temporal toTemporal() {
    if (isExactlyLocalDateTime()) {
      return toLocalDateTime();
    } else if (isOffsetDateTime()) {
      return toOffsetDateTime();
    }
    throw new UnsupportedTemporalTypeException("Conversion of ASTDateTime only supported into LocalDateTime or OffsetDateTime");
  }
  
  @Override
  default boolean isSupported(TemporalField field) {
    return getDate().isSupported(field) || getTime().isSupported(field);
  }
  
  @Override
  default  long getLong(TemporalField field) {
    if (getDate().isSupported(field)) {
      return getDate().getLong(field);
    } else if (getTime().isSupported(field)) {
      return getTime().getLong(field);
    }
    throw new UnsupportedTemporalTypeException(field.toString());
  }

  default boolean isLocalDateTime() {
    return getDate().isLocalDate() && getTime().isLocalTime();
  }

  default boolean isExactlyLocalDateTime() {
    return getDate().isLocalDate() && getTime().isExactlyLocalTime();
  }

  default LocalDateTime toLocalDateTime() {
    return getDate().toLocalDate().atTime(getTime().toLocalTime());
  }

  default boolean isOffsetDateTime() {
    return getDate().isLocalDate() && getTime().isOffsetTime();
  }

  default OffsetDateTime toOffsetDateTime() {
    return getDate().toLocalDate().atTime(getTime().toOffsetTime());
  }
}
