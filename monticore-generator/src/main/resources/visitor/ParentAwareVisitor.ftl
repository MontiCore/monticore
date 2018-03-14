<#-- (c) https://github.com/MontiCore/monticore -->
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
