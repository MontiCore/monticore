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
import static de.monticore.codegen.cd2java.visitor_new.VisitorConstants.*;

public class VisitorDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDInterface> {

  private static final String ASTNODE = "de.monticore.ast.ASTNode";

  private final VisitorService visitorService;

  public VisitorDecorator(final GlobalExtensionManagement glex, VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(final ASTCDCompilationUnit compilationUnit) {
    ASTCDDefinition astcdDefinition = compilationUnit.getCDDefinition();
    ASTType visitorType = this.visitorService.getVisitorType();

    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(this.visitorService.getVisitorSimpleTypeName())
        .setModifier(PUBLIC.build())
        .addCDMethod(addGetRealThisMethods(visitorType))
        .addCDMethod(addSetRealThisMethods(visitorType))
        .addCDMethod(addVisitASTNodeMethods())
        .addCDMethod(addEndVisitASTNodeMethods())
        .addAllCDMethods(addClassVisitorMethods(astcdDefinition.getCDClassList()))
        .addAllCDMethods(addInterfaceVisitorMethods(astcdDefinition.getCDInterfaceList()))
        .addAllCDMethods(addEnumVisitorMethods(astcdDefinition.getCDEnumList()))
            .build();
  }

  protected ASTCDMethod addGetRealThisMethods(ASTType visitorType) {
    ASTCDMethod getRealThisMethod = this.getCDMethodFactory().createMethod(PUBLIC, visitorType, GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint("return this;"));
    return getRealThisMethod;
  }

  protected ASTCDMethod addSetRealThisMethods(ASTType visitorType) {
    ASTCDParameter visitorParameter = getCDParameterFactory().createParameter(visitorType, "realThis");
    ASTCDMethod getRealThisMethod = this.getCDMethodFactory().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint("    throw new UnsupportedOperationException(\"0xA7011x709 The setter for realThis is not implemented. You might want to implement a wrapper class to allow setting/getting realThis.\");\n"));
    return getRealThisMethod;
  }

  protected ASTCDMethod addVisitASTNodeMethods() {
    ASTType astNodeType = getCDTypeFactory().createTypeByDefinition(ASTNODE);
    return getVisitorMethod(VISIT, astNodeType);
  }

  protected ASTCDMethod addEndVisitASTNodeMethods() {
    ASTType astNodeType = getCDTypeFactory().createTypeByDefinition(ASTNODE);
    return getVisitorMethod(END_VISIT, astNodeType);
  }

  protected List<ASTCDMethod> addClassVisitorMethods(List<ASTCDClass> astcdClassList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      boolean doTraverse = !(astcdClass.isPresentModifier() && astcdClass.getModifier().isAbstract());
      ASTType classType = getCDTypeFactory().createTypeByDefinition(astcdClass.getName());
      visitorMethods.add(addVisitMethod(classType));
      visitorMethods.add(addEndVisitMethod(classType));
      visitorMethods.add(addTraversMethod(classType, astcdClass));
      visitorMethods.add(addHandleMethod(classType, doTraverse));
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> addEnumVisitorMethods(List<ASTCDEnum> astcdEnumList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDEnum astcdEnum : astcdEnumList) {
      ASTType enumType = getCDTypeFactory().createTypeByDefinition(astcdEnum.getName());
      visitorMethods.add(addVisitMethod(enumType));
      visitorMethods.add(addEndVisitMethod(enumType));
      visitorMethods.add(addHandleMethod(enumType, false));
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> addInterfaceVisitorMethods(List<ASTCDInterface> astcdInterfaceList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDInterface astcdInterface : astcdInterfaceList) {
      ASTType interfaceType = getCDTypeFactory().createTypeByDefinition(astcdInterface.getName());
      visitorMethods.add(addVisitMethod(interfaceType));
      visitorMethods.add(addEndVisitMethod(interfaceType));
      visitorMethods.add(addHandleMethod(interfaceType, false));
    }
    return visitorMethods;
  }

  protected ASTCDMethod addVisitMethod(ASTType astType) {
    return getVisitorMethod(VISIT, astType);
  }

  protected ASTCDMethod addEndVisitMethod(ASTType astType) {
    return getVisitorMethod(END_VISIT, astType);
  }

  protected ASTCDMethod addHandleMethod(ASTType astType, boolean traverse) {
    ASTCDMethod handleMethod = getVisitorMethod(HANDLE, astType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint("visitor_new.Handle", traverse));
    return handleMethod;
  }

  protected ASTCDMethod addTraversMethod(ASTType astType, ASTCDClass astcdClass) {
    ASTCDMethod traverseMethod = getVisitorMethod(TRAVERSE, astType);
    this.replaceTemplate(EMPTY_BODY, traverseMethod, new TemplateHookPoint("visitor_new.Traverse", astcdClass));
    return traverseMethod;
  }

  protected ASTCDMethod getVisitorMethod(String methodType, ASTType nodeType) {
    ASTCDParameter visitorParameter = getCDParameterFactory().createParameter(nodeType, "node");
    return this.getCDMethodFactory().createMethod(PUBLIC, methodType, visitorParameter);
  }
}
