<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cliName")}
  org.apache.commons.cli.help.HelpFormatter formatter =
    org.apache.commons.cli.help.HelpFormatter.builder().get();
  try {
    formatter.printHelp("java " + this.getClass().getSimpleName(), "", options, "", true);
  }
  catch (java.io.IOException e) {
    throw new RuntimeException(e);
  }
