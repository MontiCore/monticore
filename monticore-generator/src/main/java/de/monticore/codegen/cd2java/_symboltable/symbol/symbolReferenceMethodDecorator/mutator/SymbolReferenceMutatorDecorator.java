package de.monticore.codegen.cd2java._symboltable.symbol.symbolReferenceMethodDecorator.mutator;

import de.monticore.codegen.cd2java.methods.MutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

/**
 * combination of mandatory, optional, list symbolReference setters
 * change implementation
 */
public class SymbolReferenceMutatorDecorator extends MutatorDecorator {

  public SymbolReferenceMutatorDecorator(GlobalExtensionManagement glex) {
    super(glex, new SymbolReferenceMandatoryMutatorDecorator(glex),
        new SymbolReferenceOptMutatorDecorator(glex),
        new SymbolReferenceListMutatorDecorator(glex));
  }
}
