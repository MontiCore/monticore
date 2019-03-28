package de.monticore.codegen.cd2java.ast_new.reference.referencedDefinition.referencedDefinitionMethodDecorator;

import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

public class ReferencedDefinitionAccessorDecorator extends AccessorDecorator {

  public ReferencedDefinitionAccessorDecorator(GlobalExtensionManagement glex, SymbolTableService symbolTableService) {
    super(glex, new ReferencedDefinitionOptAccessorDecorator(glex, symbolTableService), new ReferencedDefinitionOptAccessorDecorator(glex, symbolTableService), new ReferencedDefinitionListAccessorDecorator(glex, symbolTableService));
  }
}
