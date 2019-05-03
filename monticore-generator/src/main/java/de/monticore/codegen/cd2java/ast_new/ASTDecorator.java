package de.monticore.codegen.cd2java.ast_new;

import de.monticore.ast.ASTCNode;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.factory.NodeFactoryService;
import de.monticore.codegen.cd2java.visitor_new.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.ast_new.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java.ast_new.ASTConstants.CONSTRUCT_METHOD;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;


public class ASTDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private static final String VISITOR = "visitor";

  private final ASTService astService;

  private final VisitorService visitorService;

  private final NodeFactoryService nodeFactoryService;

  public ASTDecorator(final GlobalExtensionManagement glex,
      final ASTService astService,
      final VisitorService visitorService,
      final NodeFactoryService nodeFactoryService) {
    super(glex);
    this.astService = astService;
    this.visitorService = visitorService;
    this.nodeFactoryService = nodeFactoryService;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass clazz) {
    clazz.addInterface(this.astService.getASTBaseInterface());
    clazz.addCDMethod(createAcceptMethod(clazz));
    clazz.addAllCDMethods(createAcceptSuperMethods(clazz));
    clazz.addCDMethod(getConstructMethod(clazz));
    if (!clazz.isPresentSuperclass()) {
      clazz.setSuperclass(this.getCDTypeFacade().createSimpleReferenceType(ASTCNode.class));
    }
    return clazz;
  }


  protected ASTCDMethod createAcceptMethod(ASTCDClass astClass) {
    ASTCDParameter visitorParameter = this.getCDParameterFacade().createParameter(this.visitorService.getVisitorType(), VISITOR);
    ASTCDMethod acceptMethod = this.getCDMethodFacade().createMethod(PUBLIC, ACCEPT_METHOD, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, acceptMethod, new TemplateHookPoint("ast_new.Accept", astClass));
    return acceptMethod;
  }

  protected List<ASTCDMethod> createAcceptSuperMethods(ASTCDClass astClass) {
    List<ASTCDMethod> result = new ArrayList<>();
    //accept methods for super visitors
    for (ASTType superVisitorType : this.visitorService.getAllVisitorTypesInHierarchy()) {
      ASTCDParameter superVisitorParameter = this.getCDParameterFacade().createParameter(superVisitorType, VISITOR);

      ASTCDMethod superAccept = this.getCDMethodFacade().createMethod(PUBLIC, ACCEPT_METHOD, superVisitorParameter);
      String errorCode = DecorationHelper.getGeneratedErrorCode(astClass);
      this.replaceTemplate(EMPTY_BODY, superAccept, new TemplateHookPoint("ast_new.AcceptSuper",
          this.visitorService.getVisitorFullTypeName(), errorCode, astClass.getName(), TypesPrinter.printType(superVisitorType)));
      result.add(superAccept);
    }
    return result;
  }

  protected ASTCDMethod getConstructMethod(ASTCDClass astClass) {
    ASTCDMethod constructMethod;
    ASTType classType = this.getCDTypeFacade().createSimpleReferenceType(astClass.getName());
    if(astClass.isPresentModifier() && astClass.getModifier().isAbstract()){
       constructMethod = this.getCDMethodFacade().createMethod(PROTECTED_ABSTRACT, classType, CONSTRUCT_METHOD);
    }else {
       constructMethod = this.getCDMethodFacade().createMethod(PROTECTED, classType, CONSTRUCT_METHOD);
      this.replaceTemplate(EMPTY_BODY, constructMethod, new StringHookPoint(this.nodeFactoryService.getCreateInvocation(astClass)));
    }

    return constructMethod;
  }
}
