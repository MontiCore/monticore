${tc.params("java.util.List<File> nodes", "String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper","String templatesCryptedName")}

<#list nodes as node>
  ${r"<#assign"} ${node.getName()} =  ${tc.includeArgs("typesafety.setup.GlobalVariableDefinition", [helper.walkTree(node),0, modelPath, helper, templatesCryptedName])} ${r">"}
</#list>


