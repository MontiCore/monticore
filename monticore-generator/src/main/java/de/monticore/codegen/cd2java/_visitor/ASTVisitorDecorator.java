/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class ASTVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {


  private final VisitorService visitorService;

  public ASTVisitorDecorator(final GlobalExtensionManagement glex,
                             final VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit ast) {
    ASTCDCompilationUnit compilationUnit = visitorService.calculateCDTypeNamesWithPackage(ast);
    ASTMCType visitorType = this.visitorService.getVisitorType();

    ASTCDInterface symbolVisitorInterface = CD4AnalysisMill.cDInterfaceBuilder()
        .setName(this.visitorService.getVisitorSimpleTypeName())
        .addAllInterfaces(this.visitorService.getAllVisitorTypesInHierarchy())
        .setModifier(PUBLIC.build())
        .addCDMethod(addGetRealThisMethods(visitorType))
        .addCDMethod(addSetRealThisMethods(visitorType))
        .addCDMethod(addEndVisitASTNodeMethods())
        .addCDMethod(addVisitASTNodeMethods())
        .build();

    // add visitor methods, but no double signatures
    List<ASTCDMethod> classMethods = addClassVisitorMethods(compilationUnit.getCDDefinition().getCDClassList());
    for (ASTCDMethod classMethod : classMethods) {
      if (!visitorService.isMethodAlreadyDefined(classMethod, symbolVisitorInterface.getCDMethodList())) {
        symbolVisitorInterface.addCDMethod(classMethod);
      }
    }
    List<ASTCDMethod> interfaceMethods = addInterfaceVisitorMethods(compilationUnit.getCDDefinition().getCDInterfaceList());
    for (ASTCDMethod interfaceMethod : interfaceMethods) {
      if (!visitorService.isMethodAlreadyDefined(interfaceMethod, symbolVisitorInterface.getCDMethodList())) {
        symbolVisitorInterface.addCDMethod(interfaceMethod);
      }
    }
    List<ASTCDMethod> enumMethods = addEnumVisitorMethods(compilationUnit.getCDDefinition().getCDEnumList(),
        compilationUnit.getCDDefinition().getName());
    for (ASTCDMethod enumMethod : enumMethods) {
      if (!visitorService.isMethodAlreadyDefined(enumMethod, symbolVisitorInterface.getCDMethodList())) {
        symbolVisitorInterface.addCDMethod(enumMethod);
      }
    }

    return symbolVisitorInterface;
  }

  protected ASTCDMethod addVisitASTNodeMethods() {
    ASTMCType astNodeType = getCDTypeFacade().createTypeByDefinition(AST_INTERFACE);
    return visitorService.getVisitorMethod(VisitorConstants.VISIT, astNodeType);
  }

  protected ASTCDMethod addEndVisitASTNodeMethods() {
    ASTMCType astNodeType = getCDTypeFacade().createTypeByDefinition(AST_INTERFACE);
    return visitorService.getVisitorMethod(VisitorConstants.END_VISIT, astNodeType);
  }

  protected ASTCDMethod addGetRealThisMethods(ASTMCType visitorType) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(visitorType).build();
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, returnType, GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint("return this;"));
    return getRealThisMethod;
  }

  protected ASTCDMethod addSetRealThisMethods(ASTMCType visitorType) {
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, "realThis");
    ASTCDMethod setRealThis = this.getCDMethodFacade().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, setRealThis, new StringHookPoint(
        "    throw new UnsupportedOperationException(\"0xA7011x709 The setter for realThis is " +
            "not implemented. You might want to implement a wrapper class to allow setting/getting realThis.\");\n"));
    return setRealThis;
  }

  protected List<ASTCDMethod> addClassVisitorMethods(List<ASTCDClass> astcdClassList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      boolean doTraverse = !(astcdClass.isPresentModifier() && astcdClass.getModifier().isAbstract());
      ASTMCType classType = getCDTypeFacade().createTypeByDefinition(astcdClass.getName());
      visitorMethods.add(addVisitMethod(classType));
      visitorMethods.add(addEndVisitMethod(classType));
      visitorMethods.add(addHandleMethod(classType, doTraverse));
      if (doTraverse){
        visitorMethods.add(addTraversMethod(classType, astcdClass));
      }
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> addEnumVisitorMethods(List<ASTCDEnum> astcdEnumList, String definitionName) {
    // no traverse method and not for literals Enum
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDEnum astcdEnum : astcdEnumList) {
      if (!visitorService.isLiteralsEnum(astcdEnum, definitionName)) {
        ASTMCType enumType = getCDTypeFacade().createTypeByDefinition(astcdEnum.getName());
        visitorMethods.add(addVisitMethod(enumType));
        visitorMethods.add(addEndVisitMethod(enumType));
        visitorMethods.add(addHandleMethod(enumType, false));
      }
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> addInterfaceVisitorMethods(List<ASTCDInterface> astcdInterfaceList) {
    // no traverse method
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDInterface astcdInterface : astcdInterfaceList) {
      ASTMCType interfaceType = getCDTypeFacade().createTypeByDefinition(astcdInterface.getName());
      visitorMethods.add(addVisitMethod(interfaceType));
      visitorMethods.add(addEndVisitMethod(interfaceType));
      visitorMethods.add(addHandleMethod(interfaceType, false));
    }
    return visitorMethods;
  }

  protected ASTCDMethod addVisitMethod(ASTMCType astType) {
    return visitorService.getVisitorMethod(VISIT, astType);
  }

  protected ASTCDMethod addEndVisitMethod(ASTMCType astType) {
    return visitorService.getVisitorMethod(END_VISIT, astType);
  }

  protected ASTCDMethod addHandleMethod(ASTMCType astType, boolean traverse) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, astType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLE_TEMPLATE, traverse));
    return handleMethod;
  }

  protected ASTCDMethod addTraversMethod(ASTMCType astType, ASTCDClass astcdClass) {
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, astType);
    this.replaceTemplate(EMPTY_BODY, traverseMethod, new TemplateHookPoint(TRAVERSE_TEMPLATE, astcdClass));
    return traverseMethod;
  }
}
