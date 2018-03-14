/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import static com.google.common.collect.Sets.newLinkedHashSet;
import static de.se_rwth.commons.Util.listTillNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import de.monticore.ModelingLanguage;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.RegExpBuilder;
import de.monticore.grammar.grammar._ast.ASTAttributeInAST;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTLexActionOrPredicate;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdAttributeSymbol;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.grammar.symboltable.MCProdSymbolReference;
import de.monticore.grammar.symboltable.MontiCoreGrammarLanguage;
import de.monticore.grammar.symboltable.MontiCoreGrammarSymbolTableCreator;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.Util;
import de.se_rwth.commons.logging.Log;

public class MCGrammarSymbolTableHelper {
  
  public static void initializeSymbolTable(ASTMCGrammar rootNode, ModelPath modelPath) {
    ModelingLanguage grammarLanguage = new MontiCoreGrammarLanguage();
    
    ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(grammarLanguage.getResolvingFilters());
    
    MutableScope globalScope = new GlobalScope(modelPath, grammarLanguage, resolvingConfiguration);
    MontiCoreGrammarSymbolTableCreator symbolTableCreator = new MontiCoreGrammarSymbolTableCreator(
        resolvingConfiguration, globalScope);
    
    // Create Symbol Table
    symbolTableCreator.createFromAST(rootNode);
  }
  
  public static Optional<MCProdSymbol> resolveRule(ASTNode astNode, String name) {
    Optional<MCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode);
    if (!grammarSymbol.isPresent()) {
      return Optional.empty();
    }
    return grammarSymbol.get().getProdWithInherited(name);
  }
  
  public static Optional<MCProdSymbol> resolveRuleInSupersOnly(ASTNode astNode, String name) {
    Optional<MCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode);
    Stream<MCGrammarSymbol> superGrammars = grammarSymbol
        .map(symbol -> Util.preOrder(symbol, MCGrammarSymbol::getSuperGrammarSymbols)
            .stream())
        .orElse(Stream.empty()).skip(1);
    return superGrammars.map(superGrammar -> superGrammar.getProd(name))
        .filter(mcRuleSymbol -> mcRuleSymbol.isPresent())
        .map(Optional::get)
        .findFirst();
  }
  
  public static Optional<MCGrammarSymbol> getGrammarSymbol(ASTMCGrammar astNode) {
    if (!astNode.getSymbol().isPresent()) {
      return Optional.empty();
    }
    if (!(astNode.getSymbol().get() instanceof MCGrammarSymbol)) {
      return Optional.empty();
    }
    return Optional.of((MCGrammarSymbol) astNode.getSymbol().get());
  }
  
  public static Optional<MCGrammarSymbol> getMCGrammarSymbol(ASTNode astNode) {
    Set<Scope> scopes = getAllScopes(astNode);
    for (Scope s : scopes) {
      Optional<? extends ScopeSpanningSymbol> symbol = s.getSpanningSymbol();
      if (symbol.isPresent() && symbol.get() instanceof MCGrammarSymbol) {
        return Optional.of((MCGrammarSymbol) symbol.get());
      }
    }
    return Optional.empty();
    // return getAllScopes(astNode).stream()
    // .map(Scope::getSpanningSymbol)
    // .filter(Optional::isPresent)
    // .map(Optional::get)
    // .filter(EssentialMCGrammarSymbol.class::isInstance)
    // .map(EssentialMCGrammarSymbol.class::cast)
    // .findFirst();
  }
  
  public static Optional<MCProdSymbol> getEnclosingRule(ASTNode astNode) {
    return getAllScopes(astNode).stream()
        .map(Scope::getSpanningSymbol)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(MCProdSymbol.class::isInstance)
        .map(MCProdSymbol.class::cast)
        .findFirst();
  }
  
  public static Optional<MCProdSymbol> getEnclosingRule(MCProdComponentSymbol prod) {
    return prod.getEnclosingScope().getSpanningSymbol().filter(MCProdSymbol.class::isInstance)
        .map(MCProdSymbol.class::cast);
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
  
  public static boolean isFragment(Optional<ASTNode> astNode) {
    return !astNode.isPresent() || !(astNode.get() instanceof ASTLexProd)
        || ((ASTLexProd) astNode.get()).isFragment();
  }
  
  private static Set<Scope> getAllScopes(ASTNode astNode) {
    Set<Scope> ret = Sets.newHashSet();
    astNode.getSpannedScope().ifPresent(s -> ret.add(s));
    for (Symbol s : getAllSubSymbols(astNode)) {
      for (Scope l : listTillNull(s.getEnclosingScope(),
          childScope -> childScope.getEnclosingScope().orElse(null))) {
        ret.add(l);
      }
    }
    
    return ret;
    // return getAllSubSymbols(astNode).stream()
    // .map(Symbol::getEnclosingScope)
    // .flatMap(
    // scope -> listTillNull(scope, childScope ->
    // childScope.getEnclosingScope().orElse(null))
    // .stream())
    // .collect(Collectors.toSet());
  }
  
  private static String getLexString(MCGrammarSymbol grammar, ASTLexProd lexNode) {
    StringBuilder builder = new StringBuilder();
    RegExpBuilder regExp = new RegExpBuilder(builder, grammar);
    regExp.handle(lexNode);
    return builder.toString();
  }
  
  public static Optional<Pattern> calculateLexPattern(MCGrammarSymbol grammar,
      Optional<ASTNode> lexNode) {
    Optional<Pattern> ret = Optional.empty();
    
    if (!lexNode.isPresent() || !(lexNode.get() instanceof ASTLexProd)) {
      return ret;
    }
    final String lexString = getLexString(grammar, (ASTLexProd) lexNode.get());
    try {
      if ("[[]".equals(lexString)) {
        return Optional.ofNullable(Pattern.compile("[\\[]"));
      }
      else {
        return Optional.ofNullable(Pattern.compile(lexString));
      }
    }
    catch (PatternSyntaxException e) {
      Log.error("0xA0913 Internal error with pattern handling for lex rules. Pattern: " + lexString
          + "\n", e);
    }
    return ret;
  }
  
  private static Set<Symbol> getAllSubSymbols(ASTNode astNode) {
    Set<Symbol> symbols = Util.preOrder(astNode, ASTNode::get_Children).stream()
        .map(ASTNode::getSymbol)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toSet());
    
    return symbols;
    // return Util.preOrder(astNode, ASTNode::get_Children).stream()
    // .map(ASTNode::getSymbol)
    // .filter(Optional::isPresent)
    // .map(Optional::get)
    // .collect(Collectors.toSet());
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
  public static Optional<MCProdSymbol> getTypeWithInherited(String name,
      MCGrammarSymbol gramamrSymbol) {
    Optional<MCProdSymbol> ret = Optional.empty();
    if (name.startsWith("super.")) {
      name = name.substring(6);
    }
    else {
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
  public static String getQualifiedName(MCProdSymbol symbol) {
    if (!symbol.getAstNode().isPresent()) {
      return "UNKNOWN_TYPE";
    }
    if (symbol.isLexerProd()) {
      return getLexType(symbol.getAstNode());
    }
    if (symbol.isEnum()) {
      return getQualifiedName(symbol.getAstNode().get(), symbol, GeneratorHelper.AST_PREFIX, "");
      // return "int";
      // TODO GV:
      // return getConstantType();
    }
    return getQualifiedName(symbol.getAstNode().get(), symbol, GeneratorHelper.AST_PREFIX, "");
  }
  
  public static String getDefaultValue(MCProdSymbol symbol) {
    if (getQualifiedName(symbol).equals("int")) {
      return "0";
    }
    else if (getQualifiedName(symbol).equals("boolean")) {
      return "false";
    }
    else {
      return "null";
    }
  }
  
  private static String getLexType(Optional<ASTNode> node) {
    if (node.isPresent()) {
      if (node.get() instanceof ASTLexProd) {
        return HelperGrammar.createConvertType((ASTLexProd) node.get());
      }
      if (node.get() instanceof ASTLexActionOrPredicate) {
        return "String";
      }
    }
    return "UNKNOWN_TYPE";
    
  }
  
  public static String getQualifiedName(ASTNode astNode, MCProdSymbol symbol, String prefix,
      String suffix) {
    if (symbol.isExternal()) {
      return symbol.getName();
    }
    else {
      Optional<MCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode);
      String string = (grammarSymbol.isPresent()
          ? grammarSymbol.get().getFullName().toLowerCase()
          : "")
          + GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT + prefix +
          StringTransformations.capitalize(symbol.getName() + suffix);
      
      if (string.startsWith(".")) {
        string = string.substring(1);
      }
      return string;
    }
  }
  
  public static Optional<String> getConstantName(MCProdComponentSymbol compSymbol) {
    if (compSymbol.isConstantGroup() && compSymbol.getAstNode().isPresent()
        && compSymbol.getAstNode().get() instanceof ASTConstantGroup) {
      return getConstantGroupName((ASTConstantGroup) compSymbol.getAstNode().get());
    }
    if (compSymbol.isConstant() && compSymbol.getAstNode().isPresent()
        && compSymbol.getAstNode().get() instanceof ASTConstant) {
      return Optional.of(
          HelperGrammar.getAttributeNameForConstant((ASTConstant) compSymbol.getAstNode().get()));
    }
    return Optional.empty();
  }
  
  public static Optional<String> getConstantGroupName(ASTConstantGroup ast) {
    // setAttributeMinMax(a.getIteration(), att);
    if (ast.isPresentUsageName()) {
      return ast.getUsageNameOpt();
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
   *
   * @param astNode
   * @param currentSymbol
   * @return
   */
  public static Optional<String> getConstantName(ASTConstantGroup astNode,
      Optional<? extends ScopeSpanningSymbol> currentSymbol) {
    Optional<String> constName = getConstantGroupName(astNode);
    if (!currentSymbol.isPresent() || !(currentSymbol.get() instanceof MCProdSymbol)
        || !constName.isPresent()) {
      return constName;
    }
    return Optional.of(currentSymbol.get().getName() + "." + getConstantGroupName(astNode).get());
  }
  
  public static Set<MCProdSymbol> getAllSuperProds(MCProdSymbol prod) {
    Set<MCProdSymbol> supersHandled = new LinkedHashSet<>();
    List<MCProdSymbol> supersToHandle = new ArrayList<>();
    supersToHandle.addAll(getSuperProds(prod));
    Set<MCProdSymbol> supersNextRound = new LinkedHashSet<>();
    
    while (!supersToHandle.isEmpty()) {
      for (MCProdSymbol superType : supersToHandle) {
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
  
  public static Set<MCProdSymbol> getAllSuperInterfaces(MCProdSymbol prod) {
    return getAllSuperProds(prod).stream().filter(p -> p.isInterface()).collect(Collectors.toSet());
  }
  
  /**
   *
   * @param superType
   * @return
   */
  public static List<MCProdSymbol> getSuperProds(MCProdSymbol prod) {
    List<MCProdSymbol> superTypes = prod.getSuperProds().stream().map(s -> s.getReferencedSymbol())
        .collect(Collectors.toList());
    superTypes.addAll(prod.getSuperInterfaceProds().stream().map(s -> s.getReferencedSymbol())
        .collect(Collectors.toList()));
    
    superTypes.addAll(prod.getAstSuperClasses().stream().filter(s -> s.isProdRef())
        .map(s -> s.getProdRef().getReferencedSymbol()).collect(Collectors.toList()));
    superTypes.addAll(prod.getAstSuperInterfaces().stream().filter(s -> s.isProdRef())
        .map(s -> s.getProdRef().getReferencedSymbol()).collect(Collectors.toList()));
    
    return ImmutableList.copyOf(superTypes);
  }
  
  public static boolean isSubtype(MCProdSymbol subType, MCProdSymbol superType) {
    return isSubtype(subType, superType, newLinkedHashSet(Arrays.asList(subType)));
  }
  
  private static boolean isSubtype(MCProdSymbol subType, MCProdSymbol superType,
      Set<MCProdSymbol> handledTypes) {
    if (areSameTypes(subType, superType)) {
      return true;
    }
    
    // Try to find superType in super types of this type
    final Collection<MCProdSymbol> allSuperTypes = getAllSuperProds(subType);
    if (allSuperTypes.contains(superType)) {
      return true;
    }
    
    // check transitive sub-type relation
    for (MCProdSymbol t : allSuperTypes) {
      if (handledTypes.add(superType)) {
        boolean subtypeOf = isSubtype(t, superType, handledTypes);
        if (subtypeOf) {
          return true;
        }
      }
    }
    
    return false;
  }
  
  public static boolean areSameTypes(MCProdSymbol type1, MCProdSymbol type2) {
    Log.errorIfNull(type1);
    Log.errorIfNull(type2);
    
    if (type1 == type2) {
      return true;
    }
    
    if (!type1.getKind().equals(type2.getKind())) {
      return false;
    }
    
    return type1.getFullName().equals(type2.getFullName());
    
    // if (!type1.getName().equals(type2.getName())) {
    // return false;
    // }
    // // TODO NN <- PN needed?
    // // if (getOriginalLanguage() != type2.getOriginalLanguage()) {
    // // return false;
    // // }
    //
    // return
    // type1.getGrammarSymbol().getFullName().equals(type2.getGrammarSymbol().getFullName());
  }
  
  public static boolean isAssignmentCompatibleOrUndecidable(MCProdSymbol subType,
      MCProdSymbol superType) {
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
  public static Optional<MCProdSymbol> findLeastType(Collection<MCProdSymbol> types) {
    for (MCProdSymbol t1 : types) {
      boolean isLeastType = true;
      for (MCProdSymbol t2 : types) {
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
  
  public static boolean isAssignmentCompatibleOrUndecidable(MCProdSymbol subType,
      MCProdSymbol superType, Set<MCProdSymbol> handledTypes) {
    // Return true if this type or the other type are both external
    // TODO GV: check, wenn Java angebunden
    if (subType.isExternal()
        || superType.isExternal()) {
      return true;
    }
    
    // Return true if this type and the other type are the same
    if (areSameTypes(subType, superType)) {
      return true;
    }
    
    // if ((subType.getKindOfType() == KindType.IDENT) &&
    // (superType.getKindOfType() == KindType.IDENT)) {
    // return subType.getLexType().equals(superType.getLexType());
    // }
    
    // Try to find superType in supertypes of this type
    Collection<MCProdSymbol> allSuperTypes = getAllSuperProds(subType);
    if (allSuperTypes.contains(superType)) {
      return true;
    }
    
    // check transitive sub-type relation
    for (MCProdSymbol t : allSuperTypes) {
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
   *
   * @param mcProdSymbolReference
   * @param newOne
   * @return
   */
  public static boolean isSubType(MCProdSymbolReference ref1, MCProdSymbolReference ref2) {
    MCProdSymbol type1 = ref1.getReferencedSymbol();
    MCProdSymbol type2 = ref2.getReferencedSymbol();
    return areSameTypes(type1, type2) || isSubtype(type1, type2) || isSubtype(type2, type1);
  }
  
  /**
   *
   * @param prodComponent
   * @return
   */
  public static boolean isConstGroupIterated(MCProdComponentSymbol prodComponent) {
    Preconditions.checkArgument(prodComponent.isConstantGroup());
    if (!prodComponent.isList() && prodComponent.getSubProdComponents().size() <= 1) {
      return false;
    }
    prodComponent.getSubProdComponents();
    Collection<String> set = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
    for (MCProdComponentSymbol component : prodComponent.getSubProdComponents()) {
      set.add(component.getName());
    }
    return set.size() > 1;
  }
  
  public static boolean isAttributeIterated(MCProdAttributeSymbol attrSymbol) {
    return attrSymbol.getAstNode().isPresent()
        && attrSymbol.getAstNode().get() instanceof ASTAttributeInAST
        && isAttributeIterated((ASTAttributeInAST) attrSymbol.getAstNode().get());
  }
  
  /**
   *
   * @param ast
   * @return
   */
  public static boolean isAttributeIterated(ASTAttributeInAST ast) {
    if (!ast.isPresentCard()) {
      return false;
    }
    if (ast.getCard().isUnbounded()) {
      return true;
    }
    Optional<Integer> max = getMax(ast);
    return max.isPresent() && (max.get() == GeneratorHelper.STAR || max.get() > 1);
  }
  
  public static Optional<Integer> getMax(MCProdAttributeSymbol attrSymbol) {
    if (!attrSymbol.getAstNode().isPresent()
        || !(attrSymbol.getAstNode().get() instanceof ASTAttributeInAST)) {
      return Optional.empty();
    }
    return getMax((ASTAttributeInAST) attrSymbol.getAstNode().get());
  }
  
  public static Optional<Integer> getMax(ASTAttributeInAST ast) {
    if (ast.isPresentCard()
        && ast.getCard().isPresentMax()) {
      String max = ast.getCard().getMax();
      if ("*".equals(max)) {
        return Optional.of(GeneratorHelper.STAR);
      }
      else {
        try {
          int x = Integer.parseInt(max);
          return Optional.of(x);
        }
        catch (NumberFormatException ignored) {
          Log.warn("0xA0140 Failed to parse an integer value of max of ASTAttributeInAST "
              + ast.getName() + " from string " + max);
        }
      }
    }
    return Optional.empty();
  }
  
  public static Optional<Integer> getMin(MCProdAttributeSymbol attrSymbol) {
    if (!attrSymbol.getAstNode().isPresent()
        || !(attrSymbol.getAstNode().get() instanceof ASTAttributeInAST)) {
      return Optional.empty();
    }
    return getMin((ASTAttributeInAST) attrSymbol.getAstNode().get());
  }
  
  public static Optional<Integer> getMin(ASTAttributeInAST ast) {
    if (ast.isPresentCard()
        && ast.getCard().isPresentMin()) {
      String min = ast.getCard().getMin();
      try {
        int x = Integer.parseInt(min);
        return Optional.of(x);
      }
      catch (NumberFormatException ignored) {
        Log.warn("0xA0141 Failed to parse an integer value of max of ASTAttributeInAST "
            + ast.getName() + " from string " + min);
      }
    }
    return Optional.empty();
  }
  
  // public String getListType() {
  // if (isASTNode()) {
  // return getQualifiedName() + "List";
  // }
  // else {
  // return "java.util.List<" + getQualifiedName() + ">";
  // }
  // }
  
  // private String getConstantType() {
  // return (this.getEnumValues().size() > 1) ? "int" : "boolean";
  // }
  
}
