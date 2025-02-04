/* (c) https://github.com/MontiCore/monticore */
package de.monticore.ast;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommentBuilderTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void positiveTest() {
    assertEquals("super comment", new CommentBuilder().setText("super comment").build().getText());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test(expected=IllegalStateException.class)
  public void negativeTest() {
    final CommentBuilder commentBuilder = new CommentBuilder();
    assertFalse(commentBuilder.isValid());
    commentBuilder.build();
    assertTrue(Log.getFindings().isEmpty());
  }
}
