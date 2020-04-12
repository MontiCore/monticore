<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("transitions","className")}

<#-- plus String "modelName" is globally defined -->

public abstract class ${className} {

  <#-- Place the list of all transitions here -->
  ${tc.include("HandleTransitionAbstract.ftl",transitions)}

}
