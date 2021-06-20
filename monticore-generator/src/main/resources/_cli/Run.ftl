<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("startProdPresent", "cliName", "generatedError")}
init();
org.apache.commons.cli.Options options = initOptions();
try{
    //create CLI Parser and parse input options from commandline
    org.apache.commons.cli.CommandLineParser cliparser = new org.apache.commons.cli.DefaultParser();
    org.apache.commons.cli.CommandLine cmd = cliparser.parse(options,args);

    //help: when --help
    if(cmd.hasOption("h")){
        printHelp(options);
    //do not continue, when help is printed.
        return;
    }
    //version: when --version
    else if(cmd.hasOption("v")){
        printVersion();
        //do not continue when help is printed
        return;
    }

}catch (org.apache.commons.cli.ParseException e) {
   // e.getMessage displays the incorrect input-parameters
   Log.error("0xA5C01${generatedError} Could not process ${cliName} parameters: " + e.getMessage());
 }