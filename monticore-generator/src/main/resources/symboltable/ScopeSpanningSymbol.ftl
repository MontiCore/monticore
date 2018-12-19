<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "scopeClassName", "prodSymbol", "ruleSymbol", "imports")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#if prodSymbol.getSymbolDefinitionKind().isPresent()>
  <#assign ruleName = prodSymbol.getSymbolDefinitionKind().get()>
<#else>
  <#assign ruleName = prodSymbol.getName()>
</#if>
<#assign astName = prodSymbol.getName()?cap_first>
<#assign superClass = " extends de.monticore.symboltable.CommonScopeSpanningSymbol">
<#assign superInterfaces = "">
<#if ruleSymbol.isPresent()>
  <#if !ruleSymbol.get().isEmptySuperInterfaces()>
    <#assign superInterfaces = "implements " + stHelper.printGenericTypes(ruleSymbol.get().getSuperInterfaceList())>
  </#if>
  <#if !ruleSymbol.get().isEmptySuperClasss()>
    <#assign superClass = " extends " + stHelper.printGenericTypes(ruleSymbol.get().getSuperClassList())>
  </#if>
</#if>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;

import java.util.Collection;
import java.util.Optional;

<#list imports as imp>
import ${imp}._ast.*;
</#list>

public class ${className} ${superClass} ${superInterfaces} {

  ${includeArgs("symboltable.symbols.KindConstantDeclaration", ruleName)}

  public ${className}(String name) {
    super(name, KIND);
  }


  @Override
  protected ${scopeClassName} createSpannedScope() {
    return new ${scopeClassName}();
  }

  <#-- Get methods for  containing symbols -->
  <#assign fields = genHelper.symbolRuleComponents2JavaFields(prodSymbol)>
  /* Possible methods for containinig symbols
  <#list fields?keys as fname>
    <#assign type = fields[fname]>

  public Collection<${type}> get${fname?cap_first}() {
    return sortSymbolsByPosition(getSpannedScope().resolveLocally(${type}.KIND));
  }
  </#list>
  */

  ${includeArgs("symboltable.symbols.GetAstNodeMethod", astName)}
  
  <#if ruleSymbol.isPresent()>
    ${includeArgs("symboltable.symbols.SymbolRule", ruleSymbol.get())}
  </#if>

}
