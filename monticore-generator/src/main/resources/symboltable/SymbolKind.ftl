<#-- (c) https://github.com/MontiCore/monticore -->
${signature("ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#if ruleSymbol.getSymbolDefinitionKind().isPresent()>
  <#assign ruleName = ruleSymbol.getSymbolDefinitionKind().get()>
<#else>
  <#assign ruleName = ruleSymbol.getName()>
</#if>
<#t>
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}
<#t>
<#-- set package -->
package ${genHelper.getTargetPackage()};

import de.monticore.symboltable.SymbolKind;

/**
 * @deprecated SymbolKinds will be removed soon
 */
public class ${ruleName}Kind implements SymbolKind {

  private static final String NAME = "${genHelper.getTargetPackage()}.${ruleName}Kind";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName());
  }

}
