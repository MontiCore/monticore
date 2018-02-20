/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.types;

import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

import java.util.List;

/**
 * @author Pedram Mir Seyed Nazari
 */
public interface JMethodSymbol extends ScopeSpanningSymbol {

  JMethodSymbolKind KIND = new JMethodSymbolKind();

  JTypeReference<? extends JTypeSymbol> getReturnType();

  List<? extends JFieldSymbol> getParameters();

  List<? extends JTypeSymbol> getFormalTypeParameters();

  List<? extends JTypeReference<? extends JTypeSymbol>> getExceptions();

  boolean isAbstract();

  boolean isStatic();

  boolean isConstructor();

  boolean isFinal();

  boolean isEllipsisParameterMethod();

  boolean isPrivate();

  boolean isProtected();

  boolean isPublic();

}
