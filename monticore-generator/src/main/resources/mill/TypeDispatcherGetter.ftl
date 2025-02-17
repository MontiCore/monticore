<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millDispatcher", "dispatcherType")}

if (${millDispatcher}.get().typeDispatcher == null) {
  ${millDispatcher}.get().typeDispatcher = new ${dispatcherType}();
}
return ${millDispatcher}.get().typeDispatcher;