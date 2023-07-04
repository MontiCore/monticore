<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millDispatcher", "dispatcherType")}

if (${millDispatcher}.typeDispatcher == null) {
${millDispatcher}.typeDispatcher = new ${dispatcherType}();
}
return ${millDispatcher}.typeDispatcher;