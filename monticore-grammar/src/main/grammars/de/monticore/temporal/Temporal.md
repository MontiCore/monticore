# MontiCore - Temporal

The MontiCore Temporal languages provide reusable grammar components for
representing temporal values such as dates, times, combined date-times, and
periods. They define a common abstraction for temporal concepts and provide
concrete syntaxes for different standards and regional conventions.

The temporal language family is organized around a small common basis:

- `TemporalBasis` defines abstract interfaces such as `Date`, `Time`,
  `DateTime`, and `Period`.
- `ISOTemporals` realizes these interfaces using formats based on ISO 8601-1.
- `DETemporals` realizes them using date and time formats common in
  German-speaking regions.
- `EscapedTemporalLiterals` allows temporal values to be embedded as literals by
  using an explicit escape syntax.
- `ISOTemporals4Parsing` is an internal helper grammar used to extract ISO
  temporal components during parsing.

Temporal values are useful whenever a MontiCore language needs to express
scheduling data, timestamps, durations, validity ranges, historical dates, or
other time-related information in a structured way.

---

## Grammar [TemporalBasis.mc4](TemporalBasis.mc4)

### Purpose

`TemporalBasis` defines the core interfaces for temporal values. It does not
prescribe a concrete date or time syntax. Instead, it provides a common type
hierarchy that other temporal grammars can implement.

This makes it possible to define language components that work with temporal
concepts independently of whether the concrete syntax follows ISO 8601, German
DIN-style notation, or another future temporal notation.

### Hierarchical Connection

`TemporalBasis` extends `de.monticore.literals.MCCommonLiterals`

It is the root temporal grammar for the other temporal grammars:

```text
TemporalBasis
├── EscapedTemporalLiterals
├── ISOTemporals
└── DETemporals
```

### Central Nonterminals

| Nonterminal | Description                                                                | Example Realization                           |
|-------------|----------------------------------------------------------------------------|-----------------------------------------------|
| `Instant`   | A point on the time scale.                                                 |                                               |
| `Date`      | An `Instant` whose granularity ranges from centuries to days.              | `2015-04-01`, `01.04.2015`, `1. April 2015`   |
| `Time`      | An `Instant` whose granularity ranges from hours to fractions of a second. | `12:30:15`, `12:30 Uhr`                       |
| `DateTime`  | An `Instant` combining date and time components.                           | `2015-04-01T12:30:15`, `01.04.2015 12:30 Uhr` |
| `Period`    | A length or duration of time.                                              | `P2Y5M3D`, `PT6H30M`, `P2W`                   |

---

## Grammar [EscapedTemporalLiterals.mc4](EscapedTemporalLiterals.mc4)

### Purpose

`EscapedTemporalLiterals` makes temporal values usable as ordinary MontiCore
literals. It introduces an escape syntax that wraps any temporal `Instant` or
`Period`.

The escape form is useful because many temporal syntaxes may otherwise conflict
with existing literals, identifiers, or operators of a host language.

The basic idea is:

```text
d"temporal-value"
```

For example:

```text
d"2015-04-01"
d"12:30:15Z"
d"P2Y5M3D"
```

### Hierarchical Connection

`EscapedTemporalLiterals` extends `TemporalBasis`

It uses the interfaces from `TemporalBasis` and introduces one concrete literal
production:

```text
TemporalBasis
└── EscapedTemporalLiterals
    └── EscapedTemporalLiteral implements Literal
```

Because `EscapedTemporalLiteral` implements `Literal`, it can be used in
MontiCore expression languages that support literal expressions.

### Central Nonterminal

| Nonterminal              | Description                                                 |
|--------------------------|-------------------------------------------------------------|
| `EscapedTemporalLiteral` | A literal wrapper around either an `Instant` or a `Period`. |

### Concrete Syntax Examples

The escaped form can wrap temporal values provided by concrete temporal
grammars:

```text
d"2015-04-01"
d"20150401"
d"2015-W14-3"
d"12:30:15"
d"12:30:15Z"
d"2015-04-01T12:30:15"
d"P2Y5M3D"
d"PT6.5H"
d"P2W"
d"01.04.2015"
d"1. April 2015"
d"12:30 Uhr"
```

---

## Grammar [ISOTemporals.mc4](ISOTemporals.mc4)

### Purpose

`ISOTemporals` realizes the temporal interfaces from `TemporalBasis` using date
and time formats based on ISO 8601-1.

ISO 8601 is an international standard for the unambiguous representation of
temporal data. The grammar supports the main ISO-style representations for:

- combined date-time values,
- calendar dates,
- ordinal dates,
- week dates,
- times of day,
- periods.

The grammar supports both:

- basic notation, where separators are omitted, for example `20150401`;
- extended notation, where separators improve readability, for example
  `2015-04-01`.

It also supports features such as reduced precision for certain date formats,
expanded signed years, decimal fractions for times and periods, and UTC offsets.

### Hierarchical Connection

`ISOTemporals` extends `TemporalBasis`

It realizes the temporal interfaces as follows:

```text
TemporalBasis
├── Instant
│   └── ISOInstant
│       ├── ISODate
│       │   ├── CalendarDate
│       │   ├── OrdinalDate
│       │   └── WeekDate
│       │       ├── BasicWeekDate
│       │       └── ExtendedWeekDate
│       ├── ISOTime
│       └── ISODateTime
└── Period
    └── ISOPeriod
        ├── FullPeriod
        └── WeekPeriod
```

Some ISO temporal productions also implement `Literal`, allowing them to be used
directly where literals are accepted. Other temporal values can be used as
literals through `EscapedTemporalLiterals`.

### Central Nonterminals

| Nonterminal        | Description                                                                          |
|--------------------|--------------------------------------------------------------------------------------|
| `ISOInstant`       | Common interface for ISO-based instants.                                             |
| `ISODate`          | Common interface for ISO date formats.                                               |
| `CalendarDate`     | Gregorian calendar date using year, month, and day, with optional reduced precision. |
| `OrdinalDate`      | Date using year and day-of-year.                                                     |
| `WeekDate`         | Date using ISO week year, week number, and optional day of week.                     |
| `BasicWeekDate`    | Week date in basic notation.                                                         |
| `ExtendedWeekDate` | Week date in extended notation.                                                      |
| `ISOTime`          | Time of day with optional fraction and optional UTC offset.                          |
| `ISODateTime`      | Combined ISO date and time.                                                          |
| `ISOPeriod`        | Common interface for ISO periods.                                                    |
| `FullPeriod`       | Period using year, month, day, hour, minute, and second components.                  |
| `WeekPeriod`       | Period expressed in weeks.                                                           |
| `Sign`             | Optional plus or minus sign for expanded years or time shifts.                       |
| `Fraction`         | Decimal fraction using either `.` or `,`.                                            |
| `TimeShift`        | UTC marker `Z` or signed UTC offset.                                                 |

---

### ISO Calendar Dates

A calendar date represents a date in the Gregorian calendar.

Examples:

| Syntax          | Meaning                                              |
|-----------------|------------------------------------------------------|
| `2015-04-01`    | April 1, 2015, extended notation.                    |
| `20150401`      | April 1, 2015, basic notation.                       |
| `2015-04`       | April 2015, reduced precision.                       |
| `2015`          | Year 2015.                                           |
| `20`            | Century-level precision.                             |
| `+120004-03-10` | March 10 in the expanded year `120004`.              |
| `-100005-02-20` | February 20 in the expanded negative year `-100005`. |

---

### ISO Ordinal Dates

An ordinal date represents a date by year and day-of-year.

Examples:

| Syntax        | Meaning                                 |
|---------------|-----------------------------------------|
| `2015-091`    | The 91st day of 2015.                   |
| `2015091`     | Same date in basic notation.            |
| `+120004-091` | The 91st day of expanded year `120004`. |

Ordinal dates do not support reduced precision because shorter forms would
conflict with other ISO date formats.

---

### ISO Week Dates

A week date represents a date using an ISO week year, a week number, and
optionally a weekday.

Examples:

| Syntax       | Meaning                                         |
|--------------|-------------------------------------------------|
| `2015-W14-3` | Wednesday of ISO week 14 in ISO week year 2015. |
| `2015-W14`   | ISO week 14 in 2015.                            |
| `2015W143`   | Same as `2015-W14-3`, basic notation.           |
| `2015W14`    | ISO week 14 in basic notation.                  |

The weekday values follow the ISO convention:

| Value | Day       |
|-------|-----------|
| `1`   | Monday    |
| `2`   | Tuesday   |
| `3`   | Wednesday |
| `4`   | Thursday  |
| `5`   | Friday    |
| `6`   | Saturday  |
| `7`   | Sunday    |

---

### ISO Times

An ISO time represents a time of day using a 24-hour clock.

Examples:

| Syntax         | Meaning                                             |
|----------------|-----------------------------------------------------|
| `T1230`        | 12:30 in basic notation.                            |
| `T123015`      | 12:30:15 in basic notation.                         |
| `12:30`        | 12:30 in extended notation.                         |
| `12:30:15`     | 12:30:15 in extended notation.                      |
| `T12:30:15`    | Extended time with explicit time designator.        |
| `12:30:15.005` | 12:30:15 and 5 milliseconds.                        |
| `12:30:15,005` | Same with comma as decimal separator.               |
| `12:30:15Z`    | Time in UTC.                                        |
| `18:30-08:00`  | Time with UTC offset of minus 8 hours.              |
| `12:30+01:30`  | Time with UTC offset of plus 1 hour and 30 minutes. |

In basic notation, the leading `T` is used to disambiguate times from dates.

---

### ISO Date-Time Values

An ISO date-time combines an ISO date with an ISO time.

Examples:

| Syntax                 | Meaning                       |
|------------------------|-------------------------------|
| `2015-04-01T12:30`     | April 1, 2015 at 12:30.       |
| `2015-04-01T12:30:15`  | April 1, 2015 at 12:30:15.    |
| `20150401T123015`      | Same style in basic notation. |
| `2015-091T12:30:15`    | Ordinal date with time.       |
| `2015-W14-3T12:30:15Z` | Week date with UTC time.      |

The date and time are separated by the literal designator `T`.

---

### ISO Periods

An ISO period represents a duration or length of time.

The grammar supports two main period forms:

1. full periods with date and time units;
2. week periods.

Examples:

| Syntax              | Meaning                                                                |
|---------------------|------------------------------------------------------------------------|
| `P2Y5M3D`           | Period of 2 years, 5 months, and 3 days.                               |
| `PT6H30M`           | Period of 6 hours and 30 minutes.                                      |
| `PT6.5H`            | Period of 6.5 hours.                                                   |
| `P1Y2M3DT4H5M6S`    | Period of 1 year, 2 months, 3 days, 4 hours, 5 minutes, and 6 seconds. |
| `P1Y2M3DT4H5M6.25S` | Same, with fractional seconds.                                         |
| `P2W`               | Period of 2 weeks.                                                     |

The period designator `P` starts the value. If time components are used, they
are introduced by `T`.

ISO periods are different from SI unit literals such as `5s`, `3h`, or `2 km/s`.
SI Units describe physical units and support unit composition, prefixes and
compatibility checks. ISO periods instead describe structured temporal
durations, including calendar-dependent units such as years and months whose
exact length depends on context.

---

## Grammar [ISOTemporals4Parsing.mc4](parsing/ISOTemporals4Parsing.mc4)

### Purpose

`ISOTemporals4Parsing` is an internal helper grammar for ISO temporal values. It
is used during a second parsing round to extract structured component data from
ISO temporal representations.

It is **not intended for direct use** in ordinary MontiCore languages. The
grammar explicitly warns that it is incompatible with `MCBasics`.

Its main purpose is to parse the inner structure of ISO date, time, date-time,
and period values after they have been recognized by the main ISO temporal
grammar.

### Hierarchical Connection

Unlike the other temporal grammars, `ISOTemporals4Parsing` does not extend
`TemporalBasis`. It is a standalone parsing grammar in the package:

```text
de.monticore.temporal.parsing
```

Conceptually, it mirrors the ISO structures from `ISOTemporals`:

```text
ISOTemporals
└── uses ISOTemporals4Parsing internally for component extraction
```

### Central Nonterminals

| Nonterminal                                        | Description                                                           |
|----------------------------------------------------|-----------------------------------------------------------------------|
| `ISOTemporal4P`                                    | Root interface for ISO temporal values in the parsing helper grammar. |
| `ISODateTime4P`                                    | Combined ISO date and time.                                           |
| `ISODate4P`                                        | Interface for ISO date formats.                                       |
| `CalendarDate4P`                                   | Calendar date parser.                                                 |
| `OrdinalDate4P`                                    | Ordinal date parser.                                                  |
| `WeekDate4P`                                       | Week date parser.                                                     |
| `ISOTime4P`                                        | Abstract parser for ISO times.                                        |
| `TimeOnly`                                         | Time value used independently.                                        |
| `TimeOfDate`                                       | Time value used as part of a date-time.                               |
| `TimeShift4P`                                      | UTC designator or signed time offset.                                 |
| `ISOPeriod4P`                                      | Interface for ISO periods.                                            |
| `FullPeriod4P`                                     | Full ISO period parser.                                               |
| `WeekPeriod4P`                                     | Week-based ISO period parser.                                         |
| `Fraction4P`                                       | Decimal fraction parser.                                              |
| `Digit1`, `Digit2`, `Digit3`, `Digit4`, `DigitVar` | Fixed-length or variable-length digit sequences.                      |

### Design Notes

The grammar uses individual `Digit` tokens and fixed-length digit nonterminals
such as `Digit2`, `Digit3`, and `Digit4`.

This is important because ISO basic formats often rely on the exact number of
digits to distinguish between otherwise similar forms.

For example:

```text
2015
2015091
20150401
```

These values differ primarily by length and therefore require careful digit
counting.

### Concrete Syntax Examples

The helper grammar recognizes ISO forms such as:

```text
2015-04-01
20150401
2015-091
2015091
2015-W14-3
2015W143
T123015
12:30:15
12:30:15Z
2015-04-01T12:30:15
P2Y5M3D
PT6H30M
P2W
```

Although these examples match common ISO temporal syntax, users should rely on
`ISOTemporals` rather than using `ISOTemporals4Parsing` directly.

---

## Grammar [DETemporals.mc4](DETemporals.mc4)

### Purpose

`DETemporals` realizes the temporal interfaces from `TemporalBasis` using date
and time formats common in German-speaking regions.

It supports:

- numeric German-style dates,
- alphanumeric dates with German month names,
- times with the designator `Uhr`,
- combined German date-time values.

Unlike `ISOTemporals`, this grammar does not define period formats.

### Hierarchical Connection

`DETemporals` extends `TemporalBasis`

It realizes the temporal interfaces as follows:

```text
TemporalBasis
└── DETemporals
    ├── DEInstant implements Instant
    ├── DEDate implements Date, DEInstant
    │   ├── DENumericDate
    │   └── DEAlphanumericDate
    ├── DETime implements Time, DEInstant
    └── DEDateTime implements DateTime, DEInstant
```

### Central Nonterminals

| Nonterminal          | Description                                          |
|----------------------|------------------------------------------------------|
| `DEInstant`          | Common interface for German-style temporal instants. |
| `DEDate`             | Common interface for German-style dates.             |
| `DENumericDate`      | Numeric German-style date.                           |
| `DEAlphanumericDate` | Date with German month name or abbreviation.         |
| `DETime`             | Time with the suffix `Uhr`.                          |
| `DEDateTime`         | Combination of a German-style date and time.         |
| `DEMonth`            | German month names and abbreviations.                |

---

### German Numeric Dates

`DENumericDate` supports dates with optional day and month components.

Examples:

| Syntax       | Meaning        |
|--------------|----------------|
| `01.04.2015` | April 1, 2015. |
| `04.2015`    | April 2015.    |
| `2015`       | Year 2015.     |

---

### German Alphanumeric Dates

`DEAlphanumericDate` supports German month names and abbreviations.

Examples:

```text
1. April 2015
01. Apr. 2015
April 2015
März 2026
1. Mär. 2026
```

Supported month forms include:

| Month     | Full Form   | Abbreviation |
|-----------|-------------|--------------|
| January   | `Januar`    | `Jan.`       |
| February  | `Februar`   | `Feb.`       |
| March     | `März`      | `Mär.`       |
| April     | `April`     | `Apr.`       |
| May       | `Mai`       | `Mai.`       |
| June      | `Juni`      | `Jun.`       |
| July      | `Juli`      | `Jul.`       |
| August    | `August`    | `Aug.`       |
| September | `September` | `Sep.`       |
| October   | `Oktober`   | `Okt.`       |
| November  | `November`  | `Nov.`       |
| December  | `Dezember`  | `Dez.`       |

---

### German Times

`DETime` represents times using the German suffix `Uhr`.

Examples:

| Syntax         | Meaning     |
|----------------|-------------|
| `12 Uhr`       | 12 o’clock. |
| `12:30 Uhr`    | 12:30.      |
| `12:30:15 Uhr` | 12:30:15.   |

Whitespace is typically ignored between tokens in MontiCore grammars, so compact
forms such as `12Uhr` may also be accepted depending on the surrounding language
configuration.

---

### German Date-Time Values

`DEDateTime` combines a German-style date with a German-style time.

Examples:

| Syntax                    | Meaning                        |
|---------------------------|--------------------------------|
| `01.04.2015 12:30 Uhr`    | April 1, 2015 at 12:30.        |
| `1. April 2015 12 Uhr`    | April 1, 2015 at 12 o’clock.   |
| `April 2015 12:30:15 Uhr` | April 2015 with time 12:30:15. |

---

## Summary

The MontiCore Temporal language family separates temporal concepts from concrete
notation:

| Grammar                   | Role                                                              |
|---------------------------|-------------------------------------------------------------------|
| `TemporalBasis`           | Defines the abstract temporal interfaces.                         |
| `EscapedTemporalLiterals` | Makes temporal values usable as escaped literals.                 |
| `ISOTemporals`            | Provides ISO 8601-style date, time, date-time, and period syntax. |
| `ISOTemporals4Parsing`    | Internal helper grammar for extracting ISO temporal components.   |
| `DETemporals`             | Provides German-style date, time, and date-time syntax.           |

Together, these grammars allow MontiCore language developers to reuse
standardized temporal concepts while choosing a concrete syntax appropriate for
their domain.

---

## Further Information

- [Project root: MontiCore @ GitHub](https://github.com/MontiCore/monticore)
- [MontiCore documentation](https://www.monticore.de/)
- [List of languages](../../../../../../../docs/Languages.md)
- [MontiCore Core Grammar Library](../Grammars.md)
- [Best Practices](../../../../../../../docs/BestPractices.md)
- [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
- [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)