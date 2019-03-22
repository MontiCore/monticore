package de.monticore.codegen.cd2java.cocos_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.factories.SuperSymbolHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class CoCoCheckerDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDClass> {

  private static final String COCO_PACKAGE = "._cocos.";

  private static final String COCO_SUFFIX = "CoCo";

  private static final String COCOS_SUFFIX = "CoCos";

  private static final String COCO_CHECKER_SUFFIX = "CoCoChecker";

  private static final String REAL_THIS = "realThis";

  private static final String ADD_CHECKER = "addChecker";

  private static final String ADD_COCO = "addCoCo";

  private static final String VISITOR_SUFFIX = "Visitor";

  private static final String VISIT = "visit";

  private static final String AST_PACKAGE = "._ast.";

  private static final String AST_PREFIX = "AST";

  private final MethodDecorator methodDecorator;

  public CoCoCheckerDecorator(final GlobalExtensionManagement glex, final MethodDecorator methodDecorator) {
    super(glex);
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit compilationUnit) {
    ASTType cocoCheckerType = getCDTypeFactory().createSimpleReferenceType(compilationUnit.getCDDefinition().getName() + COCO_CHECKER_SUFFIX);
    ASTType visitorType = getCDTypeFactory().createSimpleReferenceType(compilationUnit.getCDDefinition().getName() + VISITOR_SUFFIX);

    ASTCDAttribute realThisAttribute = getCDAttributeFactory().createAttribute(PROTECTED, visitorType, REAL_THIS);
    List<ASTCDMethod> realThisMethods = methodDecorator.decorate(realThisAttribute);

    ASTCDConstructor constructor = this.getCDConstructorFactory().createConstructor(PRIVATE, TypesHelper.printType(cocoCheckerType));
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this."  + REAL_THIS + " = (" + TypesHelper.printType(visitorType) + ") this;"));

    ASTCDClass cocoChecker = CD4AnalysisMill.cDClassBuilder()
        .setName(TypesHelper.printType(cocoCheckerType))
        .addCDAttribute(realThisAttribute)
        .addCDConstructor(constructor)
        .addAllCDMethods(realThisMethods)
        .build();

    List<CDSymbol> allCDs = new ArrayList<>();
    allCDs.add((CDSymbol) compilationUnit.getCDDefinition().getSymbol());
    allCDs.addAll(SuperSymbolHelper.getSuperCDs(compilationUnit));
    for (CDSymbol cdSymbol : allCDs) {
      ASTType checkerType = getCDTypeFactory().createSimpleReferenceType(cdSymbol.getPackageName() + COCO_PACKAGE + cdSymbol.getName() + COCO_CHECKER_SUFFIX);
      String checkerName = TypesHelper.printType(checkerType).replaceAll("\\.", "_");

      cocoChecker.addCDAttribute(createCheckerAttribute(checkerType, checkerName));
      cocoChecker.addCDMethod(createAddCheckerMethod(checkerType, checkerName));

      for (CDTypeSymbol cdTypeSymbol : cdSymbol.getTypes()) {
        if (!cdTypeSymbol.isClass() && !cdTypeSymbol.isInterface()) {
          continue;
        }
        ASTType cocoType = getCDTypeFactory().createSimpleReferenceType(cdTypeSymbol.getPackageName() + COCO_PACKAGE + cdTypeSymbol.getName() + COCO_SUFFIX);
        ASTType astType = getCDTypeFactory().createSimpleReferenceType(cdTypeSymbol.getPackageName() + AST_PACKAGE + AST_PREFIX + cdTypeSymbol.getName());
        String cocoCollectionName = TypesHelper.printType(astType).replaceAll("\\.", "_") + COCOS_SUFFIX;

        cocoChecker.addCDAttribute(createCoCoCollectionAttribute(cocoType, cocoCollectionName));
        cocoChecker.addCDMethod(createAddCoCoMethod(cocoType, checkerType));
        cocoChecker.addCDMethod(createVisitMethod(astType));
      }
    }

    return cocoChecker;
  }

  protected ASTCDAttribute createCheckerAttribute(ASTType checkerType, String checkerName) {
    ASTType checkerListType = getCDTypeFactory().createListTypeOf(checkerType);
    ASTCDAttribute checker = getCDAttributeFactory().createAttribute(PRIVATE, checkerListType, checkerName);
    this.replaceTemplate(VALUE, checker, new StringHookPoint("= new ArrayList<>()"));
    return checker;
  }

  protected ASTCDMethod createAddCheckerMethod(ASTType checkerType, String checkerName) {
    String parameterName = "checker";
    ASTCDParameter parameter = getCDParameterFactory().createParameter(checkerType, parameterName);
    ASTCDMethod addCheckerMethod = getCDMethodFactory().createMethod(PUBLIC, ADD_CHECKER, parameter);
    this.replaceTemplate(EMPTY_BODY, addCheckerMethod, new StringHookPoint("this." + checkerName + ".add(" + parameterName + ");"));
    return addCheckerMethod;
  }

  protected ASTCDAttribute createCoCoCollectionAttribute(ASTType cocoType, String cocoCollectionName) {
    ASTType cocoCollectionType = getCDTypeFactory().createCollectionTypeOf(cocoType);
    ASTCDAttribute cocoCollectionAttribute = getCDAttributeFactory().createAttribute(PRIVATE, cocoCollectionType, cocoCollectionName);
    this.replaceTemplate(VALUE, cocoCollectionAttribute, new StringHookPoint("= new LinkedHashSet<>()"));
    return cocoCollectionAttribute;
  }

  protected ASTCDMethod createAddCoCoMethod(ASTType cocoType, ASTType checkerType) {
    String parameterName = "coco";
    ASTCDParameter parameter = getCDParameterFactory().createParameter(cocoType, parameterName);
    ASTCDMethod addCoCoMethod = getCDMethodFactory().createMethod(PUBLIC, checkerType, ADD_COCO, parameter);
//    this.replaceTemplate(EMPTY_BODY, addCoCoMethod, new TemplateHookPoint("cocos_new.AddCoCo"));
    return addCoCoMethod;
  }

  protected ASTCDMethod createVisitMethod(ASTType astType) {
    String parameterName = "node";
    ASTCDParameter parameter = getCDParameterFactory().createParameter(astType, parameterName);
    ASTCDMethod visitMethod = getCDMethodFactory().createMethod(PUBLIC, VISIT, parameter);
    //this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint("cocos_new.Visit"));
    return visitMethod;
  }
}
