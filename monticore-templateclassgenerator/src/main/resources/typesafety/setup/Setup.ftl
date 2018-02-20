<#-- (c) https://github.com/MontiCore/monticore -->
${tc.params("java.util.List<File> nodes", "String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper", "java.util.List<File> visitedNodes")}

<#list nodes as node>
  <#assign visitedNodes = visitedNodes + [node]>
  ${r"<#assign"} ${node.getName()} =  ${tc.includeArgs("typesafety.setup.SetupAssignments", [helper.walkTree(node),0, modelPath, helper, visitedNodes])} ${r">"}
</#list>


