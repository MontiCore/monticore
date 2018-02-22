<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "methodName")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  if (${methodName}().isPresent()) {
     return ${methodName}().get();
  }
  Log.error("0xA7003${genHelper.getGeneratedErrorCode(ast)} ${methodName} can't return a value. It is empty.");
  // Normally this statement is not reachable
  throw new IllegalStateException();
