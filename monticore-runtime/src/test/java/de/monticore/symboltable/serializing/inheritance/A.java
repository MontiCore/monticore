/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serializing.inheritance;

import java.io.Serializable;

/**
 *
 * @author  Pedram Mir Seyed Nazari
 *
 */
public class A extends B implements Serializable {

  private static final long serialVersionUID = -7533706844899345273L;
  
  private boolean male;
  
  // Default value is ignored
  private transient String city = "NoWhere";
  
  public A(String name, boolean male) {
    super(name);
    this.male = male;
    // Default value is ignored 
    city = "NoWhere";
  }

  public boolean isMale() {
    return male;
  }

  public void setMale(boolean male) {
    this.male = male;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }
  
}
