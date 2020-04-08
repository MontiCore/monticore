<#-- (c) https://github.com/MontiCore/monticore -->
<#assign name = ast.getName()>
  protected static ${name}State ${name?uncap_first} = ${modelName}Factory.get${name}State();
