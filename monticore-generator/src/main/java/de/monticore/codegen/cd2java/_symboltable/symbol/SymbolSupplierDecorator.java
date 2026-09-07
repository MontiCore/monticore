/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlmodifier._ast.ASTModifier;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

/**
 * creates a SymbolLoader class from a grammar
 */
public class SymbolSupplierDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected static final String TEMPLATE_PATH = "_symboltable.symbolsupplier.";
  protected SymbolTableService symbolTableService;

  public SymbolSupplierDecorator(final GlobalExtensionManagement glex,
                                 final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }
  
  @Override
  public ASTCDClass decorate(ASTCDClass symbolInput) {
    String symbolSupplierSimpleName = symbolTableService.getSymbolSupplierSimpleName(symbolInput);
    String scopeInterfaceType = symbolTableService.getScopeInterfaceFullName();
    String symbolFullName = symbolTableService.getSymbolFullName(symbolInput);
    String simpleName = symbolInput.getName();
    ASTModifier modifier = symbolTableService.createModifierPublicModifier(symbolInput.getModifier());
    
    //name and enclosing scope methods do not delegate to the symbol

    ASTCDAttribute nameAttribute = createNameAttribute();
    ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute(scopeInterfaceType);


    return CD4CodeMill.cDClassBuilder()
      .setName(symbolSupplierSimpleName)
      .setModifier(modifier)
      .setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().addInterface(getMCTypeFacade().createBasicGenericTypeOf("de.monticore.symboltable.ISymbolSupplier", symbolTableService.getSymbolFullName(symbolInput))).build())
      .addCDMember(createConstructor(symbolSupplierSimpleName, scopeInterfaceType))
      .addCDMember(nameAttribute)
      .addCDMember(enclosingScopeAttribute)
      .addCDMember(createGetMethod(symbolSupplierSimpleName, symbolFullName, simpleName, scopeInterfaceType))
      .build();
  }

  protected ASTCDConstructor createConstructor(String symbolSupplierClass, String scopeInterfaceType) {
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter enclosingScopeParameter = getCDParameterFacade().createParameter(scopeInterfaceType, ENCLOSING_SCOPE_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolSupplierClass, nameParameter, enclosingScopeParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "ConstructSymbolSupplier"));
    return constructor;
  }

  protected ASTCDAttribute createNameAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), "String", "name");
  }
  
  protected ASTCDAttribute createEnclosingScopeAttribute(String scopeType) {
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), scopeType, "enclosingScope");
  }

  protected ASTCDMethod createGetMethod(String symbolSupplierName, String symbolName, String simpleName, String scopeName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createOptionalTypeOf(symbolName), "get");
    String generatedError1 = symbolTableService.getGeneratedErrorCode("lazySymbolSupply1");
    String generatedError2 = symbolTableService.getGeneratedErrorCode("lazySymbolSupply2");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "GetSymbolSupplier", symbolSupplierName,
      symbolName, simpleName, scopeName, generatedError1, generatedError2));
    return method;
  }


}
