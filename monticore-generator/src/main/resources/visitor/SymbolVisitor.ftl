<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("astType", "symbolTablePackage", "cd", "symbols")}
<#assign genHelper = glex.getGlobalVar("visitorHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getVisitorPackage()};

<#if symbols?has_content>
import ${symbolTablePackage}.*;
</#if>
import de.monticore.symboltable.Symbol;

/**
 * Default symbol-visitor for the {@code ${genHelper.getCdName()}} language.<br/>
 * <br/>
 * <b>Running a visitor</b>: Starting a traversal of a symbol is as simple as calling {@code handle(symbol)}. Note that the visitor only handles nodes of language {@code ${genHelper.getCdName()}}.<br/>
 * <br/>
 * <b>Implementing a visitor:</b><br/>
 * You should never use {@code this}, but always make use of {@link #getRealThis()}. This ensures that the visitor can be reused by composition.<br/>
 * <br/>
 * <ul>
 *   <li><b>Visiting nodes</b>: You may override the {@code visit(node)} and {@code endVisit(node)} methods to do something at specific symbol nodes.<br/><br/></li>
 *   <li><b>Traversal</b>: You may override the {@code traverse(node)} methods, if you want to change the climb down strategy for traversing children (e.g. ordering the children). Be aware of the underlying double-dispatch mechanism: probably you want to call {@code childNode.accept(getRealThis())} and <b>not</b> {@code handle(childNode)}<br/><br/></li>
 *   <li><b>Handling of nodes</b>: You may override the {@code handle(node)} methods, if you want to change its default implementation (depth-first iteration): {@code visit(node); traverse(node); endVisit(node);}<br/><br/></li>
 * </ul>
 */
public interface ${genHelper.getSymbolVisitorType()} { 

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
  default public void setRealThis(${genHelper.getSymbolVisitorType()} realThis) {
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
  default public ${genHelper.getSymbolVisitorType()} getRealThis() {
    return this;
  }
  
  /* ------------------------------------------------------------------------*/
  
  /**
   * By default this method is not called, because the default visitor only
   * visits a symbol in its dynamic runtime type. Use an InheritanceVisitor
   * if you want to visit a node in its super types as well.
   *
   * @param symbol the symbol that is entered 
   */
  default public void visit(Symbol symbol) {
  }

  /**
   * By default this method is not called, because the default visitor only
   * visits a symbol in its dynamic runtime type. Use an InheritanceVisitor
   * if you want to visit a node in its super types as well.
   *
   * @param symbol the symbol that is left 
   */
  default public void endVisit(Symbol symbol) {
  }
  
  /* ------------------------------------------------------------------------*/
  
  
  <#list symbols as symbol>
    <#assign symbolType = symbol.getName() + "Symbol">
      
    default public void visit(${symbolType} symbol) {}
      
      default public void endVisit(${symbolType} symbol) {}
      
      default public void handle(${symbolType} symbol) {
        getRealThis().visit(symbol);
        getRealThis().traverse(symbol);
        getRealThis().endVisit(symbol);
      }
    
      default public void traverse(${symbolType} symbol) {}
  </#list>
  
}
