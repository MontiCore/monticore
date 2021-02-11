/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.constants;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.*;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java.CDModifier.*;

/**
 * created c class for the generation of the constant class for a grammar
 */
public class ASTConstantsDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  public static final String LITERALS_SUFFIX = "Literals";

  protected static final String LANGUAGE = "LANGUAGE";

  protected static final String DEFAULT = "DEFAULT";

  protected static final String SUPER_GRAMMARS = "superGrammars";

  protected static final String AST_CONSTANTS = "ASTConstants";

  protected static final String GET_ALL_LANGUAGES = "getAllLanguages";

  protected final AbstractService<?> service;

  public ASTConstantsDecorator(final GlobalExtensionManagement glex,
                               final AbstractService abstractService) {
    super(glex);
    this.service = abstractService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDCompilationUnit input) {
    String grammarName = input.getCDDefinition().getName();
    String className = AST_CONSTANTS + grammarName;
    // searches if a Literals Enum is already defined in the input
    Optional<ASTCDEnum> literalsEnum = input.getCDDefinition().getCDEnumsList().stream()
        .filter(astcdEnum -> astcdEnum.getName().equals(grammarName + LITERALS_SUFFIX))
        .findFirst();
    List<ASTCDEnumConstant> enumConstants = new ArrayList<>();
    if (literalsEnum.isPresent()) {
      // if Literals enum is already present use their enumConstants
      enumConstants = literalsEnum.get().getCDEnumConstantList().stream()
          .map(ASTCDEnumConstant::deepClone)
          .collect(Collectors.toList());
    } else {
      // otherwise search for enum constants in all enum definitions
      for (ASTCDEnum astcdEnum : input.getCDDefinition().getCDEnumsList()) {
        enumConstants.addAll(astcdEnum.getCDEnumConstantList().stream()
            .map(ASTCDEnumConstant::deepClone)
            .collect(Collectors.toList()));
      }
    }
    List<DiagramSymbol> superSymbolList = service.getSuperCDsTransitive();
    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(className)
        .addCDMember(getLanguageAttribute(grammarName))
        .addCDMember(getDefaultAttribute())
        .addAllCDMembers(getConstantAttribute(enumConstants))
        .addCDMember(getSuperGrammarsAttribute(superSymbolList))
        .addCDMember(getDefaultConstructor(className))
        .addCDMember(getGetAllLanguagesMethod(superSymbolList))
        .build();
  }

  protected ASTCDAttribute getLanguageAttribute(String grammarName) {
    ASTCDAttribute languageAttribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC_FINAL.build(), String.class, LANGUAGE);
    this.replaceTemplate(VALUE, languageAttribute, new StringHookPoint("= \"" + grammarName + "\""));
    return languageAttribute;
  }

  protected List<ASTCDAttribute> getConstantAttribute(List<ASTCDEnumConstant> enumConstants) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (int i = 0; i < enumConstants.size(); i++) {
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC_FINAL.build(), getMCTypeFacade().createIntType(), enumConstants.get(i).getName());
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + (i + 1)));
      attributeList.add(attribute);
    }
    return attributeList;
  }

  protected ASTCDAttribute getSuperGrammarsAttribute(Collection<DiagramSymbol> superSymbolList) {
    List<String> superGrammarNames = superSymbolList.stream().map(DiagramSymbol::getFullName).map(x -> "\"" + x + "\"").collect(Collectors.toList());
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC.build(), getMCTypeFacade().createArrayType(String.class, 1), SUPER_GRAMMARS);
    if (!superSymbolList.isEmpty()) {
      String s = superGrammarNames.stream().reduce((a, b) -> a + ", " + b).get();
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= {" + s + "}"));
    } else {
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= {}"));
    }
    return attribute;
  }

  protected ASTCDAttribute getDefaultAttribute() {
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC_FINAL.build(), getMCTypeFacade().createIntType(), DEFAULT);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= 0"));
    return attribute;
  }

  protected ASTCDConstructor getDefaultConstructor(String className) {
    return getCDConstructorFacade().createConstructor(PUBLIC.build(), className);
  }

  protected ASTCDMethod getGetAllLanguagesMethod(Collection<DiagramSymbol> superCDs) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(), getMCTypeFacade().createCollectionTypeOf(String.class), GET_ALL_LANGUAGES);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_ast.ast_constants.GetAllLanguages",
        superCDs));
    return method;
  }
}
