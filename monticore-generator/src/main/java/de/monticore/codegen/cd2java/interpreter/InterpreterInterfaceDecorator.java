/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.interpreter;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._visitor.VisitorConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.interpreter.InterpreterConstants.*;

public class InterpreterInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final VisitorService service;

  public InterpreterInterfaceDecorator(GlobalExtensionManagement glex, VisitorService service) {
    super(glex);
    this.service = service;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage visitorPackage = getPackage(input, decoratedCD, VisitorConstants.VISITOR_PACKAGE);
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
    List<ASTCDParameter> parameters = new ArrayList<>();

    for (CDTypeSymbol typeSymbol : service.getAllCDTypes()) {
      parameters.add(CDParameterFacade.getInstance()
          .createParameter(service.createASTFullName(typeSymbol), NODE_PARAMETER));
    }
    parameters.add(CDParameterFacade.getInstance()
        .createParameter(MCTypeFacade.getInstance()
            .createQualifiedType(NODE_TYPE), NODE_PARAMETER));

    ASTMCReturnType returnType = CD4CodeMill.mCReturnTypeBuilder()
        .setMCType(MCTypeFacade.getInstance()
            .createQualifiedType(VALUE_FULLNAME)).build();

    return parameters.stream()
        .map(parameter -> CDMethodFacade.getInstance().createMethod(
            PUBLIC_ABSTRACT.build(), returnType, "interpret", parameter))
        .collect(Collectors.toList());
  }

  public ASTCDExtendUsage getExtendUsage() {
    ASTCDExtendUsage extendUsage = CDExtendUsageFacade.getInstance().createCDExtendUsage(MODELINTERPRETER_FULLNAME);
    for (DiagramSymbol symbol : service.getSuperCDsTransitive()) {
      extendUsage.addSuperclass(service.getInterpreterInterfaceType(symbol));
    }
    return extendUsage;
  }

}
