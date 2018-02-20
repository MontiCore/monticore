/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import java.util.Set;

import de.monticore.symboltable.SymbolKind;

/**
 * Calculates the possible model names from a (un-)qualified symbol <code>name</code> depending on the
 * <code>kind</code> (see {@link #calculateModelNames(String, SymbolKind)}.
 *
 * @author Pedram Mir Seyed Nazari
 */
public interface ModelNameCalculator {


  /**
   * Calculates the possible model names from a (un-)qualified symbol <code>name</code> depending on its
   * <code>kind</code>. This is required to resolve inner model elements, since their containing
   * model needs to be loaded first. For example, a class diagram symbol is the top-level symbol
   * of a class diagram. To resolve a type <code>T</code> defined in a class diagram a.b.CD, first
   * <code>a.b.CD.MyClass</code> in the path <code>a/b/CD/MyClass.cd</code> would fail, since
   * <code>MyClass</code> is not a top level element and consequently no file <code>MyClass.cd</code>
   * exists. The name of the model (here class diagram) is <code>a.b.CD</code>, leading
   * to the correct model path <code>a/b/CD.cd</code>.
   *
   * @param name the (qualified) name
   * @param kind the symbol kind
   * @return model name depending on the
   */
  Set<String> calculateModelNames(String name, SymbolKind kind);

}
