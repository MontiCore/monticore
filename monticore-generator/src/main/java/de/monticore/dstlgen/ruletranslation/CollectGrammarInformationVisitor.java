/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.ruletranslation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.dstlgen.util.DSTLUtil;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.grammar.grammar._ast.ASTConstantsGrammar.PLUS;
import static de.monticore.grammar.grammar._ast.ASTConstantsGrammar.STAR;

/**
 * This visitor collects information needed during the generation of Rule2OD
 * infrastructure
 *
 */
public class CollectGrammarInformationVisitor implements
        GrammarVisitor2 {
  
  private final MCGrammarSymbol grammarSymbol;
  
  private Map<String, List<ASTNonTerminal>> stringAttrs = new HashMap<>();
  
  private Map<String, List<ASTNonTerminal>> stringListAttrs = new HashMap<>();
  
  private Map<String, List<ASTConstantGroup>> booleanAltAttrs = new HashMap<>();

  private Map<String, List<ASTConstantGroup>> booleanAttrs = new HashMap<>();
  
  private Map<String, List<ASTConstantGroup>> booleanListAttrs = new HashMap<>();

  private Map<String, List<String>> booleanAttrNames = new HashMap<>();
  
  private Map<String, List<String>> booleanListAttrNames = new HashMap<>();
  
  private Map<String, List<ASTNonTerminal>> componentLists = new HashMap<>();
  
  private Map<String, List<ASTNonTerminal>> componentNodes = new HashMap<>();
  
  private List<RuleComponentSymbol> knownAttributes = Lists.newArrayList();
  
  private Set<ASTParserProd> collectedParserProds = Sets.newHashSet();
  
  private Set<ASTExternalProd> collectedExternalProds = Sets.newHashSet();
  
  private Stack<ASTBlock> blockStack = new Stack<>();
  
  private ASTClassProd classprod;
  
  public CollectGrammarInformationVisitor(MCGrammarSymbol grammarSymbol) {
    this.grammarSymbol = grammarSymbol;
  }

  @Override
  public void visit(ASTInterfaceProd node) {
    collectedParserProds.add(node);
  }

  @Override
  public void visit(ASTExternalProd node) {
    collectedExternalProds.add(node);
  }

  @Override
  public void visit(ASTClassProd prod) {
    if (!prod.getName().equals("MCCompilationUnit")) {
      collectedParserProds.add(prod);
    }
    classprod = prod;
    stringAttrs.put(prod.getName(), Lists.newArrayList());
    stringListAttrs.put(prod.getName(), Lists.newArrayList());
    booleanAltAttrs.put(prod.getName(), Lists.newArrayList());
    booleanAttrs.put(prod.getName(), Lists.newArrayList());
    booleanListAttrs.put(prod.getName(), Lists.newArrayList());
    booleanAttrNames.put(prod.getName(), Lists.newArrayList());
    booleanListAttrNames.put(prod.getName(), Lists.newArrayList());
    componentLists.put(prod.getName(), Lists.newArrayList());
    componentNodes.put(prod.getName(), Lists.newArrayList());
  }

  @Override
  public void visit(ASTNonTerminal node) {
    if (classprod != null &&
            (node.getName().equals("Name") || node.getName().equals("String") || DSTLUtil
                    .isFromSupportedGrammar(node, grammarSymbol))) {
      ProdSymbol type = grammarSymbol.getProdWithInherited(classprod.getName()).get();
      String attrName = node.isPresentUsageName() ? node.getUsageName() : node.getName();
      List<RuleComponentSymbol> prods = type.getProdComponents();
      Optional<RuleComponentSymbol> att = prods.stream().filter(
              c -> c.getName().equals(attrName) || c.getName().equals(StringTransformations.uncapitalize(attrName))).findFirst();
      if (!att.isPresent()) {
        Log.warn("Missing " + classprod.getName() + "." + attrName + " in \n" + type.toString());
      } else if (node.getName().equals("Name")|| node.getName().equals("String")) {
        if (!knownAttributes.contains(att.get())) {
          if (!att.get().isIsList()) {
            stringAttrs.get(classprod.getName()).add(node);
          } else {
            stringListAttrs.get(classprod.getName()).add(node);
          }
          knownAttributes.add(att.get());
        }
      } else {
        ProdSymbol attProd = grammarSymbol.getProdWithInherited(node.getName()).get();
        if (!attProd.isIsLexerProd() && !knownAttributes.contains(att.get())) {
          ASTNonTerminal nodeToBeUsed = getCopyIfExternal(node, attProd).orElse(node);
          if (att.get().isIsList()) {
            componentLists.get(classprod.getName()).add(nodeToBeUsed);
          } else {
            componentNodes.get(classprod.getName()).add(nodeToBeUsed);
          }
          knownAttributes.add(att.get());
        }
        
      }
    }
  }
  
  private Optional<ASTNonTerminal> getCopyIfExternal(ASTNonTerminal node, ProdSymbol attProd) {
    Optional<ASTNonTerminal> result = Optional.empty();
    if (attProd.isIsExternal()) {
      ASTNonTerminal copy = node.deepClone();
      copy.setSymbol(node.isPresentSymbol() ? node.getSymbol() : null);
      if (!copy.isPresentUsageName()) {
        copy.setUsageName(copy.getName());
      }
      copy.setName(copy.getName() + "Ext");

      // Note: this copy is not in the enclosing scope
      copy.setEnclosingScope(node.getEnclosingScope());

      result = Optional.of(copy);
    }
    return result;
  }

  @Override
  public void visit(ASTConstantGroup node) {
    if (node.getConstantList().size() == 1) {
      String name = DSTLUtil.getNameForConstant(node);

      // only collect the node if the constant is not already collected
      if (isIterated()) {
        if (!booleanListAttrNames.get(classprod.getName()).contains(name)) {
          booleanListAttrs.get(classprod.getName()).add(node);
          booleanListAttrNames.get(classprod.getName()).add(name);
        }
      } else if (!booleanAttrNames.get(classprod.getName()).contains(name)) {
        booleanAttrs.get(classprod.getName()).add(node);
        booleanAttrNames.get(classprod.getName()).add(name);
      }
    } else {
      if (!booleanAltAttrs.get(classprod.getName()).contains(node)) {
        booleanAltAttrs.get(classprod.getName()).add(node);
      }
    }
  }
  
  private boolean isIterated() {
    for (ASTBlock b : blockStack) {
      if (b.getIteration() == STAR || b.getIteration() == PLUS) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void visit(ASTBlock node) {
    blockStack.push(node);
    
  }

  @Override
  public void endVisit(ASTBlock node) {
    blockStack.pop();
  }
  
  public Set<ASTParserProd> getCollectedParserProds() {
    return collectedParserProds;
  }
  
  public Set<ASTExternalProd> getCollectedExternalProds() {
    return collectedExternalProds;
  }
  
  public List<ASTNonTerminal> getComponentNodes(String name) {
    return componentNodes.get(name);
  }
  
  public List<ASTNonTerminal> getComponentLists(String name) {
    return componentLists.get(name);
  }
  
  public List<ASTConstantGroup> getBooleanAltAttrs(String name) {
    return booleanAltAttrs.get(name);
  }

  public List<ASTConstantGroup> getBooleanAttrs(String name) {
    return booleanAttrs.get(name);
  }
  
  public List<ASTConstantGroup> getBooleanListAttrs(String name) {
    return booleanListAttrs.get(name);
  }
  
  public List<ASTNonTerminal> getStringListAttrs(String name) {
    return stringListAttrs.get(name);
  }
  
  public List<ASTNonTerminal> getStringAttrs(String name) {
    return stringAttrs.get(name);
  }
  
}
