<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("generatedError")}
 if (cmd.hasOption("d")) { // backwards compatibility - might have to be registered as an option first!
  // developer enabled -> debug & trace output (for backwards compatibility)
  Log.setLogLevelDebug(true);
  Log.setLogLevelTrace(true);
}
if (cmd.hasOption("stacktrace")) {
  String[] stackTrace = cmd.getOptionValues("stacktrace");
  if (stackTrace == null) {
    // stacktrace without level -> print error stacktraces to stdout
    Log.addLogHook(new de.se_rwth.commons.logging.ErrorStacktraceConsoleLogHook(System.out));
  } else {
    // --stacktrace=WARN          <- also prints the stacktrace of warnings to stdout
    // --stacktrace=ERROR:stderr  <- prints the stacktrace of errors to stderr
    List<String> stacktraceLevels = Arrays.stream(stackTrace)
        .flatMap(v-> Arrays.stream(v.split(",")))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .toList();
    for (String level : stacktraceLevels) {
      String[] out = level.split(":", 2); // allow stdout and stderr output
      level = out[0];
      java.io.PrintStream target = out.length==2 && out[1].equalsIgnoreCase("stderr")?System.err:System.out;
      if (level.equalsIgnoreCase("error")) {
        Log.addLogHook(new de.se_rwth.commons.logging.ErrorStacktraceConsoleLogHook(target));
      } else if (level.equalsIgnoreCase("warn")) {
        Log.addLogHook(new de.se_rwth.commons.logging.WarningStacktraceConsoleLogHook(target));
      } else  if (level.equalsIgnoreCase("info")) {
        Log.addLogHook(new de.se_rwth.commons.logging.InfoStacktraceConsoleLogHook(target));
      } else {
        Log.error("0xA1064x${generatedError}: Could not set stacktraces for unknown log level " + level);
      }
    }
  }
}