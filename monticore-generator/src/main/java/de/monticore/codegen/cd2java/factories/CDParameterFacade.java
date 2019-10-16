/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.factories;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @deprecated will be transfered into CD4A
 * first the deprecation of MCTypeFacade has to be removed, then the CDParameterFacade can be tranfered to CD4A
 * after release of CD4A with CDParameterFacade this class can be removed
 */
@Deprecated
public class CDParameterFacade {

  private static CDParameterFacade cdParameterFacade;

  private CDParameterFacade() {
  }

  public static CDParameterFacade getInstance() {
    if (cdParameterFacade == null) {
      cdParameterFacade = new CDParameterFacade();
    }
    return cdParameterFacade;
  }

  public ASTCDParameter createParameter(final ASTMCType type, final String name) {
    return CD4CodeMill.cDParameterBuilder()
        .setMCType(type)
        .setName(name)
        .build();
  }

  public ASTCDParameter createParameter(final ASTMCType type) {
    return createParameter(type, StringUtils.uncapitalize(MCCollectionTypesHelper.printType(type)));
  }

  public ASTCDParameter createParameter(final Class<?> type, final String name) {
    return createParameter(MCTypeFacade.getInstance().createQualifiedType(type), name);
  }

  public ASTCDParameter createParameter(final Class<?> type) {
    return createParameter(MCTypeFacade.getInstance().createQualifiedType(type), StringUtils.uncapitalize(type.getSimpleName()));
  }

  public ASTCDParameter createParameter(final ASTCDAttribute ast) {
    return createParameter(ast.getMCType().deepClone(), ast.getName());
  }

  public List<ASTCDParameter> createParameters(final ASTCDAttribute... attributes) {
    return createParameters(Arrays.asList(attributes));
  }

  public List<ASTCDParameter> createParameters(final List<ASTCDAttribute> attributes) {
    return attributes.stream()
        .map(this::createParameter)
        .collect(Collectors.toList());
  }
}
