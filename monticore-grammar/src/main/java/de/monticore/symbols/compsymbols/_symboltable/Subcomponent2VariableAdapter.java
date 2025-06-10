/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.SourcePosition;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Adapts {@link SubcomponentSymbol}s to {@link VariableSymbol}s, e.g., so that they can
 * easily be referred to from expressions.
 */
public class Subcomponent2VariableAdapter extends VariableSymbol {

  protected SubcomponentSymbol adaptee;

  public Subcomponent2VariableAdapter(@NonNull SubcomponentSymbol adaptee) {
    super(Preconditions.checkNotNull(adaptee).getName());
    this.adaptee = adaptee;
    this.accessModifier = BasicAccessModifier.PRIVATE;
  }

  public SubcomponentSymbol getAdaptee() {
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
    throw new RuntimeException();
  }

  @Override
  public SymTypeExpression getType() {
    if (!adaptee.isTypePresent()) return SymTypeExpressionFactory.createObscureType();
    if (!adaptee.getType().isGenericComponentType()) {
      return SymTypeExpressionFactory.createTypeObject(new ComponentType2TypeSymbolAdapter(adaptee.getType().getTypeInfo()));
    } else {
      return SymTypeExpressionFactory.createGenerics(
        new ComponentType2TypeSymbolAdapter(adaptee.getType().getTypeInfo()), adaptee.getType().asGenericComponentType().getTypeBindingsAsList()
      );
    }
  }

  @Override
  public boolean isIsReadOnly() {
    return true;
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
  public Subcomponent2VariableAdapter deepClone() {
    return new Subcomponent2VariableAdapter(this.getAdaptee());
  }
}
