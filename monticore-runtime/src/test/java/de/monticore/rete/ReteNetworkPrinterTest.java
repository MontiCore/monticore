/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

import org.junit.jupiter.api.Test;

import static de.monticore.rete.AutomatonModel.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link ReteNetworkPrinter}.
 */
public class ReteNetworkPrinterTest {

  /**
   * A left outer join is printed with its kind, its optional columns marked
   * by {@code ?} and edges labeled with the sides of its indexers; inputs and
   * indexers are printed with their shapes and style classes.
   */
  @Test
  public void testPrintsLeftOuterJoinWithOptionalColumns() {
    ReteNetwork net = new ReteNetwork();
    // [state, transition?]: each state with its outgoing transitions, if any
    net.leftOuterJoin(net.indexer(net.typeInput(ASTState.class), 0),
        net.indexer(net.featureInput(SOURCE), 1));
    assertEquals("""
        flowchart TD
          n0(["TypeInput<br/>ASTState<br/>[ASTState]"])
          n1(["FeatureInput<br/>%s<br/>[ASTTransition, ASTState]"])
          n2[("Indexer<br/>key [0]: [ASTState]")]
          n3[("Indexer<br/>key [1]: [ASTState]")]
          n4["LeftOuterJoin (optional)<br/>[ASTState, ASTTransition?]"]
          n0 --> n2
          n1 --> n3
          n2 -->|left| n4
          n3 -->|right| n4
          classDef input fill:#dbeafe,stroke:#1d4ed8
          classDef memory fill:#fef3c7,stroke:#b45309
          classDef production fill:#dcfce7,stroke:#15803d
          class n0,n1 input
          class n2,n3 memory
        """.formatted(SOURCE), ReteNetworkPrinter.print(net));
  }
}
