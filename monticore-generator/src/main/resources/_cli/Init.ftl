<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millFullName")}
// initialize logging with standard logging iff not initialized already
Log.ensureInitialization();
${millFullName}.init();
${millFullName}.globalScope().clear();
