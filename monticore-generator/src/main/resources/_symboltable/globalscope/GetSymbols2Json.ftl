<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("s2jAttr", "s2jClass")}
  if(null == ${s2jAttr}) {
    //initialize on demand to avoid cyclic constructor calls
    ${s2jAttr} = new ${s2jClass}();
  }
 return ${s2jAttr};