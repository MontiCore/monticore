/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class.reference.symbol.methoddecorator;

import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

/**
 * class combines all getters for referenceSymbols
 * only getters because setters make no sense in this context
 * for mandatory and optional attributes optional getters are generated
 * for list attributes list methods are generated
 */

public class ReferencedSymbolAccessorDecorator extends AccessorDecorator {

  public ReferencedSymbolAccessorDecorator(final GlobalExtensionManagement glex, final SymbolTableService symbolTableService) {
    super(glex, new ReferencedSymbolOptAccessorDecorator(glex, symbolTableService),
        new ReferencedSymbolOptAccessorDecorator(glex, symbolTableService),
        new ReferencedSymbolListAccessorDecorator(glex, symbolTableService));
  }
}
