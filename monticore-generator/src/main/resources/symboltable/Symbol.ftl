<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "prodSymbol", "ruleSymbol", "imports", "isScopeSpanningSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>
<#assign ruleName = prodSymbol.getName()>
<#assign languageName = genHelper.getGrammarSymbol().getName()>
<#assign superClass = "">

<#if isScopeSpanningSymbol>
<#assign superInterfaces = "implements ICommon" + languageName + "Symbol"+", IScopeSpanningSymbol">
<#else>
<#assign superInterfaces = "implements ICommon" + languageName + "Symbol">
</#if>
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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import de.monticore.symboltable.*;
import de.se_rwth.commons.Names;
import de.monticore.symboltable.modifiers.AccessModifier;
import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;

<#list imports as imp>
import ${imp}._ast.*;
</#list>

public class ${className} ${superClass} ${superInterfaces} {

  protected I${languageName}Scope enclosingScope;

  protected String fullName;

  protected String name;

  protected AST${ruleName} node;

  protected String packageName;

  protected AccessModifier accessModifier = ALL_INCLUSION;

  public ${className}(String name) {
    this.name = name;
  }

<#if isScopeSpanningSymbol>
  ${includeArgs("symboltable.symbols.SpannedScope", languageName)}
</#if>

  ${includeArgs("symboltable.symbols.GetAstNodeMethod", ruleName)}

  <#assign langVisitorType = names.getQualifiedName(genHelper.getVisitorPackage(), languageName + "SymbolVisitor")>
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



  public I${languageName}Scope getEnclosingScope(){
    return this.enclosingScope;
  }

  public void setEnclosingScope(I${languageName}Scope newEnclosingScope){
    this.enclosingScope = newEnclosingScope;
  }

  @Override
  public void setAccessModifier(AccessModifier accessModifier) {
    this.accessModifier = accessModifier;
  }

  @Override
  public AccessModifier getAccessModifier() {
    return this.accessModifier;
  }

  @Override public String getName() {
    return name;
  }

  @Override public String getPackageName() {
    if (packageName == null) {
      packageName = determinePackageName();
    }

    return packageName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getFullName() {
    if (fullName == null) {
      fullName = determineFullName();
    }

    return fullName;
  }

  protected String determinePackageName() {
    Optional<? extends I${languageName}Scope> optCurrentScope = Optional.ofNullable(enclosingScope);

    while (optCurrentScope.isPresent()) {
      final I${languageName}Scope currentScope = optCurrentScope.get();
      if (currentScope.isSpannedBySymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, take its
        // package name. This check is important, since the package name of the
        // enclosing symbol might be set manually.
        return currentScope.getSpanningSymbol().get().getPackageName();
      } else if (currentScope instanceof ${languageName}ArtifactScope) {
        return ((${languageName}ArtifactScope) currentScope).getPackageName();
      }

      optCurrentScope = currentScope.getEnclosingScope();
    }

    return "";
  }

  /**
   * Determines <b>dynamically</b> the full name of the symbol.
   *
   * @return the full name of the symbol determined dynamically
   */
  protected String determineFullName() {
    if (enclosingScope == null) {
      // There should not be a symbol that is not defined in any scope. This case should only
      // occur while the symbol is built (by the symbol table creator). So, here the full name
      // should not be cached yet.
      return name;
    }

    final Deque<String> nameParts = new ArrayDeque<>();
    nameParts.addFirst(name);

    Optional<? extends I${languageName}Scope> optCurrentScope = Optional.of(enclosingScope);

    while (optCurrentScope.isPresent()) {
      final I${languageName}Scope currentScope = optCurrentScope.get();
      if (currentScope.isSpannedBySymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, the full name
        // of that symbol is the missing prefix, and hence, the calculation
        // ends here. This check is important, since the full name of the enclosing
        // symbol might be set manually.
        nameParts.addFirst(currentScope.getSpanningSymbol().get().getFullName());
        break;
      }

      if (!(currentScope instanceof I${languageName}GlobalScope)) {
        if (currentScope instanceof ${languageName}ArtifactScope) {
          // We have reached the artifact scope. Get the package name from the
          // symbol itself, since it might be set manually.
          if (!getPackageName().isEmpty()) {
            nameParts.addFirst(getPackageName());
          }
        } else {
          if (currentScope.getName().isPresent()) {
            nameParts.addFirst(currentScope.getName().get());
          }
          // ...else stop? If one of the enclosing scopes is unnamed,
          //         the full name is same as the simple name.
        }
      }
      optCurrentScope = currentScope.getEnclosingScope();
    }

    return Names.getQualifiedName(nameParts);
  }
}
