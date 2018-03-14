/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.CommonJTypeReference;
import de.monticore.symboltable.types.references.JTypeReference;

/**
 *
 * The <code>astextends</code> constructs allows both extending another rule
 * or an external type (see {@link JTypeSymbol]). This class helps resolve the respective type.
 *
 * @author  Pedram Mir Seyed Nazari
 */
public class MCProdOrTypeReference {

  private final MCProdSymbolReference prodRef;
  
  private final JTypeReference<JTypeSymbol> typeRef;

  public MCProdOrTypeReference(String referencedSymbolName, Scope enclosingScopeOfReference) {
    prodRef = new MCProdSymbolReference(referencedSymbolName, enclosingScopeOfReference);
    typeRef = new CommonJTypeReference<>(referencedSymbolName, JTypeSymbol.KIND, enclosingScopeOfReference);
  }

  public MCProdSymbolReference getProdRef() {
    return prodRef;
  }

  public JTypeReference<JTypeSymbol> getTypeRef() {
    return typeRef;
  }

  public boolean isProdRef() {
    return prodRef.existsReferencedSymbol();
  }

  public boolean isTypeRef() {
    return typeRef.existsReferencedSymbol();
  }
}
