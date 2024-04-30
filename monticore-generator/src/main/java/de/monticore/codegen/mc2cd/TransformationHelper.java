/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._parser.CD4CodeParser;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.RegExpBuilder;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.AdditionalAttributeSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public final class TransformationHelper {

  public static final String DEFAULT_FILE_EXTENSION = ".java";

  public static final String AST_PREFIX = "AST";

  public static final String LIST_SUFFIX = "s";

  public static final int STAR = -1;

  protected static List<String> reservedCdNames = Arrays.asList(
      // CD4A
      "derived",
      "association",
      "composition",
      // Common.mc4
      "local",
      "readonly");

  private TransformationHelper() {
    // noninstantiable
  }

  public static String getClassProdName(ASTClassProd classProd) {
    return classProd.getName();
  }

  public static String typeToString(ASTMCType type) {
    if (type instanceof ASTMCGenericType) {
      return ((ASTMCGenericType) type).printWithoutTypeArguments();
    } else if (type instanceof ASTMCArrayType) {
      return ((ASTMCArrayType) type).printTypeWithoutBrackets();
    }
    return Grammar_WithConceptsMill.prettyPrint(type, false);
  }

  public static String simpleName(ASTMCType type) {
    String name;
    if (type instanceof ASTMCGenericType) {
      name = ((ASTMCGenericType) type).printWithoutTypeArguments();
    } else if (type instanceof ASTMCArrayType) {
      name = ((ASTMCArrayType) type).printTypeWithoutBrackets();
    } else {
      name = Grammar_WithConceptsMill.prettyPrint(type, false);
    }
    return Names.getSimpleName(name);
  }

  public static Optional<String> getUsageName(ASTNode root,
                                              ASTNode ancestor) {
      if (ancestor instanceof ASTConstantGroup && ((ASTConstantGroup) ancestor).isPresentUsageName()) {
        return Optional.of(((ASTConstantGroup) ancestor).getUsageName());
      }
      if (ancestor instanceof ASTNonTerminal && ((ASTNonTerminal) ancestor).isPresentUsageName()) {
        return Optional.of(((ASTNonTerminal) ancestor).getUsageName());
      }
      if (ancestor instanceof ASTNonTerminalSeparator) {
        return Optional.of(((ASTNonTerminalSeparator) ancestor).getUsageName());
      }
      if (ancestor instanceof ASTITerminal && ((ASTITerminal) ancestor).isPresentUsageName()) {
        return Optional.of(((ASTITerminal) ancestor).getUsageName());
      }
      if (ancestor instanceof ASTAdditionalAttribute && ((ASTAdditionalAttribute) ancestor).isPresentName()) {
        return Optional.of(((ASTAdditionalAttribute) ancestor).getName());
      }

    return Optional.empty();
  }

  public static Optional<String> getName(ASTNode node) {
    if (node instanceof ASTNonTerminal) {
      return Optional.of(((ASTNonTerminal) node)
          .getName());
    }
    if (node instanceof ASTConstant) {
      return Optional.of(((ASTConstant) node)
          .getName());
    }
    if (node instanceof ASTNonTerminalSeparator) {
      return Optional.of(((ASTNonTerminalSeparator) node)
          .getName());
    }
    if (node instanceof ASTTerminal) {
      return Optional.of(((ASTTerminal) node).getName());
    }
    if (node instanceof ASTAdditionalAttribute && ((ASTAdditionalAttribute) node).isPresentName()) {
      return Optional.of(((ASTAdditionalAttribute) node).getName());
    }
    return Optional.empty();
  }

  public static ASTCDParameter createParameter(String typeName,
                                               String parameterName) {
    return CD4CodeMill.cDParameterBuilder().
            setName(parameterName).
            setMCType(TransformationHelper.createType(typeName)).
            build();
  }

  public static ASTModifier createPublicModifier() {
    return CD4CodeMill.modifierBuilder().setPublic(true).build();
  }

  public static ASTMCGenericType createType(
      String typeName, String generics) {
    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTMCGenericType> optType = null;
    try {
      optType = parser.parse_StringMCGenericType(typeName + "<" + generics + ">");
    } catch (IOException e) {
      Log.error("0xA4103 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static ASTMCType createType(String typeName) {
    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTMCType> optType = null;
    try {
      optType = parser.parse_StringMCType(typeName);
    } catch (IOException e) {
      Log.error("0xA4104 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static ASTMCReturnType createReturnType(String typeName) {
    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTMCReturnType> optType = null;
    try {
      optType = parser.parse_StringMCReturnType(typeName);
    } catch (IOException e) {
      Log.error("0xA4105 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static ASTMCObjectType createObjectType(String typeName) {
    CD4CodeParser parser = CD4CodeMill.parser();
    Optional<ASTMCObjectType> optType = null;
    try {
      optType = parser.parse_StringMCObjectType(typeName);
    } catch (IOException e) {
      Log.error("0xA4106 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static String createConvertType(ASTLexProd a) {
    if (!a.isPresentVariable()) {
      return "String";
    }
    String variable = a.getVariable();
    String name = a.getName();

    // default functions
    if (a.getTypeList() == null || a.getTypeList().isEmpty()) {

      if ("int".equals(variable) || "boolean".equals(variable) || "char".equals(variable)
              || "float".equals(variable) || "double".equals(variable)
              || "long".equals(variable) || "byte".equals(variable) || "short".equals(variable)) {
        return variable;
      } else if ("card".equals(variable)) {
        return "int";
      } else {
        Log.warn(
                "0xA1032 No function for " + a.getVariable() + " registered, will treat it as string!");
        return "String";
      }
    }
    // specific function
    else {
      return Names.getQualifiedName(a.getTypeList());
    }
  }

  public static String getPackageName(ProdSymbol symbol) {
    // return grammar.getName().toLowerCase() + AST_DOT_PACKAGE_SUFFIX_DOT;
    return getGrammarName(symbol) + ".";
  }

  public static String getPackageName(
      ASTCDCompilationUnit cdCompilationUnit) {
    String packageName = Names
        .getQualifiedName(cdCompilationUnit.getCDPackageList());
    if (!packageName.isEmpty()) {
      packageName = packageName + ".";
    }
    return packageName + cdCompilationUnit.getCDDefinition().getName() + ".";
  }

  public static Set<String> getAllGrammarConstants(ASTMCGrammar grammar) {
    Set<String> constants = new LinkedHashSet<>();
    MCGrammarSymbol grammarSymbol = grammar.getSymbol();
    Preconditions.checkState(grammarSymbol != null);
    for (RuleComponentSymbol component : grammarSymbol.getProds().stream()
        .flatMap(p -> p.getProdComponents().stream()).collect(Collectors.toList())) {
      if (component.isIsConstantGroup()) {
        constants.addAll(component.getSubProdsList());
      }
    }
    for (ProdSymbol type : grammarSymbol.getProds()) {
      if (type.isIsEnum() && type.isPresentAstNode()
          && type.getAstNode() instanceof ASTEnumProd) {
        for (ASTConstant enumValue : ((ASTEnumProd) type.getAstNode()).getConstantList()) {
          String humanName = enumValue.isPresentUsageName()
              ? enumValue.getUsageName()
              : enumValue.getName();
          constants.add(humanName);
        }
      }
    }
    return constants;
  }

  /**
   * Get the corresponding CD for MC grammar if exists.
   * 
   * @param globalScope The global scope in which the CD is resolved
   * @param ast The input grammar, providing the qualified name
   * @return The CD if resolved from global scope, Optional.empty() otherwise
   */
  public static Optional<ASTCDCompilationUnit> getCDforGrammar(ICD4AnalysisGlobalScope globalScope,
                                                               ASTMCGrammar ast) {
    return getCDforGrammar(globalScope, ast, "");
  }
  
  /**
   * Get the corresponding CD for MC grammar if exists.
   * 
   * @param globalScope The global scope in which the CD is resolved
   * @param ast The input grammar, providing the qualified name
   * @param nameSuffix A suffix to distinguish between different CDs
   * @return The CD if resolved from global scope, Optional.empty() otherwise
   */
  public static Optional<ASTCDCompilationUnit> getCDforGrammar(ICD4AnalysisGlobalScope globalScope,
                                                               ASTMCGrammar ast, String nameSuffix) {
    final String qualifiedCDName = Names.getQualifiedName(ast.getPackageList(), ast.getName() + nameSuffix);

    Optional<DiagramSymbol> cdSymbol = globalScope.resolveDiagramDown(
        qualifiedCDName);

    if (cdSymbol.isPresent() && cdSymbol.get().getEnclosingScope().isPresentAstNode()) {
      Log.debug("Got existed symbol table for " + cdSymbol.get().getFullName(),
          TransformationHelper.class.getName());
      return Optional
          .of((ASTCDCompilationUnit) cdSymbol.get().getEnclosingScope().getAstNode());
    }

    return Optional.empty();
  }

  public static String getQualifiedTypeNameAndMarkIfExternal(ASTMCType ruleReference,
                                                             ASTMCGrammar grammar, ASTCDType cdType) {

    Optional<ProdSymbol> typeSymbol = resolveAstRuleType(grammar, ruleReference);

    String qualifiedRuleName = getQualifiedAstName(
        typeSymbol, ruleReference);

    if (!typeSymbol.isPresent()) {
      addStereoType(cdType,
          MC2CDStereotypes.EXTERNAL_TYPE.toString(), qualifiedRuleName);
    }

    return qualifiedRuleName;
  }

  public static Optional<ProdSymbol> resolveAstRuleType(ASTMCGrammar node, ASTMCType type) {
    String simpleName = Names.getSimpleName(typeToString(type));
    if (!simpleName.startsWith(AST_PREFIX)) {
      return Optional.empty();
    }
    Optional<ProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper.resolveRule(node,
        simpleName
            .substring(AST_PREFIX.length()));
    if (ruleSymbol.isPresent()) {
      return ruleSymbol;
    }
    return Optional.empty();
  }


  public static String getGrammarName(ProdSymbol rule) {
    return Names.getQualifier(rule.getFullName());
  }

  public static String getGrammarNameAsPackage(ProdSymbol rule) {
    return getGrammarName(rule) + ".";
  }

  public static String getQualifiedAstName(Optional<ProdSymbol> typeSymbol, ASTMCType type) {
    if (typeSymbol.isPresent()) {
      return Names.getQualifier(typeSymbol.get().getFullName()) + "." + AST_PREFIX + typeSymbol.get().getName();
    } else {
      return Grammar_WithConceptsMill.prettyPrint(type, false);
    }
  }

  public static void addStereoType(ASTCDType type, String stereotypeName,
                                   String stereotypeValue) {
    addStereotypeValue(type.getModifier(),
        stereotypeName, stereotypeValue);
  }

  public static void addStereoType(ASTCDType type, String stereotypeName,
                                   String stereotypeValue, boolean multiple) {
    if (!multiple) {
      if (type.getModifier().isPresentStereotype()
              && type.getModifier().getStereotype().getValuesList().stream().anyMatch(v -> v.getName().equals(stereotypeName))) {
        return;
      }
    }
    addStereotypeValue(type.getModifier(), stereotypeName, stereotypeValue);
  }

  public static void addStereoType(ASTCDType type, String stereotypeName) {
    addStereotypeValue(type.getModifier(), stereotypeName);
  }

  public static void addStereoType(ASTCDDefinition type, String stereotypeName) {
     addStereotypeValue(type.getModifier(), stereotypeName);
  }

  public static void addStereoType(ASTCDAttribute attribute,
                                   String stereotypeName,
                                   String stereotypeValue) {
    addStereotypeValue(attribute.getModifier(), stereotypeName, stereotypeValue);
  }

  public static void addStereoType(ASTCDDefinition type, String stereotypeName,
                                   String stereotypeValue) {
    addStereotypeValue(type.getModifier(), stereotypeName, stereotypeValue);
  }

  public static void addStereotypeValue(ASTModifier astModifier,
                                        String stereotypeName,
                                        String stereotypeValue) {
    if (!astModifier.isPresentStereotype()) {
      astModifier.setStereotype(CD4CodeMill.stereotypeBuilder().uncheckedBuild());
    }
    List<ASTStereoValue> stereoValueList = astModifier.getStereotype()
        .getValuesList();
    ASTStereoValue stereoValue = CD4CodeMill.stereoValueBuilder().
            setName(stereotypeName).
            setText(CD4CodeMill.stringLiteralBuilder().setSource(stereotypeValue).build()).uncheckedBuild();
    stereoValueList.add(stereoValue);
  }

  public static void addStereotypeValue(ASTModifier astModifier,
                                        String stereotypeName) {
    if (!astModifier.isPresentStereotype()) {
      astModifier.setStereotype(CD4CodeMill.stereotypeBuilder().uncheckedBuild());
    }
    List<ASTStereoValue> stereoValueList = astModifier.getStereotype()
            .getValuesList();
    ASTStereoValue stereoValue = CD4CodeMill.stereoValueBuilder().
            setName(stereotypeName).uncheckedBuild();
    stereoValueList.add(stereoValue);
  }

  /**
   * Checks whether the given attribute is a collection type written as String
   * (e.g., List<...>).
   *
   * @param attribute The input attribute
   * @return true if the input attribute is a collection type, false otherwise
   */
  public static boolean isCollectionType(ASTCDAttribute attribute) {
    String type = attribute.printType();
    if (type.startsWith("Collection<") || type.startsWith("List<") || type.startsWith("Set<") || type.startsWith("java.util.Collection<") || type.startsWith("java.util.List<") || type.startsWith("java.util.Set<")) {
      return true;
    }
    return false;
  }

  /**
   * Computes the simple type of an attribute from a collection type.
   *
   * @param attribute The input attribute
   * @return The simple type contained by a collection
   */
  public static String getSimpleTypeFromCollection(ASTCDAttribute attribute) {
    String simpleType = attribute.printType();
    if (simpleType.startsWith("Collection<")) {
      simpleType = simpleType.replaceFirst("Collection<", "");
      simpleType = simpleType.substring(0, simpleType.length() - 1);
    } else if (simpleType.startsWith("List<")) {
      simpleType = simpleType.replaceFirst("List<", "");
      simpleType = simpleType.substring(0, simpleType.length() - 1);
    } else if (simpleType.startsWith("Set<")) {
      simpleType = simpleType.replaceFirst("Set<", "");
      simpleType = simpleType.substring(0, simpleType.length() - 1);
    } else if (simpleType.startsWith("java.util.Collection<")) {
      simpleType = simpleType.replaceFirst("java.util.Collection<", "");
      simpleType = simpleType.substring(0, simpleType.length() - 1);
    } else if (simpleType.startsWith("java.util.List<")) {
      simpleType = simpleType.replaceFirst("java.util.List<", "");
      simpleType = simpleType.substring(0, simpleType.length() - 1);
    } else if (simpleType.startsWith("java.util.Set<")) {
      simpleType = simpleType.replaceFirst("java.util.Set<", "");
      simpleType = simpleType.substring(0, simpleType.length() - 1);
    }
    return Names.getSimpleName(simpleType);
  }

  public static String getJavaAndCdConformName(String name) {
    Log.errorIfNull(name);
    return getCdLanguageConformName(getJavaConformName(name));
  }

  public static String getCdLanguageConformName(String name) {
    if (reservedCdNames.contains(name)) {
      return (JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED + name).intern();
    }
    return name.intern();
  }

  public static String getJavaConformName(String name) {
    return JavaNamesHelper.javaAttribute(name);
  }


  /**
   * @return the super productions defined in all super grammars (including
   * transitive super grammars)
   */
  public static List<ASTProd> getAllSuperProds(ASTProd astNode) {
    List<ASTProd> directSuperRules = getDirectSuperProds(astNode);
    List<ASTProd> allSuperRules = new ArrayList<>();
    for (ASTProd superRule : directSuperRules) {
      allSuperRules.addAll(getAllSuperProds(superRule));
    }
    allSuperRules.addAll(directSuperRules);
    return allSuperRules;
  }

  /**
   * @return the super productions defined in direct super grammars
   */
  public static List<ASTProd> getDirectSuperProds(ASTProd astNode) {
    if (astNode instanceof ASTClassProd) {
      List<ASTProd> directSuperProds = resolveRuleReferences(
          ((ASTClassProd) astNode).getSuperRuleList(), astNode);
      directSuperProds.addAll(
          resolveRuleReferences(((ASTClassProd) astNode).getSuperInterfaceRuleList(), astNode));
      return directSuperProds;
    } else if (astNode instanceof ASTInterfaceProd) {
      return resolveRuleReferences(((ASTInterfaceProd) astNode).getSuperInterfaceRuleList(), astNode);
    }
    return Collections.emptyList();
  }

  /**
   * @return the production definitions of B & C in "A extends B, C"
   */
  public static List<ASTProd> resolveRuleReferences(List<ASTRuleReference> ruleReferences,
                                                    ASTProd nodeWithSymbol) {
    List<ASTProd> superRuleNodes = new ArrayList<>();
    for (ASTRuleReference superRule : ruleReferences) {
      Optional<ProdSymbol> symbol = nodeWithSymbol.getEnclosingScope().resolveProd(superRule.getName());
      if (symbol.isPresent() && symbol.get().isPresentAstNode()) {
        superRuleNodes.add(symbol.get().getAstNode());
      }
    }
    return superRuleNodes;
  }

  public static List<ASTRuleComponent> getAllComponents(ASTGrammarNode node) {
    CollectRuleComponents cv = new CollectRuleComponents();
    Grammar_WithConceptsTraverser traverser = Grammar_WithConceptsMill.traverser();
    traverser.add4Grammar(cv);
    node.accept(traverser);
    return cv.getRuleComponents();
  }

  protected static class CollectRuleComponents implements GrammarVisitor2 {

    public List<ASTRuleComponent> ruleComponentList = Lists.newArrayList();

    public List<ASTRuleComponent> getRuleComponents() {
       return ruleComponentList;
    }

    @Override
    public void visit(ASTNonTerminal node) {
      ruleComponentList.add(node);
    }

    @Override
    public void visit(ASTTerminal node) {
      ruleComponentList.add(node);
    }

    @Override
    public void visit(ASTKeyTerminal node) {
      ruleComponentList.add(node);
    }

    @Override
    public void visit(ASTTokenTerminal node) {
      ruleComponentList.add(node);
    }

    @Override
    public void visit(ASTConstantGroup node) {
      ruleComponentList.add(node);
    }
  }
  
  public static boolean isConstGroupIterated(RuleComponentSymbol prodComponent) {
    Preconditions.checkArgument(prodComponent.isIsConstantGroup());
    Collection<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    for (RuleComponentSymbol comp: prodComponent.getEnclosingScope().resolveRuleComponentDownMany(prodComponent.getName())) {
      comp.getSubProdsList().stream().forEach((p -> set.add(p)));
    }
    return set.size() > 1;
  }
  
  public static String getQualifiedName(ProdSymbol symbol) {
    if (!symbol.isPresentAstNode()) {
      return "UNKNOWN_TYPE";
    }
    if (symbol.isIsLexerProd()) {
      return getLexType(symbol.getAstNode());
    }
    if (symbol.isIsEnum()) {
      return MCGrammarSymbolTableHelper.getQualifiedName(symbol.getAstNode(), symbol, "AST", "");
    }
    return MCGrammarSymbolTableHelper.getQualifiedName(symbol.getAstNode(), symbol, "AST", "");
  }
  
  protected static String getLexType(ASTNode node) {
    if (node instanceof ASTLexProd) {
      return createConvertType((ASTLexProd) node);
    }
    if (node instanceof ASTLexActionOrPredicate) {
      return "String";
    }
    return "UNKNOWN_TYPE";
  }
  
  public static Optional<Integer> getMax(AdditionalAttributeSymbol attrSymbol) {
    if (!attrSymbol.isPresentAstNode()) {
      return Optional.empty();
    }
    return getMax(attrSymbol.getAstNode());
  }
  
  public static Optional<Integer> getMax(ASTAdditionalAttribute ast) {
    if (ast.isPresentCard()
        && ast.getCard().isPresentMax()) {
      String max = ast.getCard().getMax();
      if ("*".equals(max)) {
        return Optional.of(STAR);
      }
      else {
        try {
          int x = Integer.parseInt(max);
          return Optional.of(x);
        } catch (NumberFormatException ignored) {
          Log.warn("0xA0140 Failed to parse an integer value of max of ASTAdditionalAttribute "
              + ast.getName() + " from string " + max);
        }
      }
    }
    return Optional.empty();
  }
  
  public static boolean isFragment(ASTProd astNode) {
    return !(astNode instanceof ASTLexProd)
        || ((ASTLexProd) astNode).isFragment();
  }
  
  public static Optional<Pattern> calculateLexPattern(MCGrammarSymbol grammar,
      ASTLexProd lexNode) {
    Optional<Pattern> ret = Optional.empty();
    
    final String lexString = getLexString(grammar, lexNode);
    try {
      if ("[[]".equals(lexString)) {
        return Optional.ofNullable(Pattern.compile("[\\[]"));
      } else {
        return Optional.ofNullable(Pattern.compile(lexString));
      }
    } catch (PatternSyntaxException e) {
      Log.error("0xA0913 Internal error with pattern handling for lex rules. Pattern: " + lexString
          + "\n", e);
    }
    return ret;
  }
  
  protected static String getLexString(MCGrammarSymbol grammar, ASTLexProd lexNode) {
    StringBuilder builder = new StringBuilder();
    RegExpBuilder regExp = new RegExpBuilder(builder, grammar);
    Grammar_WithConceptsTraverser traverser = Grammar_WithConceptsMill.traverser();
    traverser.add4Grammar(regExp);
    traverser.setGrammarHandler(regExp);
    lexNode.accept(traverser);
    return builder.toString();
  }
  
}
