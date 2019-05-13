package de.monticore.codegen.cd2java.enums;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.ast_new.ASTService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.CONSTANT;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

public class EnumDecorator extends AbstractDecorator<ASTCDEnum, ASTCDEnum> {

  private static final String INT_VALUE = "intValue";

  private final AccessorDecorator accessorDecorator;

  private final ASTService astService;

  public EnumDecorator(GlobalExtensionManagement glex, final AccessorDecorator accessorDecorator, final ASTService astService) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.astService = astService;
  }

  @Override
  public ASTCDEnum decorate(ASTCDEnum input) {
    String enumName = input.getName();
    String constantClassName = astService.getASTConstantClassName();
    ASTCDAttribute intValueAttribute = getIntValueAttribute();
    List<ASTCDMethod> intValueMethod = accessorDecorator.decorate(intValueAttribute);
    List<ASTCDEnumConstant> constants = input.getCDEnumConstantList().stream()
        .map(ASTCDEnumConstant::deepClone)
        .collect(Collectors.toList());
    for (ASTCDEnumConstant constant : constants) {
      this.replaceTemplate(EMPTY_BODY, constant, new TemplateHookPoint(CONSTANT, constant.getName(), constantClassName));
    }
    return CD4AnalysisMill.cDEnumBuilder()
        .setName(enumName)
        .addAllCDEnumConstants(constants)
        .addCDConstructor(getLiteralsConstructor(enumName))
        .addCDAttribute(intValueAttribute)
        .addAllCDMethods(intValueMethod)
        .build();
  }

  protected ASTCDAttribute getIntValueAttribute() {
    ASTType intType = getCDTypeFacade().createIntType();
    return getCDAttributeFacade().createAttribute(PROTECTED, intType, INT_VALUE);
  }

  protected ASTCDConstructor getLiteralsConstructor(String enumName) {
    ASTType intType = getCDTypeFacade().createIntType();
    ASTCDParameter intParameter = getCDParameterFacade().createParameter(intType, INT_VALUE);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PRIVATE.build(), enumName, intParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + INT_VALUE + " = " + INT_VALUE + ";"));
    return constructor;
  }

}
