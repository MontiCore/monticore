<#-- (c) https://github.com/MontiCore/monticore -->
org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
options = addStandardOptions(options);
options = addAdditionalOptions(options);
return options;