/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.types;

/**
 * @author Pedram Mir Seyed Nazari
 * @param <T>
 */
public class Parameter<T extends TypeSymbol> {
  
  private final String name;
  private final T type;

  public Parameter(String name, T type) {
    this.name = name;
    this.type = type;
  }
  
  public String getName() {
    return name;
  }
  
  public T getType() {
    return type;
  }
  
  @Override
  public String toString() {
    if (type == null || name == null) {
      return "";
    }
    return type.getName() + " " + name + " ";
  }
  
}
