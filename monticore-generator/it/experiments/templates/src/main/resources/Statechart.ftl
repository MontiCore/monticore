<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initialState",
               "transitions",
               "states",
               "className", 
               "existsHWCExtension")}
<#-- plus String "modelName" is globally defined -->

// TODO: zuviele Parameter (state, transition: koennte aus ast abgeleitet werden)

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

// TODO: ueberlegen ob dies ausgelagert werden kann 
// (gemeinsam mit Konstruktor)
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
  ${tc.include("StatechartStateAttributes.ftl",states)}

  /** 
   * This is the pointer to the current state 
   */ 
  protected Abstract${modelName}State currentState =
                           ${initialState.getName()?uncap_first};

  /** 
   * Current state updater 
   */
  public void setState(Abstract${modelName}State state){
      currentState = state;
  }

  <#-- Add the list of transition in form of method calls -->
  ${tc.include("StatechartTransitionMethod.ftl",transitions)}

}
