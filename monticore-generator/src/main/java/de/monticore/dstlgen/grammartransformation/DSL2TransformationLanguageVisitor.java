/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.grammartransformation;

import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.monticore.dstlgen.ruletranslation.DSTLGenInheritanceHelper;
import de.monticore.dstlgen.util.DSTLUtil;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.dstlgen.grammartransformation.ProductionType.*;

/**
 * This visitor transforms the AST of a MontiCore grammar into the AST of a
 * transformation language for that grammar.
 *
 */
public class DSL2TransformationLanguageVisitor implements
        GrammarVisitor2 {

  protected static final String LOG = "LanguageGeneration";

  private final ProductionFactory productionFactory;

  private final ASTRuleFactory astRuleFactory;


  private final Set<String> productions = new HashSet<>();

  private MCGrammarSymbol grammarSymbol;

  private int grammar_depth;

  public DSL2TransformationLanguageVisitor() {
    productionFactory = ProductionFactory.getInstance();
    astRuleFactory = ASTRuleFactory.getInstance();
  }

  /**
   * class Productions in the DSL. At the end of the visitor run, this list
   * contains all types that can be types of top level objects in the grammar.
   */
  private ASTMCGrammar tfLang = GrammarMill.mCGrammarBuilder().uncheckedBuild();

  /**
   * the mapping from
   */
  private final Map<ASTNode, LinkedList<ASTNode>> src2target = new HashMap<>();

  private void map(ASTNode src, ASTNode target) {
    if (!src2target.containsKey(src)) {
      src2target.put(src, new LinkedList<>());
    }
    src2target.get(src).add(target);
  }

  @Override
  public void visit(ASTMCGrammar srcNode) {
    grammarSymbol = srcNode.getSymbol();
    grammar_depth = recursiveCalculateGrammarDepth(grammarSymbol) * 10;
    tfLang.setPackageList(new LinkedList<>());
    tfLang.getPackageList().addAll(srcNode.getPackageList());
    tfLang.getPackageList().add("tr");
    map(srcNode, tfLang);

    if(srcNode.isComponent()) {
      tfLang.setComponent(true);
    }

    tfLang.setName(srcNode.getName() + "TR");
    Log.debug("Start producing grammar " + tfLang.getName(), LOG);

    // set supergrammars
    DSTLGenInheritanceHelper inheritanceHelper = new DSTLGenInheritanceHelper();
    boolean noUncommenSupers = true;
    for (MCGrammarSymbol dslSuper : srcNode.getSymbol().getSuperGrammarSymbols()) {
    if (inheritanceHelper.isCommonSuperGrammar(dslSuper.getName())){ 
      tfLang.getSupergrammarList().add(GrammarMill.grammarReferenceBuilder()
              .addAllNames(Splitters.DOT.splitToList(dslSuper.getFullName())).build());
      } else {
        noUncommenSupers = false;
        ASTGrammarReference tfSuper = GrammarMill.grammarReferenceBuilder().uncheckedBuild();
        // copy package if present
      ;
      tfSuper.setNameList(new ArrayList<>(Splitters.DOT.splitToList(dslSuper.getPackageName())));
        // add suffix
        tfSuper.getNameList().add("tr");
      tfSuper.getNameList().add(dslSuper.getName() + "TR");

        tfLang.getSupergrammarList().add(tfSuper);
      }
    }

    if(noUncommenSupers) {
      ASTGrammarReference superGrammar = GrammarMill.grammarReferenceBuilder()
              .setNamesList(Splitters.DOT.splitToList("de.monticore.tf.TFCommons"))
              .build();
      tfLang.getSupergrammarList().add(superGrammar);
    }
  }

  private int recursiveCalculateGrammarDepth(MCGrammarSymbol grammarSymbol) {
    DSTLGenInheritanceHelper inheritanceHelper = new DSTLGenInheritanceHelper();
    if (!inheritanceHelper.isCommonSuperGrammar(grammarSymbol.getName())) {
      Optional<Integer> depth = grammarSymbol.getSuperGrammarSymbols().stream().map(this::recursiveCalculateGrammarDepth)
          .max(Integer::compare);
      return depth.isPresent() ? 1 + depth.get() : 1;
    }

    return 0;
  }

  @Override
  public void visit(ASTGrammarOption srcNode) {
    Log.debug("Visiting " + srcNode.toString(), LOG);
    ASTGrammarOption targetNode = srcNode.deepClone();
    tfLang.setGrammarOption(targetNode);
  }

  @Override
  public void visit(ASTEnumProd srcNode) {
    Log.debug("Visiting " + srcNode.toString(), LOG);
    ASTEnumProd targetNode = srcNode.deepClone();
    targetNode.setName("ITF" + srcNode.getName());
    tfLang.addEnumProd(targetNode);
  }

  @Override
  public void endVisit(ASTMCGrammar srcNode) {
    ASTClassProd tfObjectProduction = productionFactory.createTFRuleProduction(grammarSymbol);
    tfLang.getClassProdList().add(0, tfObjectProduction);

    // Only create this interface for non-empty grammars
    if (tfLang.getClassProdList().size() > 1)
      // Create an interface substituting ITFPart per Grammar
      tfLang.addInterfaceProd(GrammarMill.interfaceProdBuilder()
                                      .setName("I" + srcNode.getName() + "TFPart")
                                      .addSuperInterfaceRule(
                                              GrammarMill.ruleReferenceBuilder()
                                                      .setName("ITFPart")
                                                      .build()
                                                            ).build());
  }

  @Override
  public void visit(ASTInterfaceProd srcNode) {
    Log.debug("Visiting interface production " + srcNode.getName(), LOG);
    List<ASTInterfaceProd> targetInterfaceProdList = tfLang.getInterfaceProdList();

    ASTInterfaceProd targetInterfaceProd = productionFactory.createInterfaceProd(srcNode, grammar_depth);
    map(srcNode, targetInterfaceProd);
    targetInterfaceProdList.add(targetInterfaceProd);

    List<ASTClassProd> targetClassProdList = tfLang.getClassProdList();
    targetClassProdList.add(productionFactory.createPatternProd(srcNode, grammarSymbol));
    targetClassProdList.add(productionFactory.createReplacementProd(srcNode, false));
    targetClassProdList.add(productionFactory.createProd(srcNode, NEGATION, false));
    targetClassProdList.add(productionFactory.createProd(srcNode, LIST, false));
    targetClassProdList.add(productionFactory.createProd(srcNode, OPTIONAL, false));

    List<ASTASTRule> targetAstRuleList = tfLang.getASTRuleList();
    targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, LIST, false, grammarSymbol));
    targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, NEGATION, false, grammarSymbol));
    targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, PATTERN, false, grammarSymbol));
    targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, OPTIONAL, false, grammarSymbol));
    targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, REPLACEMENT,  false, grammarSymbol));
  }

  @Override
  public void visit(ASTAbstractProd srcNode) {
    Log.debug("Visiting abstract production " + srcNode.getName(), LOG);

    List<ASTInterfaceProd> targetInterfaceProdList = tfLang.getInterfaceProdList();
    ASTInterfaceProd targetInterfaceProd = productionFactory.createInterfaceProd(srcNode);
    map(srcNode, targetInterfaceProd);
    targetInterfaceProdList.add(targetInterfaceProd);

    List<ASTClassProd> targetClassProdList = tfLang.getClassProdList();
    targetClassProdList.add(productionFactory.createPatternProd(srcNode,grammarSymbol));
    targetClassProdList.add(productionFactory.createReplacementProd(srcNode, false));
    targetClassProdList.add(productionFactory.createProd(srcNode, NEGATION, false));
    targetClassProdList.add(productionFactory.createProd(srcNode, LIST, false));
    targetClassProdList.add(productionFactory.createProd(srcNode, OPTIONAL, false));

    List<ASTASTRule> targetAstRuleList = tfLang.getASTRuleList();
    targetAstRuleList.add(
        astRuleFactory.createAstProd(srcNode, LIST, false, grammarSymbol));
    targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, NEGATION, false, grammarSymbol));
    targetAstRuleList.add(astRuleFactory.createAstPatternProd(srcNode, grammarSymbol ));
    targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, OPTIONAL, false, grammarSymbol));
    targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, REPLACEMENT, false, grammarSymbol));
  }

  @Override
  public void visit(ASTExternalProd srcNode) {
    Log.debug("Visiting external production " + srcNode.getName(), LOG);
    tfLang.addAbstractProd(productionFactory.createAbstractProd(srcNode));
  }


  @Override
  public void visit(ASTClassProd srcNode) {
    Log.debug("Visiting production " + srcNode.getName(), LOG);
    if (!("MCCompilationUnit").equals(srcNode.getName())) {
      productions.add(srcNode.getName());
      List<ASTClassProd> targetClassProdList = tfLang.getClassProdList();
      List<ASTASTRule> targetAstRuleList = tfLang.getASTRuleList();

      boolean overridden = false;
      boolean superExternal = false;

      boolean isLeftRecursive = srcNode.getSymbol().isIsDirectLeftRecursive() ||  srcNode.getSymbol().getSuperInterfaceProds()
              .stream().map(s -> s.lazyLoadDelegate())
              .anyMatch(x -> x.isIsDirectLeftRecursive());

      boolean isEmpty = DSTLUtil.isEmptyProduction(srcNode.getSymbol());

      if(grammarSymbol.getInheritedProd(srcNode.getName()).isPresent() ){
        ProdSymbol typeSymbol = grammarSymbol.getInheritedProd(srcNode.getName()).get();
        if (typeSymbol.isIsExternal()) {
          superExternal = true;
        } else {
          overridden = true;
        }
      } else {
        tfLang.getInterfaceProdList().add(productionFactory.createInterfaceProd(srcNode, grammar_depth, isLeftRecursive));
      }

      ASTClassProd p = productionFactory.createProd(srcNode, LIST,superExternal, isLeftRecursive);
      if (!isEmpty){
        productionFactory
                .addInterfaces(srcNode.getSuperRuleList(), p);
        productionFactory.addInterfaces(
                srcNode.getSuperInterfaceRuleList(), p);
      } else {
        p.add_PreComment(new Comment(" /*Skipping supers due to empty prod or left recursiveness*/ "));
      }
      targetClassProdList.add(0, p);

      p = productionFactory.createProd(srcNode, OPTIONAL, superExternal, isLeftRecursive);
      if (!isLeftRecursive && !isEmpty) {
        productionFactory
                .addInterfaces(srcNode.getSuperRuleList(), p);
        productionFactory.addInterfaces(
                srcNode.getSuperInterfaceRuleList(), p);
      } else {
        p.add_PreComment(new Comment(" /*Skipping supers due to empty prod or left recursiveness*/ "));
      }
      targetClassProdList.add(0, p);

      p = productionFactory.createProd(srcNode, NEGATION, superExternal, isLeftRecursive);
      if (!isLeftRecursive && !isEmpty) {
        productionFactory
                .addInterfaces(srcNode.getSuperRuleList(), p);
        productionFactory.addInterfaces(
                srcNode.getSuperInterfaceRuleList(), p);
      }else{
        p.add_PreComment(new Comment(" /*Skipping supers due to empty prod or left recursiveness*/ "));
      }
      targetClassProdList.add(0, p);

      p =  productionFactory.createPatternProd(srcNode,grammarSymbol, superExternal, isLeftRecursive, isEmpty);

      targetClassProdList.add(0, p);

      p = productionFactory.createReplacementProd(srcNode, superExternal);
      if (!isLeftRecursive && !isEmpty) {
        productionFactory.addInterfaces(srcNode.getSuperRuleList(),p);
        productionFactory.addInterfaces(srcNode.getSuperInterfaceRuleList(),p);
      }else{
        p.add_PreComment(new Comment(" /*Skipping supers due to empty prod or left recursiveness*/ "));
      }
      targetClassProdList.add(0, p);

      targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, LIST, overridden, grammarSymbol));
      targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, NEGATION, overridden, grammarSymbol));
      targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, PATTERN, overridden, grammarSymbol));
      targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, OPTIONAL, overridden, grammarSymbol));
      targetAstRuleList.add(astRuleFactory.createAstProd(srcNode, REPLACEMENT, overridden, grammarSymbol));
    }
  }

  @Override
  public void visit(ASTConstantGroup srcNode) {
    List<ASTClassProd> targetClassProdList = tfLang.getClassProdList();
    final String name = DSTLUtil.getNameForConstant(srcNode);
    Log.debug("Visiting constant " + name, LOG);
    // only create productions if they doesn't already exist
    if (!productions.contains(name)) {
      productions.add(name);
      tfLang.getInterfaceProdList().add(productionFactory.createInterfaceProd(srcNode, grammarSymbol));
      targetClassProdList.add(productionFactory.createPatternProd(srcNode, grammarSymbol, name));
      targetClassProdList.add(productionFactory.createReplacementProdForConstant(grammarSymbol, name));
      targetClassProdList.add(productionFactory.createNegationProdForConstant(grammarSymbol, name));
      targetClassProdList.add(productionFactory.createOptionalProdForConstant(grammarSymbol, name));

      List<ASTASTRule> targetAstRuleList = tfLang.getASTRuleList();
      targetAstRuleList.add(astRuleFactory.createAstNegationProdForConstant(grammarSymbol, name));
      targetAstRuleList.add(astRuleFactory.createAstOptionalProdForConstant(grammarSymbol, name));
    }
  }

  @Override
  public void visit(ASTLexProd srcNode) {
    Log.debug("Visiting lexical production " + srcNode.getName(), LOG);
    ASTLexProd targetNode = srcNode.deepClone();
    targetNode.setName("ITF" + targetNode.getName());
    tfLang.getLexProdList().add(targetNode);
  }

  @Override
  public void visit(ASTASTRule srcNode) {
    Log.debug("Visiting ast rule " + srcNode.getType(), LOG);
    List<ASTASTRule> targetAstRuleList = tfLang.getASTRuleList();
    if (!("MCCompilationUnit").equals(srcNode.getType())) {
      targetAstRuleList.add(astRuleFactory.createASTRule(srcNode, grammarSymbol));
    }
  }

  @Override
  public void visit(ASTConcept srcNode) {
    Log.debug("Visiting " + srcNode.getName(), LOG);
    List<ASTConcept> targetConceptList = tfLang.getConceptList();
    ASTConcept targetNode = srcNode.deepClone();
    targetConceptList.add(targetNode);

    map(srcNode, targetNode);
  }

  public ASTMCGrammar getTfLang() {
    return tfLang;
  }

}
