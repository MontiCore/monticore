/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.grammartransformation;

import com.google.common.collect.Lists;
import de.monticore.dstlgen.util.DSTLUtil;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.ArrayList;
import java.util.List;

import static de.se_rwth.commons.StringTransformations.uncapitalize;

public class PostprocessPatternAttributesVisitor implements
        GrammarVisitor2 {

  private final MCGrammarSymbol grammarSymbol;

  public PostprocessPatternAttributesVisitor(MCGrammarSymbol grammarSymbol) {
    this.grammarSymbol = grammarSymbol;
  }

  @Override
  public void visit(ASTAdditionalAttribute node) {
    String typeName = node.getMCType().printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter()));
    if ("Name".equals(typeName)) {
      ASTMCBasicGenericType tfElementReference = GrammarMill.mCBasicGenericTypeBuilder().uncheckedBuild();
      tfElementReference.setNameList(Lists.newArrayList(ProductionFactory.PSYM_TFIDENTIFIER));
      node.setMCType(tfElementReference);
    }
  }

  @Override
  public void visit(ASTNonTerminal node) {
    if ("Name".equals(node.getName()) || "String".equals(node.getName())) {
      if (!(node.isPresentUsageName() && node.getUsageName().equals("schemaVarName"))) {
        if (!node.isPresentUsageName()) {
          node.setUsageName(node.getName());
        }
        //Remove to avoid TFIDENTIFIER@Symbol which MC does not really Like
        if (node.isPresentReferencedSymbol())
          node.setReferencedSymbolAbsent();
        node.setName(ProductionFactory.PSYM_TFIDENTIFIER);
      }
    } else {
      if (DSTLUtil.isFromSupportedGrammar(node, grammarSymbol)) {
        if (!node.isPresentUsageName()) {
          node.setUsageName(node.getName());
        }
        // Exclude some tokenized nonterminals
        if (!node.getName().equals("Digits")) {
          node.setName("ITF" + node.getName());
        }
      }
    }
    if (node.isPresentUsageName()) {
      node.setUsageName(uncapitalize(node.getUsageName()));
    }
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
      }
    }
    for (int i = 0; i < newNTs.size(); i++) {
      node.add(newNTPositions.get(i), newNTs.get(i));
    }
  }
}
