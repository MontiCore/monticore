${tc.params("java.util.List<File> nodes", "int depthIndex","String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper", "String templatesCryptedName")}

{
<#list nodes as node>

  <#if node.isDirectory()> 
    "${node.getName()}":
    ${tc.includeArgs("typesafety.setup.GlobalVariableDefinition", [helper.walkTree(node), depthIndex+1, modelPath, helper, templatesCryptedName])}
  <#else>
    "${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}Template":
    ${templatesCryptedName}.${helper.printGettersForTemplate(node.getPath(), modelPath)}
  </#if>
<#if node?has_next>
,
</#if>
</#list>
}
