/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages;

import de.monticore.symboltable.types.CommonJTypeSymbol;
import de.monticore.symboltable.types.JFieldSymbol;
import de.monticore.symboltable.types.JMethodSymbol;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

/**
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class JTypeSymbolMock extends CommonJTypeSymbol<JTypeSymbol, JFieldSymbol, JMethodSymbol, JTypeReference<JTypeSymbol>> {

  public JTypeSymbolMock(String name) {
    super(name);
  }

}
