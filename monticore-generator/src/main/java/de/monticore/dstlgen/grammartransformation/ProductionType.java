/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.grammartransformation;

/**
 * Enum representing the different types of
 * operation resulting in production rules.
 *
 */
public enum ProductionType {

    PATTERN("Pat"), LIST("List"), REPLACEMENT("Rep"), NEGATION("Neg"), OPTIONAL("Opt");

    private final String nameString;

    ProductionType(String name) {
      this.nameString = name;
    }

    protected String getNameString() {
      return this.nameString;
    }
}
