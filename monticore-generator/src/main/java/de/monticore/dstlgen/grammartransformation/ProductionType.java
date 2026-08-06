/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.grammartransformation;

/**
 * Enum representing the different types of
 * operation resulting in production rules.
 * Each mapped to a range of priority-modifiers
 */
public enum ProductionType {

  PATTERN("Pat", 50000),
  LIST("List", 40000),
  REPLACEMENT("Rep", 60000), // TODO: see MCQualifiedType / MCQualifiedType & document
  NEGATION("Neg", 20000),
  OPTIONAL("Opt", 10000);

  private final String nameString;
  private final int prioMod;

  ProductionType(String nameString, int prioMod) {
    this.nameString = nameString;
    this.prioMod = prioMod;
  }

  public String getNameString() {
    return nameString;
  }

  public int getPrioMod() {
    return prioMod;
  }
}
