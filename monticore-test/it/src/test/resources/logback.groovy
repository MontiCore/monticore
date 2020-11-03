/* (c) https://github.com/MontiCore/monticore */

// this is a very user friendly console appender
appender("CONSOLE", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%-7([%level]) %message%n%exception{0}"
  }
}

// this is a rather technically detailed file appender
appender("MAIN", FileAppender) {
  file = "target/test.log"
  append = false
  encoder(PatternLayoutEncoder) {
    pattern = "%date{yyyy-MM-dd HH:mm:ss} %-7([%level]) %logger{26} %message%n"
  }
}

// everything with level >= DEBUG is logged to the file (see above)
root(DEBUG, ["MAIN"])

// the dedicated logger called "USER" logs everything with level >= TRACE to the console
logger("USER", TRACE, ["CONSOLE"])
