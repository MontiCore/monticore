/* (c) https://github.com/MontiCore/monticore */

// this is a very user friendly console appender
// which only outputs level >= INFO
appender("CONSOLE", ConsoleAppender) {
  filter(ch.qos.logback.classic.filter.ThresholdFilter) {
    level = INFO
  }
  encoder(PatternLayoutEncoder) {
    pattern = "%-7([%level]) %message%n"
  }
}

def bySecond = timestamp("yyyy-MM-dd-HHmmss")
def out = System.getProperty("MC_OUT") == null ? "target" : System.getProperty("MC_OUT")

// this is a rather technically detailed file appender
appender("FILE", FileAppender) {
  file = "${out}/monticore.test.${bySecond}.log"
  encoder(PatternLayoutEncoder) {
    pattern = "%date{yyyy-MM-dd HH:mm:ss} %-7([%level]) %logger{26} %message%n"
  }
}

// everything with level >= DEBUG is logged to the file (see above)
root(DEBUG, ["FILE", "CONSOLE"])
