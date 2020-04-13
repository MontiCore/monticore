<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("transitions","className")}

<#-- plus: String "modelName" is globally defined -->

/**
 * This generated class is the common (abstract) 
 * interface that all states in the state pattern implement
 */
public abstract class ${className} {

  <#-- Place the list of all transitions here -->
  ${tc.include("HandleTransitionAbstract.ftl",transitions)}

}
