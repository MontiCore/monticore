<#-- (c) https://github.com/MontiCore/monticore -->
${signature("interfaceName", "symbolNames", "superScopes")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;
import java.util.function.Predicate;
import java.util.Collection;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.monticore.symboltable.MutableScope;

public interface ${interfaceName} <#if superScopes?size != 0>extends ${superScopes?join(", ")} </#if>{

<#list symbolNames?keys as symbol>
  // all resolve Methods for ${symbol}Symbol
  public Optional<${symbolNames[symbol]}> resolve${symbol}(String name);

  public Optional<${symbolNames[symbol]}> resolve${symbol}(String name, AccessModifier modifier);

  public Optional<${symbolNames[symbol]}> resolve${symbol}(String name, AccessModifier modifier, Predicate<Symbol> predicate);

  public Optional<${symbolNames[symbol]}> resolve${symbol}(ResolvingInfo resolvingInfo, String name, AccessModifier modifier);

  // all resolveDown Methods for ${symbol}Symbol
  public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name);

  public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name, AccessModifier modifier);

  public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name, AccessModifier modifier, Predicate<Symbol> predicate);

  // all resolveDownMany Methods for ${symbol}Symbol
  public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(String name);

  public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(String name, AccessModifier modifier);

  public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(String name, AccessModifier modifier, Predicate<Symbol> predicate);

  public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(ResolvingInfo resolvingInfo, String name, AccessModifier modifier, Predicate<Symbol> predicate);

  // all resolveLocally Methods for ${symbol}Symbol
  public Optional<${symbolNames[symbol]}> resolve${symbol}Locally(String name);

  // all resolveImported Methods for ${symbol}Symbol
  public Optional<${symbolNames[symbol]}> resolve${symbol}Imported(String name, AccessModifier modifier);

  // all resolveMany Methods for ${symbol}Symbol
  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name);

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name, AccessModifier modifier);

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name, AccessModifier modifier, Predicate<Symbol> predicate);

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name, Predicate<Symbol> predicate);

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(ResolvingInfo resolvingInfo, String name, AccessModifier modifier);

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(ResolvingInfo resolvingInfo, String name, AccessModifier modifier, Predicate<Symbol> predicate);
</#list>

  <#assign langVisitorType = names.getQualifiedName(genHelper.getVisitorPackage(), genHelper.getGrammarSymbol().getName() + "ScopeVisitor")>
  public void accept(${langVisitorType} visitor);
}

