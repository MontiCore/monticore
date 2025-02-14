<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("names", "types")}

this.scopeCallstack = new Stack<de.monticore.interpreter.MIScope>();
this.scopeCallstack.push(new de.monticore.interpreter.MIScope());
this.setRealThis(this);
<#list names as name>
    this.${name?uncap_first} = new ${types[name?index]}(this);
</#list>