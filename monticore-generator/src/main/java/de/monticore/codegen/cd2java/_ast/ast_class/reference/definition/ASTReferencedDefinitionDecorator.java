/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class.reference.definition;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.codegen.cd2java._ast.ast_class.reference.definition.methoddecorator.ReferencedDefinitionAccessorDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PACKAGE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

/**
 * is a transforming class for the ast generation
 * adds the symbol reference definition getters -> uses the symbol reference attribute created in ASTReferencedSymbolDecorator
 */

public class ASTReferencedDefinitionDecorator extends AbstractTransformer<ASTCDClass> {

  public static final String DEFINITION = "Definition";

  protected final ReferencedDefinitionAccessorDecorator accessorDecorator;

  protected final SymbolTableService symbolTableService;

  public ASTReferencedDefinitionDecorator(final GlobalExtensionManagement glex, final ReferencedDefinitionAccessorDecorator accessorDecorator,
                                          final SymbolTableService symbolTableService) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass originalInput, ASTCDClass changedInput) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : originalInput.getCDAttributeList()) {
      if (symbolTableService.isReferencedSymbol(astcdAttribute)) {
        String referencedSymbolType = symbolTableService.getReferencedSymbolTypeName(astcdAttribute);
        //create referenced symbol attribute and methods
        methodList.addAll(getRefDefinitionMethods(astcdAttribute, referencedSymbolType));
      }
    }
    changedInput.addAllCDMethods(methodList);
    return changedInput;
  }

  /**
   * created dummy attribute to easily create the corresponding getters
   */
  protected List<ASTCDMethod> getRefDefinitionMethods(ASTCDAttribute astcdAttribute, String referencedSymbol) {
    ASTMCType symbolType;
    String referencedNode = referencedSymbol.substring(0, referencedSymbol.lastIndexOf("_symboltable")) +
        AST_PACKAGE + "." + AST_PREFIX + symbolTableService.getSimpleNameFromSymbolName(referencedSymbol);
    if (GeneratorHelper.isListType(astcdAttribute.printType())) {
      //if the attribute is a list
      symbolType = getMCTypeFacade().createListTypeOf(referencedNode);
    } else {
      //if the attribute is mandatory or optional
      symbolType = getMCTypeFacade().createOptionalTypeOf(referencedNode);
    }
    ASTCDAttribute refSymbolAttribute = getCDAttributeFacade().createAttribute(PROTECTED, symbolType, astcdAttribute.getName());
    TransformationHelper.addStereotypeValue(refSymbolAttribute.getModifier(), MC2CDStereotypes.REFERENCED_SYMBOL.toString(), referencedSymbol);
    return accessorDecorator.decorate(refSymbolAttribute);
  }
}
