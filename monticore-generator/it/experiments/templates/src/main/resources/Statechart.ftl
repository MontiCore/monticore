<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initialState",
               "stimuli",
               "className", 
               "existsHWCExtension")}
<#-- plus: String "modelName" is globally defined -->

/**
 * ${className} ist the main class of a Statechart that is realized
 * using a state pattern approach.
 * It contains an instance of each state and the
 * pointer to the current state.
 */
public <#if existsHWCExtension>abstract </#if>
          class ${className} {

  protected ${modelName} typedThis;

  <#-- Constructor is generated if class is not abstract -->
  <#if !existsHWCExtension>
    /**
     * Default empty constructor
     * (only sets the correctly typed Version of this)
     */
    public ${className}(){
      typedThis = this;
    }
  </#if>

  /**
   * A trick to reconstruct the de'facto type of "this" instead of
   * its TOP-type; this often needed, but always the same piece of code 
   */
  public ${modelName} getTypedThis() {
    return typedThis;
  }

  /**
   * A trick to reconstruct the de'facto type of "this" instead of
   * its TOP-type;
   */
  public void setTypedThis(${modelName} typedThis) {
    this.typedThis = typedThis;
  }

  <#-- Place the list of states here -->
  ${tc.include("StatechartStateAttributes.ftl", ast.getStateList())}

  /** 
   * This is the pointer to the current state 
   * and start with initial state
   */ 
  protected Abstract${modelName}State currentState =
                           ${initialState.getName()?uncap_first};

  /** 
   * Current state updater 
   */
  public void setState(Abstract${modelName}State state){
      currentState = state;
  }

  <#-- Add the list of stimuli as method calls -->
  <#list stimuli as stimulusName>
    /**
     * Method call delegated to the current state object
     */
    public void ${stimulusName?uncap_first}() {
      currentState.handle${stimulusName?cap_first}(getTypedThis());
    }
  </#list>
}
