<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("existingTransitions",
               "nonExistingTransitionNames",
               "className",
               "existsHWCExtension")}
<#-- plus: String "modelName" is globally defined -->

/**
 * This generated class implements one state
 * and contains the method implementations for in that state
 */
public <#if existsHWCExtension>abstract </#if>
     class ${className} extends Abstract${modelName}State {

  <#-- Iterate over the lists of (non-) existing transitions -->
  ${tc.include("HandleTransition.ftl",
               existingTransitions)}

  ${tc.include("HandleNotExistingTransition.ftl",
               nonExistingTransitionNames)}

}

