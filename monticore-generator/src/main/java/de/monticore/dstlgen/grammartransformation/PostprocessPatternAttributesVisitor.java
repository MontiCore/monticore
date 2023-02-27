/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.grammartransformation;

import com.google.common.collect.Lists;
import de.monticore.ast.Comment;
import de.monticore.dstlgen.ruletranslation.DSTLGenInheritanceHelper;
import de.monticore.dstlgen.util.DSTLUtil;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static de.se_rwth.commons.StringTransformations.uncapitalize;

public class PostprocessPatternAttributesVisitor implements
        GrammarVisitor2 {

  protected final MCGrammarSymbol grammarSymbol;
  protected final Collection<String> additionallySupportedITFProductions = new HashSet<>();

  public PostprocessPatternAttributesVisitor(MCGrammarSymbol grammarSymbol) {
    this.grammarSymbol = grammarSymbol;
  }

  @Override
  public void visit(ASTAdditionalAttribute node) {
    String typeName = node.getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
    Optional<ProdSymbol> typeSymbol = grammarSymbol.getProdWithInherited(typeName);
    if ("Name".equals(typeName) || (typeSymbol.isPresent() && typeSymbol.get().isIsLexerProd())) {
      ASTMCBasicGenericType tfElementReference = GrammarMill.mCBasicGenericTypeBuilder().uncheckedBuild();
      tfElementReference.setNameList(Lists.newArrayList(ProductionFactory.PSYM_TFIDENTIFIER + typeName));
      node.setMCType(tfElementReference);
    }
  }

  @Override
  public void visit(ASTLexNonTerminal node){
    if (!DSTLGenInheritanceHelper.getInstance().isLexicalProdKnownInTFCommons(node.getName())) {
      node.setName(ProductionFactory.LEXPROD_PREFIX + node.getName());
    }else{
      node.add_PreComment(new Comment("/* using (non LexTF) token " + node.getName() + " from TFCommons* */"));
    }
  }

  @Override
  public void visit(ASTNonTerminal node) {
    if (this.additionallySupportedITFProductions.contains(node.getName())) {
      // productions for constant groups also require the ITF prefix
      node.setName(ProductionFactory.NONTERM_PREFIX + node.getName());
    } else {
      Optional<ProdSymbol> referencingSymbol = getReferencingSymbol(node);

      if (referencingSymbol.isEmpty()) {
        node.add_PreComment(new Comment("/* unsupported/unreferented (itf) production " + node.getName() + " */"));
      } else {
        if (!node.isPresentUsageName()) {
          node.setUsageName(node.getName());
        }
        if (referencingSymbol.get().isIsLexerProd()) {
          node.setName(ProductionFactory.PSYM_TFIDENTIFIER + node.getName());
          // Remove referenced symbol to avoid ...TFIDENTIFIER@Symbol which MC does not really Like
          node.setReferencedSymbolAbsent();
        } else {
          // default case: Add the ITF prefix for all nonterminal references
          node.setName(ProductionFactory.NONTERM_PREFIX + node.getName());
        }
      }
    }
    if (node.isPresentUsageName()) {
      node.setUsageName(uncapitalize(node.getUsageName()));
    }
  }


  /**
   * Return the referenced productions symbol (resolve if necessary)
   */
  protected Optional<ProdSymbol> getReferencingSymbol(ASTNonTerminal node){
    if (node.isPresentSymbol()) {
      if (node.getSymbol().getReferencedProd().isPresent())
        return Optional.of(node.getSymbol().getReferencedProd().get().lazyLoadDelegate());
    }
    return grammarSymbol.getProdWithInherited(node.getName());
  }

  @Override
  public void visit(ASTAlt parent) {
    List<ASTRuleComponent> node = parent.getComponentList();
    List<ASTNonTerminal> newNTs = new ArrayList<>();
    List<Integer> newNTPositions = new ArrayList<>();
    for (int i = 0; i < node.size(); i++) {
      ASTRuleComponent c = node.get(i);

      if (c instanceof ASTConstantGroup) {
        ASTConstantGroup group = (ASTConstantGroup) c;
        node.remove(group);
        ASTNonTerminal nonTerminal = GrammarMill.nonTerminalBuilder().uncheckedBuild();
        nonTerminal.setName(grammarSymbol.getName() + "_" + DSTLUtil.getNameForConstant(group) + "_Constant");
        nonTerminal.setUsageName(uncapitalize(DSTLUtil.getUsageNameForConstant(group)));
        newNTs.add(nonTerminal);
        newNTPositions.add(i);

        // Ensure references to this new NonTerminal are possible/it is known
        additionallySupportedITFProductions.add(nonTerminal.getName());
      }
    }
    for (int i = 0; i < newNTs.size(); i++) {
      node.add(newNTPositions.get(i), newNTs.get(i));
    }
  }
}
