/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging.conforms;

import de.monticore.tagging.tags._ast.ASTTag;

import java.util.*;

/**
 * Recursive data structure for hierarchical tag definition
 */
public class TagData {
  protected final Map<String, TagData> inner = new HashMap<>();

  protected final List<ASTTag> tags = new ArrayList<>();

  public List<ASTTag> getTagsQualified(Iterable<String> parts) {
    return getFromIterator(parts.iterator()).tags;
  }

  public TagData getFromIterator(Iterator<String> parts) {
    if (parts.hasNext()) {
      return inner.computeIfAbsent(parts.next(), s -> new TagData()).getFromIterator(parts);
    }
    return this;
  }
}
