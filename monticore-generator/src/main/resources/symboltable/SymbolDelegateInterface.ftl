<#-- (c) https://github.com/MontiCore/monticore -->
${signature("interfaceName", "symbolName")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.function.Predicate;
import java.util.Collection;
import de.monticore.symboltable.modifiers.AccessModifier;

public interface ${interfaceName} {

  public Collection<${symbolName}Symbol> resolveAdapted${symbolName}Symbol (boolean foundSymbols,
      String symbolName, AccessModifier modifier, Predicate<${symbolName}Symbol> predicate);
}

