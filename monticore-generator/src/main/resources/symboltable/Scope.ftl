<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className","scopeRule", "symbolNames")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
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
  // all resolve Methods for ${symbol}
  public Optional<${symbolNames[symbol]}> resolve${symbol}(String name) {
    return resolve(name, ${symbolNames[symbol]}.KIND);
  }

  public Optional<${symbolNames[symbol]}> resolve${symbol}(String name, AccessModifier modifier) {
    return resolve(name, ${symbolNames[symbol]}.KIND, modifier);
  }

  public Optional<${symbolNames[symbol]}> resolve${symbol}(String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolve(name, ${symbolNames[symbol]}.KIND, modifier, predicate);
  }

  public Optional<${symbolNames[symbol]}> resolve${symbol}(ResolvingInfo resolvingInfo, String name, AccessModifier modifier) {
    return resolve(resolvingInfo, name, ${symbolNames[symbol]}.KIND, modifier);
  }

  // all resolveDown Methods for ${symbol}
  public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name) {
    return resolveDown(name, ${symbolNames[symbol]}.KIND);
  }

  public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name, AccessModifier modifier) {
  return resolveDown(name, ${symbolNames[symbol]}.KIND, modifier);
  }

  public Optional<${symbolNames[symbol]}> resolve${symbol}Down(String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolveDown(name, ${symbolNames[symbol]}.KIND, modifier, predicate);
  }

  // all resolveDownMany Methods for ${symbol}
  public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(String name) {
    return resolveDownMany(name, ${symbolNames[symbol]}.KIND);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(String name, AccessModifier modifier) {
    return resolveDownMany(name, ${symbolNames[symbol]}.KIND, modifier);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolveDownMany(name, ${symbolNames[symbol]}.KIND, modifier, predicate);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}DownMany(ResolvingInfo resolvingInfo, String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolveDownMany(resolvingInfo, name, ${symbolNames[symbol]}.KIND, modifier, predicate);
  }

  // all resolveLocally Methods for ${symbol}
  public Optional<${symbolNames[symbol]}> resolve${symbol}Locally(String name) {
    return resolveLocally(name, ${symbolNames[symbol]}.KIND);
  }

  // all resolveImported Methods for ${symbol}
  public Optional<${symbolNames[symbol]}> resolve${symbol}Imported(String name, AccessModifier modifier) {
    return resolveImported(name, ${symbolNames[symbol]}.KIND, modifier);
  }

  // all resolveMany Methods for ${symbol}
  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name) {
    return resolveMany(name, ${symbolNames[symbol]}.KIND);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name, AccessModifier modifier) {
    return resolveMany(name, ${symbolNames[symbol]}.KIND, modifier);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolveMany(name, ${symbolNames[symbol]}.KIND, modifier, predicate);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(String name, Predicate<Symbol> predicate) {
    return resolveMany(name, ${symbolNames[symbol]}.KIND, predicate);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(ResolvingInfo resolvingInfo, String name, AccessModifier modifier) {
    return resolveMany(resolvingInfo, name, ${symbolNames[symbol]}.KIND, modifier);
  }

  public Collection<${symbolNames[symbol]}> resolve${symbol}Many(ResolvingInfo resolvingInfo, String name, AccessModifier modifier, Predicate<Symbol> predicate) {
    return resolveMany(resolvingInfo, name, ${symbolNames[symbol]}.KIND, modifier, predicate);
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
}
