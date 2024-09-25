<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("enumconsts")}

<#list enumconsts as c>
  if (ctx.e_${c?index} != null)
    return ${c};
    <#sep>else
</#list>
throw new IllegalStateException("Unhandled enum constant during AST construction. Please report this error");
