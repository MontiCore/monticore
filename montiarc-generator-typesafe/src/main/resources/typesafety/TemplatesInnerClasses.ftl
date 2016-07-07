${tc.params("java.util.List<File> nodes", "int depthIndex","String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

<#list nodes as node>

  <#if node.isDirectory()>
  public static final ${helper.printPackageClassWithDepthIndex(node.getPath(), modelPath, depthIndex)?cap_first}  ${node.getName()} = new ${helper.printPackageClassWithDepthIndex(node.getPath(), modelPath, depthIndex)?cap_first}();
  
  public static class ${helper.printPackageClassWithDepthIndex(node.getPath(), modelPath, depthIndex)?cap_first} {
    ${tc.includeArgs("typesafety.TemplatesInnerClasses", [helper.walkTree(node), depthIndex+1, modelPath, helper])}
  }
  <#else>
  
    <#if helper.isTemplateName(node.getPath())>
      public static final templates.${helper.printFQNTemplateNameFromPath(node.getPath(), modelPath)}Template ${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}Template = new templates.${helper.printFQNTemplateNameFromPath(node.getPath(), modelPath)}Template();
    </#if>
  </#if>

</#list>
