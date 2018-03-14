/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serializing.inheritance;

import java.io.Serializable;

public class B implements Serializable {

  private static final long serialVersionUID = 5772581073540756479L;
  
  private String name;
  private int age;
  

  public B(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }
  
}
