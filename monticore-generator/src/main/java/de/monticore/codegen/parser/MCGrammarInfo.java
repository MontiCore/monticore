/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser;

import com.google.common.collect.*;
import de.monticore.grammar.LexNamer;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.PredicatePair;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTConceptAntlr;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTJavaCodeExt;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbolSurrogate;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._ast.ASTMCConcept;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.Map.Entry;

import static de.monticore.codegen.mc2cd.TransformationHelper.NAME_PATTERN;

/**
 * Contains information about a grammar which is required for the parser
 * generation
 */
public class MCGrammarInfo {
  
  /**
   * Keywords of the processed grammar and its super grammars
   */
  protected Set<String> keywords = Sets.newLinkedHashSet();

  /**
   * Additional java code for parser defined in antlr concepts of the processed
   * grammar and its super grammars
   */
  protected List<String> additionalParserJavaCode = new ArrayList<>();
  
  /**
   * Additional java code for lexer defined in antlr concepts of the processed
   * grammar and its super grammars
   */
  protected List<String> additionalLexerJavaCode = new ArrayList<>();
  
  /**
   * Predicates
   */
  protected ListMultimap<String, PredicatePair> predicats = LinkedListMultimap.create();
  
  /**
   * Internal: LexNamer for naming lexer symbols in the antlr source code
   */
  protected LexNamer lexNamer = new LexNamer();

  protected Map<String, String> splitRules = Maps.newLinkedHashMap();

  protected List<String> keywordRules = Lists.newArrayList();

  /**
   * The symbol of the processed grammar
   */
  protected MCGrammarSymbol grammarSymbol;
  
  public MCGrammarInfo(MCGrammarSymbol grammarSymbol) {
    this.grammarSymbol = grammarSymbol;
    findAllKeywords();
    grammarSymbol.getTokenRulesWithInherited().forEach(t -> addSplitRule(t));
    grammarSymbol.getKeywordRulesWithInherited().forEach(k -> keywordRules.add(k));

    addSubRules();
    addHWAntlrCode();
  }

  protected void addSplitRule(String s) {
    String name = "";
    for (char c:s.toCharArray()) {
      name += getLexNamer().getConstantName(String.valueOf(c));
    }
    splitRules.put(s, name.toLowerCase());
  }

  public Map<String, String> getSplitRules() {
    return splitRules;
  }

  public List<String> getKeywordRules() {
    return keywordRules;
  }

  // ------------- Handling of the antlr concept -----------------------------
  
  /**
   * Add all sub/superule-relations to the symbol table form the perspective of
   * the super rule by using addSubrule
   *
   */
  protected void addSubRules() {
    Set<MCGrammarSymbol> grammarsToHandle = Sets
        .newLinkedHashSet(Arrays.asList(grammarSymbol));
    grammarsToHandle.addAll(MCGrammarSymbolTableHelper.getAllSuperGrammars(grammarSymbol));
    for (MCGrammarSymbol grammar : grammarsToHandle) {
      HashMap<String, List<ASTRuleReference>> ruleMap = Maps.newLinkedHashMap();
      // Collect superclasses and superinterfaces for classes
      for (ASTClassProd classProd : (grammar.getAstNode())
          .getClassProdList()) {
        List<ASTRuleReference> ruleRefs = Lists.newArrayList();
        ruleRefs.addAll(classProd.getSuperRuleList());
        ruleRefs.addAll(classProd.getSuperInterfaceRuleList());
        ruleMap.put(classProd.getName(), ruleRefs);
      }
      
      // Collect superclasses and superinterfaces for abstract classes
      for (ASTAbstractProd classProd : grammar.getAstNode().getAbstractProdList()) {
        List<ASTRuleReference> ruleRefs = Lists.newArrayList();
        ruleRefs.addAll(classProd.getSuperRuleList());
        ruleRefs.addAll(classProd.getSuperInterfaceRuleList());
        ruleMap.put(classProd.getName(), ruleRefs);
      }
      
      // Collect superinterfaces for interfaces
      for (ASTInterfaceProd classProd : grammar.getAstNode().getInterfaceProdList()) {
        List<ASTRuleReference> ruleRefs = Lists.newArrayList();
        ruleRefs.addAll(classProd.getSuperInterfaceRuleList());
        ruleMap.put(classProd.getName(), ruleRefs);
      }

      // Add relation to predicats
      for (Entry<String, List<ASTRuleReference>> entry: ruleMap.entrySet()) {
        for (ASTRuleReference ref: entry.getValue()) {
          Optional<ProdSymbol> prodByName = grammarSymbol
              .getProdWithInherited(ref.getTypeName());
          if (prodByName.isPresent()) {
            addSubrule(prodByName.get().getName(), entry.getKey(), ref);
          }
          else {
            Log.error("0xA2110 Undefined rule: " + ref.getTypeName(),
                ref.get_SourcePositionStart());
          }
        }
      }
    }
  }
  
  
  protected void addSubrule(String superrule, String subrule, ASTRuleReference ruleReference) {
    PredicatePair subclassPredicatePair = new PredicatePair(subrule, ruleReference);
    predicats.put(superrule, subclassPredicatePair);
  }
  

  /**
   * @return grammarSymbol
   */
  public MCGrammarSymbol getGrammarSymbol() {
    return this.grammarSymbol;
  }
  
  /**
   * @param grammarSymbol the grammarSymbol to set
   */
  public void setGrammarSymbol(MCGrammarSymbol grammarSymbol) {
    this.grammarSymbol = grammarSymbol;
  }
  
  /**
   * @return java code
   */
  public List<String> getAdditionalParserJavaCode() {
    return this.additionalParserJavaCode;
  }
  
  /**
   * @return java code
   */
  public List<String> getAdditionalLexerJavaCode() {
    return this.additionalLexerJavaCode;
  }
  
  protected void addHWAntlrCode() {
    // Get Antlr hwc
    Set<MCGrammarSymbol> grammarsToHandle = Sets
        .newLinkedHashSet(Arrays.asList(grammarSymbol));
    grammarsToHandle.addAll(MCGrammarSymbolTableHelper.getAllSuperGrammars(grammarSymbol));
    for (MCGrammarSymbol grammar : grammarsToHandle) {
      if (grammar.isPresentAstNode()) {
        // Add additional java code for lexer and parser
        for (ASTConcept concept : grammar.getAstNode().getConceptList()) {
          if (concept.getConcept() instanceof ASTMCConcept) {
            ASTConceptAntlr conceptAntlr = ((ASTMCConcept) concept.getConcept()).getConceptAntlr();
            conceptAntlr.getAntlrParserActionList().forEach(a -> addAdditionalParserJavaCode(a.getText()));
            conceptAntlr.getAntlrLexerActionList().forEach(a -> addAdditionalLexerJavaCode(a.getText()));
          }
        }
      }
    }
  }
  
  /**
   * @param action the java code to add
   */
  protected void addAdditionalParserJavaCode(ASTJavaCodeExt action) {
    additionalParserJavaCode.add(ParserGeneratorHelper.getText(action));
  }
  
  /**
   * @param action the java code to add
   */
  protected void addAdditionalLexerJavaCode(ASTJavaCodeExt action) {
    additionalLexerJavaCode.add(ParserGeneratorHelper.getText(action));
  }
  
  // ------------- Handling of keywords -----------------------------
  
  public Set<String> getKeywords() {
    return Collections.unmodifiableSet(keywords);
  }
  
  /**
   * Checks if the terminal or constant <code>name</code> is a keyword and could
   * be replaced by a name
   * 
   * @param name - rule to check
   * @return true, if the terminal or constant <code>name</code> is a keyword and could
   * be replaced by a name
   */
  public boolean isKeyword(String name) {
    return keywords.contains(name);
  }

  public List<PredicatePair> getSubRulesForParsing(String ruleName) {
    // Consider superclass
    Optional<ProdSymbol> ruleByName = grammarSymbol.getProdWithInherited(ruleName);
    List<PredicatePair> predicateList = Lists.newArrayList();
    if (ruleByName.isEmpty()) {
      return predicateList;
    }
    
    if (predicats.containsKey(ruleName)) {
      predicateList.addAll(predicats.get(ruleName));
    }

    return predicateList;
  }
  
  /**
   * @return lexNamer
   */
  public LexNamer getLexNamer() {
    return this.lexNamer;
  }

  
  /**
   * Iterates over all Rules to find all keywords
   */
  protected void findAllKeywords() {
    for (ProdSymbol ruleSymbol : grammarSymbol.getProdsWithInherited().values()) {
      if (ruleSymbol.isParserProd()) {
        if (ruleSymbol.isPresentAstNode() && ruleSymbol.getAstNode() instanceof ASTClassProd) {
          ASTClassProd astProd = (ASTClassProd) ruleSymbol.getAstNode();
          if (astProd.getAltList().isEmpty()) {
            // if a rule has been overwritten and is empty, consider the superclass
            for (MCGrammarSymbolSurrogate g : grammarSymbol.getSuperGrammars()) {
              final Optional<ProdSymbol> ruleByName = g.lazyLoadDelegate().getProdWithInherited(astProd.getName());
              if (ruleByName.isPresent() && ruleByName.get().isClass()) {
                if (ruleByName.get().isPresentAstNode() && ruleByName.get().getAstNode() instanceof ASTClassProd) {
                  astProd = (ASTClassProd) ruleByName.get().getAstNode();
                }
              }
            }
          }
          TerminalVisitor tv = new TerminalVisitor();
          Grammar_WithConceptsTraverser traverser = Grammar_WithConceptsMill.traverser();
          traverser.add4Grammar(tv);
          astProd.accept(traverser);
        }
      }
    }
    
  }

  protected class TerminalVisitor implements GrammarVisitor2 {

    public GrammarTraverser getTraverser() {
      return traverser;
    }

    public void setTraverser(GrammarTraverser traverser) {
      this.traverser = traverser;
    }

    GrammarTraverser traverser;

    @Override
    public void visit(ASTTerminal keyword) {
      if (NAME_PATTERN.matcher(keyword.getName()).matches()) {
        keywords.add(keyword.getName());
      }
    }

    @Override
    public void visit(ASTConstant keyword) {
      if (NAME_PATTERN.matcher(keyword.getName()).matches()) {
        keywords.add(keyword.getName());
      }
    }
  }
  
}
