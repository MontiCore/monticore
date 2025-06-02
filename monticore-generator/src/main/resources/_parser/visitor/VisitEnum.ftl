<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="enumconsts" type="java.util.List<String>" -->
${tc.signature("enumconsts")}

<#list enumconsts as c>
  if (ctx.e_${c?index} != null)
    return ${c};
    <#sep>else
</#list>
throw new IllegalStateException("Unhandled enum constant during AST construction. Please report this error");
