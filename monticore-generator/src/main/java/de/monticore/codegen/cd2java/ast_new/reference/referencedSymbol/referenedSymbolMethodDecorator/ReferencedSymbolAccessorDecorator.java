package de.monticore.codegen.cd2java.ast_new.reference.referencedSymbol.referenedSymbolMethodDecorator;

import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class ReferencedSymbolAccessorDecorator extends AccessorDecorator {

  public ReferencedSymbolAccessorDecorator(final GlobalExtensionManagement glex, final SymbolTableService symbolTableService) {
    super(glex, new ReferencedSymbolOptAccessorDecorator(glex, symbolTableService),
        new ReferencedSymbolOptAccessorDecorator(glex, symbolTableService),
        new ReferencedSymbolListAccessorDecorator(glex, symbolTableService));
  }
}
