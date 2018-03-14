<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign ruleName = ruleSymbol.getName()?cap_first>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;

public class ${className} extends de.monticore.symboltable.CommonSymbol {

  ${includeArgs("symboltable.symbols.KindConstantDeclaration", ruleName)}

  public ${className}(String name) {
    super(name, KIND);
  }

  ${includeArgs("symboltable.symbols.GetAstNodeMethod", ruleName)}

  ${includeArgs("symboltable.SymbolBuilder", className)}

}
