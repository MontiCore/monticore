package de.monticore.codegen.cd2java._symboltable.symbol.symbolReferenceMethodDecorator;

import de.monticore.codegen.cd2java._symboltable.symbol.symbolReferenceMethodDecorator.accessor.SymbolReferenceAccessorDecorator;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolReferenceMethodDecorator.mutator.SymbolReferenceMutatorDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class SymbolReferenceMethodDecorator extends MethodDecorator {

  public SymbolReferenceMethodDecorator(final GlobalExtensionManagement glex) {
    super(glex, new SymbolReferenceAccessorDecorator(glex), new SymbolReferenceMutatorDecorator(glex));
  }
}
