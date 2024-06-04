/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.ast.ASTCNode;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.codegen.cd2java.JavaDoc;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.codegen.CD2JavaTemplatesFix.JAVADOC;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;

/**
 * transformation decorator which adds AST class specific properties
 */
public class ASTDecorator extends AbstractTransformer<ASTCDClass> {

  protected final ASTService astService;

  protected final VisitorService visitorService;

  protected final ASTSymbolDecorator symbolDecorator;

  protected final ASTScopeDecorator scopeDecorator;

  protected final MethodDecorator methodDecorator;

  protected final SymbolTableService symbolTableService;

  public ASTDecorator(final GlobalExtensionManagement glex,
                      final ASTService astService,
                      final VisitorService visitorService,
                      final ASTSymbolDecorator symbolDecorator,
                      final ASTScopeDecorator scopeDecorator,
                      final MethodDecorator methodDecorator,
                      final SymbolTableService symbolTableService) {
    super(glex);
    this.astService = astService;
    this.visitorService = visitorService;
    this.symbolDecorator = symbolDecorator;
    this.scopeDecorator = scopeDecorator;
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass originalClass, ASTCDClass changedClass) {
    if (!changedClass.isPresentCDInterfaceUsage()) {
      changedClass.setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().build());
    }
    changedClass.getCDInterfaceUsage().addInterface(this.astService.getASTBaseInterface());
    // have to use the changed one here because this one will get the TOP prefix
    changedClass.addCDMember(createAcceptTraverserMethod(changedClass));
    changedClass.addAllCDMembers(createAcceptTraverserSuperMethods(originalClass));
    changedClass.addCDMember(getConstructMethod(originalClass));
    if (!originalClass.isPresentCDExtendUsage()) {
      changedClass.setCDExtendUsage(
              CD4CodeMill.cDExtendUsageBuilder().addSuperclass(this.getMCTypeFacade().createQualifiedType(ASTCNode.class)).build());
    }

    List<ASTCDAttribute> symbolAttributes = symbolDecorator.decorate(originalClass);
    addSymbolTableMethods(symbolAttributes, changedClass);

    List<ASTCDAttribute> scopeAttributes = scopeDecorator.decorate(originalClass);
    addSymbolTableMethods(scopeAttributes, changedClass);

    // if a ast has a symbol definition without a name, the getName has to be implemented manually
    // class and getName method are getting abstract
    if (astService.isSymbolWithoutName(originalClass)) {
      changedClass.getModifier().setAbstract(true);
      changedClass.addCDMember(astService.createGetNameMethod());
    }
    CD4C.getInstance().addImport(changedClass, "de.monticore.ast.ASTNode");
    CD4C.getInstance().addImport(changedClass, "de.monticore.ast.ASTCNode");
    CD4C.getInstance().addImport(changedClass, "de.monticore.ast.Comment");
    return changedClass;
  }

  /**
   * creates symbol and scope methods and attributes with the help of the ASTSymbolDecorator and ASTScopeDecorator
   */
  protected void addSymbolTableMethods(List<ASTCDAttribute> astcdAttributes, ASTCDClass clazz) {
    for (ASTCDAttribute attribute : astcdAttributes) {
      if (!astService.hasStereotype(attribute.getModifier(), MC2CDStereotypes.INHERITED)) {
        clazz.addCDMember(attribute);
        clazz.addAllCDMembers(methodDecorator.decorate(attribute));
      } else {
        String scopeInterfaceType = symbolTableService.getScopeInterfaceFullName();

        methodDecorator.disableTemplates();
        List<ASTCDMethod> methods = methodDecorator.getMutatorDecorator().decorate(attribute);
        String errorCode = astService.getGeneratedErrorCode(clazz.getName());
        methods.stream().filter(m -> m.getName().equals("setEnclosingScope")).forEach(m ->
            this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("_ast.ast_class.symboltable.InheritedSetEnclosingScope", errorCode,
                MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(m.getCDParameter(0).getMCType()), scopeInterfaceType)));
        methods.stream().filter(m -> m.getName().equals("setSpannedScope")).forEach(m ->
                this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("_ast.ast_class.symboltable.InheritedSetSpannedScope", errorCode,
                        MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(m.getCDParameter(0).getMCType()), scopeInterfaceType)));
        methodDecorator.enableTemplates();
        clazz.addAllCDMembers(methods);
      }
    }
  }

  protected ASTCDMethod createAcceptTraverserMethod(ASTCDClass astClass) {
    ASTCDParameter visitorParameter = this.getCDParameterFacade().createParameter(this.visitorService.getTraverserInterfaceType(), VISITOR_PREFIX);
    ASTCDMethod acceptMethod = this.getCDMethodFacade().createMethod(PUBLIC.build(), ASTConstants.ACCEPT_METHOD, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, acceptMethod, new TemplateHookPoint("_ast.ast_class.Accept", astClass));
    this.replaceTemplate(JAVADOC, acceptMethod,
            JavaDoc.of("Entry point for the Visitor pattern.",
                    "Cf. MontiCore handbook chapter 8.").asHP());
    return acceptMethod;
  }
  
  protected List<ASTCDMethod> createAcceptTraverserSuperMethods(ASTCDClass astClass) {
    List<ASTCDMethod> result = new ArrayList<>();
    //accept methods for super visitors
    List<ASTMCQualifiedType> l = this.visitorService.getAllTraverserInterfacesTypesInHierarchy();
    l.add(getMCTypeFacade().createQualifiedType(VisitorConstants.ITRAVERSER_FULL_NAME));
    for (ASTMCType superVisitorType : l) {
      ASTCDParameter superVisitorParameter = this.getCDParameterFacade().createParameter(superVisitorType, VISITOR_PREFIX);

      ASTCDMethod superAccept = this.getCDMethodFacade().createMethod(PUBLIC.build(), ASTConstants.ACCEPT_METHOD, superVisitorParameter);
      String errorCode = "0x70000" + astService.getGeneratedErrorCode(astClass.getName()+
              superVisitorType.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
      this.replaceTemplate(EMPTY_BODY, superAccept, new TemplateHookPoint("data.AcceptSuper",
          this.visitorService.getTraverserInterfaceFullName(), errorCode, astClass.getName(),
              MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter().prettyprint(superVisitorType), "AST node"));
      this.replaceTemplate(JAVADOC, superAccept,
              JavaDoc.of("Entry point for the Visitor pattern.",
                      "Cf. MontiCore handbook chapter 8.").asHP());
      result.add(superAccept);
    }
    return result;
  }

  protected ASTCDMethod getConstructMethod(ASTCDClass astClass) {
    ASTCDMethod constructMethod;
    ASTMCType classType = this.getMCTypeFacade().createQualifiedType(astClass.getName());
    if (astClass.getModifier().isAbstract()) {
      constructMethod = this.getCDMethodFacade().createMethod(PROTECTED_ABSTRACT.build(), classType, ASTConstants.CONSTRUCT_METHOD);
    } else {
      constructMethod = this.getCDMethodFacade().createMethod(PROTECTED.build(), classType, ASTConstants.CONSTRUCT_METHOD);
      this.replaceTemplate(EMPTY_BODY, constructMethod,
          new StringHookPoint("return " + astService.getMillFullName() + "."+ StringTransformations.uncapitalize(astService.removeASTPrefix(astClass.getName())) +  BUILDER_SUFFIX + "().uncheckedBuild();"));
    }
    return constructMethod;
  }
}
