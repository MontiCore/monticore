<#-- (c) https://github.com/MontiCore/monticore -->
${signature("interfaceName", "ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign ruleName = ruleSymbol.getName()?cap_first>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

public interface ${interfaceName} extends de.monticore.symboltable.Symbol {

  ${includeArgs("symboltable.symbols.KindConstantDeclaration", ruleName)}
  
}
