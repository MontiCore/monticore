/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods;

import java.util.Arrays;


public class AccessAsSupplierTyps {

  public static final String SUPPLIER_TYPE = "java.util.function.Supplier";

  // TODO also accept non-fully-qualified spellings
  public static final String[] SUPPLYLIST = {
      "de.monticore.types.check.SymTypeExpression",
      "java.util.List<de.monticore.types.check.SymTypeExpression>",
      "java.util.Optional<de.monticore.types.check.SymTypeExpression>"
  };


  public static boolean shouldBeSupplied(String t) {
    return Arrays.asList(SUPPLYLIST).contains(t);
  }
}
