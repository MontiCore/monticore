package mc.typescalculator.combineexpressionswithliterals._symboltable;

import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.names.CommonQualifiedNamesCalculator;
import de.monticore.symboltable.names.QualifiedNamesCalculator;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.Predicate;

public class CombineExpressionsWithLiteralsArtifactScope extends CombineExpressionsWithLiteralsArtifactScopeTOP {
  public CombineExpressionsWithLiteralsArtifactScope(String packageName, List<ImportStatement> imports) {
    super(packageName, imports);
    this.packageName = packageName;
    this.imports = imports;
  }

  public CombineExpressionsWithLiteralsArtifactScope(Optional<ICombineExpressionsWithLiteralsScope> enclosingScope, String packageName, List<ImportStatement> imports) {
    super(enclosingScope, packageName, imports);
    this.packageName = packageName;
    this.imports = imports;
  }

  private QualifiedNamesCalculator qualifiedNamesCalculator = new CommonQualifiedNamesCalculator();
  private String packageName;

  private List<ImportStatement> imports;

  

  public void setImports(List<ImportStatement> imports) {
    super.setImports(imports);
    this.imports = imports;
  }

  @Override
  public Collection<TypeSymbol> continueTypeWithEnclosingScope(
          boolean foundSymbols, String name,
          AccessModifier modifier, Predicate<TypeSymbol> predicate) {
    final Collection<TypeSymbol> result = new LinkedHashSet<>();

    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope().isPresent())) {
      if (!(enclosingScope instanceof ICombineExpressionsWithLiteralsGlobalScope) ) {
        Log.warn("0xA1039 The artifact scope " + getName().orElse("") + " should have a global scope as enclosing scope or no "
                + "enclosing scope at all.");
      }
      foundSymbols = foundSymbols | result.size() > 0;
      final Set<String> potentialQualifiedNames = qualifiedNamesCalculator.calculateQualifiedNames(name, packageName, imports);

      for (final String potentialQualifiedName : potentialQualifiedNames) {
        final Collection<TypeSymbol> resolvedFromEnclosing = enclosingScope.resolveTypeMany(foundSymbols, potentialQualifiedName, modifier, predicate);
        foundSymbols = foundSymbols | resolvedFromEnclosing.size() > 0;
        result.addAll(resolvedFromEnclosing);
      }
    }
    return result;
  }


  @Override
  public Collection<FieldSymbol> continueFieldWithEnclosingScope(
          boolean foundSymbols, String name,
          AccessModifier modifier, Predicate<FieldSymbol> predicate) {
    final Collection<FieldSymbol> result = new LinkedHashSet<>();

    if (checkIfContinueWithEnclosingScope(foundSymbols) && (getEnclosingScope().isPresent())) {
      if (!(enclosingScope instanceof ICombineExpressionsWithLiteralsGlobalScope) ) {
        Log.warn("0xA1039 The artifact scope " + getName().orElse("") + " should have a global scope as enclosing scope or no "
                + "enclosing scope at all.");
      }
      foundSymbols = foundSymbols | result.size() > 0;
      final Set<String> potentialQualifiedNames = qualifiedNamesCalculator.calculateQualifiedNames(name, packageName, imports);

      for (final String potentialQualifiedName : potentialQualifiedNames) {
        final Collection<FieldSymbol> resolvedFromEnclosing = enclosingScope.resolveFieldMany(foundSymbols, potentialQualifiedName, modifier, predicate);
        foundSymbols = foundSymbols | resolvedFromEnclosing.size() > 0;
        result.addAll(resolvedFromEnclosing);
      }
    }
    return result;
  }
}
