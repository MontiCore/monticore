<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarname","millFullName")}

 ${grammarname}CLI cli = new  ${grammarname}CLI();
    // initialize logging with standard logging
    Log.init();
    ${millFullName}.init();
    cli.run(args);
