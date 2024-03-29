/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd.codegen.CD2JavaTemplates.VALUE;
import static de.monticore.cd.facade.CDModifier.*;

/**
 * creates a list of symbol attributes that are used for the AST class
 */
public class ASTSymbolDecorator extends AbstractCreator<ASTCDType, List<ASTCDAttribute>> {

  protected final SymbolTableService symbolTableService;

  public ASTSymbolDecorator(final GlobalExtensionManagement glex,
                            final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public List<ASTCDAttribute> decorate(final ASTCDType clazz) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();

    String symbolName = "";
    if (symbolTableService.hasSymbolStereotype(clazz.getModifier())) {
      symbolName = symbolTableService.getSymbolFullName(clazz);
      //symbolTableService.get
    } else if (symbolTableService.hasInheritedSymbolStereotype(clazz.getModifier())) {
      symbolName = symbolTableService.getInheritedSymbol(clazz);
    }

    if (!symbolName.isEmpty()) {
      ASTMCType symbolType = this.getMCTypeFacade().createOptionalTypeOf(symbolName);
      attributeList.add(createSymbolAttribute(symbolType));
    }
    return attributeList;
  }

  protected ASTCDAttribute createSymbolAttribute(ASTMCType symbolType) {
    String attributeName = "symbol";
    ASTCDAttribute attribute = this.getCDAttributeFacade().createAttribute(PROTECTED.build(), symbolType, attributeName);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    return attribute;
  }
}
