<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "prodSymbol", "ruleSymbol", "imports")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign ruleName = prodSymbol.getName()>
<#assign superClass = " extends de.monticore.symboltable.CommonSymbol">
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

import java.util.Optional;
<#list imports as imp>
import ${imp}._ast.*;
</#list>

public class ${className} ${superClass} ${superInterfaces} {

  ${includeArgs("symboltable.symbols.KindConstantDeclaration", ruleName)}

  public ${className}(String name) {
    super(name, KIND);
  }

  ${includeArgs("symboltable.symbols.GetAstNodeMethod", ruleName)}
  
  <#if ruleSymbol.isPresent()>
  ${includeArgs("symboltable.symbols.SymbolRule", ruleSymbol.get())}
  </#if>

}
