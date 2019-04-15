package de.monticore.codegen.cd2java.ast_new.reference.referencedSymbol.referenedSymbolMethodDecorator;

import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class ReferencedSymbolAccessorDecorator extends AccessorDecorator {

  public ReferencedSymbolAccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex, new ReferencedSymbolOptAccessorDecorator(glex),
        new ReferencedSymbolOptAccessorDecorator(glex),
        new ReferencedSymbolListAccessorDecorator(glex));
  }
}
