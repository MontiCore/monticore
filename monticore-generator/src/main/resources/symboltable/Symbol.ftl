<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "prodSymbol", "ruleSymbol", "imports")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>
<#assign ruleName = prodSymbol.getName()>
<#assign superClass = " extends de.monticore.symboltable.CommonSymbol">
<#assign superInterfaces = "implements ICommon" + genHelper.getGrammarSymbol().getName() + "Symbol">
<#if ruleSymbol.isPresent()>
  <#if !ruleSymbol.get().isEmptySuperInterfaces()>
    <#assign superInterfaces = ", " + stHelper.printGenericTypes(ruleSymbol.get().getSuperInterfaceList())>
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
  
  <#assign langVisitorType = names.getQualifiedName(genHelper.getVisitorPackage(), genHelper.getGrammarSymbol().getName() + "SymbolVisitor")>
   public void accept(${langVisitorType} visitor) {
  <#if genHelper.isSupertypeOfHWType(className, "")>
  <#assign plainName = className?remove_ending("TOP")>
    if (this instanceof ${plainName}) {
      visitor.handle((${plainName}) this);
    } else {
      throw new UnsupportedOperationException("0xA7010{genHelper.getGeneratedErrorCode(ast)} Only handwritten class ${plainName} is supported for the visitor");
    }
  <#else>
    visitor.handle(this);
  </#if>
  }
  
  <#if ruleSymbol.isPresent()>
  ${includeArgs("symboltable.symbols.SymbolRule", ruleSymbol.get())}
  </#if>

}
