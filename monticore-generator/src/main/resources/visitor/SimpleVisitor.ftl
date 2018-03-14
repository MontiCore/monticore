<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astType", "astPackage", "cd")}
<#assign genHelper = glex.getGlobalVar("visitorHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getVisitorPackage()};

import java.util.Iterator;
import ${astPackage}.${genHelper.getASTNodeBaseType()};
import de.monticore.ast.ASTNode;

/**
 * Default AST-visitor for the {@code ${genHelper.getCdName()}} language.<br/>
 * <br/>
 * <b>Running a visitor</b>: Starting a traversal of an AST with root {@code astNode} is as simple as calling {@code handle(astNode)}. Note that the visitor only handles nodes of language {@code ${genHelper.getCdName()}}.<br/>
 * <br/>
 * <b>Implementing a visitor:</b><br/>
 * You should never use {@code this}, but always make use of {@link #getRealThis()}. This ensures that the visitor can be reused by composition.<br/>
 * <br/>
 * <ul>
 *   <li><b>Visiting nodes</b>: You may override the {@code visit(node)} and {@code endVisit(node)} methods to do something at specific AST-nodes.<br/><br/></li>
 *   <li><b>Traversal</b>: You may override the {@code traverse(node)} methods, if you want to change the climb down strategy for traversing children (e.g. ordering the children). Be aware of the underlying double-dispatch mechanism: probably you want to call {@code childNode.accept(getRealThis())} and <b>not</b> {@code handle(childNode)}<br/><br/></li>
 *   <li><b>Handling of nodes</b>: You may override the {@code handle(node)} methods, if you want to change its default implementation (depth-first iteration): {@code visit(node); traverse(node); endVisit(node);}<br/><br/></li>
 * </ul>
 * <b>Special node type {@code ASTNode}:</b><br/>
 * Visitors do not provide handle or traverse methods for {@code ASTNode},
 * because handling and traversal are defined in the language depending node
 * types. However, an {@link ${genHelper.getInheritanceVisitorType()}} visits and
 * endVisits each node as {@code ASTNode}. Due to composition of all kinds of
 * visitors we must define the methods here in the main visitor interface.
 *
 * @see ${genHelper.getASTNodeBaseType()}#accept(${genHelper.getVisitorType()} visitor)
 */
public interface ${genHelper.getVisitorType()} ${genHelper.getVisitorSuperInterfaces()} {

  /**
   * Sets the visitor to use for handling and traversing nodes.
   * This method is not implemented by default and visitors intended for reusage
   * in other languages should override this method together with
   * {@link #getRealThis()} to make a visitor composable.
   * RealThis is used to allow visitor composition, where a delegating visitor
   * utilizes this setter to set another visitor as the handle/traversal
   * controller. If this method is not overridden by the language developer,
   * the visitor still can be reused, by implementing this method in a
   * decorator.
   * @param realThis the real instance to use for handling and traversing nodes.
   * @see ${genHelper.getCdName()}DelegatorVisitor 
   */
  default public void setRealThis(${genHelper.getVisitorType()} realThis) {
    throw new UnsupportedOperationException("0xA7011${genHelper.getGeneratedErrorCode(ast)} The setter for realThis is not implemented. You might want to implement a wrapper class to allow setting/getting realThis.");
  }

  /**
   * By default this method returns {@code this}. Visitors intended for reusage
   * in other languages should override this method together with
   * {@link #setRealThis(${genHelper.getVisitorType()})} to make a visitor
   * composable.
   * See {@link #setRealThis(${genHelper.getVisitorType()})} for more information.
   * @see #setRealThis(${genHelper.getVisitorType()})
   * @see ${genHelper.getCdName()}DelegatorVisitor
   */
  default public ${genHelper.getVisitorType()} getRealThis() {
    return this;
  }
  
  /* ------------------------------------------------------------------------*/
  
  /**
   * By default this method is not called, because the default visitor only
   * visits a node in its dynamic runtime type. Use an InheritanceVisitor
   * if you want to visit a node in its super types as well.
   *
   * @param node the node that is entered 
   */
  default public void visit(ASTNode node) {
  }

  /**
   * By default this method is not called, because the default visitor only
   * visits a node in its dynamic runtime type. Use an InheritanceVisitor
   * if you want to visit a node in its super types as well.
   *
   * @param node the node that is left 
   */
  default public void endVisit(ASTNode node) {
  }
  
  /* ------------------------------------------------------------------------*/
  
  
  <#list cd.getTypes() as type>
    <#if type.isClass() || type.isInterface() >
      <#assign astName = genHelper.getJavaASTName(type)>
      
      default public void visit(${astName} node) {}
      
      default public void endVisit(${astName} node) {}
      
      default public void handle(${astName} node) {
        getRealThis().visit(node);
        <#if type.isInterface() || type.isEnum()>
        // no traverse() for interfaces and enums, only concrete classes are traversed
        <#elseif type.isAbstract() >
          // no traverse() for abstract classes, only concrete subtypes are traversed
        <#else>
          getRealThis().traverse(node);
        </#if>
        getRealThis().endVisit(node);
      }
    </#if>
    
    <#if type.isClass() && !type.isAbstract()>
      default public void traverse(${astName} node) {
        // One might think that we could call traverse(subelement) immediately,
        // but this is not true for interface-types where we do not know the
        // concrete type of the element.
        // Instead we double-dispatch the call, to call the correctly typed
        // traverse(...) method with the elements concrete type.

          <#list type.getAllVisibleFields() as field>
            <#if genHelper.isAstNode(field) || genHelper.isOptionalAstNode(field) >
              <#assign attrGetter = genHelper.getPlainGetter(field)>
              <#if genHelper.isOptional(field)>
                if (node.${attrGetter}().isPresent()) {
                  node.${attrGetter}().get().accept(getRealThis());
                }
              <#else>
                if (null != node.${attrGetter}()) {          
                  node.${attrGetter}().accept(getRealThis());
                }
              </#if>
            <#elseif genHelper.isListAstNode(field)>
              <#assign attrGetter = genHelper.getPlainGetter(field)>
              <#assign astChildTypeName = genHelper.getAstClassNameForASTLists(field)>
              {
                Iterator<${astChildTypeName}> iter_${field.getName()} = node.${attrGetter}().iterator();
                while (iter_${field.getName()}.hasNext()) {
                  iter_${field.getName()}.next().accept(getRealThis());
                }
              }
            </#if>
          </#list>
      }
    </#if>
  </#list>
}
