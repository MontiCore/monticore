${tc.params("java.util.List<File> nodes", "int depthIndex","String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

<#list nodes as node>

  <#if node.isDirectory()>
  public  ${helper.printPackageClassWithDepthIndex(node.getPath(), modelPath, depthIndex, nodes)?cap_first}  ${node.getName()} = new ${helper.printPackageClassWithDepthIndex(node.getPath(), modelPath, depthIndex, nodes)?cap_first}();
  
  public  ${helper.printPackageClassWithDepthIndex(node.getPath(), modelPath, depthIndex, nodes)?cap_first} get${helper.printPackageClassWithDepthIndex(node.getPath(), modelPath, depthIndex, nodes)?cap_first}(){
    return ${node.getName()};
  }
  
  public class ${helper.printPackageClassWithDepthIndex(node.getPath(), modelPath, depthIndex, nodes)?cap_first} {
    ${tc.includeArgs("typesafety.setup.TemplatesInnerClasses", [helper.walkTree(node), depthIndex+1, modelPath, helper])}
  }
  <#else>
  
    <#if helper.isTemplateName(node.getPath())>
      public  ${glex.getGlobalValue("TemplateClassPackage")}.${helper.printFQNTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")} ${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")} = new ${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}Extended();
    
    public class ${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}Extended extends ${glex.getGlobalValue("TemplateClassPackage")}.${helper.printFQNTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")} {
      
      protected ${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}Extended () {}
      
    }
    
    public ${glex.getGlobalValue("TemplateClassPackage")}.${helper.printFQNTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")} get${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")}() {
      return ${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalValue("TemplatePostfix")};
    }
    </#if> 
  </#if>

</#list>
