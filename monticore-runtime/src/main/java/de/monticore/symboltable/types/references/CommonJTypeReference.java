/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.types.references;

import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;

/**
 * Default implementation of {@link JTypeReference}.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class CommonJTypeReference<T extends JTypeSymbol> extends CommonTypeReference<T> implements JTypeReference<T> {

  public CommonJTypeReference(String referencedSymbolName, SymbolKind referencedSymbolKind,
      Scope definingScopeOfReference) {
    super(referencedSymbolName, referencedSymbolKind, definingScopeOfReference);
  }
}
