<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "astNodeName", "methodName")}
    {
      if (mill${astNodeName} == null) {
        mill${astNodeName} = getMill();
      }
      return mill${astNodeName}._${methodName}Builder();
    }
