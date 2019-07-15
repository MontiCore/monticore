/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.symboltable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.generating.GeneratorEngine;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.se_rwth.commons.Names;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.monticore.codegen.GeneratorHelper.existsHandwrittenClass;
import static de.monticore.codegen.GeneratorHelper.getSimpleTypeNameToGenerate;
import static de.se_rwth.commons.Names.getPathFromPackage;
import static de.se_rwth.commons.Names.getSimpleName;
import static java.nio.file.Paths.get;


public class CommonSymbolTableCreatorGenerator implements SymbolTableCreatorGenerator {
  
  protected final String millName = "SymTabMill";

  protected String millTemplate = "%s%s._symboltable.%s"+millName;
  protected String scopeTemplate = "%s.%sScope";
  
  @Override
  public void generate(GeneratorEngine genEngine, SymbolTableGeneratorHelper genHelper,
                       IterablePath handCodedPath, MCGrammarSymbol grammarSymbol) {
    String className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName() + "SymbolTableCreator"),
            genHelper.getTargetPackage(), handCodedPath);

    final String languageName = genHelper.getGrammarSymbol().getName();

    Path filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
  
    Set<ProdSymbol> symbolDefiningRules = Sets.newHashSet();
    Set<ProdSymbol> nonSymbolDefiningRules = Sets.newHashSet();
    Set<String> symbolKinds = Sets.newHashSet();

    for(ProdSymbol rule: grammarSymbol.getProds()) {
      if(rule.isSymbolDefinition()) {
        symbolDefiningRules.add(rule);
      }
      else {
        if(rule.isParserProd()) {
          nonSymbolDefiningRules.add(rule);
        }
      }
    }
    symbolDefiningRules.forEach(p -> symbolKinds.add(genHelper.getQualifiedSymbolName(p.getEnclosingScope(),
        p.getSymbolDefinitionKind().orElse(""))));

    List<CDDefinitionSymbol> directSuperCds = genHelper.getDirectSuperCds(genHelper.getCd());
    if(grammarSymbol.getStartProd().isPresent()) {
      genEngine
          .generate("symboltable.SymbolTableCreator", filePath, grammarSymbol.getAstNode().get(),
              className, directSuperCds, symbolDefiningRules, nonSymbolDefiningRules, symbolKinds, handCodedPath);
      String stcName;
      if(className.endsWith("TOP")){
        stcName = className.replaceAll("TOP","");
      }else{
        stcName = className;
      }

      className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName() + "SymbolTableCreatorBuilder"),
          genHelper.getTargetPackage(), handCodedPath);

      filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");

      genEngine.generate("symboltable.SymbolTableCreatorBuilder",filePath,grammarSymbol.getAstNode().get(),className, stcName);


      className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName() + "SymbolTableCreatorDelegator"),
          genHelper.getTargetPackage(), handCodedPath);
    
      filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    
      List<MCGrammarSymbol> supergrammars = grammarSymbol.getAllSuperGrammars().stream()
          .filter(x->x.getStartProd().isPresent()).collect(Collectors.toList());
      
      genEngine
          .generate("symboltable.SymbolTableCreatorDelegator", filePath, grammarSymbol.getAstNode().get(),
              className, supergrammars , grammarSymbol.getStartProd().get());

      for(MCGrammarSymbol g : grammarSymbol.getAllSuperGrammars()) {
        if(g.getStartProd().isPresent()) {
          className = getSimpleTypeNameToGenerate(
              getSimpleName(g.getFullName() + "STCFor" + grammarSymbol.getName()),
              genHelper.getTargetPackage(), handCodedPath);
  
          filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
  
          String scopeName = "I" + grammarSymbol.getName() + "Scope";
          String superGrammarScope = "I" + g.getName() + "Scope";
          String superGrammarPackage = g.getPackageName().isEmpty() ?
              g.getName().toLowerCase() :
              g.getPackageName() + "." + g.getName().toLowerCase();
          genEngine.generate("symboltable.SymbolTableCreatorForSuperGrammar", filePath,
              grammarSymbol.getAstNode().get(), className, scopeName, g.getName(),
              superGrammarScope, superGrammarPackage);
        }
      
      }
      if(!grammarSymbol.isComponent()) {
        stcName = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName()) + "SymbolTableCreatorDelegator", genHelper.getTargetPackage(), handCodedPath);

        className = getSimpleTypeNameToGenerate(getSimpleName(grammarSymbol.getFullName()) + "SymbolTableCreatorDelegatorBuilder", genHelper.getTargetPackage(), handCodedPath);

        filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");


        genEngine.generate("symboltable.SymbolTableCreatorDelegatorBuilder", filePath, grammarSymbol.getAstNode().get(), className, stcName);
      }
    }

    String name = grammarSymbol.getName() + millName;
    className = getSimpleTypeNameToGenerate(getSimpleName(name), genHelper.getTargetPackage(), handCodedPath);
  
    filePath = get(getPathFromPackage(genHelper.getTargetPackage()), className + ".java");
    
    boolean hasHWCImpl = existsHandwrittenClass(getSimpleName(name), genHelper.getTargetPackage() , handCodedPath);
  
    // local symbols
    Map<String,String> localSymbolsAndScope = Maps.newHashMap();
    for(ProdSymbol s : genHelper.getAllSymbolDefiningRules()) {
      localSymbolsAndScope.put(genHelper.getQualifiedProdName(s)+"Symbol", s.getName()+"Symbol");
    }
    
    // super grammar symbols
    Map<String,String> superSymbols = Maps.newHashMap();
    Map<String,String> symbolToMill = Maps.newHashMap();
    Set<ProdSymbol> superSyms = Sets.newHashSet(genHelper.getAllSymbolDefiningRulesInSuperGrammar());
    superSyms.removeAll(genHelper.getAllSymbolDefiningRules());
    for(ProdSymbol s : superSyms) {
      superSymbols.put(genHelper.getQualifiedProdName(s)+"Symbol", s.getName()+"Symbol");
      String mill = Names.getQualifier(genHelper.getQualifiedProdName(s))+ "." + s.getEnclosingScope().getName().get() + millName;
      symbolToMill.put(genHelper.getQualifiedProdName(s)+"Symbol", mill);
    }
    
    // scope
    String qualifiedScopeName = String.format(scopeTemplate, genHelper.getTargetPackage(), grammarSymbol.getName());
    localSymbolsAndScope.put(qualifiedScopeName, grammarSymbol.getName()+"Scope");

    // super mills
    Set<String> mills = Sets.newHashSet();
    for(String s : genHelper.getSuperGrammarCds()){
      String _package = Names.getQualifier(s).isEmpty()? "" : Names.getQualifier(s) + ".";
      String grammar = Names.getSimpleName(s);
      mills.add(String.format(millTemplate, _package, grammar.toLowerCase(), grammar));
    }
    final boolean existsHW = existsHandwrittenClass(getSimpleName(grammarSymbol.getFullName() + "Language"),
        genHelper.getTargetPackage(), handCodedPath);
    genEngine.generate("symboltable.SymTabMill", filePath, grammarSymbol.getAstNode().get(),
        hasHWCImpl, className, name,localSymbolsAndScope,mills, superSymbols,symbolToMill, languageName, existsHW);
  }
}
