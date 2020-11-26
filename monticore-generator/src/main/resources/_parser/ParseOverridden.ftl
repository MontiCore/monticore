<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ruleName", "fullRuleName", "millFullName")}
  Optional<${fullRuleName}> ast = ${millFullName}.parser().parse${ruleName}(fileName);
  if(ast.isPresent()){
    return Optional.of(ast.get());
  }else{
    return Optional.empty();
  }