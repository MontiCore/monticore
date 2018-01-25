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
${tc.signature("astType", "astPackage", "cd")}
<#assign genHelper = glex.getGlobalVar("visitorHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getVisitorPackage()};

import ${astPackage}.${genHelper.getASTNodeBaseType()};

import de.monticore.ast.ASTNode;

/**
 * Visitor for the {@code ${genHelper.getCdName()}} language that visits super
 * types of the AST nodes as well.
 *
 * @see ${genHelper.getVisitorType()}
 * @see ${genHelper.getASTNodeBaseType()}#accept(${genHelper.getVisitorType()} visitor)
 */
 public interface ${genHelper.getInheritanceVisitorType()} extends ${genHelper.getVisitorType()} ${genHelper.getSuperInheritanceVisitorTypes()} {

  <#list cd.getTypes() as type>

    <#if type.isClass() || type.isInterface() >
      <#assign astName = genHelper.getJavaASTName(type)>
      default public void handle(${astName} node) {
        getRealThis().visit((ASTNode) node);
        getRealThis().visit((AST${genHelper.getCdName()}Node) node);
        <#list genHelper.getSuperTypes(type)?reverse as superType>
        getRealThis().visit((${genHelper.getJavaASTName(superType)}) node);
        </#list>
        ${genHelper.getVisitorType()}.super.handle(node);
        <#list genHelper.getSuperTypes(type) as superType>
        getRealThis().endVisit((${genHelper.getJavaASTName(superType)}) node);
        </#list>
        getRealThis().endVisit((AST${genHelper.getCdName()}Node) node);
        getRealThis().endVisit((ASTNode) node);
      }
      </#if>
  </#list>
}
