/* (c) https://github.com/MontiCore/monticore */
package types;

/**
 * Used for testing
 * 
 * @author Jerome Pfeiffer
 * 
 */
public class Attribute {
  
  private String type;
  
  private String name;
  
  /**
   * Constructor for types.Parameter
   * 
   * @param type
   * @param name
   */
  public Attribute(String type, String name) {
    super();
    this.type = type;
    this.name = name;
  }
  
  /**
   * @return type
   */
  public String getType() {
    return this.type;
  }
  
  /**
   * @return name
   */
  public String getName() {
    return this.name;
  }
  
}
