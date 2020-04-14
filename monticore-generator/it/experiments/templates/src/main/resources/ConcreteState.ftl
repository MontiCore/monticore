<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("outgoing",
               "className",
               "existsHWCExtension")}
<#-- plus: String "modelName" is globally defined -->

/**
 * This generated class implements one state
 * and contains the method implementations for in that state
 */
public <#if existsHWCExtension>abstract </#if>
     class ${className} extends Abstract${modelName}State {

<#-- Add the list of stimuli as method calls with default implemementations-->
<#list outgoing as stimulusName, transitionAST>
  /**
   * Signature of handle${stimulusName?cap_first}
   * The method contains the state change to ${transitionAST.getTo()?uncap_first}
   * @param ${modelName} sc
   */
  @Override
  public void handle${stimulusName?cap_first}(${modelName} sc) {
      sc.setState(${modelName}.${transitionAST.getTo()?uncap_first});
  }
</#list>

}

