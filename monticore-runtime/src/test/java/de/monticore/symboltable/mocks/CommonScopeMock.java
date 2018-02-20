/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvingInfo;

/**
 * Mock for {@link CommonScope} which enables to test the data of the
 * {@link ResolvingInfo} collected during the resolution process
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class CommonScopeMock extends CommonScope {

  private ResolvingInfo resolvingInfo;

  public CommonScopeMock(boolean isShadowingScope) {
    super(isShadowingScope);
  }

  public CommonScopeMock(Optional<MutableScope> enclosingScope, boolean isShadowingScope) {
    super(enclosingScope, isShadowingScope);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveMany(ResolvingInfo resolvingInfo, String name,
      SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate) {
    this.resolvingInfo = resolvingInfo;
    return super.resolveMany(resolvingInfo, name, kind, modifier, predicate);
  }

  @Override
  public <T extends Symbol> Collection<T> resolveDownMany(ResolvingInfo resolvingInfo, String name, SymbolKind kind, AccessModifier modifier,
      Predicate<Symbol> predicate) {
    this.resolvingInfo = resolvingInfo;
    return super.resolveDownMany(resolvingInfo, name, kind, modifier, predicate);
  }

  public ResolvingInfo getResolvingInfo() {
    return resolvingInfo;
  }
}
