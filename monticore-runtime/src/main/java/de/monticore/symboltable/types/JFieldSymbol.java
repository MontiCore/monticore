/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.types;/* (c)  https://github.com/MontiCore/monticore */

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.types.references.JTypeReference;

/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public interface JFieldSymbol extends Symbol {

  JAttributeSymbolKind KIND = new JAttributeSymbolKind();

  JTypeReference<? extends JTypeSymbol> getType();

  boolean isStatic();

  boolean isFinal();

  boolean isParameter();

  boolean isPrivate();

  boolean isProtected();

  boolean isPublic();

}
