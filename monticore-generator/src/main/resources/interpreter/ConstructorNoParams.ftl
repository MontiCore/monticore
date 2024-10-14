<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("names", "types")}

this.contextMap = new java.util.HashMap<>();
this.setRealThis(this);
<#list names as name>
    this.${name?uncap_first} = new ${types[name?index]}(this);
</#list>