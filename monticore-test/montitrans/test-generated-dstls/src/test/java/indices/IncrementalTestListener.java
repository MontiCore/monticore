package indices;

import de.monticore.ast.ASTNode;
import de.monticore.tf.runtime.inc.IIncrementalListener;
import de.monticore.tf.runtime.inc.ModificationOp;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedList;

public class IncrementalTestListener implements IIncrementalListener {
  
  private final LinkedList<NotificationRecord> calls = new LinkedList<>();
  
  /**
   * Common supertype for all recorded listener calls.
   */
  public sealed interface NotificationRecord
      permits TransformationStartCall, TransformationEndCall, ASTNodeAttachCall, ASTNodeDetachCall,
      ASTNodeModificationCall, ASTNodeListModificationCall {}
  
  public record TransformationStartCall(@NonNull String transformationName)
      implements NotificationRecord {}
  
  public record TransformationEndCall(@NonNull String transformationName)
      implements NotificationRecord {}
  
  public record ASTNodeAttachCall(@NonNull ASTNode node, @Nullable ASTNode parent)
      implements NotificationRecord {}
  
  public record ASTNodeDetachCall(@NonNull ASTNode node, @NonNull ASTNode parent)
      implements NotificationRecord {}
  
  public record ASTNodeModificationCall(@NonNull ASTNode node, @NonNull String attributeName,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue)
      implements NotificationRecord {}
  
  public record ASTNodeListModificationCall(@NonNull ASTNode node, String attributeName, int idx,
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
  public void onTransformationStart(@NonNull String transformationName) {
    calls.add(new TransformationStartCall(transformationName));
  }
  
  @Override
  public void onTransformationEnd(@NonNull String transformationName) {
    calls.add(new TransformationEndCall(transformationName));
  }
  
  @Override
  public void onASTNodeAttach(@NonNull ASTNode node, @Nullable ASTNode parent) {
    calls.add(new ASTNodeAttachCall(node, parent));
  }
  
  @Override
  public void onASTNodeDetach(@NonNull ASTNode node, @NonNull ASTNode parent) {
    calls.add(new ASTNodeDetachCall(node, parent));
  }
  
  @Override
  public void onASTNodeModification(@NonNull ASTNode node, @NonNull String attributeName,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue) {
    calls.add(
        new ASTNodeModificationCall(node, attributeName, modificationType, oldValue, newValue));
  }
  
  @Override
  public void onASTNodeListModification(@NonNull ASTNode node, String attributeName, int idx,
      ModificationOp modificationType, @Nullable Object oldValue, @Nullable Object newValue) {
    calls.add(new ASTNodeListModificationCall(node, attributeName, idx, modificationType, oldValue,
        newValue));
  }
}