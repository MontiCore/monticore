/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.isotemporals.util;

import de.monticore.temporal.isotemporals._ast.ASTCalendarDate;
import de.monticore.temporal.isotemporals._ast.ASTISODateTime;
import de.monticore.temporal.isotemporals._ast.ASTISOTime;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Java-time conversion helpers for {@link de.monticore.temporal.isotemporals._ast}
 * AST nodes.
 *
 * <p>All methods require an explicit UTC offset in the source value.
 * Named time-zone identifiers are not supported; only {@code Z} and numeric
 * offsets such as {@code +02:00} or {@code -05:30} are accepted.</p>
 */
public final class ISOTemporalsConversions {

  private ISOTemporalsConversions() {
  }

  /**
   * Converts an ISO 8601 date-time node to an {@link OffsetDateTime}.
   *
   * <p>Hour and minute components of the UTC offset are preserved
   * separately, so offsets such as {@code +05:45} and {@code -00:30}
   * are represented correctly.</p>
   *
   * @param value the parsed ISO date-time node (must not be {@code null})
   * @return the corresponding {@link OffsetDateTime}
   * @throws java.time.format.DateTimeParseException if the value cannot be parsed
   */
  public static OffsetDateTime toOffsetDateTime(ASTISODateTime value) {
    Objects.requireNonNull(value, "value");
    return OffsetDateTime.parse(value.toRawString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }

  /**
   * Converts an ISO 8601 calendar-date node to a {@link LocalDate}.
   *
   * <p>Date-only values carry no time-zone information. Callers that need
   * an instant must supply a time zone and time-of-day themselves.</p>
   *
   * @param value the parsed ISO calendar-date node (must not be {@code null})
   * @return the corresponding {@link LocalDate}
   * @throws java.time.format.DateTimeParseException if the value cannot be parsed
   */
  public static LocalDate toLocalDate(ASTCalendarDate value) {
    Objects.requireNonNull(value, "value");
    return LocalDate.parse(value.toRawString(), DateTimeFormatter.ISO_LOCAL_DATE);
  }

  /**
   * Converts an ISO 8601 time node to an {@link OffsetTime}.
   *
   * <p>An explicit UTC offset is required. The optional leading {@code T}
   * designator is stripped before parsing so that both {@code T12:00:00Z}
   * and {@code 12:00:00Z} are accepted.</p>
   *
   * @param value the parsed ISO time node (must not be {@code null})
   * @return the corresponding {@link OffsetTime}
   * @throws java.time.format.DateTimeParseException if the value cannot be parsed
   *         or lacks a UTC offset
   */
  public static OffsetTime toOffsetTime(ASTISOTime value) {
    Objects.requireNonNull(value, "value");
    String source = value.toRawString();
    if (source.startsWith("T")) {
      source = source.substring(1);
    }
    return OffsetTime.parse(source, DateTimeFormatter.ISO_OFFSET_TIME);
  }

}
