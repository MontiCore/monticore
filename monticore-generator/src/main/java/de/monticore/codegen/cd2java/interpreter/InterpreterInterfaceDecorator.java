/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.interpreter;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._visitor.VisitorConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolTOP;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.interpreter.InterpreterConstants.*;

public class InterpreterInterfaceDecorator
    extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final VisitorService service;

  public InterpreterInterfaceDecorator(
      GlobalExtensionManagement glex,
      VisitorService service) {
    super(glex);
    this.service = service;
  }

  public void decorate(ASTCDCompilationUnit input,
                       ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage visitorPackage = getPackage(
        input, decoratedCD, VisitorConstants.VISITOR_PACKAGE);
    visitorPackage.addCDElement(decorate(input));
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    return CD4CodeMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(service.getInterpreterInterfaceSimpleName())
        .setCDExtendUsage(getExtendUsage())
        .addAllCDMembers(createInterpretMethods())
        .build();
  }

  private List<ASTCDMethod> createInterpretMethods() {
    ASTMCReturnType returnType = CD4CodeMill.mCReturnTypeBuilder()
        .setMCType(mcTypeFacade.createQualifiedType(VALUE_FULLNAME))
        .build();

    return service.getAllCDTypes()
        .stream()
        .filter(OOTypeSymbolTOP::isIsClass)
        .map(service::createASTFullName)
        .map(name -> cdParameterFacade.createParameter(name, NODE_PARAMETER))
        .map(parameter -> cdMethodFacade.createMethod(
            PUBLIC_ABSTRACT.build(), returnType,
            INTERPRET_METHOD_NAME, parameter))
        .collect(Collectors.toList());
  }

  public ASTCDExtendUsage getExtendUsage() {
    List<ASTMCObjectType> types = service.getSuperCDsDirect()
        .stream()
        .map(service::getInterpreterInterfaceType)
        .collect(Collectors.toList());

    types.add(mcTypeFacade.createQualifiedType(MODELINTERPRETER_FULLNAME));

    return CDExtendUsageFacade.getInstance().createCDExtendUsage(types);
  }

}
