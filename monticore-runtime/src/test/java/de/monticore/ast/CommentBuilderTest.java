/* (c) https://github.com/MontiCore/monticore */
package de.monticore.ast;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommentBuilderTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void positiveTest() {
    assertEquals("super comment", new CommentBuilder().setText("super comment").build().getText());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void negativeTest() {
    final CommentBuilder commentBuilder = new CommentBuilder();
    assertFalse(commentBuilder.isValid());
    assertThrows(IllegalStateException.class, commentBuilder::build);
    assertEquals(1L, Log.getFindingsCount());
    assertEquals("0xA4322 text of type String must not be null", Log.getFindings().get(0).getMsg());
  }
}
