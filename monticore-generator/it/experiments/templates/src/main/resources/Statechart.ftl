<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("initialState","transitions","states","className", "existsHWCExtension")}
public <#if existsHWCExtension>abstract </#if>class ${className}{

  protected ${modelName} realThis;

  <#if !existsHWCExtension>
    public ${className}(){
      realThis = this;
    }
  </#if>

  public ${modelName} getRealThis() {
    return realThis;
  }

  public void setRealThis(${modelName} realThis) {
    this.realThis = realThis;
  }

  ${tc.include("StatechartStateAttributes.ftl",states)}

  protected Abstract${modelName}State currentState = ${modelName}Factory.get${initialState.getName()}State();

  public void setState(Abstract${modelName}State state){
      currentState = state;
  }

  ${tc.include("StatechartTransitionMethod.ftl",transitions)}

}