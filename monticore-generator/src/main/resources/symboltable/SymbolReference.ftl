<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "ruleName")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign referencedSymbol = ruleName+"Symbol">
<#assign package = genHelper.getTargetPackage()?lower_case>
<#assign languageName = genHelper.getGrammarSymbol().getName()>
<#assign astClass = "AST"+ruleName>
<#assign fqn = genHelper.getQualifiedGrammarName()?lower_case>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${package};

import ${fqn}._ast.*;

import java.util.Optional;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.references.ISymbolReference;
import de.se_rwth.commons.logging.Log;


/**
 * Represents a reference of {@link ${referencedSymbol}}.
 */
public class ${className} extends ${referencedSymbol} implements ISymbolReference {

  protected AccessModifier accessModifier = AccessModifier.ALL_INCLUSION;

  protected Predicate<${referencedSymbol}> predicate = x -> true;

  protected ${astClass} astNode;

  protected ${referencedSymbol} referencedSymbol;

  public ${className}(final String name, final I${languageName}Scope enclosingScopeOfReference) {
    super(name);
    this.name = name;
    this.enclosingScope = enclosingScopeOfReference;
  }


  @Override
  public String getName() {
    return getReferencedSymbol().getName();
  }

  @Override
  public String getFullName() {
    return getReferencedSymbol().getFullName();
  }

  @Override
  public void setEnclosingScope(I${languageName}Scope scope) {
    getReferencedSymbol().setEnclosingScope(scope);
  }

  @Override
  public I${languageName}Scope getEnclosingScope() {
    return getReferencedSymbol().getEnclosingScope();
  }

  @Override
  public AccessModifier getAccessModifier() {
    return getReferencedSymbol().getAccessModifier();
  }

  @Override
  public void setAccessModifier(AccessModifier accessModifier) {
    getReferencedSymbol().setAccessModifier(accessModifier);
  }

  public Optional<${astClass}> getAstNode() {
    return getReferencedSymbol().getAstNode();
  }

  @Override
  public void setAstNode(${astClass} astNode) {
    getReferencedSymbol().setAstNode(astNode);
  }


  public void setPredicate(Predicate<${referencedSymbol}> predicate) {
    this.predicate = predicate;
  }

  @Override
  public ${referencedSymbol} getReferencedSymbol() {
    if (!isReferencedSymbolLoaded()) {
      referencedSymbol = loadReferencedSymbol().orElse(null);

      if (!isReferencedSymbolLoaded()) {
        Log.error("0xA1038 " + ${referencedSymbol}Reference.class.getSimpleName() + " Could not load full information of '" +
            name + "' (Kind " + "${referencedSymbol}" + ").");
      }
    }

    return referencedSymbol;
  }


  @Override
  public boolean existsReferencedSymbol() {
    return isReferencedSymbolLoaded() || loadReferencedSymbol().isPresent();
  }

  public boolean isReferencedSymbolLoaded() {
    return referencedSymbol != null;
  }

  protected Optional<${referencedSymbol}> loadReferencedSymbol() {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name), " 0xA4070 Symbol name may not be null or empty.");

    Log.debug("Load full information of '" + name + "' (Kind " + "${referencedSymbol}" + ").",
        ${className}.class.getSimpleName());
    Optional<${referencedSymbol}> resolvedSymbol = enclosingScope.resolve${ruleName}(name, accessModifier, predicate);

    if (resolvedSymbol.isPresent()) {
      Log.debug("Loaded full information of '" + name + "' successfully.",
          ${className}.class.getSimpleName());
    }
    else {
      Log.debug("Cannot load full information of '" + name,
          ${className}.class.getSimpleName());
    }
    return resolvedSymbol;
  }

}

