<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign ruleName = ruleSymbol.getName()?cap_first>
<#t>
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}
<#t>
<#-- set package -->
package ${genHelper.getTargetPackage()};

import de.monticore.symboltable.SymbolKind;

public class ${ruleName}Kind implements SymbolKind {

  private static final String NAME = "${genHelper.getTargetPackage()}.${ruleName}Kind";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
  }

}
