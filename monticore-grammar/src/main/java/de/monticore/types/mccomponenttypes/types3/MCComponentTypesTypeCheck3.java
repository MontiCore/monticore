/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccomponenttypes.types3;

import com.google.common.base.Preconditions;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.mccomponenttypes._ast.ASTMCComponentType;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

/**
 * TypeCheck3 extension for component type references.
 *
 * <p>Language-specific type checks using MCComponentTypes can derive from this
 * class and register an {@link MCComponentTypesTypeVisitor} that shares the
 * supplied {@link CompType4Ast}.</p>
 */
public class MCComponentTypesTypeCheck3 extends MapBasedTypeCheck3 {

  protected final CompType4Ast compType4Ast;

  public MCComponentTypesTypeCheck3(
      @NonNull ITraverser typeTraverser,
      @NonNull Type4Ast type4Ast,
      @NonNull InferenceContext4Ast ctx4Ast,
      @NonNull CompType4Ast compType4Ast) {
    super(typeTraverser, type4Ast, ctx4Ast);
    this.compType4Ast = Preconditions.checkNotNull(compType4Ast);
  }

  public CompType4Ast getCompType4Ast() {
    return compType4Ast;
  }

  /**
   * Calculates the component type represented by the given AST node.
   * Results, including unresolved references, are cached by the delegate.
   */
  public static Optional<CompKindExpression> compTypeFromAST(
      @NonNull ASTMCComponentType componentType) {
    Preconditions.checkNotNull(componentType);
    Preconditions.checkState(getDelegate() instanceof MCComponentTypesTypeCheck3,
        "The TypeCheck3 delegate does not support MCComponentTypes");
    return ((MCComponentTypesTypeCheck3) getDelegate())
        ._compTypeFromAST(componentType);
  }

  protected Optional<CompKindExpression> _compTypeFromAST(
      @NonNull ASTMCComponentType componentType) {
    boolean cached = getCompType4Ast().hasTypeOfComponent(componentType);
    if (!cached) {
      componentType.accept(getTypeTraverser());
    }
    Optional<CompKindExpression> result =
        getCompType4Ast().getTypeOfComponent(componentType);
    if (!cached && result.isEmpty()) {
      Log.error(String.format("0xD0104 Cannot resolve component '%s'",
              componentType.printType()),
          componentType.get_SourcePositionStart(),
          componentType.get_SourcePositionEnd());
    }
    return result;
  }
}
