/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.interpreter;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._visitor.VisitorConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.interpreter.InterpreterConstants.*;

public class InterpreterInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final VisitorService visitorService;

  public InterpreterInterfaceDecorator(GlobalExtensionManagement glex, VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage visitorPackage = getPackage(input, decoratedCD, VisitorConstants.VISITOR_PACKAGE);
    visitorPackage.addCDElement(decorate(input));
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    String interfaceName = getInterfaceName(visitorService.getCDName());

    return  CD4CodeMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(interfaceName)
        .setCDExtendUsage(getExtendUsage())
        .build();
  }

  public ASTCDExtendUsage getExtendUsage() {
    List<String> types = new ArrayList<>();
    ASTCDExtendUsage extendUsage = CDExtendUsageFacade.getInstance().createCDExtendUsage(MODELINTERPRETER_FULLNAME);
    for (DiagramSymbol symbol: visitorService.getSuperCDsTransitive()) {
      extendUsage.addSuperclass(visitorService.getInterpreterInterfaceType(symbol));
    }
    return extendUsage;
  }

  public String getInterfaceName(String name) {
    return "I" + name + INTERPRETER_NAME_SUFFIX;
  }
}
