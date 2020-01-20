/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.monticore.ast.ASTNode;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.RegExpBuilder;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.*;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.Util;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newLinkedHashSet;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;

public class MCGrammarSymbolTableHelper {

  public static final String AST_DOT_PACKAGE_SUFFIX_DOT = "._ast.";

  public static Optional<ProdSymbol> resolveRule(ASTMCGrammar astNode, String name) {
    if (astNode.isPresentSymbol()) {
      return astNode.getSymbol().getProdWithInherited(name);
    }
    return Optional.empty();
  }

  public static Optional<ProdSymbol> resolveRuleInSupersOnly(ASTClassProd astNode, String name) {
    Optional<MCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode.getEnclosingScope());
    Stream<MCGrammarSymbol> superGrammars = grammarSymbol
        .map(symbol -> Util.preOrder(symbol, MCGrammarSymbol::getSuperGrammarSymbols)
            .stream())
        .orElse(Stream.empty()).skip(1);
    return superGrammars.map(superGrammar -> superGrammar.getProd(name))
        .filter(mcRuleSymbol -> mcRuleSymbol.isPresent())
        .map(Optional::get)
        .findFirst();
  }

  public static Optional<MCGrammarSymbol> getMCGrammarSymbol(IGrammarScope scope) {
    boolean exist = true;
    while (exist) {
      if (scope.isPresentSpanningSymbol() && scope.getSpanningSymbol() instanceof MCGrammarSymbol) {
        return Optional.of((MCGrammarSymbol) scope.getSpanningSymbol());
      }
      if (scope instanceof Grammar_WithConceptsGlobalScope) {
        exist = false;
      } else {
        scope = scope.getEnclosingScope();
      }
    }
    return Optional.empty();
  }

  public static Optional<ProdSymbol> getEnclosingRule(ASTRuleComponent astNode) {
    return getAllScopes(astNode.getEnclosingScope()).stream()
        .filter(IScope::isPresentSpanningSymbol)
        .map(IScope::getSpanningSymbol)
        .filter(ProdSymbol.class::isInstance)
        .map(ProdSymbol.class::cast)
        .findFirst();
  }

  public static Optional<ProdSymbol> getEnclosingRule(RuleComponentSymbol prod) {
    if (prod.getEnclosingScope().isPresentSpanningSymbol() && prod.getEnclosingScope().getSpanningSymbol() instanceof ProdSymbol) {
      return Optional.of((ProdSymbol) prod.getEnclosingScope().getSpanningSymbol());
    }
    return Optional.empty();
  }

  /**
   * Returns a set of all super grammars of the given grammar (transitively)
   *
   * @return
   */
  public static Set<MCGrammarSymbol> getAllSuperGrammars(
      MCGrammarSymbol grammarSymbol) {
    Set<MCGrammarSymbol> allSuperGrammars = new LinkedHashSet<>();
    Set<MCGrammarSymbol> tmpList = new LinkedHashSet<>();
    allSuperGrammars.addAll(grammarSymbol.getSuperGrammarSymbols());
    boolean modified = false;
    do {
      for (MCGrammarSymbol curGrammar : allSuperGrammars) {
        tmpList.addAll(curGrammar.getSuperGrammarSymbols());
      }
      modified = allSuperGrammars.addAll(tmpList);
      tmpList.clear();
    } while (modified);

    return ImmutableSet.copyOf(allSuperGrammars);
  }

  public static boolean isFragment(ASTProd astNode) {
    return !(astNode instanceof ASTLexProd)
        || ((ASTLexProd) astNode).isFragment();
  }

  private static Set<IGrammarScope> getAllScopes(IGrammarScope scope) {
    Set<IGrammarScope> ret = Sets.newHashSet(scope);
    // TODO
    // return getAllSubSymbols(astNode).stream()
    // .map(Symbol::getEnclosingScope)
    // .flatMap(
    // scope -> listTillNull(scope, childScope ->
    // childScope.getEnclosingScope().orElse(null))
    // .stream())
    // .collect(Collectors.toSet());
    return ret;

  }

  private static String getLexString(MCGrammarSymbol grammar, ASTLexProd lexNode) {
    StringBuilder builder = new StringBuilder();
    RegExpBuilder regExp = new RegExpBuilder(builder, grammar);
    regExp.handle(lexNode);
    return builder.toString();
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

  // TODO GV:

  /**
   * Returns the STType associated with this name Use "super." as a prefix to
   * explicitly access the type of supergrammars this grammar overriddes.
   * Native/external types are types that are not defined in the grammar but are
   * refered from it. These types are indicated by the suffix "/" in the grammar
   * and refer to regular Java types. To access these type use the prefix "/"
   * e.g. "/String" or "/int"
   *
   * @param name Name of the type
   * @return Symboltable entry for this type
   */
  public static Optional<ProdSymbol> getTypeWithInherited(String name,
                                                          MCGrammarSymbol gramamrSymbol) {
    Optional<ProdSymbol> ret = Optional.empty();
    if (name.startsWith("super.")) {
      name = name.substring(6);
    } else {
      ret = gramamrSymbol.getProd(name);
    }

    if (!ret.isPresent()) {
      ret = gramamrSymbol.getProdWithInherited(name);
    }
    return ret;
  }

  /**
   * @return the qualified name for this type
   */
  // TODO GV: change implementation
  public static String getQualifiedName(ProdSymbol symbol) {
    if (!symbol.isPresentAstNode()) {
      return "UNKNOWN_TYPE";
    }
    if (symbol.isIsLexerProd()) {
      return getLexType(symbol.getAstNode());
    }
    if (symbol.isIsEnum()) {
      return getQualifiedName(symbol.getAstNode(), symbol, AST_PREFIX, "");
      // return "int";
      // TODO GV:
      // return getConstantType();
    }
    return getQualifiedName(symbol.getAstNode(), symbol, AST_PREFIX, "");
  }

  public static String getDefaultValue(ProdSymbol symbol) {
    if ("int".equals(getQualifiedName(symbol))) {
      return "0";
    } else if ("boolean".equals(getQualifiedName(symbol))) {
      return "false";
    } else {
      return "null";
    }
  }

  private static String getLexType(ASTNode node) {
    if (node instanceof ASTLexProd) {
      return HelperGrammar.createConvertType((ASTLexProd) node);
    }
    if (node instanceof ASTLexActionOrPredicate) {
      return "String";
    }
    return "UNKNOWN_TYPE";
  }

  public static String getQualifiedName(ASTProd astNode, ProdSymbol symbol, String prefix,
                                        String suffix) {
    if (symbol.isIsExternal()) {
      return symbol.getName();
    } else {
      Optional<MCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode.getEnclosingScope());
      String string = (grammarSymbol.isPresent()
          ? grammarSymbol.get().getFullName().toLowerCase()
          : "")
          + AST_DOT_PACKAGE_SUFFIX_DOT + prefix +
          StringTransformations.capitalize(symbol.getName() + suffix);

      if (string.startsWith(".")) {
        string = string.substring(1);
      }
      return string;
    }
  }

  public static String getConstantName(RuleComponentSymbol compSymbol) {
    if (compSymbol.isIsConstantGroup() && compSymbol.isPresentAstNode()
        && compSymbol.getAstNode() instanceof ASTConstantGroup) {
      return getConstantGroupName((ASTConstantGroup) compSymbol.getAstNode()).orElse("");
    }
    if (compSymbol.isIsConstant() && compSymbol.isPresentAstNode()
        && compSymbol.getAstNode() instanceof ASTConstant) {
      return
          HelperGrammar.getAttributeNameForConstant((ASTConstant) compSymbol.getAstNode());
    }
    return "";
  }

  public static Optional<String> getConstantGroupName(ASTConstantGroup ast) {
    // setAttributeMinMax(a.getIteration(), att);
    if (ast.isPresentUsageName()) {
      return Optional.of(ast.getUsageName());
    }
    // derive attribute name from constant entry (but only if we have
    // one entry!)
    else if (ast.getConstantList().size() == 1) {
      return Optional.of(HelperGrammar.getAttributeNameForConstant(ast.getConstantList().get(0)));
    }

    Log.error("0xA2345 The name of the constant group could't be ascertained",
        ast.get_SourcePositionStart());

    return Optional.empty();
  }

  public void addEnum(String name, String constant) {
    // List of enum values for this type
    Map<String, Set<String>> possibleValuesForEnum = new HashMap<>();
    List<String> enumValues = new ArrayList<>();
    Set<String> constantsInGrammar = possibleValuesForEnum.get(name.intern());
    if (constantsInGrammar == null) {
      constantsInGrammar = new LinkedHashSet<>();
      possibleValuesForEnum.put(name.intern(), constantsInGrammar);
    }
    constantsInGrammar.add(constant.intern());
    if (!enumValues.contains(name.intern())) {
      enumValues.add(name.intern());
    }
  }

  /**
   * @param astNode
   * @param currentSymbol
   * @return
   */
  public static Optional<String> getConstantName(ASTConstantGroup astNode,
                                                 Optional<ProdSymbol> currentSymbol) {
    Optional<String> constName = getConstantGroupName(astNode);
    if (!currentSymbol.isPresent() || !constName.isPresent()) {
      return constName;
    }
    return Optional.of(getConstantGroupName(astNode).get());
  }

  public static Set<ProdSymbol> getAllSuperProds(ProdSymbol prod) {
    Set<ProdSymbol> supersHandled = new LinkedHashSet<>();
    List<ProdSymbol> supersToHandle = new ArrayList<>();
    supersToHandle.addAll(getSuperProds(prod));
    Set<ProdSymbol> supersNextRound = new LinkedHashSet<>();

    while (!supersToHandle.isEmpty()) {
      for (ProdSymbol superType : supersToHandle) {
        if (!supersHandled.contains(superType)) {
          supersNextRound.addAll(getSuperProds(superType));
        }
        supersHandled.add(superType);
      }
      supersToHandle.clear();
      supersToHandle.addAll(supersNextRound);
      supersNextRound.clear();
    }
    return ImmutableSet.copyOf(supersHandled);
  }

  public static Set<ProdSymbol> getAllSuperInterfaces(ProdSymbol prod) {
    return getAllSuperProds(prod).stream().filter(p -> p.isIsInterface()).collect(Collectors.toSet());
  }

  /**
   * @param prod
   * @return
   */
  public static List<ProdSymbol> getSuperProds(ProdSymbol prod) {
    List<ProdSymbol> superTypes = prod.getSuperProds().stream().filter(s -> s.isSymbolLoaded())
        .map(s -> s.getLoadedSymbol()).collect(Collectors.toList());
    superTypes.addAll(prod.getSuperInterfaceProds().stream().filter(s -> s.isSymbolLoaded())
        .map(s -> s.getLoadedSymbol()).collect(Collectors.toList()));

    superTypes.addAll(prod.getAstSuperClasses().stream().filter(s -> s.isSymbolLoaded())
        .map(s -> s.getLoadedSymbol()).collect(Collectors.toList()));
    superTypes.addAll(prod.getAstSuperInterfaces().stream().filter(s -> s.isSymbolLoaded())
        .map(s -> s.getLoadedSymbol()).collect(Collectors.toList()));

    return ImmutableList.copyOf(superTypes);
  }

  public static boolean isSubtype(ProdSymbol subType, ProdSymbol superType) {
    return isSubtype(subType, superType, newLinkedHashSet(Arrays.asList(subType)));
  }

  private static boolean isSubtype(ProdSymbol subType, ProdSymbol superType,
                                   Set<ProdSymbol> handledTypes) {
    if (areSameTypes(subType, superType)) {
      return true;
    }

    // Try to find superType in super types of this type
    final Collection<ProdSymbol> allSuperTypes = getAllSuperProds(subType);
    if (allSuperTypes.contains(superType)) {
      return true;
    }

    // check transitive sub-type relation
    for (ProdSymbol t : allSuperTypes) {
      if (handledTypes.add(superType)) {
        boolean subtypeOf = isSubtype(t, superType, handledTypes);
        if (subtypeOf) {
          return true;
        }
      }
    }

    return false;
  }

  public static boolean areSameTypes(ProdSymbol type1, ProdSymbol type2) {
    Log.errorIfNull(type1);
    Log.errorIfNull(type2);

    if (type1 == type2) {
      return true;
    }

    return type1.getFullName().equals(type2.getFullName());

  }

  public static boolean isAssignmentCompatibleOrUndecidable(ProdSymbol subType,
                                                            ProdSymbol superType) {
    return isAssignmentCompatibleOrUndecidable(subType, superType,
        newLinkedHashSet(Arrays.asList(subType)));
  }

  /**
   * Returns the type of the collection <code>types</code> that is the sub type
   * of all other types in this collection. Else, null is returned.
   *
   * @param types Collection of types
   * @return type that is subtype of all other types or null.
   */
  public static Optional<ProdSymbol> findLeastType(Collection<ProdSymbol> types) {
    for (ProdSymbol t1 : types) {
      boolean isLeastType = true;
      for (ProdSymbol t2 : types) {
        if (!isSubtype(t2, t1) && !areSameTypes(t2, t1)) {
          isLeastType = false;
          break;
        }
      }
      if (isLeastType) {
        return Optional.of(t1);
      }
    }
    return Optional.empty();
  }

  public static boolean isAssignmentCompatibleOrUndecidable(ProdSymbol subType,
                                                            ProdSymbol superType, Set<ProdSymbol> handledTypes) {
    // Return true if this type or the other type are both external
    // TODO GV: check, wenn Java angebunden
    if (subType.isIsExternal()
        || superType.isIsExternal()) {
      return true;
    }

    // Return true if this type and the other type are the same
    if (areSameTypes(subType, superType)) {
      return true;
    }

    // Try to find superType in supertypes of this type
    Collection<ProdSymbol> allSuperTypes = getAllSuperProds(subType);
    if (allSuperTypes.contains(superType)) {
      return true;
    }

    // check transitive sub-type relation
    for (ProdSymbol t : allSuperTypes) {
      if (handledTypes.add(superType)) {
        boolean subtypeOf = isAssignmentCompatibleOrUndecidable(t, superType, handledTypes);
        if (subtypeOf) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * @param ref1
   * @param ref2
   * @return
   */
  public static boolean isSubType(ProdSymbolLoader ref1, ProdSymbolLoader ref2) {
    ProdSymbol type1 = ref1.getLoadedSymbol();
    ProdSymbol type2 = ref2.getLoadedSymbol();
    return areSameTypes(type1, type2) || isSubtype(type1, type2) || isSubtype(type2, type1);
  }

  /**
   * @param prodComponent
   * @return
   */
  public static boolean isConstGroupIterated(RuleComponentSymbol prodComponent) {
    Preconditions.checkArgument(prodComponent.isIsConstantGroup());
    if (!prodComponent.isIsList() && prodComponent.getSubProdList().size() <= 1) {
      return false;
    }
    prodComponent.getSubProdList();
    Collection<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    for (String component : prodComponent.getSubProdList()) {
      set.add(component);
    }
    return set.size() > 1;
  }

  public static boolean isAttributeIterated(AdditionalAttributeSymbol attrSymbol) {
    return attrSymbol.isPresentAstNode()
        && isAttributeIterated((ASTAdditionalAttribute) attrSymbol.getAstNode());
  }

  /**
   * @param ast
   * @return
   */
  public static boolean isAttributeIterated(ASTAdditionalAttribute ast) {
    if (!ast.isPresentCard()) {
      return false;
    }
    if (ast.getCard().getIteration() == ASTConstantsGrammar.PLUS || ast.getCard().getIteration() == ASTConstantsGrammar.STAR) {
      return true;
    }
    Optional<Integer> max = getMax(ast);
    return max.isPresent() && (max.get() == TransformationHelper.STAR || max.get() > 1);
  }

  public static Optional<Integer> getMax(AdditionalAttributeSymbol attrSymbol) {
    if (!attrSymbol.isPresentAstNode()) {
      return Optional.empty();
    }
    return getMax((ASTAdditionalAttribute) attrSymbol.getAstNode());
  }

  public static Optional<Integer> getMax(ASTAdditionalAttribute ast) {
    if (ast.isPresentCard()
        && ast.getCard().isPresentMax()) {
      String max = ast.getCard().getMax();
      if ("*".equals(max)) {
        return Optional.of(TransformationHelper.STAR);
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

  public static Optional<Integer> getMin(AdditionalAttributeSymbol attrSymbol) {
    if (!attrSymbol.isPresentAstNode()) {
      return Optional.empty();
    }
    return getMin((ASTAdditionalAttribute) attrSymbol.getAstNode());
  }

  public static Optional<Integer> getMin(ASTAdditionalAttribute ast) {
    if (ast.isPresentCard()
        && ast.getCard().isPresentMin()) {
      String min = ast.getCard().getMin();
      try {
        int x = Integer.parseInt(min);
        return Optional.of(x);
      } catch (NumberFormatException ignored) {
        Log.warn("0xA0141 Failed to parse an integer value of max of ASTAdditionalAttribute "
            + ast.getName() + " from string " + min);
      }
    }
    return Optional.empty();
  }

}
