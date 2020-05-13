/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.ast.ASTCNode;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryConstants;
import de.monticore.codegen.cd2java._ast.factory.NodeFactoryService;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;

/**
 * transformation decorator which adds AST class specific properties
 */
public class ASTDecorator extends AbstractTransformer<ASTCDClass> {

  protected final ASTService astService;

  protected final VisitorService visitorService;

  protected final NodeFactoryService nodeFactoryService;

  protected final ASTSymbolDecorator symbolDecorator;

  protected final ASTScopeDecorator scopeDecorator;

  protected final MethodDecorator methodDecorator;

  protected final SymbolTableService symbolTableService;

  public ASTDecorator(final GlobalExtensionManagement glex,
                      final ASTService astService,
                      final VisitorService visitorService,
                      final NodeFactoryService nodeFactoryService,
                      final ASTSymbolDecorator symbolDecorator,
                      final ASTScopeDecorator scopeDecorator,
                      final MethodDecorator methodDecorator,
                      final SymbolTableService symbolTableService) {
    super(glex);
    this.astService = astService;
    this.visitorService = visitorService;
    this.nodeFactoryService = nodeFactoryService;
    this.symbolDecorator = symbolDecorator;
    this.scopeDecorator = scopeDecorator;
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass originalClass, ASTCDClass changedClass) {
    changedClass.addInterface(this.astService.getASTBaseInterface());
    // have to use the changed one here because this one will get the TOP prefix
    changedClass.addCDMethod(createAcceptMethod(changedClass));
    changedClass.addAllCDMethods(createAcceptSuperMethods(originalClass));
    changedClass.addCDMethod(getConstructMethod(originalClass));
    changedClass.addCDMethod(createGetChildrenMethod(originalClass));
    if (!originalClass.isPresentSuperclass()) {
      changedClass.setSuperclass(this.getMCTypeFacade().createQualifiedType(ASTCNode.class));
    }

    List<ASTCDAttribute> symbolAttributes = symbolDecorator.decorate(originalClass);
    addSymbolTableMethods(symbolAttributes, changedClass);

    List<ASTCDAttribute> scopeAttributes = scopeDecorator.decorate(originalClass);
    addSymbolTableMethods(scopeAttributes, changedClass);

    // if a ast has a symbol definition without a name, the getName has to be implemented manually
    // class and getName method are getting abstract
    if (astService.isSymbolWithoutName(originalClass)) {
      changedClass.getModifier().setAbstract(true);
      changedClass.addCDMethod(astService.createGetNameMethod());
    }
    return changedClass;
  }

  /**
   * creates symbol and scope methods and attributes with the help of the ASTSymbolDecorator and ASTScopeDecorator
   */
  protected void addSymbolTableMethods(List<ASTCDAttribute> astcdAttributes, ASTCDClass clazz) {
    for (ASTCDAttribute attribute : astcdAttributes) {
      if (!astService.hasStereotype(attribute.getModifier(), MC2CDStereotypes.INHERITED)) {
        clazz.addCDAttribute(attribute);
        clazz.addAllCDMethods(methodDecorator.decorate(attribute));
      } else {
        String scopeInterfaceType = symbolTableService.getScopeInterfaceFullName();

        methodDecorator.disableTemplates();
        List<ASTCDMethod> methods = methodDecorator.getMutatorDecorator().decorate(attribute);
        String generatedErrorCode = astService.getGeneratedErrorCode(clazz.getName() + attribute.getName());
        methods.forEach(m ->
            this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("_ast.ast_class.symboltable.InheritedSetEnclosingScope", generatedErrorCode,
                MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(m.getCDParameter(0).getMCType()), scopeInterfaceType)));
        methodDecorator.enableTemplates();
        clazz.addAllCDMethods(methods);
      }
    }
  }

  protected ASTCDMethod createAcceptMethod(ASTCDClass astClass) {
    ASTCDParameter visitorParameter = this.getCDParameterFacade().createParameter(this.visitorService.getVisitorType(), VISITOR_PREFIX);
    ASTCDMethod acceptMethod = this.getCDMethodFacade().createMethod(PUBLIC, ASTConstants.ACCEPT_METHOD, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, acceptMethod, new TemplateHookPoint("_ast.ast_class.Accept", astClass));
    return acceptMethod;
  }

  protected ASTCDMethod createGetChildrenMethod(ASTCDClass astClass) {
    ASTMCType astNodeType = getMCTypeFacade().createCollectionTypeOf(ASTConstants.AST_INTERFACE);
    ASTCDMethod getChildrenMethod = this.getCDMethodFacade().createMethod(PUBLIC, astNodeType, ASTConstants.GET_CHILDREN_METHOD);
    this.replaceTemplate(EMPTY_BODY, getChildrenMethod, new TemplateHookPoint("_ast.ast_class.GetChildren", astClass));
    return getChildrenMethod;
  }

  protected List<ASTCDMethod> createAcceptSuperMethods(ASTCDClass astClass) {
    List<ASTCDMethod> result = new ArrayList<>();
    //accept methods for super visitors
    for (ASTMCType superVisitorType : this.visitorService.getAllVisitorTypesInHierarchy()) {
      ASTCDParameter superVisitorParameter = this.getCDParameterFacade().createParameter(superVisitorType, VISITOR_PREFIX);

      ASTCDMethod superAccept = this.getCDMethodFacade().createMethod(PUBLIC, ASTConstants.ACCEPT_METHOD, superVisitorParameter);
      String errorCode = astService.getGeneratedErrorCode(astClass.getName()+
              superVisitorType.printType(new MCFullGenericTypesPrettyPrinter(new IndentPrinter())));
      this.replaceTemplate(EMPTY_BODY, superAccept, new TemplateHookPoint("_ast.ast_class.AcceptSuper",
          this.visitorService.getVisitorFullName(), errorCode, astClass.getName(),
              MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(superVisitorType)));
      result.add(superAccept);
    }
    return result;
  }

  protected ASTCDMethod getConstructMethod(ASTCDClass astClass) {
    ASTCDMethod constructMethod;
    ASTMCType classType = this.getMCTypeFacade().createQualifiedType(astClass.getName());
    if (astClass.isPresentModifier() && astClass.getModifier().isAbstract()) {
      constructMethod = this.getCDMethodFacade().createMethod(PROTECTED_ABSTRACT, classType, ASTConstants.CONSTRUCT_METHOD);
    } else {
      constructMethod = this.getCDMethodFacade().createMethod(PROTECTED, classType, ASTConstants.CONSTRUCT_METHOD);
      this.replaceTemplate(EMPTY_BODY, constructMethod,
          new StringHookPoint("return " + nodeFactoryService.getNodeFactoryFullTypeName() + "." + NodeFactoryConstants.CREATE_METHOD + astClass.getName() + "();"));
    }
    return constructMethod;
  }
}
