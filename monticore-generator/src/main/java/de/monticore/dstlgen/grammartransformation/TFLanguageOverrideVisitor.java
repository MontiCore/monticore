/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.grammartransformation;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.se_rwth.commons.Joiners;

import java.util.HashMap;

public class TFLanguageOverrideVisitor implements
        GrammarVisitor2 {


    private final ASTMCGrammar overideGrammar;
    private final ASTMCGrammar tFGrammar;
    private HashMap<String, ASTClassProd> tFClassProds = new HashMap<>();
    private HashMap<String, ASTEnumProd> tFEnumProds = new HashMap<>();
    private HashMap<String, ASTInterfaceProd> tFInterfaceProds = new HashMap<>();
    private HashMap<String, ASTAbstractProd> tFAbstractProds = new HashMap<>();
    private HashMap<String, ASTExternalProd> tFExternalProds = new HashMap<>();
    private HashMap<String, ASTLexProd> tFLexProds = new HashMap<>();
    private HashMap<String, ASTASTRule> tFASTRules = new HashMap<>();


    public TFLanguageOverrideVisitor(ASTMCGrammar tFGrammar, ASTMCGrammar overrideGrammar) {
        this.overideGrammar = overrideGrammar;
        this.tFGrammar = tFGrammar;
        tFGrammar.getClassProdList().forEach(p -> tFClassProds.put(p.getName(), p));
        tFGrammar.getEnumProdList().forEach(p -> tFEnumProds.put(p.getName(), p));
        tFGrammar.getInterfaceProdList().forEach(p -> tFInterfaceProds.put(p.getName(), p));
        tFGrammar.getAbstractProdList().forEach(p -> tFAbstractProds.put(p.getName(), p));
        tFGrammar.getExternalProdList().forEach(p -> tFExternalProds.put(p.getName(), p));
        tFGrammar.getLexProdList().forEach(p -> tFLexProds.put(p.getName(), p));
        tFGrammar.getASTRuleList().forEach(p -> tFASTRules.put(p.getType(), p));
    }

    @Override
    public void visit(ASTMCGrammar srcNode){
        tFGrammar.setComponent(srcNode.isComponent());
        for(ASTGrammarReference overrideSuperGrammar : srcNode.getSupergrammarList()) {
            boolean isNewSuper = true;
            for(ASTGrammarReference tfSuperGrammar : tFGrammar.getSupergrammarList()) {
                String overrideSuperGrammarName = Joiners.DOT.join(overrideSuperGrammar.getNameList());
                String tfSuperGrammarName = Joiners.DOT.join(tfSuperGrammar.getNameList());
                if(overrideSuperGrammarName.equals(tfSuperGrammarName)) {
                    isNewSuper = false;
                }
            }
            if(isNewSuper) {
                tFGrammar.addSupergrammar(overrideSuperGrammar);
            }
        }
    }

  @Override
    public void visit(ASTClassProd srcNode) {
        if(srcNode.getName().equals("TFRule")) {

            tFGrammar.addClassProd(0, srcNode);
            tFGrammar.removeClassProd(1);
        } else {
            if (tFClassProds.containsKey(srcNode.getName())) {
                tFGrammar.removeClassProd(tFClassProds.get(srcNode.getName()));
            }
            tFGrammar.addClassProd(srcNode);
        }
    }

  @Override
    public void visit(ASTEnumProd srcNode) {
        if(tFEnumProds.containsKey(srcNode.getName())) {
            tFGrammar.removeEnumProd(tFEnumProds.get(srcNode.getName()));
        }
        tFGrammar.addEnumProd(srcNode);
    }

  @Override
    public void visit(ASTInterfaceProd srcNode) {
        if(tFInterfaceProds.containsKey(srcNode.getName())) {
            tFGrammar.removeInterfaceProd(tFInterfaceProds.get(srcNode.getName()));
        }
        tFGrammar.addInterfaceProd(srcNode);
    }

  @Override
    public void visit(ASTAbstractProd srcNode) {
        if(tFAbstractProds.containsKey(srcNode.getName())) {
            tFGrammar.removeAbstractProd(tFAbstractProds.get(srcNode.getName()));
        }
        tFGrammar.addAbstractProd(srcNode);
    }

  @Override
    public void visit(ASTExternalProd srcNode) {
        if(tFExternalProds.containsKey(srcNode.getName())) {
            tFGrammar.removeExternalProd(tFExternalProds.get(srcNode.getName()));
        }
        tFGrammar.addExternalProd(srcNode);
    }

  @Override
    public void visit(ASTLexProd srcNode) {
        if(tFLexProds.containsKey(srcNode.getName())) {
            tFGrammar.removeLexProd(tFLexProds.get(srcNode.getName()));
        }
        tFGrammar.addLexProd(srcNode);
    }

  @Override
    public void visit(ASTASTRule srcNode) {
        if(tFASTRules.containsKey(srcNode.getType())) {
            tFGrammar.removeASTRule(tFASTRules.get(srcNode.getType()));
        }
        tFGrammar.addASTRule(srcNode);
    }

  @Override
    public void visit(ASTGrammarOption srcNode) {
        tFGrammar.setGrammarOption(srcNode);
    }

  @Override
    public void visit(ASTKeywordRule srcNode) {
      tFGrammar.addKeywordRule(srcNode);
    }
}
