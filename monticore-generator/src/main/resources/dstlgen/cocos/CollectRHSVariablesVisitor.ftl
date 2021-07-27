<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "package")}
package ${package}.${grammarNameLower}tr._cocos;

import ${package}.${grammarNameLower}tr._visitor.*;
import ${package}.${grammarNameLower}tr._ast.*;
import ${package}.${grammarNameLower}tr.${ast.getName()}TRMill;
import de.monticore.tf.tfcommons._ast.ASTTfIdentifier;
import java.util.Set;
import com.google.common.collect.Sets;
import de.monticore.tf.grammartransformation.CollectCoCoInformationState;
import de.monticore.tf.tfcommons._ast.ASTITFPart;

public class ${className} implements ${ast.getName()}TRVisitor2, ${ast.getName()}TRHandler  {

  private CollectCoCoInformationState state;

  public ${className}(CollectCoCoInformationState state) {
    this.state = state;
  }

  public ${className}() {
    this(new CollectCoCoInformationState());
  }


private ${ast.getName()}TRTraverser traverser;
@Override public ${ast.getName()}TRTraverser getTraverser() {
return this.traverser;
}
@Override public void setTraverser(${ast.getName()}TRTraverser t) {
this.traverser = t;
}

  public Set<String> getRHSOnlyVars() {
    return state.getRHSOnlyVars();
  }

  public Set<String> getVarsOnLHS() {
    return state.getVarsOnLHS();
  }

  public Set<String> getVarsOnRHS() {
    return state.getVarsOnRHS();
  }

/*  @Override
  public void traverse(AST${ast.getName()}TFRule node) {
    if (null != node.getTFRule()) {
      node.getTFRule().accept(getTraverser());
      for (ASTITFPart part : node.getTFRule().getITFPartList()) {
        part.accept(getTraverser());
      }
    }
  }
*/
  <#list productions as prod>
  @Override
  public void visit(AST${prod.getName()}_Pat node) {
    <#if grammarInfo.getStringAttrs(prod.getName())?has_content >
      ${tc.include(collect_rhs_vars, grammarInfo.getStringAttrs(prod.getName()))}
    </#if>
    <#if grammarInfo.getStringListAttrs(prod.getName())?has_content >
       ${tc.include(collect_rhs_listvars, grammarInfo.getStringListAttrs(prod.getName()))}
    </#if>
  }

  </#list>

  <#list productions as prod>
  @Override
  public void traverse(AST${prod.getName()}_Rep node) {
<#--  <#if grammarInfo.getStringAttrs(prod.getName())?has_content >-->
    boolean oldPos = state.getRHS();
    state.setRHS(false);
    if (node.isPresentLhs()) {
      node.getLhs().accept(getTraverser());
    }
    state.setRHS(true);
    if (node.isPresentRhs()) {
      node.getRhs().accept(getTraverser());
    }
    state.setRHS(oldPos);
<#--  </#if>-->
<#--  <#if grammarInfo.getStringListAttrs(prod.getName())?has_content >-->
<#--    boolean oldPos = state.getRHS();-->
<#--    state.setRHS(false);-->
<#--    if (node.isPresentLhs()) {-->
<#--    node.getLhs().accept(getTraverser());-->
<#--    }-->
<#--    state.setRHS(true);-->
<#--    if (node.isPresentRhs()) {-->
<#--    node.getRhs().accept(getTraverser());-->
<#--    }-->
<#--    state.setRHS(oldPos);-->
<#--  </#if>-->
  }

  </#list>
}
