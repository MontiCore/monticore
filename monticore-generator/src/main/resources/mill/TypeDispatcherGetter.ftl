<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("dispatcherType")}

if (getMill().typeDispatcher == null) {
  getMill().typeDispatcher = new ${dispatcherType}();
}
return getMill().typeDispatcher;