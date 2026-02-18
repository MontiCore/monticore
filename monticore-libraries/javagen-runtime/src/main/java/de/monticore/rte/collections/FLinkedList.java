package de.monticore.rte.collections;

import de.monticore.rte.functions.Function1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implementation of {@link FList} with a linked list.
 * For optimization purposes we not only link single elements,
 * but whole arrays of elements.
 * Generally, we assume that extending a list happens way more often
 * than removing from a list.
 *
 * @param <T>
 */
public class FLinkedList<T> implements FList<T> {

  static final int MAX_ARRAY_SIZE = 100;
  static final int MIN_NO_MERGE_ARRAY_SIZE = 10;

  static final LinkedElementIterator<?> EMPTY_ITERATOR = new LinkedElementIterator<>(null);

  static final FList<?> EMPTY = new FLinkedList<>();

  protected final int size;
  protected final LinkedElement<T> head;

  protected FLinkedList(int size, LinkedElement<T> head) {
    this.size = size;
    this.head = head;
  }

  public FLinkedList() {
    this.size = 0;
    this.head = null;
  }

  public FLinkedList(T[] values) {
    this.size = values.length;
    this.head = createFromUnsafeArray(values, null);
  }

  public FLinkedList(FCollection<T> other) {
    Object[] values = other.toArray();
    this.size = values.length;
    this.head = createFromSafeArray(values, null);
  }

  public FLinkedList(Collection<T> other) {
    Object[] values = other.toArray();
    this.size = values.length;
    this.head = createFromSafeArray(values, null);
  }

  /**
   * Create a linked element from an array which will never change in the future (e.g. we created it)
   */
  @SuppressWarnings("unchecked")
  protected static <T> LinkedElement<T> createFromSafeArray(Object[] values, LinkedElement<T> next) {
    if (values.length > MAX_ARRAY_SIZE) {
      return createFromUnsafeArray(values, next);
    }
    if (values.length == 0) {
      return null;
    }
    if (values.length == 1) {
      return new LinkedElement<>((T) values[0], null, next);
    }
    return new LinkedElement<>(null, values, next);
  }

  /**
   * Create a linked element from an array which might change in the future (e.g. we didn't create it)
   */
  @SuppressWarnings("unchecked")
  protected static <T> LinkedElement<T> createFromUnsafeArray(Object[] values, LinkedElement<T> next) {
    LinkedElement<T> head = next;
    int smallestReadIndex = values.length;
    while (smallestReadIndex > 0) {
      if (smallestReadIndex == 1) {
        head = new LinkedElement<>((T) values[0], null, head);
        smallestReadIndex--;
      }
      else {
        Object[] copy = new Object[Math.min(smallestReadIndex, MAX_ARRAY_SIZE)];
        System.arraycopy(values, smallestReadIndex - copy.length, copy, 0, copy.length);
        head = new LinkedElement<>(null, copy, head);
        smallestReadIndex -= copy.length;
      }

    }
    return head;
  }

  /**
   * Create a linked element from a list
   */
  protected static <T> LinkedElement<T> createFromList(List<T> values, LinkedElement<T> next) {
    LinkedElement<T> head = next;
    int smallestReadIndex = values.size();
    while (smallestReadIndex > 0) {
      if (smallestReadIndex == 1) {
        head = new LinkedElement<>(values.get(0), null, head);
        smallestReadIndex--;
      }
      else {
        int chunkLength = Math.min(smallestReadIndex, MAX_ARRAY_SIZE);
        Object[] chunk = values.subList(smallestReadIndex - chunkLength, smallestReadIndex).toArray();
        head = new LinkedElement<>(null, chunk, head);
        smallestReadIndex -= chunkLength;
      }

    }
    return head;
  }

  @Override
  public int size() {
    return this.size;
  }

  @Override
  public boolean isEmpty() {
    return this.head == null;
  }

  @Override
  public boolean contains(Object element) {
    LinkedElement<T> head = this.head;
    while (head != null) {
      if (head.contains(element)) {
        return true;
      }
      head = head.next;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<T> iterator() {
    if (this.head == null) {
      return (Iterator<T>) EMPTY_ITERATOR;
    }
    return new LinkedElementIterator<>(this.head);
  }

  @Override
  public void forEach(Consumer<? super T> action) {
    LinkedElement<T> head = this.head;
    while (head != null) {
      head.forEach(action);
      head = head.next;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof FList<?>))
      return false;

    FList<?> other = (FList<?>) obj;

    if (this.size != other.size()) {
      return false;
    }

    Iterator<?> otherIter = other.iterator();

    LinkedElement<T> myHead = this.head;
    while (myHead != null) {
      if (!myHead.checkEqual(otherIter)) {
        return false;
      }
      myHead = myHead.next;
    }
    return true;
  }

  @Override
  public int hashCode() {
    LinkedElement<T> head = this.head;
    int hashCode = 1;
    while (head != null) {
      hashCode = head.updateHashCode(hashCode);
      head = head.next;
    }
    return hashCode;
  }

  @Override
  public String toString() {
    if (isEmpty()) {
      return "[]";
    }
    StringBuilder res = new StringBuilder();
    res.append("[");
    for (T ele : this) {
      res.append(ele).append(", ");
    }
    res.delete(res.length() - 2, res.length());
    res.append("]");
    return res.toString();
  }

  @SuppressWarnings("unchecked")
  @Override
  public T get(int index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Negative index " + index + " is not allowed");
    }
    int toSkip = index;
    LinkedElement<T> head = this.head;
    while (head != null) {
      if (head.elementsArray == null) {
        if (toSkip == 0) {
          return head.ele;
        }
        toSkip--;
      }
      else {
        if (head.elementsArray.length > toSkip) {
          return (T) head.elementsArray[toSkip];
        }
        toSkip -= head.elementsArray.length;
      }
      head = head.next;
    }
    throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for length " + size());
  }

  @Override
  public FList<T> withPrepended(T ele) {
    return new FLinkedList<>(this.size + 1, new LinkedElement<>(ele, null, this.head));
  }

  @Override
  public FList<T> withPrepended(FCollection<? extends T> elements) {
    if (elements.size() == 0) {
      return this;
    }

    Object[] values = elements.toArray();
    return new FLinkedList<>(
        this.size + values.length,
        createFromSafeArray(values, this.head)
    );
  }

  /**
   * Skips some values from the beginning of the provided head, puts them into the provided list, and returns a new head which starts after the skipped
   * values
   *
   * @param toSkip             The amount of values to skip
   * @param skippedValueArrays The list to which arrays with all skipped values are added. The concatenation of all those arrays gives all skipped values, in
   *                           order.
   * @param head               The head from which to start skipping
   * @return The head after the skipped values.
   */
  protected LinkedElement<T> skipElements(int toSkip, List<Object[]> skippedValueArrays, LinkedElement<T> head) {
    Object[] currentlyFillingArray = null; // Never full. Must always be set to null (and added to skippedValueArrays) once it's full
    int currentlyFillingI = 0;

    while (toSkip > 0) {
      if (head.elementsArray == null) {
        toSkip--;

        if (currentlyFillingArray == null) {
          currentlyFillingArray = new Object[MAX_ARRAY_SIZE];
          currentlyFillingI = 0;
        }
        currentlyFillingArray[currentlyFillingI] = head.ele;
        currentlyFillingI++;
        if (currentlyFillingI == currentlyFillingArray.length) {
          skippedValueArrays.add(currentlyFillingArray);
          currentlyFillingArray = null;
        }
      }
      else {
        int eleLength = head.elementsArray.length;
        assert eleLength > 1;

        boolean isLastSkipAndSplittingArray = eleLength > toSkip;
        int toSkipEleLength = Math.min(eleLength, toSkip);

        // Skip elements

        // Now there are three choices:
        // 1. Finish the currentlyFillingArray and reuse the head's array. This requires to copy currentlyFillingArray, if that's not null (since it's definitively not full)
        // 2. Copy the head's array fully into currentlyFillingArray
        // 3. Copy whatever fits of the head's array into currentlyFillingArray and put the remaining elements into a new currentlyFillingArray.

        if (currentlyFillingArray == null) {
          if (isLastSkipAndSplittingArray) {
            // These are the last values to skip. Copying is our only choice
            skippedValueArrays.add(Arrays.copyOf(head.elementsArray, toSkipEleLength));
          }
          else {
            // This makes it easy
            skippedValueArrays.add(head.elementsArray);
          }
        }
        else if (currentlyFillingArray.length - currentlyFillingI < toSkipEleLength) {
          int remainingSpaceInCurrentFilling = currentlyFillingArray.length - currentlyFillingI;
          // head's array doesn't fit into currentlyFillingArray

          if (toSkipEleLength < MIN_NO_MERGE_ARRAY_SIZE || isLastSkipAndSplittingArray) {
            // It isn't worth to keep the head's array. Use choice 3.
            System.arraycopy(head.elementsArray, 0, currentlyFillingArray, currentlyFillingI, remainingSpaceInCurrentFilling);
            skippedValueArrays.add(currentlyFillingArray);

            currentlyFillingArray = new Object[isLastSkipAndSplittingArray ? toSkipEleLength - remainingSpaceInCurrentFilling : MAX_ARRAY_SIZE];
            System.arraycopy(head.elementsArray, remainingSpaceInCurrentFilling, currentlyFillingArray, 0, toSkipEleLength - remainingSpaceInCurrentFilling);
            currentlyFillingI = toSkipEleLength - remainingSpaceInCurrentFilling;
            if (isLastSkipAndSplittingArray) {
              skippedValueArrays.add(currentlyFillingArray);
              currentlyFillingArray = null;
            }
          }
          else {
            // Choice 1.
            skippedValueArrays.add(Arrays.copyOf(currentlyFillingArray, currentlyFillingI));
            currentlyFillingArray = null;
            skippedValueArrays.add(head.elementsArray);
          }
        }
        else {
          // Choice 2.
          System.arraycopy(head.elementsArray, 0, currentlyFillingArray, currentlyFillingI, toSkipEleLength);
          currentlyFillingI += toSkipEleLength;
          if (currentlyFillingI == currentlyFillingArray.length) {
            skippedValueArrays.add(currentlyFillingArray);
            currentlyFillingArray = null;
          }
        }
        toSkip -= toSkipEleLength;

        // Skip finished for this element

        if (isLastSkipAndSplittingArray) {
          // Have to create new linked element from ending of array

          Object[] remainingValues = new Object[eleLength - toSkipEleLength];
          System.arraycopy(head.elementsArray, toSkipEleLength, remainingValues, 0, remainingValues.length);
          head = createFromSafeArray(remainingValues, head.next);
          break;
        }
      }
      head = head.next;
    }
    if (currentlyFillingArray != null) {
      skippedValueArrays.add(Arrays.copyOf(currentlyFillingArray, currentlyFillingI));
    }

    assert toSkip == 0;
    return head;
  }

  protected LinkedElement<T> reconstructSkippedElements(List<Object[]> skippedValueArrays, LinkedElement<T> next) {
    for (int i = skippedValueArrays.size() - 1; i >= 0; i--) {
      next = createFromSafeArray(skippedValueArrays.get(i), next);
    }
    return next;
  }

  @Override
  public FList<T> withInserted(int index, FCollection<? extends T> elements) {
    if (index < 0 || index > this.size()) {
      throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for length " + size());
    }
    if (elements.size() == 0) {
      return this;
    }
    if (index == 0) {
      return withPrepended(elements);
    }

    List<Object[]> skippedValueArrays = new ArrayList<>();
    LinkedElement<T> head = skipElements(index, skippedValueArrays, this.head);
    return new FLinkedList<>(
        this.size + elements.size(),
        reconstructSkippedElements(skippedValueArrays, createFromSafeArray(elements.toArray(), head))
    );
  }

  @Override
  public FList<T> withInserted(int index, T ele) {
    if (index < 0 || index > this.size()) {
      throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for length " + size());
    }
    if (index == 0) {
      return withPrepended(ele);
    }

    List<Object[]> skippedValueArrays = new ArrayList<>();
    LinkedElement<T> head = skipElements(index, skippedValueArrays, this.head);
    return new FLinkedList<>(
        this.size + 1,
        reconstructSkippedElements(skippedValueArrays, new LinkedElement<>(ele, null, head))
    );
  }

  @Override
  public FList<T> withRemoved(int index, int n) {
    if (n < 0) {
      throw new IllegalArgumentException("Negative size " + n + " not allowed");
    }
    if (index < 0) {
      throw new IndexOutOfBoundsException("Index " + index + " is negative");
    }
    if (index + n > this.size()) {
      throw new IllegalArgumentException("Size " + n + " and index " + index + " are out of bounds for length " + size());
    }
    if (n == 0) {
      return this;
    }
    if (n == this.size()) {
      return FList.of();
    }

    LinkedElement<T> head = this.head;

    // Skip elements
    List<Object[]> skippedValueArrays = null;
    if (index != 0) {
      skippedValueArrays = new ArrayList<>();
      head = skipElements(index, skippedValueArrays, head);
    }

    int toRemove = n;
    // Now start removing
    while (toRemove > 0) {
      if (head.elementsArray == null) {
        toRemove--;
      }
      else {
        if (head.elementsArray.length > toRemove) {
          Object[] remainingValues = new Object[head.elementsArray.length - toRemove];
          System.arraycopy(head.elementsArray, toRemove, remainingValues, 0, remainingValues.length);
          head = createFromSafeArray(remainingValues, head.next);
          toRemove = 0;
          break;
        }
        toRemove -= head.elementsArray.length;
      }
      head = head.next;
    }
    assert toRemove == 0;

    // And now put it back together
    if (skippedValueArrays == null) {
      return new FLinkedList<>(
          this.size - n,
          head
      );
    }
    else {
      return new FLinkedList<>(
          this.size - n,
          reconstructSkippedElements(skippedValueArrays, head)
      );
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<T> toJava() {
    if (this.head == null) {
      return List.of();
    }
    if (this.head.next == null) {
      if (this.head.elementsArray != null) {
        return Collections.unmodifiableList((List<T>) Arrays.asList(this.head.elementsArray));
      }
      return List.of(this.head.ele);
    }
    return Collections.unmodifiableList((List<T>) Arrays.asList(toArray()));
  }

  protected void insertToArray(Object[] array) {
    LinkedElement<T> head = this.head;

    int i = 0;
    while (head != null) {
      i = head.insertToArray(array, i);
      head = head.next;
    }
  }

  @Override
  public Object[] toArray() {
    Object[] res = new Object[this.size];
    insertToArray(res);
    return res;
  }

  @Override
  public T[] toArray(Function1<T[], Integer> constructor) {
    T[] res = constructor.apply(this.size);
    insertToArray(res);
    return res;
  }

  @Override
  public FList<T> reversed() {
    if (this.size == 0) {
      return FList.of();
    }
    if (this.size == 1) {
      return this;
    }
    Object[] values = toArray();
    Collections.reverse(Arrays.asList(values));
    return new FLinkedList<>(
        this.size,
        createFromSafeArray(values, null)
    );
  }

  @Override
  public FList<T> filtered(Function1<Boolean, ? super T> predicate) {
    if (this.size == 0) {
      return FList.of();
    }
    List<T> values = new ArrayList<>();
    this.forEach(ele -> {
      if (predicate.apply(ele)) {
        values.add(ele);
      }
    });
    if (values.size() == 0) {
      return FList.of();
    }
    return new FLinkedList<>(
        values.size(),
        createFromList(values, null)
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public <R> FList<R> mapped(Function1<? extends R, ? super T> mapper) {
    if (this.size == 0) {
      return FList.of();
    }
    if (this.size == 1) {
      return FList.of(mapper.apply(this.head.ele));
    }
    Object[] values = toArray();
    for (int i = 0; i < values.length; i++) {
      values[i] = mapper.apply((T) values[i]);
    }
    return new FLinkedList<>(
        this.size,
        createFromSafeArray(values, null)
    );
  }

  @Override
  public <R> FList<R> flatMapped(Function1<? extends FCollection<? extends R>, ? super T> mapper) {
    if (this.size == 0) {
      return FList.of();
    }
    List<R> values = new ArrayList<>();
    this.forEach(ele -> mapper.apply(ele).forEach(values::add));
    if (values.size() == 0) {
      return FList.of();
    }
    return new FLinkedList<>(
        values.size(),
        createFromList(values, null)
    );
  }

  @Override
  public FList<T> distinct() {
    return filtered(new HashSet<T>()::add);
  }

  @Override
  public FList<T> sorted() {
    if (this.size == 0) {
      return FList.of();
    }
    if (this.size == 1) {
      return this;
    }
    Object[] values = toArray();
    Arrays.sort(values);
    return new FLinkedList<>(
        this.size,
        createFromSafeArray(values, null)
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public FList<T> sorted(Comparator<? super T> comparator) {
    if (this.size == 0) {
      return FList.of();
    }
    if (this.size == 1) {
      return this;
    }
    Object[] values = toArray();
    Arrays.sort((T[]) values, comparator);
    return new FLinkedList<>(
        this.size,
        createFromSafeArray(values, null)
    );
  }

  protected static class LinkedElement<T> {

    public final T ele;
    public final Object[] elementsArray;
    public final LinkedElement<T> next;

    protected LinkedElement(T ele, Object[] elementsArray, LinkedElement<T> next) {
      this.ele = ele;
      this.elementsArray = elementsArray;
      this.next = next;
      assert elementsArray == null || elementsArray.length > 1;
    }

    public boolean contains(Object obj) {
      if (elementsArray == null) {
        return Objects.equals(ele, obj);
      }
      for (Object ele : elementsArray) {
        if (Objects.equals(ele, obj)) {
          return true;
        }
      }
      return false;
    }

    @SuppressWarnings("unchecked")
    public void forEach(Consumer<? super T> action) {
      if (elementsArray == null) {
        action.accept(this.ele);
      }
      else {
        for (Object ele : elementsArray) {
          action.accept((T) ele);
        }
      }
    }

    /**
     * We already know that we have the same size.
     */
    public boolean checkEqual(Iterator<?> iter) {
      if (elementsArray == null) {
        assert iter.hasNext();
        return Objects.equals(this.ele, iter.next());
      }
      for (Object ele : elementsArray) {
        assert iter.hasNext();
        if (!Objects.equals(ele, iter.next())) {
          return false;
        }
      }
      return true;
    }

    public int updateHashCode(int hashCode) {
      if (elementsArray == null) {
        return 31 * hashCode + Objects.hashCode(this.ele);
      }
      for (Object ele : elementsArray) {
        hashCode = 31 * hashCode + Objects.hashCode(ele);
      }
      return hashCode;
    }

    public int insertToArray(Object[] array, int index) {
      if (elementsArray == null) {
        array[index] = this.ele;
        return index + 1;
      }
      System.arraycopy(elementsArray, 0, array, index, elementsArray.length);
      return index + elementsArray.length;
    }

  }

  protected static class LinkedElementIterator<T> implements Iterator<T> {

    protected LinkedElement<T> head;
    protected int nextEleI;

    protected LinkedElementIterator(LinkedElement<T> head) {
      setToHead(head);
    }

    protected void setToHead(LinkedElement<T> head) {
      this.head = head;
      if (head == null || head.elementsArray == null) {
        this.nextEleI = -1;
      }
      else {
        this.nextEleI = 0;
      }
    }

    @Override
    public boolean hasNext() {
      return this.nextEleI != -1 || this.head != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T next() {
      if (this.nextEleI != -1) {
        T ele = (T) this.head.elementsArray[this.nextEleI];
        this.nextEleI++;
        if (this.nextEleI >= this.head.elementsArray.length) {
          setToHead(head.next);
        }
        return ele;
      }
      else if (this.head != null) {
        T ele = this.head.ele;
        setToHead(head.next);
        return ele;
      }
      else {
        throw new NoSuchElementException();
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void forEachRemaining(Consumer<? super T> action) {
      LinkedElement<T> head = this.head;
      if (this.nextEleI != -1) {
        for (int i = this.nextEleI; i < head.elementsArray.length; i++) {
          action.accept((T) head.elementsArray[i]);
        }
        head = head.next;
      }
      while (head != null) {
        if (head.elementsArray == null) {
          action.accept(head.ele);
        }
        else {
          for (Object ele : head.elementsArray) {
            action.accept((T) ele);
          }
        }
        head = head.next;
      }
      this.head = null;
    }

  }

}
