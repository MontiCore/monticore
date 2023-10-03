/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.types.check.SymTypeExpression;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PortSymbol extends PortSymbolTOP {

  protected Boolean delayed = null;

  protected PortSymbol(String name) {
    super(name);
  }

  /**
   * @param name      the name of this port.
   * @param incoming  whether the port is incoming.
   * @param outgoing  whether the port is outgoing.
   * @param type      the type of this port.
   * @param timing    the timing of this port.
   */
  protected PortSymbol(String name,
                       boolean incoming,
                       boolean outgoing,
                       SymTypeExpression type,
                       Timing timing) {
    super(name);
    this.type = type;
    this.timing = timing;
    this.incoming = incoming;
    this.outgoing = outgoing;
  }

  public boolean isTypePresent() {
    return this.type != null;
  }

  public SymTypeExpression getType() {
    Preconditions.checkState(this.type != null);
    return this.type;
  }

  public void setType(@NonNull SymTypeExpression type) {
    Preconditions.checkNotNull(type);
    this.type = type;
  }

  public TypeSymbol getTypeInfo() {
    return this.getType().getTypeInfo() instanceof TypeSymbolSurrogate ?
      ((TypeSymbolSurrogate) this.getType().getTypeInfo()).lazyLoadDelegate() : this.getType().getTypeInfo();
  }

  @Override
  public Timing getTiming() {
    return this.timing;
  }

  @Override
  public void setTiming(@Nullable Timing timing) {
    this.timing = timing;
  }

  public Boolean getDelayed() {
    return this.isDelayed();
  }

  public boolean isDelayed() {
    return this.delayed;
  }

  public void setDelayed(@Nullable Boolean delayed) {
    this.delayed = delayed;
  }

  public boolean isStronglyCausal() {
    return this.stronglyCausal;
  }
}
