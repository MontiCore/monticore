/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class.reference;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.codegen.cd2java.CompositeDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.definition.ASTReferencedDefinitionDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.definition.methoddecorator.ReferencedDefinitionAccessorDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.symbol.ASTReferencedSymbolDecorator;
import de.monticore.codegen.cd2java._ast.ast_class.reference.symbol.methoddecorator.ReferencedSymbolAccessorDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

/**
 * combines the referenceSymbol and referenceDefinition decorators to execute both
 * if you use the referenceDefinition decorator you also need the referenceSymbol decorator
 * the other way around is no problem
 */
public class ASTReferenceDecorator extends CompositeDecorator<ASTCDClass> {

  public ASTReferenceDecorator(GlobalExtensionManagement glex, SymbolTableService symbolTableService) {
    this(new ASTReferencedSymbolDecorator(glex, new ReferencedSymbolAccessorDecorator(glex, symbolTableService), symbolTableService),
        new ASTReferencedDefinitionDecorator(glex, new ReferencedDefinitionAccessorDecorator(glex, symbolTableService), symbolTableService));
  }

  public ASTReferenceDecorator(ASTReferencedSymbolDecorator referencedSymbolDecorator, ASTReferencedDefinitionDecorator referencedDefinitionDecorator) {
    super(referencedSymbolDecorator, referencedDefinitionDecorator);
  }
}
