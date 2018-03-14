/* (c) https://github.com/MontiCore/monticore */
package freemarker.core;

/**
 * @author Jerome Pfeiffer
 */
public class Parameter {
  
  private String type;
  
  private String name;
  
  /**
   * Constructor for freemarker.core.Argument
   * 
   * @param type
   * @param name
   */
  public Parameter(String type, String name) {
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
