package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.List;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.ENCLOSING_SCOPE_VAR;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SYMBOL;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class CommonSymbolInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected final MethodDecorator methodDecorator;

  public CommonSymbolInterfaceDecorator(final GlobalExtensionManagement glex,
                                        final SymbolTableService symbolTableService,
                                        final VisitorService visitorService,
                                        final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    String commonSymbolInterfaceName = symbolTableService.getCommonSymbolInterfaceSimpleName();
    String scopeInterfaceName = symbolTableService.getScopeInterfaceFullName();
    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(commonSymbolInterfaceName)
        .setModifier(PUBLIC.build())
        .addInterface(getMCTypeFacade().createQualifiedType(I_SYMBOL))
        .addCDMethod(createAcceptMethod())
        .addAllCDMethods(createEnclosingScopeMethods(scopeInterfaceName))
        .build();
  }

  protected ASTCDMethod createAcceptMethod() {
    ASTMCQualifiedType symbolVisitorType = getMCTypeFacade().createQualifiedType(visitorService.getSymbolVisitorFullName());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(symbolVisitorType, VISITOR_PREFIX);
    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, ACCEPT_METHOD, parameter);
  }

  protected ASTCDAttribute createEnclosingScopeAttribute(String scopeInterface) {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, scopeInterface, ENCLOSING_SCOPE_VAR);
  }

  protected List<ASTCDMethod> createEnclosingScopeMethods(String scopeInterface) {
    ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute(scopeInterface);
    List<ASTCDMethod> methods = methodDecorator.decorate(enclosingScopeAttribute);
    methods.forEach(m -> m.getModifier().setAbstract(true));
    return methods;
  }
}
