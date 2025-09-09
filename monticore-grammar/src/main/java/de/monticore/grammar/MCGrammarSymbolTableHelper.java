/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.*;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.Util;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newLinkedHashSet;

public class MCGrammarSymbolTableHelper {

  public static final String AST_DOT_PACKAGE_SUFFIX_DOT = "._ast.";
  protected static final Integer STAR = -1;

  public static Optional<ProdSymbol> resolveRule(ASTMCGrammar astNode, String name) {
    if (astNode.isPresentSymbol()) {
      return astNode.getSymbol().getProdWithInherited(name);
    }
    return Optional.empty();
  }

  public static Optional<ProdSymbol> resolveRuleInSupersOnly(ASTClassProd astNode, String name) {
    Optional<MCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode.getEnclosingScope());
    Stream<MCGrammarSymbol> superGrammars = grammarSymbol
        .map(symbol -> Util.preOrder(symbol, MCGrammarSymbol::getSuperGrammarSymbols)
            .stream())
        .orElse(Stream.empty()).skip(1);
    return superGrammars.map(superGrammar -> superGrammar.getProd(name))
        .filter(mcRuleSymbol -> mcRuleSymbol.isPresent())
        .map(Optional::get)
        .findFirst();
  }

  public static Optional<MCGrammarSymbol> getMCGrammarSymbol(IGrammarScope scope) {
    boolean exist = true;
    while (exist) {
      if (scope.isPresentSpanningSymbol() && scope.getSpanningSymbol() instanceof MCGrammarSymbol) {
        return Optional.of((MCGrammarSymbol) scope.getSpanningSymbol());
      }
      if (scope instanceof IGrammarGlobalScope) {
        exist = false;
      } else {
        scope = scope.getEnclosingScope();
      }
    }
    return Optional.empty();
  }

  public static Optional<ProdSymbol> getEnclosingRule(ASTRuleComponent astNode) {
    if (astNode.getEnclosingScope().isPresentSpanningSymbol()) {
      IScopeSpanningSymbol s = astNode.getEnclosingScope().getSpanningSymbol();
      if (s instanceof ProdSymbol) {
        return Optional.of((ProdSymbol) s);
      }
    }
    return Optional.empty();
  }

  public static Optional<ProdSymbol> getEnclosingRule(RuleComponentSymbol prod) {
    if (prod.getEnclosingScope().isPresentSpanningSymbol() && prod.getEnclosingScope().getSpanningSymbol() instanceof ProdSymbol) {
      return Optional.of((ProdSymbol) prod.getEnclosingScope().getSpanningSymbol());
    }
    return Optional.empty();
  }

  /**
   * Returns a set of all super grammars of the given grammar (transitively)
   *
   * @return
   */
  public static Set<MCGrammarSymbol> getAllSuperGrammars(
      MCGrammarSymbol grammarSymbol) {
    Set<MCGrammarSymbol> allSuperGrammars = new LinkedHashSet<>();
    Set<MCGrammarSymbol> tmpList = new LinkedHashSet<>();
    allSuperGrammars.addAll(grammarSymbol.getSuperGrammarSymbols());
    boolean modified = false;
    do {
      for (MCGrammarSymbol curGrammar : allSuperGrammars) {
        tmpList.addAll(curGrammar.getSuperGrammarSymbols());
      }
      modified = allSuperGrammars.addAll(tmpList);
      tmpList.clear();
    } while (modified);

    return ImmutableSet.copyOf(allSuperGrammars);
  }

  public static String getQualifiedName(ASTProd astNode, ProdSymbol symbol, String prefix,
                                        String suffix) {
    if (symbol.isIsExternal()) {
      return symbol.getName();
    } else {
      Optional<MCGrammarSymbol> grammarSymbol = getMCGrammarSymbol(astNode.getEnclosingScope());
      String string = (grammarSymbol.isPresent()
          ? grammarSymbol.get().getFullName().toLowerCase()
          : "")
          + AST_DOT_PACKAGE_SUFFIX_DOT + prefix +
          StringTransformations.capitalize(symbol.getName() + suffix);

      if (string.startsWith(".")) {
        string = string.substring(1);
      }
      return string;
    }
  }


  public static String getConstantGroupName(ASTConstantGroup ast) {
    // setAttributeMinMax(a.getIteration(), att);
    if (ast.isPresentUsageName()) {
      return ast.getUsageName();
    }
    // derive attribute name from constant entry (but only if we have
    // one entry!)
    else if (ast.getConstantList().size() == 1) {
      return ast.getConstantList().get(0).getHumanName();
    }

    Log.error("0xA2345 The name of the constant group could't be ascertained",
        ast.get_SourcePositionStart());

    return "";
  }

  public static Set<ProdSymbol> getAllSuperProds(ProdSymbol prod) {
    Set<ProdSymbol> supersHandled = new LinkedHashSet<>();
    List<ProdSymbol> supersToHandle = new ArrayList<>();
    supersToHandle.addAll(getSuperProds(prod));
    Set<ProdSymbol> supersNextRound = new LinkedHashSet<>();

    while (!supersToHandle.isEmpty()) {
      for (ProdSymbol superType : supersToHandle) {
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

  public static Set<ProdSymbol> getAllSuperInterfaces(ProdSymbol prod) {
    return getAllSuperProds(prod).stream().filter(p -> p.isIsInterface()).collect(Collectors.toSet());
  }

  protected final static LoadingCache<ProdSymbol, List<ProdSymbol>> superProdCache = CacheBuilder.newBuilder().maximumSize(10000)
    .build(new CacheLoader<ProdSymbol, List<ProdSymbol>>() {
      @Override
      public List<ProdSymbol> load(ProdSymbol prod) {
        List<ProdSymbol> superTypes = prod.getSuperProds().stream().filter(s -> s.isSymbolPresent())
            .map(s -> s.lazyLoadDelegate()).collect(Collectors.toList());
        superTypes.addAll(prod.getSuperInterfaceProds().stream().filter(s -> s.isSymbolPresent())
            .map(s -> s.lazyLoadDelegate()).collect(Collectors.toList()));

        superTypes.addAll(prod.getAstSuperClasses().stream().filter(s -> s.isSymbolPresent())
            .map(s -> s.lazyLoadDelegate()).collect(Collectors.toList()));
        superTypes.addAll(prod.getAstSuperInterfaces().stream().filter(s -> s.isSymbolPresent())
            .map(s -> s.lazyLoadDelegate()).collect(Collectors.toList()));
        return ImmutableList.copyOf(superTypes);
      }
    });

  /**
   * @param prod
   * @return
   */
  public static List<ProdSymbol> getSuperProds(ProdSymbol prod) {
    return superProdCache.getUnchecked(prod);
  }

  public static boolean isSubtype(ProdSymbol subType, ProdSymbol superType) {
    return isSubtype(subType, superType, newLinkedHashSet(Arrays.asList(subType)));
  }

  protected static boolean isSubtype(ProdSymbol subType, ProdSymbol superType,
                                   Set<ProdSymbol> handledTypes) {
    if (areSameTypes(subType, superType)) {
      return true;
    }

    // Try to find superType in super types of this type
    final Collection<ProdSymbol> allSuperTypes = getAllSuperProds(subType);
    if (allSuperTypes.contains(superType)) {
      return true;
    }

    // check transitive sub-type relation
    for (ProdSymbol t : allSuperTypes) {
      if (handledTypes.add(superType)) {
        boolean subtypeOf = isSubtype(t, superType, handledTypes);
        if (subtypeOf) {
          return true;
        }
      }
    }

    return false;
  }

  public static boolean areSameTypes(ProdSymbol type1, ProdSymbol type2) {
    Log.errorIfNull(type1);
    Log.errorIfNull(type2);

    if (type1 == type2) {
      return true;
    }

    return type1.getFullName().equals(type2.getFullName());

  }

  /**
   * Returns the type of the collection <code>types</code> that is the sub type
   * of all other types in this collection. Else, null is returned.
   *
   * @param types Collection of types
   * @return type that is subtype of all other types or null.
   */
  public static Optional<ProdSymbol> findLeastType(Collection<ProdSymbol> types) {
    for (ProdSymbol t1 : types) {
      boolean isLeastType = true;
      for (ProdSymbol t2 : types) {
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

  public static Optional<Integer> getMin(AdditionalAttributeSymbol attrSymbol) {
    if (!attrSymbol.isPresentAstNode()) {
      return Optional.empty();
    }
    return getMin(attrSymbol.getAstNode());
  }

  public static Optional<Integer> getMin(ASTAdditionalAttribute ast) {
    if (ast.isPresentCard()
        && ast.getCard().isPresentMin()) {
      String min = ast.getCard().getMin();
      try {
        int x = Integer.parseInt(min);
        return Optional.of(x);
      } catch (NumberFormatException ignored) {
        Log.warn("0xA0141 Failed to parse an integer value of max of ASTAdditionalAttribute "
            + ast.getName() + " from string " + min);
      }
    }
    return Optional.empty();
  }

  public static void cleanUp() {
    superProdCache.invalidateAll();
  }


}
