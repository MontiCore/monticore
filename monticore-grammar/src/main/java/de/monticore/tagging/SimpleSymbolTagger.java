/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging;

import com.google.common.collect.Streams;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.ASTContext;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.tagging.tags._ast.ASTTargetElement;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simple tagger supporting tagging of symbols.
 * This tagger is language independent
 */
public class SimpleSymbolTagger extends AbstractTagger implements ISymbolTagger {

  // Use a supplier, such that changes to the origin can be respected
  protected final Supplier<Iterable<ASTTagUnit>> backingTagUnits;

  public SimpleSymbolTagger(@Nonnull Supplier<Iterable<ASTTagUnit>> tagUnitsSupplier) {
    this.backingTagUnits = tagUnitsSupplier;
  }

  public SimpleSymbolTagger() {
    this(TagRepository::getLoadedTagUnits);
  }

  @Override
  @Nonnull
  public List<ASTTag> getTags(@Nonnull ISymbol symbol) {
    // Return the tags of all matching target elements
    return findTagTargets(symbol).map(ASTTargetElement::getTagList).flatMap(List::stream).collect(Collectors.toList());
  }

  @Override
  public boolean removeTag(@Nonnull ISymbol symbol, @Nonnull ASTTag tag) {
    return findTagTargets(symbol)
            .anyMatch(target -> target.getTagList().remove(tag));
  }


  /**
   * Returns all the {@link ASTTargetElement}s targeting a specific symbol
   *
   * @param symbol the symbol
   * @return the tag target elements targeting the symbol
   */
  protected Stream<ASTTargetElement> findTagTargets(@Nonnull ISymbol symbol) {
    // A list of enclosing scopes of this symbol
    // As it is possible to recursively use the within notation (to describe contexts),
    // we have to step through each scope between the artifact scope and symbol itself to find these contexts
    List<String> scopesToArtifact = getScopeDifferences(symbol.getEnclosingScope(), getArtifactScope(symbol.getEnclosingScope()));

    // We return (the stream of) an iterator
    Iterator<ASTTargetElement> iterator = new Iterator<ASTTargetElement>() {
      // With a buffer of target elements
      final LinkedList<ASTTargetElement> targetElementBuffer = new LinkedList<>();
      // And an iterator to the backing tag units
      final Iterator<ASTTagUnit> backing = backingTagUnits.get().iterator();

      @Override
      public boolean hasNext() {
        // In case the buffer is not empty => return true
        if (!targetElementBuffer.isEmpty()) return true;
        // try to advance the buffer (find targets within the next tagunit)
        tryToAdvance();
        // and return whether the buffer now contains targets
        return !targetElementBuffer.isEmpty();
      }

      @Override
      public ASTTargetElement next() {
        return targetElementBuffer.removeFirst();
      }

      /**
       * Advance to the next backing tagunit and add matching targets to the buffer
       */
      void tryToAdvance() {
        if (!backing.hasNext()) return;
        ASTTagUnit tagUnit = backing.next();

        if (scopesToArtifact.isEmpty()) {
          // No within is possible, as the symbol is directly in the artifact scope
          // Find all matching targets within the tag unit and add them to the buffer
          findTargetsBy(tagUnit, symbol.getName()).forEach(targetElementBuffer::add);
        } else {
          // within/context must always be on scopes, so we can use name matching instead of pattern matching
          // (not that they work on symbols)
          scopesToArtifact.add(symbol.getName());

          // Find within-contexts, that match the highest enclosing scope (the one within the artifact scope)
          List<ASTContext> contexts = findContextBy(tagUnit, scopesToArtifact.get(0)).collect(Collectors.toList());

          // Construct a pseudo, right now only the (not FQ) symbol name
          String joinedNames = Joiners.DOT.join(scopesToArtifact);
          // Find all matching targets based on the symbol name within the within contexts and add them to the buffer
          findTargetsBy(tagUnit, joinedNames).forEach(targetElementBuffer::add);

          // and pop the outermost scope
          scopesToArtifact.remove(0);

          // repeat the last steps for all following scopes, until we reach the directly enclosing scope
          while (scopesToArtifact.size() > 1) {
            // Store the contexts up to this point in the tempContexts variable
            List<ASTContext> tempContexts = contexts;
            // and clear the existing contexts
            contexts = new ArrayList<>();
            // add the scope we currently work on to the qualified name
            joinedNames = Joiners.DOT.join(scopesToArtifact);
            // and pop the scope
            String name = scopesToArtifact.remove(0);

            // For all previous contexts
            for (ASTContext context : tempContexts) {
              // find more precise contexts within the previous contexts based on the qualified name
              findContextBy(context, name).forEach(contexts::add);
              // and all matching targets based on the qualified name within the contexts and add them to the buffer
              findTargetsBy(context, joinedNames).forEach(targetElementBuffer::add);
              ;
            }
          }
          // Finally, all matching targets based on the qualified name within the contexts and add them to the buffer
          for (ASTContext context : contexts) {
            findTargetsBy(context, scopesToArtifact.get(0)).forEach(targetElementBuffer::add);
            ;
          }
        }
      }
    };

    // Return a stream of this iterator
    return Streams.stream(iterator);
  }

  @Override
  public void addTag(@Nonnull ISymbol symbol, @Nonnull ASTTag tag) {
    // We simply add a tag using the FQN of the symbol
    List<String> partsFQN = Splitters.QUALIFIED_NAME_DELIMITERS.splitToList(symbol.getFullName());
    ASTMCQualifiedName symbolFQN = MCBasicTypesMill.mCQualifiedNameBuilder().setPartsList(partsFQN).build();
    getTagUnit().addTags(TagsMill.targetElementBuilder()
            .addModelElementIdentifier(TagsMill.defaultIdentBuilder().setMCQualifiedName(symbolFQN).build())
            .addTag(tag).build());
  }

  /**
   * Return any backing tag unit model
   *
   * @throws IllegalStateException in case no tag unit exists
   */
  protected ASTTagUnit getTagUnit() throws IllegalStateException {
    Iterator<ASTTagUnit> tagUnits = backingTagUnits.get().iterator();
    if (tagUnits.hasNext())
      return tagUnits.next();
    Log.error("0x74695: No backing tag unit found");
    throw new IllegalStateException("0x74695: No backing tag unit found");
  }

  @Override
  protected List<String> getScopeDifferences(IScope scope, IScope target) {
    List<String> scopeStack = new ArrayList<>();
    do {
      if (!scope.isPresentName()) {
        break;
      }
      scopeStack.add(0, scope.getName());
      scope = scope.getEnclosingScope();
    } while ((scope != target.getEnclosingScope()));
    return scopeStack;
  }

}
