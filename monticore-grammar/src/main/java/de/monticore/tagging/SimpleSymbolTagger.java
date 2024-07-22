/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Streams;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.tagging.tags.TagsMill;
import de.monticore.tagging.tags._ast.*;
import de.monticore.tagging.tags._visitor.TagsTraverser;
import de.monticore.tagging.tags._visitor.TagsVisitor2;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simple tagger supporting tagging of symbols.
 * This tagger is language independent and keeps a cache of loaded tags
 */
public class SimpleSymbolTagger extends AbstractTagger implements ISymbolTagger {

  // Use a supplier, such that changes to the origin can be respected
  protected final Supplier<Iterable<ASTTagUnit>> backingTagUnits;

  // Mapping for TagUnit (.tags) -> TagFQNMapping
  protected final LoadingCache<ASTTagUnit, TagFQNMapping> tagUnitMapping;

  public SimpleSymbolTagger(@Nonnull Supplier<Iterable<ASTTagUnit>> tagUnitsSupplier) {
    this.backingTagUnits = tagUnitsSupplier;
    this.tagUnitMapping = CacheBuilder.newBuilder()
            .weakValues() // when TagUnits are unloaded from the backing supplier, do not hold onto them
            .build(new CacheLoader<ASTTagUnit, TagFQNMapping>() {
              @Override
              public TagFQNMapping load(ASTTagUnit tagUnit) throws Exception {
                // When required, compute the FQNMapping
                return computeFQNMapping(tagUnit);
              }
            });
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
   * Compute the concrete {@link ASTTargetElement}s of a single {@link ASTTagUnit} matching a given FQN (symbol)
   */
  @Nonnull
  protected List<ASTTargetElement> findTagTargetsOfTagUnit(@Nonnull ASTTagUnit tagUnit, @Nonnull String fqn) {
    // Get the FQN -> [ASTTargetElement] mapping
    TagFQNMapping unitMapping = tagUnitMapping.getUnchecked(tagUnit);
    // and add all found ASTTargetElements to the buffer
    List<ASTTargetElement> foundTags = unitMapping.mapping.get(fqn);
    if (foundTags == null) {
      return Collections.emptyList();
    } else {
      return foundTags;
    }
  }


  /**
   * Returns all the {@link ASTTargetElement}s targeting a specific symbol
   * <p>
   * Implementation specifics: {@link #findTagTargetsOfTagUnit(ASTTagUnit, String)} is lazily called for every loaded TagUnit
   *
   * @param symbol the symbol
   * @return the tag target elements targeting the symbol
   */
  protected Stream<ASTTargetElement> findTagTargets(@Nonnull ISymbol symbol) {
    String fqn = symbol.getFullName();

    // We return (the stream of) an iterator
    Iterator<ASTTargetElement> iterator = new ProgressiveIterator<ASTTargetElement, ASTTagUnit>
            (backingTagUnits.get().iterator()) {
      @Override
      Collection<? extends ASTTargetElement> doWork(ASTTagUnit tagUnit) {
        // Add the matching ASTTargetElement from #findTagTargetsOfTagUnit to the buffer
        return findTagTargetsOfTagUnit(tagUnit, fqn);
      }

      @Override
      void cleanup() {
        // cleanup (remove unloaded TagUnits)
        tagUnitMapping.cleanUp();
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
    var te = TagsMill.targetElementBuilder()
            .addModelElementIdentifier(TagsMill.defaultIdentBuilder().setMCQualifiedName(symbolFQN).build())
            .addTag(tag).build();
    getTagUnit().addTags(te);
    // and also update our mapping map
    @Nullable
    TagFQNMapping mOpt = tagUnitMapping.getIfPresent(getTagUnit());
    if (mOpt != null) {
      mOpt.mapping.computeIfAbsent(symbol.getFullName(), s -> new ArrayList<>()).add(te);
    }

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
    throw new IllegalStateException();
  }

  /**
   * Computes a (new) FQN->[ASTTargetElement] mapping of an ASTTagUnit
   */
  protected TagFQNMapping computeFQNMapping(ASTTagUnit tagUnit) {
    Map<String, List<ASTTargetElement>> fqnMapping = new LinkedHashMap<>();

    TagsTraverser t = TagsMill.traverser();
    t.add4Tags(new FQNMappingVisitor(fqnMapping));
    tagUnit.accept(t);

    return new TagFQNMapping(fqnMapping);
  }


  protected static class FQNMappingVisitor implements TagsVisitor2 {
    final Stack<String> contextStack;
    final Map<String, List<ASTTargetElement>> fqnMapping;

    public FQNMappingVisitor(Map<String, List<ASTTargetElement>> fqnMapping) {
      this.fqnMapping = fqnMapping;
      contextStack = new Stack<>();
    }

    @Override
    public void visit(ASTContext node) {
      // On "within", add the ModelElementIdentifier to the stack
      contextStack.add(((ASTDefaultIdent) node.getModelElementIdentifier()).getMCQualifiedName().getQName());
    }

    @Override
    public void visit(ASTTargetElement node) {
      for (ASTModelElementIdentifier id : node.getModelElementIdentifierList()) {
        // Map [withinPrefix.][ModelElementIdentifier] to this target element (and possible further elements => List)
        List<String> fqn = new ArrayList<>(this.contextStack);
        fqn.add(((ASTDefaultIdent) id).getMCQualifiedName().getQName());
        fqnMapping.computeIfAbsent(Joiners.DOT.join(fqn), s -> new LinkedList<>())
                .add(node);
      }

    }

    @Override
    public void endVisit(ASTContext node) {
      // pop the within-stick
      contextStack.pop();
    }
  }

  /**
   * Buffering iterator of type A with an element of flattening.
   * calls {@link #doWork(B)} when demanded and iterates on the doWorks return value.
   */
  protected abstract static class ProgressiveIterator<A, B> implements Iterator<A> {
    // With a buffer of target elements
    final LinkedList<A> targetElementBuffer = new LinkedList<>();
    // And an iterator to the backing tag units
    final Iterator<B> backing;

    public ProgressiveIterator(Iterator<B> backing) {
      this.backing = backing;
    }

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
    public A next() {
      return targetElementBuffer.removeFirst();
    }

    /**
     * Advance to the next backing tagunit and add matching targets to the buffer
     */
    void tryToAdvance() {
      if (!backing.hasNext()) {
        this.cleanup();
        return;
      }
      // Actually do the tagging related logic
      targetElementBuffer.addAll(this.doWork(backing.next()));
    }

    /**
     * Perform some work and add it to a buffer
     *
     * @param next the unit to work on
     * @return the values to be added to a buffer
     */
    @Nonnull
    abstract Collection<? extends A> doWork(B next);

    abstract void cleanup();
  }

  protected static class TagFQNMapping {
    // FQN -> [TargetElement]
    final Map<String, List<ASTTargetElement>> mapping;

    public TagFQNMapping(Map<String, List<ASTTargetElement>> mapping) {
      this.mapping = mapping;
    }
  }

}
