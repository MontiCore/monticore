/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast_emf.ast_class;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast_emf.ast_class.mutatordecorator.EmfMutatorDecorator;
import de.monticore.codegen.cd2java.data.DataDecorator;
import de.monticore.codegen.cd2java.data.DataDecoratorUtil;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.StringTransformations;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.E_DATA_TYPE_E_LIST;
import static de.monticore.codegen.cd2java._ast_emf.EmfConstants.E_OBJECT_CONTAINMENT_E_LIST;

public class DataEmfDecorator extends DataDecorator {

  protected final EmfMutatorDecorator emfMutatorDecorator;

  public DataEmfDecorator(final GlobalExtensionManagement glex,
                          final MethodDecorator methodDecorator,
                          final AbstractService service,
                          final DataDecoratorUtil dataDecoratorUtil,
                          final EmfMutatorDecorator emfMutatorDecorator) {
    super(glex, methodDecorator, service, dataDecoratorUtil);
    this.emfMutatorDecorator = emfMutatorDecorator;
  }

  @Override
  protected void addAttributeDefaultValues(ASTCDAttribute attribute) {
    if (GeneratorHelper.isListType(attribute.printType())) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint(calculateListType(attribute, service.getCDName(), clazzName)));
    } else if (GeneratorHelper.isOptional(attribute)) {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    }
  }

  protected String calculateListType(ASTCDAttribute attribute, String grammarName, String classname) {
    if (attribute.getMCType() instanceof ASTMCBasicGenericType && ((ASTMCBasicGenericType) attribute.getMCType()).getMCTypeArgumentList().size() == 1) {
      String simpleAttributeType = ((ASTMCBasicGenericType) attribute.getMCType()).getMCTypeArgumentList().get(0).getMCTypeOpt().get()
              .printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
      DecorationHelper decorationHelper = new DecorationHelper();
      String listType;
      if (decorationHelper.isListAstNode(attribute)) {
        listType = E_OBJECT_CONTAINMENT_E_LIST;
      } else {
        listType = E_DATA_TYPE_E_LIST;
      }
      return "= new " + listType + "<" + simpleAttributeType + ">(" + simpleAttributeType +
          ".class, this, " + grammarName + "Package." + classname + "_" + StringTransformations.capitalize(attribute.getName()) + ")";
    }
    return "";
  }

  @Override
  protected List<ASTCDMethod> createSetter(List<ASTCDAttribute> attributeList) {
    // have to set className before calling the emfMutatorDecorator
    // because the className information is needed and hard to get otherwise
    this.emfMutatorDecorator.setClassName(clazzName);
    return attributeList.stream()
        .map(emfMutatorDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }
}
