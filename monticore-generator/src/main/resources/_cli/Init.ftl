<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millFullName")}
// reset any previous initializations of the mill
// This should not be necessary iff all tools reset their mills
${millFullName}.reset();
// initialize logging with standard logging iff not initialized already
Log.ensureInitialization();
${millFullName}.init();
