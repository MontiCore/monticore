package de.monticore.codegen.cd2java.ast_new;

import de.monticore.ast.ASTCNode;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;


public class ASTDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private static final String AST_PREFIX = "AST";

  private static final String ACCEPT_METHOD = "accept";

  private static final String CONSTRUCT_METHOD = "_construct";

  private static final String VISITOR_PACKAGE = "._visitor.";

  private static final String VISITOR_SUFFIX = "Visitor";

  private static final String VISITOR_PREFIX = "visitor";

  private final ASTCDCompilationUnit compilationUnit;

  public ASTDecorator(final GlobalExtensionManagement glex, final ASTCDCompilationUnit compilationUnit) {
    super(glex);
    this.compilationUnit = compilationUnit;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass clazz) {
    clazz.setName(AST_PREFIX + clazz.getName());

    if (!clazz.isPresentSuperclass()) {
      clazz.setSuperclass(this.getCDTypeFactory().createSimpleReferenceType(ASTCNode.class));
    }

    clazz.addInterface(this.getCDTypeFactory().createReferenceTypeByDefinition(AST_PREFIX + compilationUnit.getCDDefinition().getName() + "Node"));

    String visitorPackage = (String.join(".", compilationUnit.getPackageList()) + "." + compilationUnit.getCDDefinition().getName() + VISITOR_PACKAGE).toLowerCase();
    ASTType visitorType = this.getCDTypeFactory().createSimpleReferenceType(visitorPackage + compilationUnit.getCDDefinition().getName() + VISITOR_SUFFIX);

    clazz.addCDMethod(createAcceptMethod(clazz, visitorType));
    clazz.addAllCDMethods(createAcceptSuperMethods(clazz, visitorType));
    clazz.addCDMethod(getConstructMethod(clazz));

    return clazz;
  }

  private ASTCDMethod createAcceptMethod(ASTCDClass astClass, ASTType visitorType) {
    ASTCDParameter visitorParameter = this.getCDParameterFactory().createParameter(visitorType, VISITOR_PREFIX);
    ASTCDMethod acceptMethod = this.getCDMethodFactory().createMethod(PUBLIC, ACCEPT_METHOD, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, acceptMethod, new TemplateHookPoint("ast_new.Accept", astClass));
    return acceptMethod;
  }

  private List<ASTCDMethod> createAcceptSuperMethods(ASTCDClass astClass, ASTType visitorType) {
    List<ASTCDMethod> result = new ArrayList<>();
    //accept methods for super grammar visitors
    for (CDSymbol superSymbol : SuperSymbolHelper.getSuperCDs(compilationUnit)) {
      ASTType superVisitorType = this.getCDTypeFactory().createSimpleReferenceType(superSymbol.getFullName().toLowerCase() + VISITOR_PACKAGE + superSymbol.getName() + VISITOR_SUFFIX);
      ASTCDParameter superVisitorParameter = this.getCDParameterFactory().createParameter(superVisitorType, VISITOR_PREFIX);

      ASTCDMethod superAccept = this.getCDMethodFactory().createMethod(PUBLIC, ACCEPT_METHOD, superVisitorParameter);
      String errorCode = DecorationHelper.getGeneratedErrorCode(astClass);
      this.replaceTemplate(EMPTY_BODY, superAccept, new TemplateHookPoint("ast_new.AcceptSuper",
          TypesPrinter.printType(visitorType), errorCode, astClass.getName(), TypesPrinter.printType(superVisitorType)));
      result.add(superAccept);
    }
    return result;
  }

  private ASTCDMethod getConstructMethod(ASTCDClass astClass) {
    ASTType classType = this.getCDTypeFactory().createSimpleReferenceType(astClass.getName());
    ASTCDMethod constructMethod = this.getCDMethodFactory().createMethod(PROTECTED, classType, CONSTRUCT_METHOD);
    this.replaceTemplate(EMPTY_BODY, constructMethod, new StringHookPoint(
        "return " + compilationUnit.getCDDefinition().getName() + "NodeFactory.create" + astClass.getName() + "();\n"));
    return constructMethod;
  }
}
