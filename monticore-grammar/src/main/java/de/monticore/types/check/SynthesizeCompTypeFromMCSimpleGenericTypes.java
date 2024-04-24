/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCBasicTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCPrimitiveTypeArgument;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesHandler;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A visitor (a handler indeed) that creates a {@link KindOfComponent} from an
 * {@link ASTMCBasicGenericType}, given that there is a matching resolvable
 * component symbol.
 */
public class SynthesizeCompTypeFromMCSimpleGenericTypes implements MCSimpleGenericTypesHandler {

  protected MCSimpleGenericTypesTraverser traverser;

  /**
   * Common state with other visitors, if this visitor is part of a visitor composition.
   */
  protected CompTypeCheckResult resultWrapper;

  /**
   * Used to create {@link SymTypeExpression}s for the ast-representation of the generic component type's type.
   */
  protected ISynthesize typeSynth;

  public SynthesizeCompTypeFromMCSimpleGenericTypes(@NonNull CompTypeCheckResult result,
                                                    @NonNull ISynthesize typeSynth) {
    this.resultWrapper = result;
    this.typeSynth = typeSynth;
  }

  @Override
  public MCSimpleGenericTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NonNull MCSimpleGenericTypesTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  @Override
  public void handle(@NonNull ASTMCBasicGenericType mcType) {
    Preconditions.checkNotNull(mcType);
    Preconditions.checkNotNull(mcType.getEnclosingScope());
    Preconditions.checkArgument(mcType.getEnclosingScope() instanceof ICompSymbolsScope);

    ICompSymbolsScope enclScope = (ICompSymbolsScope) mcType.getEnclosingScope();
    String compName = String.join(".", mcType.getNameList());
    List<ComponentSymbol> compSym = enclScope.resolveComponentMany(compName);

    if (compSym.isEmpty()) {
      this.resultWrapper.setResultAbsent();
    } else {
      List<SymTypeExpression> typeArgExpressions = typeArgumentsToTypes(mcType.getMCTypeArgumentList()).stream()
        .map(typeArg -> {
          TypeCheckResult typeResult = null;
          try {
            typeResult = typeSynth.synthesizeType(typeArg);
          }  catch (ResolvedSeveralEntriesForSymbolException ignored) { }
          return typeResult != null && typeResult.isPresentResult() ? typeResult.getResult() : null;
        })
        .collect(Collectors.toList());
      this.resultWrapper.setResult(new KindOfGenericComponent(compSym.get(0), typeArgExpressions));
    }
  }

  /**
   * Given that all {@link ASTMCTypeArgument}s in {@code typeArgs} are {@link ASTMCType}s, this method returns a list
   * with these {@code ASTMCType}s in the same order. Else, an exception is thrown.
   */
  protected List<ASTMCType> typeArgumentsToTypes(@NonNull List<ASTMCTypeArgument> typeArgs) {
    Preconditions.checkNotNull(typeArgs);

    List<ASTMCType> types = new ArrayList<>(typeArgs.size());
    for (ASTMCTypeArgument typeArg : typeArgs) {
      if (typeArg instanceof ASTMCBasicTypeArgument) {
        types.add(((ASTMCBasicTypeArgument) typeArg).getMCQualifiedType());
      } else if (typeArg instanceof ASTMCPrimitiveTypeArgument) {
        types.add(((ASTMCPrimitiveTypeArgument) typeArg).getMCPrimitiveType());
      } else if (typeArg instanceof ASTMCCustomTypeArgument) {
        types.add(((ASTMCCustomTypeArgument) typeArg).getMCType());
      } else {
        throw new IllegalStateException();
      }
    }
    return types;
  }
}
