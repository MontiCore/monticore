<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className","scopeRule", "symbolNames", "superScopeVisitors")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>
<#assign superClass = " extends de.monticore.symboltable.CommonScope ">
<#assign superInterfaces = "implements I"+ className>
<#if scopeRule.isPresent()>
  <#if !scopeRule.get().isEmptySuperInterfaces()>
    <#assign superInterfaces = superInterfaces + ", "+ stHelper.printGenericTypes(scopeRule.get().getSuperInterfaceList())>
  </#if>
  <#if !scopeRule.get().isEmptySuperClasss()>
    <#assign superClass = " extends " + stHelper.printGenericTypes(scopeRule.get().getSuperClassList())>
  </#if>
</#if>


<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;
import java.util.function.Predicate;
import java.util.Collection;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvingInfo;
import de.monticore.symboltable.MutableScope;
import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;
import static java.util.Collections.emptySet;

public class ${className} ${superClass} ${superInterfaces} {

  public ${className}() {
    super();
  }

  public ${className}(boolean isShadowingScope) {
    super(isShadowingScope);
  }

  public ${className}(Optional<MutableScope> enclosingScope) {
    super(enclosingScope, true);
  }
  
  <#list symbolNames?keys as symbol>
  // all resolve Methods for symbol ${symbol}
  public Optional<${symbolNames[symbol]}> resolve${symbol}(String name) {
    return getResolvedOrThrowException(resolve${symbol}Many(name));
  }

  public Optional<${symbolNames[symbol]}> resolve${symbol}(String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolve${symbol}Many(name, modifier));
  }

  public Optional<${symbolNames[symbol]}> resolve${symbol}(String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return getResolvedOrThrowException(resolve${symbol}Many(name, modifier, predicate));
  }

  public Optional<${symbolNames[symbol]}> resolve${symbol}(ResolvingInfo resolvingInfo, String name, AccessModifier modifier) {
    return getResolvedOrThrowException(resolve${symbol}Many(resolvingInfo, name, modifier));
  }

  // all resolveDown Methods for symbol ${symbol}
  public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name) {
    return getResolvedOrThrowException(resolve${symbol}DownMany(name));
  }

  public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name, AccessModifier modifier) {
  return getResolvedOrThrowException(resolve${symbol}DownMany(name, modifier));
  }

  public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return getResolvedOrThrowException(resolve${symbol}DownMany(name, modifier, predicate));
  }

  // all resolveDownMany Methods for symbol ${symbol}
  public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(String name) {
    return resolve${symbol}DownMany(new ResolvingInfo(getResolvingFilters()), name, ALL_INCLUSION, x -> true);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(String name, AccessModifier modifier) {
    return resolve${symbol}DownMany(new ResolvingInfo(getResolvingFilters()), name, modifier, x -> true);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolve${symbol}DownMany(new ResolvingInfo(getResolvingFilters()), name, modifier, predicate);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(ResolvingInfo resolvingInfo, String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolveDownMany(resolvingInfo, name, ${symbolNames[symbol]}.KIND, modifier, predicate);
  }

  // all resolveLocally Methods for ${symbol}
  public Optional<${symbolNames[symbol]}> resolve${symbol}Locally(String name) {
    return getResolvedOrThrowException(resolve${symbol}ManyLocally(new ResolvingInfo(getResolvingFilters()), name, ALL_INCLUSION, x -> true));
  }

  public java.util.Set<${symbolNames[symbol]}> resolve${symbol}ManyLocally(ResolvingInfo resolvingInfo, String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolveManyLocally(resolvingInfo, name, ${symbolNames[symbol]}.KIND, modifier, predicate);
  }

  public java.util.List<${symbolNames[symbol]}> resolve${symbol}Locally() {
    return resolveLocally(${symbolNames[symbol]}.KIND);
  }

  // all resolveImported Methods for symbol ${symbol}
  public Optional<${symbolNames[symbol]}> resolve${symbol}Imported(String name, AccessModifier modifier) {
    return resolve${symbol}Locally(name);
  }

  // all resolveMany Methods for symbol ${symbol}
  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name) {
    return resolve${symbol}Many(name, ALL_INCLUSION);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name, AccessModifier modifier) {
    return resolve${symbol}Many(name, modifier, x -> true);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolve${symbol}Many(name, predicate);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name, Predicate<Symbol> predicate) {
    return resolve${symbol}Many(new ResolvingInfo(getResolvingFilters()), name, ALL_INCLUSION, predicate);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(ResolvingInfo resolvingInfo, String name, AccessModifier modifier) {
    return resolve${symbol}Many(resolvingInfo, name, modifier, x -> true);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(ResolvingInfo resolvingInfo, String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolveMany(resolvingInfo, name, ${symbolNames[symbol]}.KIND, modifier, predicate);
  }

  //continue with scope methods for symbol ${symbol}
  public Collection<${symbolNames[symbol]}> continue${symbol}AsSubScope(ResolvingInfo resolvingInfo, String symbolName,  AccessModifier modifier, Predicate<Symbol> predicate) {
    if (checkIfContinue${symbol}AsSubScope(symbolName)) {
      final String remainingSymbolName = getRemainingNameForResolveDown(symbolName);
      return this.resolve${symbol}DownMany(resolvingInfo, remainingSymbolName, modifier, predicate);
    }
    return emptySet();
  }

  protected boolean checkIfContinue${symbol}AsSubScope(String symbolName) {
    return checkIfContinueAsSubScope(symbolName, ${symbolNames[symbol]}.KIND);
  }

  protected Collection<${symbolNames[symbol]}> continue${symbol}WithEnclosingScope(ResolvingInfo resolvingInfo, String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    if (checkIfContinueWithEnclosingScope(resolvingInfo.areSymbolsFound()) && (getEnclosingScope().isPresent())) {
      return getEnclosingScope().get().resolveMany(resolvingInfo, name, ${symbolNames[symbol]}.KIND, modifier, predicate);
    }
    return emptySet();
  }

  </#list>
  
  <#if scopeRule.isPresent()>
    <#list scopeRule.get().getAdditionalAttributeList() as attr>
      <#assign attrName=attr.getName()>
      <#assign attrType=attr.getGenericType().getTypeName()>
      private ${genHelper.getQualifiedASTName(attrType)} ${attrName};
    </#list>

    <#list scopeRule.get().getMethodList() as meth>
      ${genHelper.printMethod(meth)}
    </#list>
  </#if>
  
  <#assign langVisitorType = names.getQualifiedName(genHelper.getVisitorPackage(), genHelper.getGrammarSymbol().getName() + "ScopeVisitor")>
  public void accept(${langVisitorType} visitor) {
  <#if genHelper.isSupertypeOfHWType(className, "")>
  <#assign plainName = className?remove_ending("TOP")>
    if (this instanceof ${plainName}) {
      visitor.handle((${plainName}) this);
    } else {
      throw new UnsupportedOperationException("0xA7010${genHelper.getGeneratedErrorCode(ast)} Only handwritten class ${plainName} is supported for the visitor");
    }
  <#else>
    visitor.handle(this);
  </#if>
  }
  
  <#list superScopeVisitors as superScopeVisitor>
  public void accept(${superScopeVisitor} visitor) {
    if (visitor instanceof ${langVisitorType}) {
      accept((${langVisitorType}) visitor);
    } else {
      throw new UnsupportedOperationException("0xA7010${genHelper.getGeneratedErrorCode(ast)} Scope node type ${className} expected a visitor of type ${langVisitorType}, but got ${superScopeVisitor}.");
    }
  }
  </#list>
}
