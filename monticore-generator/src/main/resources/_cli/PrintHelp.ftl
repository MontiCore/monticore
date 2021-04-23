<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("runnerName")}

  org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
  formatter.setWidth(80);
  formatter.printHelp("${runnerName}", options);
