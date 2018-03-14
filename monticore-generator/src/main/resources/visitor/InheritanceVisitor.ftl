<#-- (c) https://github.com/MontiCore/monticore -->
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
