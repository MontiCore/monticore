/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.stereotypes;

import de.monticore.symboltable.ISymbol;

/**
 * Symbolic representation of a stereotype. Various model elements, such as
 * symbols, may have stereotypes. <p>
 * This interface provides the abstraction on how the symbolic representation
 * of a stereotype looks like. No implementation is provided. If you want an
 * implementation, extend the language
 * {@code de.monticore.symbols.StereotypeSymbols}. This will also generate
 * resolving infrastructure for stereotype symbols and provide
 * de-/serialization capabilities.
 *
 * @see ISymbol#getStereoinfo()
 */
public interface IStereotypeSymbol extends ISymbol {

}
