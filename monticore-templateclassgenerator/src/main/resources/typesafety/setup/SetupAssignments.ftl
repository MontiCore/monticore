<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
-->
${tc.params("java.util.List<File> nodes", "int depthIndex","String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper", "java.util.List<File> visitedNodes")}

{
<#list nodes as node>

  <#if node.isDirectory()> 
    "${node.getName()}":
    <#assign visitedNodes = visitedNodes + [node]>
    ${tc.includeArgs("typesafety.setup.SetupAssignments", [helper.walkTree(node), depthIndex+1, modelPath, helper, visitedNodes])}
    <#if node?has_next>
    ,
    </#if>
  <#else>
    <#if helper.isTemplateName(node.getPath())>
      "${helper.printSimpleTemplateNameFromPath(node.getPath(), modelPath)}${glex.getGlobalVar("TemplatePostfix")}":
      ${glex.getGlobalVar("TemplatesAlias")}.${helper.printGettersForTemplate(node.getPath(), modelPath)}
      <#if node?has_next>
      ,
      </#if>
    </#if>
  </#if>

</#list>
}
