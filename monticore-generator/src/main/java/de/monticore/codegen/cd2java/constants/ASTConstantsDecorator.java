package de.monticore.codegen.cd2java.constants;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.factories.SuperSymbolHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.symboltable.CDSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class ASTConstantsDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDClass> {

  private static final String LITERALS_SUFFIX = "Literals";

  private static final String LANGUAGE = "LANGUAGE";

  private static final String DEFAULT = "DEFAULT";

  private static final String SUPER_GRAMMARS = "superGrammars";

  private static final String AST_CONSTANTS = "ASTConstants";

  private static final String GET_ALL_LANGUAGES = "getAllLanguages";

  public ASTConstantsDecorator(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String grammarName = input.getCDDefinition().getName();
    String className = AST_CONSTANTS + grammarName;
    Optional<ASTCDEnum> literalsEnum = input.getCDDefinition().getCDEnumList().stream()
        .filter(astcdEnum -> astcdEnum.getName().equals(grammarName + LITERALS_SUFFIX))
        .findFirst();
    List<ASTCDEnumConstant> enumConstants = new ArrayList<>();
    if (literalsEnum.isPresent()) {
      enumConstants = literalsEnum.get().getCDEnumConstantList().stream()
          .map(ASTCDEnumConstant::deepClone)
          .collect(Collectors.toList());
    } else {
      for (ASTCDEnum astcdEnum : input.getCDDefinition().getCDEnumList()) {
        enumConstants.addAll(astcdEnum.getCDEnumConstantList().stream()
            .map(ASTCDEnumConstant::deepClone)
            .collect(Collectors.toList()));
      }
    }
    return CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(className)
        .addCDAttribute(getLanguageAttribute(grammarName))
        .addCDAttribute(getDefaultAttribute())
        .addAllCDAttributes(getConstantAttribute(enumConstants))
        .addCDAttribute(getSuperGrammarsAttribute(input))
        .addCDConstructor(getDefaultConstructor(className))
        .addCDMethod(getGetAllLanguagesMethod())
        .build();
  }

  protected ASTCDAttribute getLanguageAttribute(String grammarName) {
    ASTCDAttribute languageAttribute = getCDAttributeFactory().createAttribute(PUBLIC_STATIC_FINAL, String.class, LANGUAGE);
    this.replaceTemplate(VALUE, languageAttribute, new StringHookPoint("\"" + grammarName + "\""));
    return languageAttribute;
  }

  protected List<ASTCDAttribute> getConstantAttribute(List<ASTCDEnumConstant> enumConstants) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (int i = 0; i < enumConstants.size(); i++) {
      ASTCDAttribute attribute = getCDAttributeFactory().createAttribute(PUBLIC_STATIC_FINAL, getCDTypeFactory().createIntType(), enumConstants.get(i).getName());
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + (i + 1)));
      attributeList.add(attribute);
    }
    return attributeList;
  }

  protected ASTCDAttribute getDefaultAttribute() {
    ASTCDAttribute attribute = getCDAttributeFactory().createAttribute(PUBLIC_STATIC_FINAL, getCDTypeFactory().createIntType(), DEFAULT);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= 0"));
    return attribute;
  }

  protected ASTCDAttribute getSuperGrammarsAttribute(ASTCDCompilationUnit compilationUnit) {
    List<CDSymbol> superSymbolList = SuperSymbolHelper.getSuperCDs(compilationUnit);
    List<String> superGrammarNames = superSymbolList.stream().map(CDSymbol::getFullName).map(x -> "\"" + x + "\"").collect(Collectors.toList());
    ASTCDAttribute attribute = getCDAttributeFactory().createAttribute(PUBLIC_STATIC, getCDTypeFactory().createArrayType(String.class, 1), SUPER_GRAMMARS);
    String s = superGrammarNames.stream().reduce((a, b) -> a + ", " + b).get();
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= {" + s + "}"));
    return attribute;
  }

  protected ASTCDConstructor getDefaultConstructor(String className) {
    return getCDConstructorFactory().createConstructor(PUBLIC, className);
  }

  protected ASTCDMethod getGetAllLanguagesMethod() {
    ASTCDMethod method = getCDMethodFactory().createMethod(PUBLIC_STATIC, getCDTypeFactory().createTypeByDefinition("Collection<String>"), GET_ALL_LANGUAGES);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("ast_constants.GetAllLanguages"));
    return method;
  }
}
