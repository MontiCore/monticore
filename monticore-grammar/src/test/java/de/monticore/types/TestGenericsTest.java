/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

public class TestGenericsTest {

  public void main() {
    TestGenerics<String> k = new TestGenerics<>();
    TestGenerics<String>.Inner<Integer> ku = k.new Inner<Integer>();
  }

}
