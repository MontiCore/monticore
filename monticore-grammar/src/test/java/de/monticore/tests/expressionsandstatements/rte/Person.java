// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements.rte;

/**
 * simple class to test inheritance behavior
 */
public class Person {

  public String name;
  public int age;

  public Person(String name, int age) {
    this.name = name;
    this.age = age;
  }

  public String getName() {
    return name;
  }

  public int getAge() {
    return age;
  }

}
