/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class.reference.referencedDefinition.referencedDefinitionMethodDecorator;

import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class ReferencedDefinitionAccessorDecorator extends AccessorDecorator {

  public ReferencedDefinitionAccessorDecorator(final GlobalExtensionManagement glex, SymbolTableService symbolTableService) {
    super(glex, new ReferencedDefinitionOptAccessorDecorator(glex, symbolTableService),
        new ReferencedDefinitionOptAccessorDecorator(glex, symbolTableService),
        new ReferencedDefinitionListAccessorDecorator(glex, symbolTableService));
  }
}
