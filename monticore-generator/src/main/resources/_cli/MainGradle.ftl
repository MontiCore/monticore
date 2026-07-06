<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarname")}
    // Gradle main method of this tool - does not initialize a log/exit/catch exceptions/etc.
    ${grammarname}Tool tool = new  ${grammarname}Tool();
    tool.run(args);
