<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initialState","transitions","states","className", "existsHWCExtension")}
public <#if existsHWCExtension>abstract </#if>class ${className}{

  protected ${modelName} typedThis;

  <#if !existsHWCExtension>
    public ${className}(){
      typedThis = this;
    }
  </#if>

  public ${modelName} getTypedThis() {
    return typedThis;
  }

  public void setTypedThis(${modelName} typedThis) {
    this.typedThis = typedThis;
  }

  ${tc.include("StatechartStateAttributes.ftl",states)}

  protected Abstract${modelName}State currentState = ${modelName}Factory.get${initialState.getName()}State();

  public void setState(Abstract${modelName}State state){
      currentState = state;
  }

  ${tc.include("StatechartTransitionMethod.ftl",transitions)}

}