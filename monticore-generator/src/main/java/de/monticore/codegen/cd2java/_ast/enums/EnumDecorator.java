/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.enums;

import com.google.common.collect.Lists;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCD4CodeEnumConstant;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PRIVATE;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.INT_VALUE;

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
    ASTModifier modifier = astService.createModifierPublicModifier(input.getModifier());
    String enumName = input.getName();
    String constantClassName = astService.getASTConstantClassFullName();
    ASTLiteralExpression expr = CD4CodeMill.literalExpressionBuilder()
            .setLiteral(CD4CodeMill.stringLiteralBuilder().setSource(constantClassName).build()).build();
    ASTCDAttribute intValueAttribute = getIntValueAttribute();
    List<ASTCDMethod> intValueMethod = accessorDecorator.decorate(intValueAttribute);
    List<ASTCD4CodeEnumConstant> constants = Lists.newArrayList();
    for (ASTCDEnumConstant enumConstant: input.getCDEnumConstantList()) {
      try {
        constants.add(CD4CodeMill.parser().parse_StringCD4CodeEnumConstant(
                enumConstant.getName() + "(" + constantClassName + "." + enumConstant.getName() + ")").get());
      } catch (IOException e) {
        Log.error("0xA5C09 Cannot decorate enum constant " + enumConstant.getName(), enumConstant.get_SourcePositionStart());
      }
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
