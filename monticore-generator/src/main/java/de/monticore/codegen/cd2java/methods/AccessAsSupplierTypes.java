/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods;

import de.monticore.cdbasis._ast.ASTCDAttribute;

import java.util.Arrays;

/**
 * Some attributes in symbolrules must be decorated with a supplier, since they might not be known immediatly during the deserialization.
 *
 * This class contains what those types are
 */
public class AccessAsSupplierTypes {

  /**
   * Fully qualified name of the wrapper type used for lazily resolved attributes.
   */
  public static final String SUPPLIER_TYPE = "de.monticore.symboltable.__internal__Supplier";

  private static final String[] SUPPLYLIST = {
      "de.monticore.types.check.SymTypeExpression",
      "java.util.List<de.monticore.types.check.SymTypeExpression>",
      "java.util.Optional<de.monticore.types.check.SymTypeExpression>"
  };

  /**
   * @param type the fully qualified, printed attribute type
   * @return whether an attribute of this type should be wrapped in / accessed via a {@code Supplier}
   */
  public static boolean shouldHaveSupplier(String type) {
    return Arrays.asList(SUPPLYLIST).contains(type);
  }

  public static boolean shouldHaveSupplier(ASTCDAttribute attribute) {
    return shouldHaveSupplier(attribute.getMCType().printType());
  }

}
