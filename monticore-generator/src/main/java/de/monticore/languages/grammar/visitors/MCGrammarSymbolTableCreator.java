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

package de.monticore.languages.grammar.visitors;

import com.google.common.collect.Sets;
import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.cocos.GrammarCoCos;
import de.monticore.grammar.cocos.GrammarInheritanceCycle;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.languages.grammar.*;
import de.monticore.languages.grammar.symbolreferences.MCExternalTypeSymbolReference;
import de.monticore.languages.grammar.symbolreferences.MCGrammarSymbolReference;
import de.monticore.languages.grammar.symbolreferences.MCRuleSymbolReference;
import de.monticore.languages.grammar.symbolreferences.MCTypeSymbolReference;
import de.monticore.symboltable.*;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static de.se_rwth.commons.Names.getQualifiedName;

public class MCGrammarSymbolTableCreator extends CommonSymbolTableCreator implements Grammar_WithConceptsVisitor {
  
  private final Grammar_WithConceptsPrettyPrinter prettyPrinter;
  
  private String packageName = "";
  
  private MCGrammarSymbol grammarSymbol;
  
  public MCGrammarSymbolTableCreator(
      ResolverConfiguration resolverConfig,
      @Nullable MutableScope enclosingScope,
      Grammar_WithConceptsPrettyPrinter prettyPrinter) {
    super(resolverConfig, enclosingScope);
    
    this.prettyPrinter = prettyPrinter;
  }
  
  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public Scope createFromAST(ASTMCGrammar rootNode) {
    Log.errorIfNull(rootNode);
    handle(rootNode);
    return getFirstCreatedScope();
  }
  
  // PN is tested
  @Override
  public void visit(ASTMCGrammar astGrammar) {
    Log.debug("Building Symboltable for Grammar: " + astGrammar.getName(),
        MCGrammarSymbolTableCreator.class.getSimpleName());
    
    packageName = getQualifiedName(astGrammar.getPackage());
    
    final List<ImportStatement> imports = new ArrayList<>();
    if (astGrammar.getImportStatements() != null) {
      for (ASTMCImportStatement imp : astGrammar.getImportStatements()) {
        imports.add(new ImportStatement(getQualifiedName(imp.getImportList()), imp
            .isStar()));
      }
    }
    
    final ArtifactScope scope = new ArtifactScope(Optional.empty(), packageName, imports);
    putOnStack(scope);
    
    final String grammarName = astGrammar.getName();

    grammarSymbol = MCGrammarSymbolsFactory.createMCGrammarSymbol(grammarName);
    grammarSymbol.setComponent(astGrammar.isComponent());
    // TODO PN set package name explicitly?

    addToScopeAndLinkWithNode(grammarSymbol, astGrammar);
    
    addSuperGrammars(astGrammar, grammarSymbol);
  }
  
  // PN is tested
  private void addSuperGrammars(ASTMCGrammar astGrammar, MCGrammarSymbol grammarSymbol) {
    // Step 1: Load symbol tables of supergrammars
    Log.debug("Step 1: Load symbol tables of supergrammars",
        MCGrammarSymbolTableCreator.class.getSimpleName());
    for (ASTGrammarReference ref : astGrammar.getSupergrammar()) {
      String superGrammarName = getQualifiedName(ref.getNames());
      
      if (superGrammarName.contains(".")) {
        MCGrammarSymbolReference superGrammar = new MCGrammarSymbolReference(superGrammarName,
            currentScope().orElse(null));
        grammarSymbol.addSuperGrammar(superGrammar);
      }
      else {
        Log.error("0xA2043 The name of a super grammar must be a qualified name but: "
            + superGrammarName);
      }
    }
  }
  
  @Override
  public void endVisit(ASTMCGrammar astGrammar) {
    setEnclosingScopeOfNodes(astGrammar);
    new GrammarInheritanceCycle().check(astGrammar);
    new GrammarCoCos().getSymbolTableCoCoChecker().handle(astGrammar);
    createMissingTypesForASTRules(astGrammar);


    astGrammar.getClassProds().forEach(this::setSuperTypesOfClassProdTypes);
    
    addASTRuleTypesToTypeSymbols(astGrammar.getASTRules());
    addSubRules(astGrammar.getClassProds());
    addSubRules(astGrammar.getInterfaceProds());
    
    computeStartParserRule(astGrammar);
    
    // Level 3x: Consider follow options (requires 3b, should normally be
    // done in 3a)
    setFollowOptions(astGrammar);
    
    // Step 3b:
    // Build up type system of the grammar (All types are set up) - s.
    // visit-methods
    
    // Step 3d:
    // Consider interfaces and abstract rules that override the standard
    // Add additional inheritance for grammarlanguage inheritance
    checkLanguageInheritance();
    
    // Step 3e:
    // Adding of elements by concepts; is handled by concept visitors now
    
    // ----------------------------------------------------------------------
    // --
    // The type system is complete in the sense that no new types are added
    // after step 3

    // Step 4a:
    // Check all occurences of types and production references in the AST
    // before building up attributes
    checkTypesAndProds(astGrammar);
    
    // Step 4b:
    // Check all occurences of types and production references in concepts
    // before building up attributes; is handled by concept check visitors now
    
    // Step 5a:
    // Build up Attributes from grammar
    AttributesForMCTypeSymbolCreator
        .createAttributes(astGrammar, grammarSymbol, currentScope().orElse(null), prettyPrinter);
    
    // Step 5b:
    // Build up Attributes from concepts; is handled by concept visitors now
    
    // Level 6a:
    // Further checks, including attributes; is handled by check visitors now
    
    // Level 6b:
    // Check context conditions of concepts; is handled by concept check
    // visitors now
    
    // Build up Attributes from concepts
    
    removeCurrentScope();
    

    
  }
  
  /**
   * If only an ast rule <code>R</code> is defined without a corresponding
   * "normal" production <code>R</code>, e.g., <code>R = ...</code>, no type for
   * <code>R</code> will be created. This method creates the missing types for
   * those cases.
   *
   * @param astGrammar the grammar ast
   */
  private void createMissingTypesForASTRules(ASTMCGrammar astGrammar) {
    for (ASTASTRule rule : astGrammar.getASTRules()) {
      MCTypeSymbol typeSymbol = grammarSymbol.getTypeWithInherited(rule.getType());
      if (typeSymbol == null) {
        typeSymbol = MCGrammarSymbolsFactory.createMCTypeSymbol(rule.getType(),
            grammarSymbol, rule);
        
        // TODO NN <- PN is EXTERN the correct kind for this case? - It leads to
        // problems e.g. for MethodInvocation in JavaDSL (GV)
        // typeSymbol.setKindOfType(MCTypeSymbol.KindType.EXTERN);
        
        grammarSymbol.addType(typeSymbol);
      }
    }
  }
  
  /**
   * Adds super classes and interfaces of an ast rule to the corresponding type
   * symbol. For example, <code>ast R extends S = ...</code> adds <code>S</code>
   * as super type to the type symbol of <code>R</code>.
   *
   * @param astRuleList the ast node
   */
  private void addASTRuleTypesToTypeSymbols(ASTASTRuleList astRuleList) {
    for (ASTASTRule astRule : astRuleList) {
      
      final MCTypeSymbol typeSymbol = grammarSymbol.getTypeWithInherited(astRule.getType());
      
      if (typeSymbol == null) {
        Log.error("0xA1011 Undefined rule type '" + astRule.getType() + "' Pos: " + astRule
            .get_SourcePositionStart());
      }
      
      for (ASTGenericType astSuperClass : astRule.getASTSuperClass()) {
        final MCTypeSymbol superType = createTypeReference(astSuperClass.getTypeName(),
            astSuperClass.isExternal(), astSuperClass);
        
        // TODO GV: check
        // TODO NN <- PN wenn superType eine Referenz ist, macht es keinen Sinn
        // das MCGrammarSymbol zu setzen??
        superType.setGrammarSymbol(grammarSymbol);
        
        if (typeSymbol.getKindOfType().equals(MCTypeSymbol.KindType.CLASS)) {
          typeSymbol.addSuperClass(superType, true);
        }
        else if (typeSymbol.getKindOfType().equals(MCTypeSymbol.KindType.INTERFACE)) {
          typeSymbol.addSuperInterface(superType, true);
        }
      }
      
      for (ASTGenericType astSuperInterface : astRule.getASTSuperInterface()) {
        MCTypeSymbol superType = createTypeReference(astSuperInterface.getTypeName(),
            astSuperInterface.isExternal(), astSuperInterface);
        
        if (typeSymbol.getKindOfType().equals(MCTypeSymbol.KindType.CLASS)) {
          typeSymbol.addSuperInterface(superType, true);
        }
      }
      
      for (ASTAttributeInAST astAttribute : astRule.getAttributeInASTs()) {
        if (astAttribute.getGenericType().isExternal()) {
          // TODO NN<-PN do something? Previously, nothing seemed to happen:
          // getOrCreateTypeReference(astAttribute.getTypeReference().getTypeName(),
          // true,
          // astAttribute.getTypeReference());
        }
      }
      
    }
  }
  
  /**
   * Add all sub/superule-relations to the symbol table form the perspective of
   * the super rule by using addSubrule
   *
   * @param classProds Rule
   */
  private void addSubRules(ASTClassProdList classProds) {
    for (ASTClassProd classProd : classProds) {
      for (ASTRuleReference superRule : classProd.getSuperRule()) {
        MCRuleSymbol prodByName = grammarSymbol.getRuleWithInherited(superRule.getTypeName());
        if (prodByName != null) {
          addSubrule(superRule.getTypeName(), HelperGrammar.getRuleName(classProd), superRule);
        }
        else {
          undefinedRuleError(superRule.getTypeName(), superRule.get_SourcePositionStart());
        }
      }
      
      for (ASTRuleReference ruleref : classProd.getSuperInterfaceRule()) {
        MCRuleSymbol prodByName = grammarSymbol.getRuleWithInherited(ruleref.getTypeName());
        if (prodByName != null) {
          addSubrule(ruleref.getTypeName(), HelperGrammar.getRuleName(classProd), ruleref);
        }
        else {
          undefinedRuleError(ruleref.getTypeName(), ruleref.get_SourcePositionStart());
        }
      }
    }
    
  }
  
  private void undefinedRuleError(String name, SourcePosition pos) {
    Log.error("0xA0964 Undefined rule: " + name + " Pos: " + pos);
  }
  
  /**
   * Add all sub/superule-realtions to the symboltable form the perspective of
   * the superrule by using addSubrule
   *
   * @param interfaceProdList Rule
   */
  private void addSubRules(ASTInterfaceProdList interfaceProdList) {
    for (ASTInterfaceProd r : interfaceProdList) {
      for (ASTRuleReference ruleRef : r.getSuperInterfaceRule()) {
        String typeName = ruleRef.getTypeName();
        MCRuleSymbol prodByName = grammarSymbol.getRuleWithInherited(typeName);
        if (prodByName != null) {
          addSubrule(ruleRef.getTypeName(), r.getName(), ruleRef);
        }
        else {
          undefinedRuleError(typeName, ruleRef.get_SourcePositionStart());
        }
      }
    }
  }
  
  private void addSubrule(String superrule, String subrule, ASTRuleReference ruleReference) {
    ASTRuleComponent component = null;
    if (ruleReference.getSemanticpredicateOrAction().isPresent()) {
      if (ruleReference.getSemanticpredicateOrAction().get().isPredicate()) {
        component = ruleReference.getSemanticpredicateOrAction().get();
     }
    }
    PredicatePair subclassPredicatePair = new PredicatePair(subrule, component);
    grammarSymbol.addPredicate(superrule, subclassPredicatePair);
  }
  
  private void computeStartParserRule(ASTMCGrammar astGrammar) {
    if (astGrammar.getStartRules().isPresent()) {
      String name = astGrammar.getStartRules().get().getRuleReference().getName();
      MCRuleSymbol rule = grammarSymbol.getRuleWithInherited(name);
      if (rule == null) {
        Log.error("0xA0243 Rule " + name + " couldn't be found!");
      }
      else {
        rule.setStartRule(true);
        grammarSymbol.setStartRule(rule);
      }      
    } else {
      final Set<ASTProd> firstProductions = Sets.newLinkedHashSet();
      // The start rule for parsing is the first occurring Interface-, Abstract-
      // or Class-Production in this grammar
      if (astGrammar.getClassProds().size() != 0) {
        firstProductions.add(astGrammar.getClassProds().get(0));
      }
      if (astGrammar.getInterfaceProds().size() != 0) {
        firstProductions.add(astGrammar.getInterfaceProds().get(0));
      }
      if (astGrammar.getAbstractProds().size() != 0) {
        firstProductions.add(astGrammar.getAbstractProds().get(0));
      }
      setStartProd(firstProductions);
    }
  }
  
  /**
   * Set start parser production
   */
  private void setStartProd(Set<ASTProd> firstProductions) {
    // Set start parser rule
    ASTProd firstProduction = null;
    for (ASTProd prod : firstProductions) {
      // TODO: add a common interface to the MC grammar for all these
      // productions and remove this heck
      if ((firstProduction == null)
          || (firstProduction.get_SourcePositionStart().compareTo(prod.get_SourcePositionStart()) > 0)) {
        firstProduction = prod;
      }
    }
    
    if (firstProduction != null) {
      MCRuleSymbol rule = grammarSymbol.getRuleWithInherited(firstProduction.getName());
      if (rule == null) {
        Log.error("0xA2074 Rule " + firstProduction.getName() + " couldn't be found! Pos: "
            + firstProduction.get_SourcePositionStart());
      }
      else {
        rule.setStartRule(true);
        grammarSymbol.setStartRule(rule);
      }
    }
  }
  
  private void setFollowOptions(ASTMCGrammar astGrammar) {
    // Level 3x: Consider follow options (requires 3b, should normally be done
    // in 3a)
    Log.debug("Level 3x: Consider follow options",
        MCGrammarSymbolTableCreator.class.getSimpleName());
    if (astGrammar.getGrammarOptions().isPresent()) {
      for (ASTFollowOption v : astGrammar.getGrammarOptions().get().getFollowOptions()) {
        MCRuleSymbol ruleByName = grammarSymbol.getRuleWithInherited(v.getProdName());
        if (ruleByName == null) {
          Log.error("0xA0261 Superclass " + v.getProdName() + " refers to a non-existing rule!");
        }
        else {
          ruleByName.setFollow(v.getAlt());
        }
      }
    }
  }
  
  private void checkLanguageInheritance() {
    // Step 3d:
    // Consider interfaces and abstract rules that override the standard
    // Add additional inheritance for grammarlanguage inheritance
    Log.debug("Step 3d: Consider interfaces and abstract rules that override the standard",
        MCGrammarSymbolTableCreator.class.getSimpleName());
    for (MCTypeSymbol type : grammarSymbol.getTypes()) {
      
      // TODO NN<-PN What is this for? Here the prefix "super." is added and in
      // getTypeWithInherited()
      // it then dropped again. Why?!
      MCTypeSymbol superType = grammarSymbol.getTypeWithInherited("super." + type.getName());
      
      // Found superType in other grammar
      if (superType != null) {
        
        // Check that new rule does not add superclasses
        if (type.getAllSuperclasses().size() > 0) {
          Log.error("0xA0241 The overridden type " + type.getName() + " cannot have superTypes, "
              + "but: " + type.getAllSuperclasses().get(0) + ". Pos:" + type.getSourcePosition());
        }
        
        // Add superclass or super interface
        if (superType.getKindOfType().equals(MCTypeSymbol.KindType.CLASS)) {
          type.addSuperClass(
              grammarSymbol.getTypeWithInherited("super." + superType.getName()), true);
        }
        else if (superType.getKindOfType().equals(MCTypeSymbol.KindType.INTERFACE)) {
          type.addSuperInterface(
              grammarSymbol.getTypeWithInherited("super." + superType.getName()), true);
        }
        
        if (!superType.getKindOfType().equals(type.getKindOfType())) {
          Log.error("0xA0242 Overridden rule must remain same type: " + type.getName() + " (" +
              type.getKindOfType() + " vs. " + superType.getKindOfType() + "). Pos: "
              + type.getSourcePosition());
        }
      }
    }
  }
  
  // TODO NN <- PN check
  private void checkTypesAndProds(ASTMCGrammar astGrammar) {
    // Step 4a:
    // Check all occurences of types and production references in the AST before
    // building up
    // attributes
    Log.debug("Step 4a: Check all occurences of types and production references in the AST",
        MCGrammarSymbolTableCreator.class.getSimpleName());
    
    TypeCheckVisitor typeCheckVisitor = new TypeCheckVisitor(grammarSymbol);
    typeCheckVisitor.check(astGrammar);
    
    if (typeCheckVisitor.hasFatalError()) {
      Log.error("0xA1012 Found fatal error, stopped after step 4a!");
    }
  }
  
  /**
   * An interface production always defines a type itself. It never references a
   * type, i.e., interface A:B = ... is not allowed.
   */
  @Override
  public void visit(ASTInterfaceProd astInterfaceProd) {
    final String typeName = astInterfaceProd.getName();
    
    final MCTypeSymbol definedType =
        MCGrammarSymbolsFactory.createMCTypeSymbol(typeName, grammarSymbol, astInterfaceProd);
    // TODO GV: Check handling of external interfaces
    definedType.setKindOfType(MCTypeSymbol.KindType.INTERFACE);
    definedType.setInterface(true);
    
    // Setup super interfaces
    // A implements B
    for (ASTRuleReference astSuperInterface : astInterfaceProd.getSuperInterfaceRule()) {
      final String superTypeName = astSuperInterface.getTypeName();
      MCTypeSymbol superType = createTypeReference(superTypeName, astSuperInterface.isExternal(),
          astSuperInterface);
      definedType.addSuperInterface(superType, false);
    }
    
    // A astimplements B
    for (ASTGenericType astInterface : astInterfaceProd.getASTSuperInterface()) {
      String superTypeName = astInterface.getTypeName();
      MCTypeSymbol superType = createTypeReference(superTypeName, astInterface.isExternal(),
          astInterface);
      definedType.addSuperInterface(superType, true);
    }
    
    putInScopeIfNotExists(definedType);
    
    MCInterfaceOrAbstractRuleSymbol ruleSymbol = MCGrammarSymbolsFactory
        .createMCInterfaceRuleOrAbstractProdSymbol(astInterfaceProd, astInterfaceProd.getName(), true);
    
    ruleSymbol.setDefinedType(definedType);

    final Optional<ASTSymbolDefinition> symbolDefinition = astInterfaceProd.getSymbolDefinition();

    setSymbolDefinition(ruleSymbol, symbolDefinition);

    // Add Grammardoc comment
    for (Comment c : astInterfaceProd.get_PreComments()) {
      if (c.getText().startsWith("/**")) {
        ruleSymbol.getDefinedType().addComment(c);
      }
    }
    
    ruleSymbol.setGrammarSymbol(grammarSymbol);
    addToScopeAndLinkWithNode(ruleSymbol, astInterfaceProd);
  }

  private void setSymbolDefinition(MCRuleSymbol ruleSymbol, Optional<ASTSymbolDefinition> symbolDefinition) {
    if (symbolDefinition.isPresent()) {
      if (symbolDefinition.get().getSymbolKind().isPresent() && !symbolDefinition.get().getSymbolKind().get().isEmpty()) {
        final String symbolKindName = symbolDefinition.get().getSymbolKind().get();
        if (symbolKindName.equals(ruleSymbol.getName())) {
          ruleSymbol.setRuleDefiningSymbolKind(ruleSymbol);
        }
        else {
          final MCRuleSymbolReference reference = new MCRuleSymbolReference(symbolKindName, ruleSymbol.getSpannedScope());
          ruleSymbol.setRuleDefiningSymbolKind(reference);
        }
      }
      else {
        ruleSymbol.setRuleDefiningSymbolKind(ruleSymbol);
      }
    }
  }

  @Override
  public void endVisit(ASTInterfaceProd astInterfaceProd) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTLexProd astLexProd) {
    final MCLexRuleSymbol ruleSymbol = createLexProd(astLexProd, currentScope().get());
    
    ruleSymbol.setGrammarSymbol(grammarSymbol);
    addToScopeAndLinkWithNode(ruleSymbol, astLexProd);
  }
  
  @Override
  public void endVisit(ASTLexProd astLexProd) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTClassProd astClassProd) {
    final MCTypeSymbol definedType = createTypeForClassProd(astClassProd);
    
    MCClassRuleSymbol ruleSymbol = null;
    if (astClassProd.getAlts().size() == 0) {
      
      for (MCGrammarSymbol g : grammarSymbol.getSuperGrammars()) {
        final MCRuleSymbol ruleByName = g.getRuleWithInherited(astClassProd.getName());
        if (ruleByName instanceof MCClassRuleSymbol) {
          Optional<ASTClassProd> astProd = ((MCClassRuleSymbol) ruleByName).getRuleNode();
          if (astProd.isPresent()) {
            // TODO NN<-PN why do we create a rule symbol for a rule node of a
            // super grammar??
            ruleSymbol = MCGrammarSymbolsFactory.createMCClassProdSymbol(astProd.get());
            break;
          }
        }
      }
    }
    
    if (ruleSymbol == null) {
      ruleSymbol = MCGrammarSymbolsFactory.createMCClassProdSymbol(astClassProd);
    }
    
    ruleSymbol.setDefinedType(definedType);

    setSymbolDefinition(ruleSymbol, astClassProd.getSymbolDefinition());
    
    // Add Grammardoc comment
    for (Comment c : astClassProd.get_PreComments()) {
      if (c.getText().startsWith("/**")) {
        ruleSymbol.getDefinedType().addComment(c);
      }
    }
    
    ruleSymbol.setGrammarSymbol(grammarSymbol);
    addToScopeAndLinkWithNode(ruleSymbol, astClassProd);
  }
  
  public void endVisit(ASTClassProd astClassProd) {
    removeCurrentScope();
  }
  
  // TODO PN Wrapper für ASTAbstractProd und ASTInterfaceProd schreiben, um die
  // visit-Methoden
  // zusammenzufassen
  @Override
  public void visit(ASTAbstractProd astAbstractProd) {
    // Create defined type if not already created
    String typeName = astAbstractProd.getName();
    MCTypeSymbol definedType = getOrCreateType(typeName, false, astAbstractProd);
    // definedType.setEntryState(STEntryState.FULL, definedType);
    definedType.setKindOfType(MCTypeSymbol.KindType.CLASS);
    definedType.setAbstract(true);
    
    // Setup superclasses and superinterfaces
    // A extends B
    for (ASTRuleReference superRule : astAbstractProd.getSuperRule()) {
      String superTypeName = superRule.getTypeName();
      MCTypeSymbol superType = createTypeReference(superTypeName, superRule.isExternal(),
          superRule);
      definedType.addSuperClass(superType, false);
    }
    
    // A astextends B
    for (ASTGenericType astSuperClass : astAbstractProd.getASTSuperClass()) {
      String superTypeName = astSuperClass.getTypeName();
      MCTypeSymbol superType = createTypeReference(superTypeName, astSuperClass.isExternal(),
          astSuperClass);
      definedType.addSuperClass(superType, true);
    }
    
    // A implements B
    for (ASTRuleReference astInterface : astAbstractProd.getSuperInterfaceRule()) {
      String superTypeName = astInterface.getTypeName();
      MCTypeSymbol superType = createTypeReference(superTypeName, astInterface.isExternal(),
          astInterface);
      definedType.addSuperInterface(superType, false);
    }
    
    // A astimplements B
    for (ASTGenericType astInterface : astAbstractProd.getASTSuperInterface()) {
      String superTypeName = astInterface.getTypeName();
      MCTypeSymbol superType = createTypeReference(superTypeName, astInterface.isExternal(),
          astInterface);
      definedType.addSuperInterface(superType, true);
    }
    
    grammarSymbol.addType(definedType);
    
    MCInterfaceOrAbstractRuleSymbol ruleSymbol = MCGrammarSymbolsFactory
        .createMCInterfaceRuleOrAbstractProdSymbol(astAbstractProd, astAbstractProd.getName(), false);
    
    // Set defined type of this rule
    ruleSymbol.setType(definedType);

    setSymbolDefinition(ruleSymbol, astAbstractProd.getSymbolDefinition());
    
    // Add Grammardoc comment
    for (Comment c : astAbstractProd.get_PreComments()) {
      if (c.getText().startsWith("/**")) {
        ruleSymbol.getDefinedType().addComment(c);
      }
    }
    
    ruleSymbol.setGrammarSymbol(grammarSymbol);
    
    addToScopeAndLinkWithNode(ruleSymbol, astAbstractProd);
  }
  
  @Override
  public void endVisit(ASTAbstractProd astAbstractProd) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTExternalProd astExternalProd) {
    // Create defined type if not already created and set it as defined type of
    // this rule
    String typeName = astExternalProd.getName();
    MCExternalTypeSymbol definedType;
    
    if (grammarSymbol.getType(typeName) == null) {
      if (astExternalProd.getGenericType().isPresent()) {
        String printGenericType = astExternalProd.getGenericType().get().getTypeName();
        definedType = getOrCreateExternalType(printGenericType);
        definedType.setASTNode(true);
      }
      else {
        // TODO NN<-PN create reference?
        definedType = MCGrammarSymbolsFactory.createMCExternalTypeSymbol("mc.ast.ASTNode",
            grammarSymbol);
        definedType.setASTNode(true);
        Log.debug("Create external type " + definedType, MCGrammarSymbolTableCreator.class
            .getSimpleName());
      }
      
      grammarSymbol.addType(definedType);
      
      MCRuleSymbol ruleSymbol = MCGrammarSymbolsFactory.createMCExternalProdSymbol(astExternalProd,
          astExternalProd.getName());
      
      ruleSymbol.setType(definedType);

      setSymbolDefinition(ruleSymbol, astExternalProd.getSymbolDefinition());
      
      // Add Grammardoc comment
      for (Comment c : astExternalProd.get_PreComments()) {
        if (c.getText().startsWith("/**")) {
          if (ruleSymbol.getType() != null) {
            ruleSymbol.getType().addComment(c);
          }
        }
      }
      
      ruleSymbol.setGrammarSymbol(grammarSymbol);
      addToScopeAndLinkWithNode(ruleSymbol, astExternalProd);
    }
  }
  
  @Override
  public void endVisit(ASTExternalProd astExternalProd) {
    removeCurrentScope();
  }
  
  @Override
  public void visit(ASTEnumProd astEnumProd) {
    // Create defined type if not already created
    final String typeName = astEnumProd.getName();
    final MCTypeSymbol definedType = getOrCreateType(typeName, false, astEnumProd);
    
    definedType.setKindOfType(MCTypeSymbol.KindType.ENUM);
    
    grammarSymbol.addType(definedType);
    
    final MCEnumRuleSymbol ruleSymbol = MCGrammarSymbolsFactory.createMCEnumProdSymbol(astEnumProd);
    
    // Set defined type of this rule
    ruleSymbol.setType(definedType);
    
    // Add Grammardoc comment
    for (Comment c : astEnumProd.get_PreComments()) {
      if (c.getText().startsWith("/**")) {
        ruleSymbol.getDefinedType().addComment(c);
      }
    }
    
    ruleSymbol.setGrammarSymbol(grammarSymbol);
    addToScopeAndLinkWithNode(ruleSymbol, astEnumProd);
  }
  
  @Override
  public void endVisit(ASTEnumProd astEnumProd) {
    removeCurrentScope();
  }
  
  /**
   * Create entry for an implicit rule defined in another lexrule by using an
   * action and changing the type of the token
   */
  @Override
  public void visit(ASTLexActionOrPredicate action) {
    
    List<String> types = HelperGrammar.findImplicitTypes(action, prettyPrinter);
    for (String typeName : types) {
      // Create type if not already created
      final MCTypeSymbol mcType = getOrCreateType(typeName, false, action);
      
      mcType.setConvertFunction(HelperGrammar.createStringConvertFunction(typeName));
      mcType.setLexType("String");
      mcType.setKindOfType(MCTypeSymbol.KindType.IDENT);
      
      grammarSymbol.addType(mcType);
      
      // Create rule if needed
      MCRuleSymbol rule = grammarSymbol.getRule(typeName);
      if (rule == null) {
        // Create entry for an implicit rule
        MCRuleSymbol stLexRule = MCGrammarSymbolsFactory.createMCLexProdSymbol(typeName);
        grammarSymbol.addRule(stLexRule);
        // set type
        stLexRule.setType(mcType);
      }
    }
    
  }
  
  @Override
  public void visit(ASTTerminal astNode) {
    // TODO NN<-PN was astNode.getVariableName() is ignored. Is this ok?
    String usageName = astNode.getUsageName().orElse(null);
    
    String astName = astNode.getName();
    MCRuleComponentSymbol entry =
        addRuleComponent(astName, astNode, usageName, MCRuleComponentSymbol
            .KindRuleComponent.TERMINAL);
    
    setComponentMultiplicity(entry, astNode.getIteration());
  }
  
  @Override
  public void visit(ASTNonTerminal astNode) {
    // TODO NN<-PN was astNode.getVariableName() is ignored. Is this ok?
    String usageName = astNode.getUsageName().orElse(null);
    
    MCRuleComponentSymbol entry =
        addRuleComponent(astNode.getName(), astNode, usageName, MCRuleComponentSymbol
            .KindRuleComponent.NONTERMINAL);
    
    if (entry != null) {
      entry.setReferencedRuleName(astNode.getName());
      entry.setReferencedSymbolName(astNode.getReferencedSymbol().orElse(""));
      setComponentMultiplicity(entry, astNode.getIteration());
    }
    
  }
  
  void setComponentMultiplicity(MCRuleComponentSymbol entry, int iteration) {
    if (entry == null) {
      return;
    }
    
    if ((iteration == ASTConstantsGrammar.PLUS) || (iteration == ASTConstantsGrammar.STAR)) {
      entry.setList(true);
    }
    else if (iteration == ASTConstantsGrammar.QUESTION) {
      entry.setOptional(true);
    }
  }
  
  @Override
  public void visit(ASTLexNonTerminal astNode) {
    // TODO NN<-PN use astNode.getVariable()?
    addRuleComponent(nullToEmpty(astNode.getName()), astNode, astNode.getVariable().orElse(null),
        MCRuleComponentSymbol.KindRuleComponent.LEXNONTERMINAL);
  }
  
  @Override
  public void visit(ASTConstantGroup astNode) {
    // TODO NN<-PN use astNode.getVariable()?
    addRuleComponent(astNode.getUsageName().orElse(""), astNode, astNode.getUsageName()
        .orElse(null),
        MCRuleComponentSymbol.KindRuleComponent.CONSTANTGROUP);
  }
  
  @Override
  public void visit(ASTConstant astNode) {
    addRuleComponent(astNode.getName(), astNode, astNode.getHumanName().orElse(null),
        MCRuleComponentSymbol.KindRuleComponent.CONSTANT);
  }
  
  private MCRuleComponentSymbol addRuleComponent(String name, ASTNode node, String usageName,
      MCRuleComponentSymbol.KindRuleComponent kind) {
    Symbol currentSymbol = currentSymbol().orElse(null);
    if (currentSymbol != null) {
      
      String symbolName = isNullOrEmpty(usageName) ? name : usageName;
      MCRuleComponentSymbol ruleComponent = MCGrammarSymbolsFactory
          .createRuleComponentSymbol(symbolName);
      ruleComponent.setKindOfRuleComponent(kind);
      
      ruleComponent.setGrammarSymbol(grammarSymbol);
      
      ruleComponent.setUsageName(usageName);
      
      if (currentSymbol instanceof MCRuleSymbol) {
        MCRuleSymbol surroundingRule = (MCRuleSymbol) currentSymbol;
        ruleComponent.setEnclosingRule(surroundingRule);
        surroundingRule.addRuleComponent(ruleComponent);
      }
      else {
        addToScope(ruleComponent);
      }
      
      setLinkBetweenSymbolAndNode(ruleComponent, node);
      
      return ruleComponent;
    }
    
    return null;
    
  }
  
  private MCLexRuleSymbol createLexProd(final ASTLexProd astLexProd, final MutableScope scope) {
    // Create defined type if not already created
    final String typeName = astLexProd.getName();
    
    // TODO NN <- PN How is a type for a lexer production defined?
    
    final MCTypeSymbol definedType = getOrCreateType(typeName, false, astLexProd);
    
    definedType.setKindOfType(MCTypeSymbol.KindType.IDENT);
    
    definedType.setConvertFunction(HelperGrammar.createConvertFunction(astLexProd, prettyPrinter));
    definedType.setLexType(HelperGrammar.createConvertType(astLexProd));
    
    grammarSymbol.addType(definedType);
    
    final MCLexRuleSymbol ruleProd = MCGrammarSymbolsFactory.createLexProdSymbol(astLexProd);
    
    // Set defined type of this rule
    ruleProd.setType(definedType);
    // Add Grammardoc comment
    for (Comment c : astLexProd.get_PreComments()) {
      if (c.getText().startsWith("/**")) {
        ruleProd.getDefinedType().addComment(c);
      }
    }
    
    return ruleProd;
    
  }
  
  // PN: ok
  private MCTypeSymbol createTypeReference(String typeName, boolean isExternal, ASTNode refNode) {
    MCTypeSymbol definedType;
    
    if (isExternal) {
      definedType = new MCExternalTypeSymbolReference(typeName, grammarSymbol, currentScope()
          .orElse(null));
    }
    else {
      definedType = new MCTypeSymbolReference(typeName, grammarSymbol, currentScope().orElse(null));
    }
    
    definedType.setAstNode(refNode);
    
    return definedType;
  }
  
  private MCTypeSymbol getOrCreateType(String typeName, boolean isExtrenal, ASTNode refNode) {
    MCTypeSymbol definedType = grammarSymbol.getType(typeName);
    if (definedType == null) {
      if (isExtrenal) {
        return getOrCreateExternalType(typeName);
      }
      else {
        definedType = MCGrammarSymbolsFactory.createMCTypeSymbol(typeName, grammarSymbol, refNode);
      }
    }
    return definedType;
  }
  
  /**
   * Finds or creates an external type (reference)
   *
   * @param name name of the type
   */
  private MCExternalTypeSymbol getOrCreateExternalType(String name) {
    MCTypeSymbol type = grammarSymbol.getType(name);
    if (type instanceof MCExternalTypeSymbol) {
      return (MCExternalTypeSymbol) type;
    }
    MCExternalTypeSymbol createdType = new MCExternalTypeSymbolReference(name, grammarSymbol,
        currentScope().orElse(null));
    // TODO GV: change name?
    Log.debug("Create external type " + name, MCGrammarSymbolTableCreator.class.getSimpleName());
    return createdType;
  }
  
  /**
   * The type of a production <code>A:T = ...</code> is <code>T</code>. If no
   * type is stated explicitly, an implicit type is created. E.g., for
   * <code>A = ...</code> the same-named type <code>A</code> is created. TODO NN
   * <- PN Is T always a type reference? Or is T defined, if it does not exist
   * yet?
   *
   * @param astClassProd
   * @return
   */
  protected MCTypeSymbol createTypeForClassProd(ASTClassProd astClassProd) {
    // Create defined type if not already created
    final String typeName = HelperGrammar.getRuleName(astClassProd);
    
    // case: A = ... where A is implicit type defined by the production A
    // We need to define a type A in the enclosing scope
    MCTypeSymbol definedType = MCGrammarSymbolsFactory.createMCTypeSymbol(typeName, grammarSymbol,
        astClassProd);
    definedType.setKindOfType(MCTypeSymbol.KindType.CLASS);
    
    grammarSymbol.addType(definedType);
    
    return definedType;
  }
  
  /**
   * References should not be resolved during the symbol table creation. Hence,
   * this method is invoked at the end of the creation phase and finishes the
   * creation of the class production types.
   *
   * @param astClassProd the class production ast node
   */
  private void setSuperTypesOfClassProdTypes(ASTClassProd astClassProd) {
    final MCClassRuleSymbol ruleSymbol = (MCClassRuleSymbol) grammarSymbol.getRule(astClassProd
        .getName());
    final MCTypeSymbol definedType = ruleSymbol.getType();
    
    Log.errorIfNull(definedType);
    
    // Setup superclasses and superinterfaces
    // A extends B
    for (ASTRuleReference superRule : astClassProd.getSuperRule()) {
      String superTypeName = superRule.getTypeName();
      MCTypeSymbol superType = createTypeReference(superTypeName, superRule.isExternal(), superRule);
      definedType.addSuperClass(superType, false);
    }
    
    // A astextends B
    for (ASTGenericType astSuperClass : astClassProd.getASTSuperClass()) {
      String superTypeName = astSuperClass.getTypeName();
      MCTypeSymbol superType = createTypeReference(superTypeName, astSuperClass.isExternal(),
          astSuperClass);
      definedType.addSuperClass(superType, true);
    }
    
    // A implements B
    for (ASTRuleReference astInterface : astClassProd.getSuperInterfaceRule()) {
      String superTypeName = astInterface.getTypeName();
      MCTypeSymbol superType = createTypeReference(superTypeName, astInterface.isExternal(),
          astInterface);
      definedType.addSuperInterface(superType, false);
    }
    
    // A astimplements B
    for (ASTGenericType astInterface : astClassProd.getASTSuperInterface()) {
      String superTypeName = astInterface.getTypeName();
      MCTypeSymbol superType = createTypeReference(superTypeName, astInterface.isExternal(),
          astInterface);
      definedType.addSuperInterface(superType, true);
    }
  }
  
  public void putInScopeIfNotExists(final Symbol symbol) {
    if (currentScope().isPresent()) {
      if (!currentScope().get().resolveLocally(symbol.getName(), symbol.getKind()).isPresent()) {
        addToScope(symbol);
      }
      else {
        Log.debug("Symbol '" + symbol.getName() + "' (Kind '" + symbol.getKind() + "') is already "
            + "defined in scope " + currentScope().get().getName() + ".",
            MCGrammarSymbolTableCreator.class.getSimpleName());
      }
    }
    
  }
}
