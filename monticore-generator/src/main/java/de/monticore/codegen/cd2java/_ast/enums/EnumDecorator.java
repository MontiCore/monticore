/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.enums;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.CONSTANT;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.INT_VALUE;
import static de.monticore.codegen.cd2java.CDModifier.*;

/**
 * creates corresponding AST enums for enum definitions in grammars
 */
public class EnumDecorator extends AbstractCreator<ASTCDEnum, ASTCDEnum> {

  protected final AccessorDecorator accessorDecorator;

  protected final ASTService astService;

  public EnumDecorator(final GlobalExtensionManagement glex,
                       final AccessorDecorator accessorDecorator,
                       final ASTService astService) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.astService = astService;
  }

  @Override
  public ASTCDEnum decorate(final ASTCDEnum input) {
    ASTModifier modifier = input.isPresentModifier() ?
        astService.createModifierPublicModifier(input.getModifier()):
        PUBLIC.build();
    String enumName = input.getName();
    String constantClassName = astService.getASTConstantClassFullName();
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
        .setModifier(modifier)
        .addAllCDEnumConstants(constants)
        .addCDMember(getLiteralsConstructor(enumName))
        .addCDMember(intValueAttribute)
        .addAllCDMembers(intValueMethod)
        .build();
  }

  protected ASTCDAttribute getIntValueAttribute() {
    ASTMCType intType = getMCTypeFacade().createIntType();
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), intType, INT_VALUE);
  }

  protected ASTCDConstructor getLiteralsConstructor(String enumName) {
    ASTMCType intType = getMCTypeFacade().createIntType();
    ASTCDParameter intParameter = getCDParameterFacade().createParameter(intType, INT_VALUE);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PRIVATE.build(), enumName, intParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + INT_VALUE + " = " + INT_VALUE + ";"));
    return constructor;
  }

}
