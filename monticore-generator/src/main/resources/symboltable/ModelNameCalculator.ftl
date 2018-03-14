<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "ruleNames")}

<#assign genHelper = glex.getGlobalVar("stHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;

public class ${className} extends de.monticore.CommonModelNameCalculator {

  @Override
  public Set<String> calculateModelNames(final String name, final SymbolKind kind) {
    final Set<String> calculatedModelNames = new LinkedHashSet<>();

  <#list ruleNames as ruleName>
      if (${ruleName?cap_first}Symbol.KIND.isKindOf(kind)) {
        calculatedModelNames.addAll(calculateModelNamesFor${ruleName?cap_first}(name));
      }
  </#list>

    return calculatedModelNames;
  }

  <#list ruleNames as ruleName>
  protected Set<String> calculateModelNamesFor${ruleName?cap_first}(String name) {
    final Set<String> modelNames = new LinkedHashSet<>();
    modelNames.add(name);
    return modelNames;
  }
  </#list>


}
