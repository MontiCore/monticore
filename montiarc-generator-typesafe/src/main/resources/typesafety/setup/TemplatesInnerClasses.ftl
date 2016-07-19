${tc.params("java.util.List<File> nodes", "int depthIndex","String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

<#list nodes as node>

  <#if node.isDirectory()>
  public  ${helper.printPackageClassWithDepthIndex(node.getPath(), modelPath, depthIndex)?cap_first}  ${node.getName()} = new ${helper.printPackageClassWithDepthIndex(node.getPath(), modelPath, depthIndex)?cap_first}();
  
  public  ${helper.printPackageClassWithDepthIndex(node.getPath(), modelPath, depthIndex)?cap_first} get${node.getName()?cap_first}(){
    return ${node.getName()};
  }
  
  public class ${helper.printPackageClassWithDepthIndex(node.getPath(), modelPath, depthIndex)?cap_first} {
    ${tc.includeArgs("typesafety.setup.TemplatesInnerClasses", [helper.walkTree(node), depthIndex+1, modelPath, helper])}
  }
  <#else>
  
    <#if helper.isTemplateName(node.getPath())>
      public  ${glex.getGlobalValue("TemplateClassPackage")}.${helper.printFQNTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")} ${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")} = ${glex.getGlobalValue("TemplateClassPackage")}.${helper.printFQNTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")}.get${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")}();
    
    
    public ${glex.getGlobalValue("TemplateClassPackage")}.${helper.printFQNTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")} get${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")}() {
      return ${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")};
    }
    </#if> 
  </#if>

</#list>
