package de.monticore.codegen.cd2java._symboltable.symbol.symbolReferenceMethodDecorator.accessor;

import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

/**
 * combination of mandatory, optional, list symbolReference getters
 * change implementation
 */
public class SymbolReferenceAccessorDecorator extends AccessorDecorator {

  public SymbolReferenceAccessorDecorator(GlobalExtensionManagement glex) {
    super(glex, new SymbolReferenceMandatoryAccessorDecorator(glex),
        new SymbolReferenceOptAccessorDecorator(glex),
        new SymbolReferenceListAccessorDecorator(glex));
  }
}
