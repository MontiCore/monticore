package de.monticore.ast;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CommentBuilderTest {
  @BeforeClass
  public static void before() {
    LogStub.init();;
  }

  @Test
  public void positiveTest() {
    assertEquals("super comment", new CommentBuilder().setText("super comment").build().getText());
  }

  @Test(expected=IllegalStateException.class)
  public void negativeTest() {
    final CommentBuilder commentBuilder = new CommentBuilder();
    assertFalse(commentBuilder.isValid());
    commentBuilder.build();
  }
}
