/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTLexActionOrPredicate;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.grammar.symboltable.EssentialMCGrammarSymbol;
import de.monticore.grammar.symboltable.EssentialMontiCoreGrammarLanguage;
import de.monticore.grammar.symboltable.EssentialMontiCoreGrammarSymbolTableCreator;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.grammar.symboltable.MCProdSymbolReference;
import de.monticore.io.paths.ModelPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.Util;
import de.se_rwth.commons.logging.Log;

public class EssentialMCGrammarSymbolTableHelper {
  
  private static Optional<? extends ScopeSpanningSymbol> c;
  
  public static void initializeSymbolTable(ASTMCGrammar rootNode, ModelPath modelPath) {
    ModelingLanguage grammarLanguage = new EssentialMontiCoreGrammarLanguage();
    
    ResolverConfiguration resolverConfiguration = new ResolverConfiguration();
    resolverConfiguration.addDefaultFilters(grammarLanguage.getResolvers());
    
    Grammar_WithConceptsPrettyPrinter prettyPrinter = new Grammar_WithConceptsPrettyPrinter(
        new IndentPrinter());
    
    MutableScope globalScope = new GlobalScope(modelPath, grammarLanguage, resolverConfiguration);
    EssentialMontiCoreGrammarSymbolTableCreator symbolTableCreator = new EssentialMontiCoreGrammarSymbolTableCreator(
        resolverConfiguration, globalScope, prettyPrinter);
    
    // Create Symbol Table
    symbolTableCreator.createFromAST(rootNode);
  }
  
  public static Optional<MCProdSymbol> resolveRule(ASTNode astNode, String name) {
    Optional<EssentialMCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode);
    if (!grammarSymbol.isPresent()) {
      return Optional.empty();
    }
    return grammarSymbol.get().getProdWithInherited(name);
  }
  
  public static Optional<MCProdSymbol> resolveRuleInSupersOnly(ASTNode astNode, String name) {
    Optional<EssentialMCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode);
    Stream<EssentialMCGrammarSymbol> superGrammars = grammarSymbol
        .map(symbol -> Util.preOrder(symbol, EssentialMCGrammarSymbol::getSuperGrammarSymbols)
            .stream())
        .orElse(Stream.empty()).skip(1);
    return superGrammars.map(superGrammar -> superGrammar.getProd(name))
        .filter(mcRuleSymbol -> mcRuleSymbol.isPresent())
        .map(Optional::get)
        .findFirst();
  }
  
  public static Optional<EssentialMCGrammarSymbol> getMCGrammarSymbol(ASTNode astNode) {
    Set<Scope> scopes = getAllScopes(astNode);
    for (Scope s : scopes) {
      Optional<? extends ScopeSpanningSymbol> symbol = s.getSpanningSymbol();
      if (symbol.isPresent() && symbol.get() instanceof EssentialMCGrammarSymbol) {
        return Optional.of((EssentialMCGrammarSymbol) symbol.get());
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
  public static Set<EssentialMCGrammarSymbol> getAllSuperGrammars(
      EssentialMCGrammarSymbol grammarSymbol) {
    Set<EssentialMCGrammarSymbol> allSuperGrammars = new LinkedHashSet<>();
    Set<EssentialMCGrammarSymbol> tmpList = new LinkedHashSet<>();
    allSuperGrammars.addAll(grammarSymbol.getSuperGrammarSymbols());
    boolean modified = false;
    do {
      for (EssentialMCGrammarSymbol curGrammar : allSuperGrammars) {
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
  
  private static String getLexString(EssentialMCGrammarSymbol grammar, ASTLexProd lexNode) {
    StringBuilder builder = new StringBuilder();
    RegExpBuilder regExp = new RegExpBuilder(builder, grammar);
    regExp.handle(lexNode);
    return builder.toString();
  }
  
  public static Optional<Pattern> calculateLexPattern(EssentialMCGrammarSymbol grammar,
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
      EssentialMCGrammarSymbol gramamrSymbol) {
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
      Optional<EssentialMCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode);
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
      return getConstantName((ASTConstantGroup) compSymbol.getAstNode().get());
    }
    if (compSymbol.isConstant() && compSymbol.getAstNode().isPresent()
        && compSymbol.getAstNode().get() instanceof ASTConstant) {
      return Optional.of(
          HelperGrammar.getAttributeNameForConstant((ASTConstant) compSymbol.getAstNode().get()));
    }
    return Optional.empty();
  }
  
  public static Optional<String> getConstantName(ASTConstantGroup ast) {
    // setAttributeMinMax(a.getIteration(), att);
    if (ast.getUsageName().isPresent()) {
      return ast.getUsageName();
    }
    // derive attribute name from constant entry (but only if we have
    // one entry!)
    else if (ast.getConstants().size() == 1) {
      return Optional.of(HelperGrammar.getAttributeNameForConstant(ast.getConstants().get(0)));
    }
    else {
      Log.error("0xA2345 The name of the constant group could't be ascertained",
          ast.get_SourcePositionStart());
    }
    
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
   * TODO: Write me!
   * 
   * @param astNode
   * @param currentSymbol
   * @return
   */
  public static Optional<String> getConstantName(ASTConstantGroup astNode,
      Optional<? extends ScopeSpanningSymbol> currentSymbol) {
    Optional<String> constName = getConstantName(astNode);
    if (!currentSymbol.isPresent() || !(currentSymbol.get() instanceof MCProdSymbol)
        || !constName.isPresent()) {
      return constName;
    }
    return Optional.of(currentSymbol.get().getName() + "." + getConstantName(astNode).get());
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
  
  /**
   * TODO: Write me!
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
   * TODO: Write me!
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
   * TODO: Write me!
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
    return set.size() > 1 ;
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
