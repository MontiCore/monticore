<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarname", "startprod", "millFullName")}

 ${grammarname}Runner runner = new  ${grammarname}Runner();
    // initialize logging with standard logging
    Log.init();
    ${millFullName}.init();
    runner.run(args);
