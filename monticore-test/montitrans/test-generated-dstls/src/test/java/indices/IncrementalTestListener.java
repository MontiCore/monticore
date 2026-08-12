package indices;

import de.monticore.ast.ASTNode;
import de.monticore.tf.runtime.inc.IIncrementalListener;
import de.monticore.tf.runtime.inc.ModificationOp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class IncrementalTestListener implements IIncrementalListener {
  
  private final LinkedList<NotificationRecord> calls = new LinkedList<>();
  
  /**
   * Common supertype for all recorded listener calls.
   */
  public sealed interface NotificationRecord
      permits TransformationStartCall, TransformationEndCall, ASTNodeAttachCall, ASTNodeDetachCall,
      ASTNodeCreationCall, ASTNodeModificationCall, ASTNodeListModificationCall {}
  
  /**
   * Recorded call of {@link #onTransformationStart(String)}.
   *
   * @param transformationName the transformation name passed to the listener
   */
  public record TransformationStartCall(@Nonnull String transformationName)
      implements NotificationRecord {}
  
  /**
   * Recorded call of {@link #onTransformationEnd(String)}.
   *
   * @param transformationName the transformation name passed to the listener
   */
  public record TransformationEndCall(@Nonnull String transformationName)
      implements NotificationRecord {}

  /**
   * Recorded call of {@link #onASTNodeCreation(ASTNode)}.
   *
   * @param node the created AST node
   */
  public record ASTNodeCreationCall(@Nonnull ASTNode node) implements NotificationRecord {}
  
  /**
   * Recorded call of {@link #onASTNodeAttach(ASTNode, ASTNode)}.
   *
   * @param node the attached AST node
   * @param parent the parent node, or {@code null} if {@code node} is a root node
   */
  public record ASTNodeAttachCall(@Nonnull ASTNode node, @Nullable ASTNode parent)
      implements NotificationRecord {}
  
  /**
   * Recorded call of {@link #onASTNodeDetach(ASTNode, ASTNode)}.
   *
   * @param node the detached AST node
   * @param parent the previous parent node
   */
  public record ASTNodeDetachCall(@Nonnull ASTNode node, @Nonnull ASTNode parent)
      implements NotificationRecord {}
  
  /**
   * Recorded call of {@link #onASTNodeModification(ASTNode, String, ModificationOp, Object, Object)}.
   *
   * @param node the modified AST node
   * @param attributeName the modified attribute name
   * @param modificationType the kind of modification
   * @param oldValue the previous value, or {@code null}
   * @param newValue the new value, or {@code null}
   */
  public record ASTNodeModificationCall(@Nonnull ASTNode node, @Nonnull String attributeName,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue)
      implements NotificationRecord {}
  
  /**
   * Recorded call of {@link #onASTNodeListModification(ASTNode, String, int, ModificationOp, Object, Object)}.
   *
   * @param node the modified AST node
   * @param attributeName the modified list attribute name
   * @param idx the index of the modified list element
   * @param modificationType the kind of modification
   * @param oldValue the previous value, or {@code null}
   * @param newValue the new value, or {@code null}
   */
  public record ASTNodeListModificationCall(@Nonnull ASTNode node, String attributeName, int idx,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue)
      implements NotificationRecord {}
  
  /**
   * Returns the mutable backing list of recorded notifications.
   */
  public LinkedList<NotificationRecord> getNotifications() {
    return calls;
  }
  
  /**
   * Returns the {@link NotificationRecord} at the specified position.
   *
   * @param idx the zero-based index of the desired entry
   * @return the {@link NotificationRecord} at position {@code idx}
   * @throws IndexOutOfBoundsException if {@code idx} is less than {@code 0}
   *                                   or greater than or equal to the number of entries
   */
  public NotificationRecord getNotification(int idx) {
    return calls.get(idx);
  }
  
  /**
   * Removes all recorded listener calls.
   */
  public void clearCalls() {
    calls.clear();
  }
  
  /**
   * Records a transformation-start callback.
   *
   * @param transformationName the started transformation name
   */
  @Override
  public void onTransformationStart(@Nonnull String transformationName) {
    calls.add(new TransformationStartCall(transformationName));
  }
  
  /**
   * Records a transformation-end callback.
   *
   * @param transformationName the ended transformation name
   */
  @Override
  public void onTransformationEnd(@Nonnull String transformationName) {
    calls.add(new TransformationEndCall(transformationName));
  }

  /**
   * Records an AST-node-creation callback.
   *
   * @param node the created AST node
   */
  @Override
  public void onASTNodeCreation(@Nonnull ASTNode node) {
    calls.add(new ASTNodeCreationCall(node));
  }

  /**
   * Records an AST-node-attach callback.
   *
   * @param node the attached AST node
   * @param parent the parent node, or {@code null} for root attachment
   */
  @Override
  public void onASTNodeAttach(@Nonnull ASTNode node, @Nullable ASTNode parent) {
    calls.add(new ASTNodeAttachCall(node, parent));
  }
  
  /**
   * Records an AST-node-detach callback.
   *
   * @param node the detached AST node
   * @param parent the previous parent node
   */
  @Override
  public void onASTNodeDetach(@Nonnull ASTNode node, @Nonnull ASTNode parent) {
    calls.add(new ASTNodeDetachCall(node, parent));
  }
  
  /**
   * Records an AST-node-attribute modification callback.
   *
   * @param node the modified AST node
   * @param attributeName the modified attribute name
   * @param modificationType the kind of modification
   * @param oldValue the previous value, or {@code null}
   * @param newValue the new value, or {@code null}
   */
  @Override
  public void onASTNodeModification(@Nonnull ASTNode node, @Nonnull String attributeName,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue) {
    calls.add(
        new ASTNodeModificationCall(node, attributeName, modificationType, oldValue, newValue));
  }
  
  /**
   * Records an AST-node-list modification callback.
   *
   * @param node the modified AST node
   * @param attributeName the modified list attribute name
   * @param idx the modified list index
   * @param modificationType the kind of modification
   * @param oldValue the previous value, or {@code null}
   * @param newValue the new value, or {@code null}
   */
  @Override
  public void onASTNodeListModification(@Nonnull ASTNode node, String attributeName, int idx,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue) {
    calls.add(new ASTNodeListModificationCall(node, attributeName, idx, modificationType, oldValue,
        newValue));
  }
  
  /**
   * Asserts the number of recorded notifications.
   *
   * @param expected expected number of entries in {@link #getNotifications()}
   */
  public void assertNumberOfNotifications(int expected) {
    assertEquals(expected, this.getNotifications().size(),
        "Expected " + expected + " notifications, but got " + calls.size());
  }
  
  /**
   * Asserts that the notification at {@code idx} is a transformation-start call,
   * passes it to {@code consumer}, and returns it.
   *
   * @param idx zero-based notification index
   * @param consumer assertion callback for the extracted call
   * @return the typed transformation-start call
   */
  public TransformationStartCall assertTransformationStartCall(int idx, Consumer<TransformationStartCall> consumer) {
    assertInstanceOf(IncrementalTestListener.TransformationStartCall.class,
        this.getNotification(idx));
    IncrementalTestListener.TransformationStartCall call =
        (IncrementalTestListener.TransformationStartCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }
  
  /**
   * Asserts that the notification at {@code idx} is a transformation-end call,
   * passes it to {@code consumer}, and returns it.
   *
   * @param idx zero-based notification index
   * @param consumer assertion callback for the extracted call
   * @return the typed transformation-end call
   */
  public TransformationEndCall assertTransformationEndCall(int idx, Consumer<TransformationEndCall> consumer) {
    assertInstanceOf(IncrementalTestListener.TransformationEndCall.class,
        this.getNotification(idx));
    IncrementalTestListener.TransformationEndCall call =
        (IncrementalTestListener.TransformationEndCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }

  /**
   * Asserts that the notification at {@code idx} is an AST-node-creation call,
   * passes it to {@code consumer}, and returns it.
   *
   * @param idx zero-based notification index
   * @param consumer assertion callback for the extracted call
   * @return the typed AST-node-creation call
   */
  public ASTNodeCreationCall assertASTNodeCreationCall(int idx, Consumer<ASTNodeCreationCall> consumer) {
    assertInstanceOf(IncrementalTestListener.ASTNodeCreationCall.class,
        this.getNotification(idx));
    IncrementalTestListener.ASTNodeCreationCall call =
        (IncrementalTestListener.ASTNodeCreationCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }
  
  /**
   * Asserts that the notification at {@code idx} is an AST-node-attach call,
   * passes it to {@code consumer}, and returns it.
   *
   * @param idx zero-based notification index
   * @param consumer assertion callback for the extracted call
   * @return the typed AST-node-attach call
   */
  public ASTNodeAttachCall assertASTNodeAttachCall(int idx, Consumer<ASTNodeAttachCall> consumer) {
    assertInstanceOf(IncrementalTestListener.ASTNodeAttachCall.class,
        this.getNotification(idx));
    IncrementalTestListener.ASTNodeAttachCall call =
        (IncrementalTestListener.ASTNodeAttachCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }
  
  /**
   * Asserts that the notification at {@code idx} is an AST-node-detach call,
   * passes it to {@code consumer}, and returns it.
   *
   * @param idx zero-based notification index
   * @param consumer assertion callback for the extracted call
   * @return the typed AST-node-detach call
   */
  public ASTNodeDetachCall assertASTNodeDetachCall(int idx, Consumer<ASTNodeDetachCall> consumer) {
    assertInstanceOf(IncrementalTestListener.ASTNodeDetachCall.class,
        this.getNotification(idx));
    IncrementalTestListener.ASTNodeDetachCall call =
        (IncrementalTestListener.ASTNodeDetachCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }
  
  /**
   * Asserts that the notification at {@code idx} is an AST-node-modification call,
   * passes it to {@code consumer}, and returns it.
   *
   * @param idx zero-based notification index
   * @param consumer assertion callback for the extracted call
   * @return the typed AST-node-modification call
   */
  public ASTNodeModificationCall assertASTNodeModificationCall(int idx, Consumer<ASTNodeModificationCall> consumer) {
    assertInstanceOf(IncrementalTestListener.ASTNodeModificationCall.class,
        this.getNotification(idx));
    IncrementalTestListener.ASTNodeModificationCall call =
        (IncrementalTestListener.ASTNodeModificationCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }
  
  /**
   * Asserts that the notification at {@code idx} is an AST-node-list-modification call,
   * passes it to {@code consumer}, and returns it.
   *
   * @param idx zero-based notification index
   * @param consumer assertion callback for the extracted call
   * @return the typed AST-node-list-modification call
   */
  public ASTNodeListModificationCall assertASTNodeListModificationCall(int idx, Consumer<ASTNodeListModificationCall> consumer) {
    assertInstanceOf(IncrementalTestListener.ASTNodeListModificationCall.class,
        this.getNotification(idx));
    IncrementalTestListener.ASTNodeListModificationCall call =
        (IncrementalTestListener.ASTNodeListModificationCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }
}