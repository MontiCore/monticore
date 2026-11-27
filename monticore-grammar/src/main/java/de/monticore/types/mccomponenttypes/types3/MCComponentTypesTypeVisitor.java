/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccomponenttypes.types3;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.CompKindOfGenericComponentType;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccomponenttypes._ast.ASTMCComponentType;
import de.monticore.types.mccomponenttypes._ast.ASTMCGenericComponentType;
import de.monticore.types.mccomponenttypes._ast.ASTMCQualifiedComponentType;
import de.monticore.types.mccomponenttypes._visitor.MCComponentTypesHandler;
import de.monticore.types.mccomponenttypes._visitor.MCComponentTypesTraverser;
import de.monticore.types.mccomponenttypes._visitor.MCComponentTypesVisitor2;
import de.monticore.types3.AbstractTypeVisitor;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Calculates component type expressions for the MCComponentTypes grammar. */
public class MCComponentTypesTypeVisitor extends AbstractTypeVisitor
    implements MCComponentTypesVisitor2, MCComponentTypesHandler {

  protected static final String AMBIGUOUS_REFERENCE =
      "0xD0105 Ambiguous reference, both '%s' and '%s' match'";
  protected static final String UNSUPPORTED_TYPE_ARGUMENT =
      "0xD0106 Used an unsupported type argument for component. "
          + "You can not use WildCards as type arguments, "
          + "such as GenericType<? extends Person>.";

  protected MCComponentTypesTraverser traverser;
  protected CompType4Ast compType4Ast;

  public void setCompType4Ast(@NonNull CompType4Ast compType4Ast) {
    this.compType4Ast = Preconditions.checkNotNull(compType4Ast);
  }

  protected CompType4Ast getCompType4Ast() {
    return Preconditions.checkNotNull(compType4Ast);
  }

  @Override
  public MCComponentTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NonNull MCComponentTypesTraverser traverser) {
    this.traverser = Preconditions.checkNotNull(traverser);
  }

  @Override
  public void handle(@NonNull ASTMCQualifiedComponentType node) {
    endVisit(node);
  }

  @Override
  public void handle(@NonNull ASTMCGenericComponentType node) {
    node.getMCTypeArgumentList()
        .forEach(typeArgument -> typeArgument.accept(getTraverser()));
    endVisit(node);
  }

  @Override
  public void endVisit(@NonNull ASTMCQualifiedComponentType node) {
    List<ComponentTypeSymbol> components = resolveComponent(
        node, node.getMCQualifiedName().getQName());
    if (components.isEmpty()) {
      getCompType4Ast().setTypeOfComponentAbsent(node);
      return;
    }
    setResult(node, new CompKindOfComponentType(components.getFirst()));
  }

  @Override
  public void endVisit(@NonNull ASTMCGenericComponentType node) {
    List<ComponentTypeSymbol> components = resolveComponent(
        node, String.join(".", node.getNameList()));
    if (components.isEmpty()) {
      getCompType4Ast().setTypeOfComponentAbsent(node);
      return;
    }
    setResult(node, new CompKindOfGenericComponentType(
        components.getFirst(), calculateTypeArguments(node.getMCTypeArgumentList())));
  }

  protected List<ComponentTypeSymbol> resolveComponent(
      @NonNull ASTMCComponentType node,
      @NonNull String name) {
    Preconditions.checkNotNull(node);
    Preconditions.checkNotNull(node.getEnclosingScope());
    Preconditions.checkArgument(node.getEnclosingScope() instanceof ICompSymbolsScope);

    List<ComponentTypeSymbol> components =
        ((ICompSymbolsScope) node.getEnclosingScope()).resolveComponentTypeMany(name);
    logAmbiguousReference(components, node);
    return components;
  }

  protected List<SymTypeExpression> calculateTypeArguments(
      @NonNull List<ASTMCTypeArgument> typeArguments) {
    Preconditions.checkNotNull(typeArguments);
    List<SymTypeExpression> types = new ArrayList<>(typeArguments.size());
    for (ASTMCTypeArgument typeArgument : typeArguments) {
      Optional<ASTMCType> type = typeArgument.getMCTypeOpt();
      if (type.isPresent()
          && getType4Ast().hasPartialTypeOfTypeIdentifier(typeArgument)) {
        types.add(getType4Ast().getPartialTypeOfTypeId(typeArgument));
      }
      else {
        Log.error(UNSUPPORTED_TYPE_ARGUMENT);
      }
    }
    return types;
  }

  protected void logAmbiguousReference(
      @NonNull List<ComponentTypeSymbol> components,
      @NonNull ASTNode node) {
    if (components.size() > 1) {
      Log.error(String.format(AMBIGUOUS_REFERENCE,
              components.get(0).getFullName(), components.get(1).getFullName()),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }

  protected void setResult(
      @NonNull ASTMCComponentType node,
      @NonNull CompKindExpression result) {
    node.setDefiningSymbol(result.getTypeInfo());
    result.setSourceNode(node);
    getCompType4Ast().setTypeOfComponent(node, result);
  }
}
