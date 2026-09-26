/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods;

import de.monticore.cdbasis._ast.ASTCDAttribute;

import java.util.Set;

/**
 * Some attributes in symbolrules must be decorated with a supplier, since they might not be known immediately during the deserialization.
 * <p>
 * This class contains what those types are
 */
public final class AccessAsSupplierTypes {

  /**
   * Fully qualified name of the wrapper type used for lazily resolved attributes.
   */
  public static final String SUPPLIER_TYPE = "de.monticore.symboltable.__internal__Supplier";

  /**
   * Fully qualified name of the public supplier type exposed by generated getters and setters.
   */
  public static final String STD_SUPPLIER_TYPE = "java.util.function.Supplier";

  private static final Set<String> SUPPLIED_TYPES = Set.of(
      "de.monticore.types.check.SymTypeExpression",
      "java.util.List<de.monticore.types.check.SymTypeExpression>",
      "java.util.Optional<de.monticore.types.check.SymTypeExpression>",
      "List<de.monticore.types.check.SymTypeExpression>",
      "Optional<de.monticore.types.check.SymTypeExpression>",
      "SymTypeExpression",
      "java.util.List<SymTypeExpression>",
      "java.util.Optional<SymTypeExpression>",
      "List<SymTypeExpression>",
      "Optional<SymTypeExpression>"
  );

  private AccessAsSupplierTypes() {
  }

  /**
   * @param type the printed attribute type
   * @return whether an attribute of this type should be wrapped in / accessed via a {@code Supplier}
   */
  public static boolean shouldHaveSupplier(String type) {
    return SUPPLIED_TYPES.contains(type);
  }

  public static boolean shouldHaveSupplier(ASTCDAttribute attribute) {
    return shouldHaveSupplier(attribute.printType());
  }

}
