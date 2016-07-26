${tc.params("java.util.List<File> nodes", "int depthIndex","String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper", "java.util.List<File> visitedNodes")}

{
<#list nodes as node>

  <#if node.isDirectory()> 
    "${node.getName()}":
    <#assign visitedNodes = visitedNodes + [node]>
    ${tc.includeArgs("typesafety.setup.GlobalVariableDefinition", [helper.walkTree(node), depthIndex+1, modelPath, helper, visitedNodes])}
    <#if node?has_next>
    ,
    </#if>
  <#else>
    <#if helper.isTemplateName(node.getPath())>
      "${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")}":
      ${glex.getGlobalValue("TemplatesAlias")}.${helper.printGettersForTemplate(node.getPath(), modelPath)}
      <#if node?has_next>
      ,
      </#if>
    </#if>
  </#if>

</#list>
}
