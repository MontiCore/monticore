<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("stimuli",
               "className")}

<#-- plus: String "modelName" is globally defined -->

/**
 * This generated class is the common (abstract) 
 * interface that all states in the state pattern implement
 */
public abstract class ${className} {

<#-- Add the list of stimuli as method calls -->
<#list stimuli as stimulusName>
  /**
   * Signature of handle${stimulusName?cap_first}
   * The method is to be overwritten in each concrete subclass
   * @param ${modelName} sc
   */
  void handle${stimulusName?cap_first}(${modelName} sc) {
    // here comes handling of incompleteness: 
    // we only ignore
  }
</#list>
}
