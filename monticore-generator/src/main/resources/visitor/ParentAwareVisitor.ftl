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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import ${astPackage}.${genHelper.getASTNodeBaseType()};
import de.monticore.ast.ASTNode;


/**
* The ParentAwareVisitor of the language keeps track of the parent nodes during a traversal.<br/>
* <br/>
* <b>Access current parent</b>: use {@code getParent()} to access the current parent.<br/>
* <br/>
* <b>Access the complete parents list:</b> use {@code getParents()} to access a list of all parents.<br/>
*/
public abstract class ${genHelper.getParentAwareVisitorType()} ${genHelper.getParentAwareVisitorSuperInterfaces()} {

  <#assign astRootNode = genHelper.getASTNodeBaseType()>
  final Stack<${astRootNode}> parents = new Stack<>();

  public Optional<${astRootNode}> getParent() {
    if (!parents.isEmpty()) {
      ${astRootNode} topElement = (${astRootNode}) parents.peek();
      return Optional.of(topElement);
    }
    // no parent, return an absent value
    return Optional.empty();
  }

  public List<${astRootNode}> getParents() {
    return new ArrayList<>(parents);
  }

  <#list cd.getTypes() as type>
    <#if type.isClass() && !type.isAbstract()>
      <#assign astName = genHelper.getJavaASTName(type)>
      @Override
      public void traverse(${astName} node) {
        parents.push(node);
        ${genHelper.getVisitorType()}.super.traverse(node);
        parents.pop();
      }
    </#if>
  </#list>
}
