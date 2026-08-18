/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesHandler;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * A visitor (a handler indeed) that creates a {@link CompKindOfComponentType} from an
 * {@link ASTMCQualifiedType}, given that there is matching, resolvable
 * component type symbol.
 */
public class SynthesizeCompKindFromMCBasicTypes implements MCBasicTypesHandler {

  protected MCBasicTypesTraverser traverser;

  /**
   * Common state with other visitors, if this visitor is part of a visitor composition.
   */
  protected CompKindCheckResult resultWrapper;

  public SynthesizeCompKindFromMCBasicTypes(@NonNull CompKindCheckResult resultWrapper) {
    this.resultWrapper = Preconditions.checkNotNull(resultWrapper);
  }

  @Override
  public MCBasicTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NonNull MCBasicTypesTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  @Override
  public void handle(@NonNull ASTMCQualifiedType node) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkArgument(node.getEnclosingScope() instanceof ICompSymbolsScope);

    ICompSymbolsScope enclScope = ((ICompSymbolsScope) node.getEnclosingScope());
    List<ComponentTypeSymbol> comp = enclScope.resolveComponentTypeMany(node.getMCQualifiedName().getQName());

    if (comp.isEmpty()) {
      this.resultWrapper.setResultAbsent();
    } else {
      CompKindExpression result = new CompKindOfComponentType(comp.getFirst());
      result.setSourceNode(node);
      this.resultWrapper.setResult(result);

      if (comp.size() > 1) {
        Log.error(
          String.format(
            "0xD0105 Ambiguous reference, both '%s' and '%s' match'",
            comp.get(0).getFullName(), comp.get(1).getFullName()
          ),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd()
        );
      }
    }
  }
}
