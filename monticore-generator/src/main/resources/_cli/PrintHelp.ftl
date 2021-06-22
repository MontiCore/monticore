<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cliName")}

  org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
  formatter.setWidth(80);
  formatter.printHelp("${cliName}", options);
