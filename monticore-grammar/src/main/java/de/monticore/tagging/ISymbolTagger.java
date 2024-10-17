/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging;

import de.monticore.symboltable.ISymbol;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A tagger is always backed by a set of {@link ASTTagUnit}s, such as TagRepository.getLoadedTagUnits
 */
public interface ISymbolTagger {
  /**
   * @param symbol the symbol
   * @return a list of tags associated with the symbol
   */
  @Nonnull
  List<ASTTag> getTags(@Nonnull ISymbol symbol);

  /**
   * Removes a tag from a symbol
   *
   * @param symbol the sybmol
   * @param tag    the tag
   * @return whether a tag was removed
   */
  boolean removeTag(@Nonnull ISymbol symbol, @Nonnull ASTTag tag);

  /**
   * Tags a symbol with a specific tag.
   * In case multiple tagging models are backing a tagger,
   * it is up to the specific tagger implementation to decide which model to add to.
   *
   * @param symbol the symbol
   * @param tag    the tag to be added
   */
  void addTag(@Nonnull ISymbol symbol, @Nonnull ASTTag tag);

}
