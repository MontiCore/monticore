/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

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
    Optional<ASTCDType> symbolClass = symbolTableService.getTypeWithSymbolInfo(clazz);
    if (symbolClass.isPresent()) {
      ASTMCType symbolType = this.getMCTypeFacade().createOptionalTypeOf(symbolTableService.getSymbolFullName(clazz));
      attributeList.add(createSymbolAttribute(symbolType));
    }
    return attributeList;
  }

  protected ASTCDAttribute createSymbolAttribute(ASTMCType symbolType) {
    String attributeName = "symbol";
    ASTCDAttribute attribute = this.getCDAttributeFacade().createAttribute(PROTECTED, symbolType, attributeName);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    return attribute;
  }
}
