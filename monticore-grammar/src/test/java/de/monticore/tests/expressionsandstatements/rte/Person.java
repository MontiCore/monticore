// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements.rte;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Person person = (Person) o;
    return age == person.age && name.equals(person.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, age);
  }

}
