/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
@Deprecated
public class SpanningSymbolReference {
  
  public SpanningSymbolReference(
      String kind,
      String name) {
    this.kind = kind;
    this.name = name;
  }
  
  protected String kind;
  
  protected String name;
  
  /**
   * @return kind
   */
  public String getKind() {
    return this.kind;
  }
  
  /**
   * @return name
   */
  public String getName() {
    return this.name;
  }
  
}
