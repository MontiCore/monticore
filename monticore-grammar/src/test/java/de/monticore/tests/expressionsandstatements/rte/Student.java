// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements.rte;

import java.util.Objects;

/**
 * simple class to test inheritance behavior
 */
public class Student extends Person {

  protected int studentID;

  public Student(String name, int age, int studentID) {
    super(name, age);
    this.studentID = studentID;
  }

  public int getStudentID() {
    return studentID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    Student student = (Student) o;
    return studentID == student.studentID;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), studentID);
  }
}
