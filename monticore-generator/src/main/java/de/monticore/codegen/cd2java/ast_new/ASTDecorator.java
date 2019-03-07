package de.monticore.codegen.cd2java.ast_new;

import de.monticore.ast.ASTCNode;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;


public class ASTDecorator implements Decorator<ASTCDClass, ASTCDClass> {

  private static final String AST_PREFIX = "AST";

  private static final String ACCEPT_METHOD = "accept";

  private static final String CONSTRUCT_METHOD = "_construct";

  private static final String VISITOR_PACKAGE = "._visitor.";

  private static final String VISITOR_SUFFIX = "Visitor";

  private static final String VISITOR_PREFIX = "visitor";

  private final GlobalExtensionManagement glex;

  private final CDTypeFactory cdTypeFactory;

  private final CDParameterFactory cdParameterFactory;

  private final CDMethodFactory cdMethodFactory;

  private final ASTCDCompilationUnit compilationUnit;

  public ASTDecorator(final GlobalExtensionManagement glex, final ASTCDCompilationUnit compilationUnit) {
    this.glex = glex;
    this.compilationUnit = compilationUnit;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdParameterFactory = CDParameterFactory.getInstance();
    this.cdMethodFactory = CDMethodFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(ASTCDClass clazz) {
    ASTCDClass astClass = clazz.deepClone();
    astClass.setName(AST_PREFIX + clazz.getName());

    if (!astClass.isPresentSuperclass()) {
      astClass.setSuperclass(cdTypeFactory.createSimpleReferenceType(ASTCNode.class));
    }

    astClass.addInterface(cdTypeFactory.createReferenceTypeByDefinition(AST_PREFIX + compilationUnit.getCDDefinition().getName() + "Node"));

    String visitorPackage = (String.join(".", compilationUnit.getPackageList()) + "." + compilationUnit.getCDDefinition().getName() + VISITOR_PACKAGE).toLowerCase();
    ASTType visitorType = this.cdTypeFactory.createSimpleReferenceType(visitorPackage + compilationUnit.getCDDefinition().getName() + VISITOR_SUFFIX);

    astClass.addCDMethod(createAcceptMethod(astClass, visitorType));
    astClass.addAllCDMethods(createAcceptSuperMethods(astClass, visitorType));
    astClass.addCDMethod(getConstructMethod(astClass));

    return astClass;
  }

  private ASTCDMethod createAcceptMethod(ASTCDClass astClass, ASTType visitorType) {
    ASTCDParameter visitorParameter = this.cdParameterFactory.createParameter(visitorType, VISITOR_PREFIX);
    ASTCDMethod acceptMethod = this.cdMethodFactory.createMethod(PUBLIC, ACCEPT_METHOD, visitorParameter);
    this.glex.replaceTemplate(EMPTY_BODY, acceptMethod, new TemplateHookPoint("ast_new.Accept", astClass));
    return acceptMethod;
  }

  private List<ASTCDMethod> createAcceptSuperMethods(ASTCDClass astClass, ASTType visitorType) {
    List<ASTCDMethod> result = new ArrayList<>();
    //accept methods for super grammar visitors
    for (CDSymbol superSymbol : SuperSymbolHelper.getSuperCDs(compilationUnit)) {
      ASTType superVisitorType = this.cdTypeFactory.createSimpleReferenceType(superSymbol.getFullName().toLowerCase() + VISITOR_PACKAGE + superSymbol.getName() + VISITOR_SUFFIX);
      ASTCDParameter superVisitorParameter = this.cdParameterFactory.createParameter(superVisitorType, VISITOR_PREFIX);

      ASTCDMethod superAccept = this.cdMethodFactory.createMethod(PUBLIC, ACCEPT_METHOD, superVisitorParameter);
      String errorCode = DecorationHelper.getGeneratedErrorCode(astClass);
      this.glex.replaceTemplate(EMPTY_BODY, superAccept, new TemplateHookPoint("ast_new.AcceptSuper",
          TypesPrinter.printType(visitorType), errorCode, astClass.getName(), TypesPrinter.printType(superVisitorType)));
      result.add(superAccept);
    }
    return result;
  }

  private ASTCDMethod getConstructMethod(ASTCDClass astClass) {
    ASTType classType = this.cdTypeFactory.createSimpleReferenceType(astClass.getName());
    ASTCDMethod constructMethod = this.cdMethodFactory.createMethod(PROTECTED, classType, CONSTRUCT_METHOD);
    this.glex.replaceTemplate(EMPTY_BODY, constructMethod, new StringHookPoint(
        "return " + compilationUnit.getCDDefinition().getName() + "NodeFactory.create" + astClass.getName() + "();\n"));
    return constructMethod;
  }
}
