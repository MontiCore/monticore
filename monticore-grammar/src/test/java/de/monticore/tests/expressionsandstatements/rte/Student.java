// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements.rte;

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
}
