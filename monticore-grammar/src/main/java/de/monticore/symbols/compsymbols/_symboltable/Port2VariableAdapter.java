/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.SourcePosition;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Adapts {@link PortSymbol}s to {@link VariableSymbol}s, e.g., so that they can
 * easily be referred to from expressions.
 */
public class Port2VariableAdapter extends VariableSymbol {

  protected PortSymbol adaptee;

  public Port2VariableAdapter(@NonNull PortSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee).getName());
    this.adaptee = adaptee;
    this.accessModifier = BasicAccessModifier.PUBLIC;
  }

  public PortSymbol getAdaptee() {
    return adaptee;
  }

  @Override
  public void setName(@NonNull String name) {
    Preconditions.checkNotNull(name);
    Preconditions.checkArgument(!name.isBlank());
    this.getAdaptee().setName(name);
  }

  @Override
  public String getName() {
    return this.getAdaptee().getName();
  }

  @Override
  public String getFullName() {
    return this.getAdaptee().getFullName();
  }

  @Override
  public void setType(@NonNull SymTypeExpression type) {
    Preconditions.checkNotNull(type);
    this.getAdaptee().setType(type);
  }

  @Override
  public SymTypeExpression getType() {
    return this.getAdaptee().getType();
  }

  @Override
  public boolean isIsReadOnly() {
    return this.getAdaptee().isIncoming();
  }

  @Override
  public IBasicSymbolsScope getEnclosingScope() {
    return this.getAdaptee().getEnclosingScope();
  }

  @Override
  public SourcePosition getSourcePosition() {
    return this.getAdaptee().getSourcePosition();
  }

  @Override
  public Port2VariableAdapter deepClone() {
    Port2VariableAdapter clone = new Port2VariableAdapter(this.getAdaptee());
    clone.setAccessModifier(this.getAccessModifier());
    clone.setEnclosingScope(this.getEnclosingScope());
    clone.setFullName(this.getFullName());
    clone.setIsReadOnly(this.isIsReadOnly());
    if (this.isPresentAstNode()) {
      clone.setAstNode(this.getAstNode());
    }
    if (this.getType() != null) {
      clone.setType(this.getType().deepClone());
    }
    return clone;
  }
}
