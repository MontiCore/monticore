package de.monticore.codegen.cd2java.ast_new.reference.referencedDefinition;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.ast_new.reference.referencedDefinition.referencedDefinitionMethodDecorator.ReferencedDefinitionAccessorDecorator;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;

public class ASTReferencedDefinitionDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  public static final String DEFINITION = "Definition";

  private final ReferencedDefinitionAccessorDecorator accessorDecorator;

  private final SymbolTableService symbolTableService;

  public ASTReferencedDefinitionDecorator(final GlobalExtensionManagement glex, final ReferencedDefinitionAccessorDecorator accessorDecorator,
      final SymbolTableService symbolTableService) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass input) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : input.getCDAttributeList()) {
      if (symbolTableService.isReferencedSymbol(astcdAttribute)) {
        String referencedSymbolType = symbolTableService.getReferencedSymbolTypeName(astcdAttribute);
        //create referenced symbol attribute and methods
        methodList.addAll(getRefDefinitionMethods(astcdAttribute, referencedSymbolType));
      }
    }
    input.addAllCDMethods(methodList);
    input.addAllCDAttributes(attributeList);
    return input;
  }

  protected List<ASTCDMethod> getRefDefinitionMethods(ASTCDAttribute astcdAttribute, String referencedSymbol) {
    ASTType symbolType;
    String referencedNode = referencedSymbol.substring(0, referencedSymbol.lastIndexOf("_symboltable")) + GeneratorHelper.AST_PACKAGE_SUFFIX_DOT + GeneratorHelper.AST_PREFIX + symbolTableService.getSimpleSymbolName(referencedSymbol);
    if (GeneratorHelper.isListType(astcdAttribute.printType())) {
      //if the attribute is a list
      symbolType = getCDTypeFacade().createListTypeOf(referencedNode);
    } else {
      //if the attribute is mandatory or optional
      symbolType = getCDTypeFacade().createOptionalTypeOf(referencedNode);
    }
    ASTCDAttribute refSymbolAttribute = getCDAttributeFacade().createAttribute(PRIVATE, symbolType, astcdAttribute.getName());
    refSymbolAttribute.getModifier().setStereotype(astcdAttribute.getModifier().getStereotype().deepClone());
    return accessorDecorator.decorate(refSymbolAttribute);
  }
}
