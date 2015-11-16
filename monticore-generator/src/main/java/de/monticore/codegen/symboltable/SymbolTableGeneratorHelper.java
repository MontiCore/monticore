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

package de.monticore.codegen.symboltable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.visitor.VisitorGeneratorHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleComponentSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

public class SymbolTableGeneratorHelper extends GeneratorHelper {

  public static final String NAME_NONTERMINAL = "Name";

  private final String qualifiedGrammarName;
  private final ASTMCGrammar astGrammar;
  private final MCGrammarSymbol grammarSymbol;

  // TODO PN refactor
  public SymbolTableGeneratorHelper(ASTMCGrammar ast, GlobalScope globalScope, ASTCDCompilationUnit astCd) {
    super(astCd, globalScope);
    Log.errorIfNull(ast);
    this.astGrammar = ast;
    this.qualifiedGrammarName = astGrammar.getPackage().isEmpty() ? astGrammar.getName() :
        Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackage()),
            astGrammar.getName());

    grammarSymbol = globalScope.<MCGrammarSymbol> resolve(
        qualifiedGrammarName, MCGrammarSymbol.KIND).orElse(null);
    Log.errorIfNull(grammarSymbol, "0xA4036 Grammar " + qualifiedGrammarName
        + " can't be resolved in the scope " + globalScope);

    checkState(qualifiedGrammarName.equals(grammarSymbol.getFullName()));
  }

  public MCGrammarSymbol getGrammarSymbol() {
    return grammarSymbol;
  }

  /**
   * @return the package for the generated symbol table files
   */
  public String getTargetPackage() {
    return getQualifiedGrammarName().toLowerCase() + "." + SymbolTableGenerator.PACKAGE;
  }

  /**
   * @return the qualified grammar's name
   */
  public String getQualifiedGrammarName() {
    return qualifiedGrammarName;
  }

  public static String getQualifiedSymbolType(String packageName, String symbolName) {
    return getPackageName(packageName, SymbolTableGenerator.PACKAGE) + "." + symbolName;
  }

  /**
   * @return the name of the top ast, i.e., the ast of the start rule.
   */
  public String getTopAstName() {
    if (grammarSymbol.getStartRule().isPresent()) {
      return "AST" + grammarSymbol.getStartRule().get().getName();
    }

    return "";
  }

  /**
   * @return the name of the start rule
   */
  public String getStartRuleName() {
    if (grammarSymbol.getStartRule().isPresent()) {
      return grammarSymbol.getStartRule().get().getName();
    }

    return "";
  }

  /**
   * @return all rules using the nonterminal <code>Name</code>. If a usage name is specified,
   * it must be <code>name</code> (case insenstive), e.g. <code>name:Name</code> or
   * <code>Name:Name</code>.
   */
  public Collection<MCRuleSymbol> getAllSymbolDefiningRules() {
    final Set<MCRuleSymbol> ruleSymbolsWithName = new LinkedHashSet<>();

    // TODO PN include inherited rules?
    for (final MCRuleSymbol rule : grammarSymbol.getRules()) {
      if (rule.isSymbolDefinition()) {
        ruleSymbolsWithName.add(rule);
      }
    }

    return ImmutableList.copyOf(ruleSymbolsWithName);
  }

  public Map<String, String> ruleComponents2JavaFields(MCRuleSymbol ruleSymbol) {
    Log.errorIfNull(ruleSymbol);

    // fieldName -> fieldType
    final Map<String, String> fields = new HashMap<>();

    for (MCRuleComponentSymbol componentSymbol : ruleSymbol.getRuleComponents()) {

      checkArgument(componentSymbol.getAstNode().isPresent());

      switch (componentSymbol.getKindOfRuleComponent()) {
        case NONTERMINAL:
          nonterminal2JavaField(componentSymbol, fields);
          break;
        case CONSTANT:
          constant2JavaField(componentSymbol, fields);
          break;
        case CONSTANTGROUP:
          // TODO PN handle this case
          break;
        case TERMINAL:
          // ignore terminals
          break;
        default:
          // TODO PN remove this exception
          throw new RuntimeException("0xA4078 TODO PN implement in " + SymbolTableGeneratorHelper.class.getSimpleName());
      }

    }

    return fields;
  }

  private void nonterminal2JavaField(MCRuleComponentSymbol componentSymbol, Map<String, String> fields) {
    final Optional<String> componentName = getRuleComponentName(componentSymbol);
    if (componentName.isPresent()) {
      if (componentSymbol.isSymbolReference()) {
        // the case: Transition = from:Name@State ..., i.e., a reference to another symbol
        fields.put(componentName.get(), componentSymbol.getReferencedSymbolName().get() + "Symbol");
      }
    }
    // TODO PN else, do something?
  }



  private void constant2JavaField(MCRuleComponentSymbol componentSymbol, Map<String, String> fields) {
    final Optional<String> componentName = getRuleComponentName(componentSymbol);
    if (componentName.isPresent()) {
      fields.put(componentName.get(), "boolean");
    }
  }

  private Optional<String> getRuleComponentName(MCRuleComponentSymbol componentSymbol) {
    if (canBeTransformedToValidJavaName(componentSymbol.getName())) {
      return Optional.of(componentSymbol.getName());
    }
    else if (canBeTransformedToValidJavaName(componentSymbol.getUsageName())) {
      return Optional.of(componentSymbol.getUsageName());
    }

    return Optional.empty();
  }

  public Map<String, String> symbolRuleComponents2JavaFields(MCRuleSymbol ruleSymbol) {
    Log.errorIfNull(ruleSymbol);

    // fieldName -> fieldType
    final Map<String, String> fields = new HashMap<>();

    for (MCRuleComponentSymbol componentSymbol : ruleSymbol.getRuleComponents()) {

      checkArgument(componentSymbol.getAstNode().isPresent());

      switch (componentSymbol.getKindOfRuleComponent()) {
        case NONTERMINAL:
          symbolNonTerminal2JavaField(componentSymbol, fields);
          break;
      }

    }

    return fields;
  }

  private void symbolNonTerminal2JavaField(MCRuleComponentSymbol componentSymbol, Map<String, String> fields) {
    final Optional<String> componentName = getRuleComponentName(componentSymbol);
    if (componentName.isPresent()) {
      // the case: Automaton = Name ... State* ..., i.e., the containment of another symbol
      final MCRuleSymbol referencedRule = grammarSymbol.getRule(componentSymbol.getReferencedRuleName());
      if ((referencedRule != null) && referencedRule.isSymbolDefinition()) {
        fields.put(componentName.get(), referencedRule.getName() + "Symbol");
      }
    }
  }

  public static String getterPrefix(final String type) {
    if ("boolean".equals(type) || "Boolean".equals(type)) {
      return "is";
    }
    return "get";
  }

  /**
   * Returns true, if <code>name</code> is a valid Java name or can be
   * transformed to a valid Java name using {@link JavaNamesHelper#getNonReservedName(String)}.
   * For example, <code>final</code> is no valid Java name, but can be transformed
   * to one, e.g., <code>r__final</code>. However, <code>+-</code> is neither a
   * valid Java nor can it be transformed to one.
   *
   * @param name
   * @return true, if <code>name</code> is a valid Java name or can be transformed
   * to a valid Java name using {@link JavaNamesHelper#getNonReservedName(String)}.
   */
  private boolean canBeTransformedToValidJavaName(String name) {
    return isValidName(nonReservedName(name));
  }

  public static String nonReservedName(final String name) {
    return JavaNamesHelper.getNonReservedName(name);
  }

  // TODO PN move to JavaNamesHelper

  private static final Set<String> KEYWORDS = new LinkedHashSet<>(Arrays.asList(
      "abstract", "continue", "for", "new", "switch", "assert", "default", "goto",
      "package", "synchronized", "boolean", "do", "if", "private", "this", "break",
      "double", "implements", "protected", "throw", "byte", "else", "import", "public",
      "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends",
      "int", "short", "try", "char", "final", "interface", "static", "void", "class",
      "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while"));

  private static final Pattern JAVA_SIMPLE_NAME_PATTERN =
      Pattern.compile("[A-Za-z_$]+[a-zA-Z0-9_$]*");

  public static boolean isValidName(final String name) {
    if ("".equals(nullToEmpty(name))) {
      return false;
    }

    return !KEYWORDS.contains(name) && JAVA_SIMPLE_NAME_PATTERN.matcher(name).matches();
  }

  public static String getGeneratedErrorCode(ASTNode ast) {
    return GeneratorHelper.getGeneratedErrorCode(ast);
  }

  // TODO refactor
  public String getVisitorType() {
    return VisitorGeneratorHelper.getVisitorType(getCdName());
  }
  
  // TODO refactor
  public String getCommonDelegatorVisitorType() {
    return "Common" + VisitorGeneratorHelper.getDelegatorVisitorType(getCdName());
  }

  // TODO refactor
  public String getDelegatorVisitorType() {
    return VisitorGeneratorHelper.getDelegatorVisitorType(getCdName());
  }
  
  // TODO refactor
  public static String getQualifiedSymTabCreatorType(String packageName, String cdName) {
    return getPackageName(packageName, getVisitorPackageSuffix()) + "."
        + getSymTabCreatorType(cdName);
  }

  // TODO refactor
  public static String getSymTabCreatorType(String cdName) {
    return cdName + "SymbolTableCreator";
  }

  // TODO refactor
  public String getQualifiedVisitorNameAsJavaName(CDSymbol cd) {
    return VisitorGeneratorHelper.qualifiedJavaTypeToName(getQualifiedVisitorType(cd));
  }

  // TODO refactor
  public static String getQualifiedSymTabCreator(String qualifiedLanguageName) {
    String packageName = getCdPackage(qualifiedLanguageName);
    String cdName = getCdName(qualifiedLanguageName);
    return getQualifiedSymTabCreatorType(packageName, cdName);
  }
  
  // TODO refactor
  public String getQualifiedVisitorType(CDSymbol cd) {
    return VisitorGeneratorHelper.getQualifiedVisitorType(cd.getFullName());
  }
}
