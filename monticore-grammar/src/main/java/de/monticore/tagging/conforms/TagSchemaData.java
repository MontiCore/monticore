/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging.conforms;

import de.monticore.tagging.tagschema.TagSchemaMill;
import de.monticore.tagging.tagschema._symboltable.*;
import de.monticore.tagging.tagschema._visitor.TagSchemaTraverser;
import de.monticore.tagging.tagschema._visitor.TagSchemaVisitor2;

import java.util.*;

/**
 * Stores data of TagTypeSymbols in a [NonTerminal => [TagTypeSymbols]] form
 */
public class TagSchemaData {
  protected Map<String, List<SimpleTagTypeSymbol>> simpleTagTypes = new HashMap<>();
  protected Map<String, List<ValuedTagTypeSymbol>> valuedTagTypes = new HashMap<>();
  protected Map<String, List<EnumeratedTagTypeSymbol>> enumeratedTagTypes = new HashMap<>();
  protected Map<String, List<ComplexTagTypeSymbol>> complexTagTypes = new HashMap<>();

  public final static String WILDCARD = "___WILDCARD_TAG_TYPE_%";

  public TagSchemaData(TagSchemaSymbol symbol) {
    TagSchemaTraverser tagSchemaTraverser = TagSchemaMill.traverser();
    tagSchemaTraverser.add4TagSchema(new CollectorVisitor());
    symbol.getSpannedScope().accept(tagSchemaTraverser);
  }

  public Map<String, List<SimpleTagTypeSymbol>> getSimpleTagTypes() {
    return simpleTagTypes;
  }

  public Map<String, List<ValuedTagTypeSymbol>> getValuedTagTypes() {
    return valuedTagTypes;
  }

  public Map<String, List<EnumeratedTagTypeSymbol>> getEnumeratedTagTypes() {
    return enumeratedTagTypes;
  }

  public Map<String, List<ComplexTagTypeSymbol>> getComplexTagTypes() {
    return complexTagTypes;
  }

  public class CollectorVisitor implements TagSchemaVisitor2 {
    @Override
    public void visit(SimpleTagTypeSymbol node) {
      for (String nonTermName : getFors(node)) {
        getSimpleTagTypes().computeIfAbsent(nonTermName, (s) -> new ArrayList<>()).add(node);
      }
    }

    @Override
    public void visit(ValuedTagTypeSymbol node) {
      for (String nonTermName : getFors(node)) {
        getValuedTagTypes().computeIfAbsent(nonTermName, (s) -> new ArrayList<>()).add(node);
      }
    }

    @Override
    public void visit(EnumeratedTagTypeSymbol node) {
      for (String nonTermName : getFors(node)) {
        getEnumeratedTagTypes().computeIfAbsent(nonTermName, (s) -> new ArrayList<>()).add(node);
      }
    }

    @Override
    public void visit(ComplexTagTypeSymbol node) {
      for (String nonTermName : getFors(node)) {
        getComplexTagTypes().computeIfAbsent(nonTermName, (s) -> new ArrayList<>()).add(node);
      }
    }

    protected Iterable<String> getFors(TagTypeSymbol symbol) {
      if (symbol.isScopeWildcard()) {
        return Collections.singleton(WILDCARD);
      }
      return symbol.getScopesList();
    }
  }

}
