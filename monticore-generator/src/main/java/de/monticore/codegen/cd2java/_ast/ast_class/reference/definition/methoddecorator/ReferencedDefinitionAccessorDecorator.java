/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class.reference.definition.methoddecorator;

import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

/**
 * class combines all getters for referenceDefinition ASTs
 * only getters because setters make no sense in this context
 * for mandatory and optional attributes optional getters are generated
 * for list attributes list methods are generated
 */

public class ReferencedDefinitionAccessorDecorator extends AccessorDecorator {

  public ReferencedDefinitionAccessorDecorator(final GlobalExtensionManagement glex, SymbolTableService symbolTableService) {
    super(glex, new ReferencedDefinitionOptAccessorDecorator(glex, symbolTableService),
        new ReferencedDefinitionOptAccessorDecorator(glex, symbolTableService),
        new ReferencedDefinitionListAccessorDecorator(glex, symbolTableService));
  }
}
