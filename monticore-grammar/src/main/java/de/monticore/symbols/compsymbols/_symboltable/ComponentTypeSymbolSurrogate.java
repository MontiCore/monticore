/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import de.monticore.symbols.compsymbols._ast.ASTComponentType;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

public class ComponentTypeSymbolSurrogate extends ComponentTypeSymbolSurrogateTOP {

  public ComponentTypeSymbolSurrogate(@NonNull String name) {
    super(name);
  }

  @Override
  public Set<PortSymbol> getAllPorts() {
    if (!checkLazyLoadDelegate()) {
      return super.getAllPorts();
    }
    return lazyLoadDelegate().getAllPorts();
  }

  @Override
  public ASTComponentType getAstNode() {
    if (!checkLazyLoadDelegate()) {
      return super.getAstNode();
    }
    return lazyLoadDelegate().getAstNode();
  }
}
