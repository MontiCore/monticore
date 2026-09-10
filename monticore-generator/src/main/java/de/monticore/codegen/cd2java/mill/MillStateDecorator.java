/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;

/**
 * Creates the [Language]MillState class for a grammar to capture the 
 * static Mill instances of this language and all its super languages.
 */
public class MillStateDecorator extends AbstractCreator<List<ASTCDPackage>, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  public MillStateDecorator(final GlobalExtensionManagement glex,
                            final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(List<ASTCDPackage> packageList) {
    String millStateClassName = symbolTableService.getCDName() + "MillState";

    ASTCDClass stateClass = CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(millStateClassName)
        .build();

    String currentMillFullName = symbolTableService.getMillFullName();
    ASTMCType currentMillType = this.getMCTypeFacade().createQualifiedType(currentMillFullName);
    ASTCDAttribute currentMillAttr = this.getCDAttributeFacade().createAttribute(PUBLIC.build(), currentMillType, "mill");
    stateClass.addCDMember(currentMillAttr);

    List<DiagramSymbol> superSymbolList = symbolTableService.getSuperCDsTransitive();

    for (DiagramSymbol superSymbol : superSymbolList) {
      String superMillFullName = symbolTableService.getMillFullName(superSymbol);
      ASTMCType superMillType = this.getMCTypeFacade().createQualifiedType(superMillFullName);
      // Create a unique variable name for the attribute
      String attrName = StringTransformations.uncapitalize(superSymbol.getName()) + "Mill";
      ASTCDAttribute superMillAttr = this.getCDAttributeFacade().createAttribute(PUBLIC.build(), superMillType, attrName);
      stateClass.addCDMember(superMillAttr);
    }

    return stateClass;
  }
}