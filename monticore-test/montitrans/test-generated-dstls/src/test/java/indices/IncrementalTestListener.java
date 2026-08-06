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
      ASTNodeModificationCall, ASTNodeListModificationCall {}
  
  public record TransformationStartCall(@Nonnull String transformationName)
      implements NotificationRecord {}
  
  public record TransformationEndCall(@Nonnull String transformationName)
      implements NotificationRecord {}
  
  public record ASTNodeAttachCall(@Nonnull ASTNode node, @Nullable ASTNode parent)
      implements NotificationRecord {}
  
  public record ASTNodeDetachCall(@Nonnull ASTNode node, @Nonnull ASTNode parent)
      implements NotificationRecord {}
  
  public record ASTNodeModificationCall(@Nonnull ASTNode node, @Nonnull String attributeName,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue)
      implements NotificationRecord {}
  
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
  
  @Override
  public void onTransformationStart(@Nonnull String transformationName) {
    calls.add(new TransformationStartCall(transformationName));
  }
  
  @Override
  public void onTransformationEnd(@Nonnull String transformationName) {
    calls.add(new TransformationEndCall(transformationName));
  }
  
  @Override
  public void onASTNodeAttach(@Nonnull ASTNode node, @Nullable ASTNode parent) {
    calls.add(new ASTNodeAttachCall(node, parent));
  }
  
  @Override
  public void onASTNodeDetach(@Nonnull ASTNode node, @Nonnull ASTNode parent) {
    calls.add(new ASTNodeDetachCall(node, parent));
  }
  
  @Override
  public void onASTNodeModification(@Nonnull ASTNode node, @Nonnull String attributeName,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue) {
    calls.add(
        new ASTNodeModificationCall(node, attributeName, modificationType, oldValue, newValue));
  }
  
  @Override
  public void onASTNodeListModification(@Nonnull ASTNode node, String attributeName, int idx,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue) {
    calls.add(new ASTNodeListModificationCall(node, attributeName, idx, modificationType, oldValue,
        newValue));
  }
  
  public void assertNumberOfNotifications(int expected) {
    assertEquals(expected, this.getNotifications().size(),
        "Expected " + expected + " notifications, but got " + calls.size());
  }
  
  public TransformationStartCall assertTransformationStartCall(int idx, Consumer<TransformationStartCall> consumer) {
    assertInstanceOf(IncrementalTestListener.TransformationStartCall.class,
        this.getNotification(idx));
    IncrementalTestListener.TransformationStartCall call =
        (IncrementalTestListener.TransformationStartCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }
  
  public TransformationEndCall assertTransformationEndCall(int idx, Consumer<TransformationEndCall> consumer) {
    assertInstanceOf(IncrementalTestListener.TransformationEndCall.class,
        this.getNotification(idx));
    IncrementalTestListener.TransformationEndCall call =
        (IncrementalTestListener.TransformationEndCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }
  
  public ASTNodeAttachCall assertASTNodeAttachCall(int idx, Consumer<ASTNodeAttachCall> consumer) {
    assertInstanceOf(IncrementalTestListener.ASTNodeAttachCall.class,
        this.getNotification(idx));
    IncrementalTestListener.ASTNodeAttachCall call =
        (IncrementalTestListener.ASTNodeAttachCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }
  
  public ASTNodeDetachCall assertASTNodeDetachCall(int idx, Consumer<ASTNodeDetachCall> consumer) {
    assertInstanceOf(IncrementalTestListener.ASTNodeDetachCall.class,
        this.getNotification(idx));
    IncrementalTestListener.ASTNodeDetachCall call =
        (IncrementalTestListener.ASTNodeDetachCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }
  
  public ASTNodeModificationCall assertASTNodeModificationCall(int idx, Consumer<ASTNodeModificationCall> consumer) {
    assertInstanceOf(IncrementalTestListener.ASTNodeModificationCall.class,
        this.getNotification(idx));
    IncrementalTestListener.ASTNodeModificationCall call =
        (IncrementalTestListener.ASTNodeModificationCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }
  
  public ASTNodeListModificationCall assertASTNodeListModificationCall(int idx, Consumer<ASTNodeListModificationCall> consumer) {
    assertInstanceOf(IncrementalTestListener.ASTNodeListModificationCall.class,
        this.getNotification(idx));
    IncrementalTestListener.ASTNodeListModificationCall call =
        (IncrementalTestListener.ASTNodeListModificationCall) this.getNotification(idx);
    consumer.accept(call);
    return call;
  }
}