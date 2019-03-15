package de.monticore.codegen.cd2java.visitor_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class VisitorDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDInterface> {

  private ASTCDCompilationUnit compilationUnit;

  private static final String VISITOR_SUFFIX = "Visitor";

  private static final String GET_REAL_THIS = "getRealThis";

  private static final String SET_REAL_THIS = "setRealThis";

  private static final String VISIT = "visit";

  private static final String END_VISIT = "endVisit";

  private static final String HANDLE = "handle";

  private static final String TRAVERSE = "traverse";

  private static final String ASTNODE = "de.monticore.ast.ASTNode";

  private static final String AST_PACKAGE = "._ast.";

  public VisitorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    this.compilationUnit = input;
    ASTCDDefinition astcdDefinition = compilationUnit.getCDDefinition();
    String visitorInterfaceName = astcdDefinition.getName() + VISITOR_SUFFIX;
    ASTType visitoryType = this.getCDTypeFactory().createSimpleReferenceType(visitorInterfaceName);

    String astPath = astcdDefinition.getName().toLowerCase() + AST_PACKAGE;
    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(visitorInterfaceName)
        .setModifier(PUBLIC.build())
        .addCDMethod(addGetRealThisMethods(visitoryType))
        .addCDMethod(addSetRealThisMethods(visitoryType))
        .addCDMethod(addVisitASTNodeMethods())
        .addCDMethod(addEndVisitASTNodeMethods())
        .addAllCDMethods(addClassVisitorMethods(astcdDefinition.getCDClassList(), astPath))
        .addAllCDMethods(addInterfaceVisitorMethods(astcdDefinition.getCDInterfaceList(), astPath))
        .addAllCDMethods(addEnumVisitorMethods(astcdDefinition.getCDEnumList(), astPath))
        .build();
  }

  private ASTCDMethod addGetRealThisMethods(ASTType visitoryType) {
    ASTCDMethod getRealThisMethod = this.getCDMethodFactory().createMethod(PUBLIC, visitoryType, GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint("return this;"));
    return getRealThisMethod;
  }

  private ASTCDMethod addSetRealThisMethods(ASTType visitoryType) {
    ASTCDParameter visitorParameter = getCDParameterFactory().createParameter(visitoryType, "realThis");
    ASTCDMethod getRealThisMethod = this.getCDMethodFactory().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint("    throw new UnsupportedOperationException(\"0xA7011x709 The setter for realThis is not implemented. You might want to implement a wrapper class to allow setting/getting realThis.\");\n"));
    return getRealThisMethod;
  }

  private ASTCDMethod addVisitASTNodeMethods() {
    ASTType astNodeType = getCDTypeFactory().createTypeByDefinition(ASTNODE);
    return getVisitorMethod(VISIT, astNodeType);
  }

  private ASTCDMethod addEndVisitASTNodeMethods() {
    ASTType astNodeType = getCDTypeFactory().createTypeByDefinition(ASTNODE);
    return getVisitorMethod(END_VISIT, astNodeType);
  }

  private List<ASTCDMethod> addClassVisitorMethods(List<ASTCDClass> astcdClassList, String path) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      boolean doTraverse = !(astcdClass.isPresentModifier() && astcdClass.getModifier().isAbstract());
      ASTType classType = getCDTypeFactory().createTypeByDefinition(path + astcdClass.getName());
      visitorMethods.add(addVisitMethod(classType));
      visitorMethods.add(addEndVisitMethod(classType));
      visitorMethods.add(addTraversMethod(classType, astcdClass));
      visitorMethods.add(addHandleMethod(classType, doTraverse));
    }
    return visitorMethods;
  }

  private List<ASTCDMethod> addEnumVisitorMethods(List<ASTCDEnum> astcdEnumList, String path) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDEnum astcdEnum : astcdEnumList) {
      ASTType enumType = getCDTypeFactory().createTypeByDefinition(path + astcdEnum.getName());
      visitorMethods.add(addVisitMethod(enumType));
      visitorMethods.add(addEndVisitMethod(enumType));
      visitorMethods.add(addHandleMethod(enumType, false));
    }
    return visitorMethods;
  }

  private List<ASTCDMethod> addInterfaceVisitorMethods(List<ASTCDInterface> astcdInterfaceList, String path) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDInterface astcdInterface : astcdInterfaceList) {
      ASTType interfaceType = getCDTypeFactory().createTypeByDefinition(path + astcdInterface.getName());
      visitorMethods.add(addVisitMethod(interfaceType));
      visitorMethods.add(addEndVisitMethod(interfaceType));
      visitorMethods.add(addHandleMethod(interfaceType, false));
    }
    return visitorMethods;
  }

  private ASTCDMethod addVisitMethod(ASTType astType) {
    return getVisitorMethod(VISIT, astType);
  }

  private ASTCDMethod addEndVisitMethod(ASTType astType) {
    return getVisitorMethod(END_VISIT, astType);
  }

  private ASTCDMethod addHandleMethod(ASTType astType, boolean traverse) {
    ASTCDMethod handleMethod = getVisitorMethod(HANDLE, astType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint("visitor_new.Handle", traverse));
    return handleMethod;
  }

  private ASTCDMethod addTraversMethod(ASTType astType, ASTCDClass astcdClass) {
    ASTCDMethod traverseMethod = getVisitorMethod(TRAVERSE, astType);
    this.replaceTemplate(EMPTY_BODY, traverseMethod, new TemplateHookPoint("visitor_new.Traverse", astcdClass));
    return traverseMethod;
  }

  private ASTCDMethod getVisitorMethod(String methodType, ASTType nodeType) {
    ASTCDParameter visitorParameter = getCDParameterFactory().createParameter(nodeType, "node");
    return this.getCDMethodFactory().createMethod(PUBLIC, methodType, visitorParameter);
  }
}
