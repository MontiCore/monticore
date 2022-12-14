/* (c) https://github.com/MontiCore/monticore */
package social;

import de.monticore.tf.DeleteAllEntriesFromUser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.social.socialnetwork._ast.ASTNetwork;
import mc.testcases.social.socialnetwork._parser.SocialNetworkParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class DeleteAllEntriesFromUserTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testNetwork1() throws IOException {
    SocialNetworkParser parser = new SocialNetworkParser();
    Optional<ASTNetwork> astNetwork = parser.parse("src/test/resources/social/Network1.net");
    assertTrue(astNetwork.isPresent());
    assertEquals(3, astNetwork.get().sizeUsers());
    assertEquals(0, astNetwork.get().getUser(0).sizeTextEntrys());
    assertEquals(3, astNetwork.get().getUser(0).sizePhotoEntrys());

    assertEquals(0, astNetwork.get().getUser(1).sizeTextEntrys());
    assertEquals(0, astNetwork.get().getUser(1).sizePhotoEntrys());

    assertEquals(0, astNetwork.get().getUser(2).sizeTextEntrys());
    assertEquals(3, astNetwork.get().getUser(2).sizePhotoEntrys());

    DeleteAllEntriesFromUser matchingClass = new DeleteAllEntriesFromUser(astNetwork.get());
    assertTrue(matchingClass.doPatternMatching());
    matchingClass.doReplacement();
    assertEquals(0, astNetwork.get().getUser(0).sizeTextEntrys());
    assertEquals(0, astNetwork.get().getUser(0).sizePhotoEntrys());

    assertEquals(0,Log.getErrorCount());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSimpleNetwork() throws IOException {
    SocialNetworkParser parser = new SocialNetworkParser();
    Optional<ASTNetwork> astNetwork = parser.parse("src/test/resources/social/SimpleNetwork.net");
    assertTrue(astNetwork.isPresent());
    assertEquals(1, astNetwork.get().sizeUsers());
    assertEquals(1, astNetwork.get().getUser(0).sizeTextEntrys());
    assertEquals(0, astNetwork.get().getUser(0).sizePhotoEntrys());
    DeleteAllEntriesFromUser matchingClass = new DeleteAllEntriesFromUser(astNetwork.get());
    assertTrue(matchingClass.doPatternMatching());
    assertEquals(1, matchingClass.getMatches().size());
    matchingClass.doReplacement();
    assertEquals(1, matchingClass.getMatches().size());
    assertEquals(0, astNetwork.get().getUser(0).sizeTextEntrys());
    assertEquals(0, astNetwork.get().getUser(0).sizePhotoEntrys());

    assertEquals(0,Log.getErrorCount());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSimpleNetwork2() throws IOException {
    SocialNetworkParser parser = new SocialNetworkParser();
    Optional<ASTNetwork> astNetwork = parser.parse("src/test/resources/social/SimpleNetwork2.net");
    assertTrue(astNetwork.isPresent());
    assertEquals(2, astNetwork.get().sizeUsers());
    assertEquals(1, astNetwork.get().getUser(0).sizeTextEntrys());
    assertEquals(1, astNetwork.get().getUser(0).sizePhotoEntrys());
    assertEquals(1, astNetwork.get().getUser(1).sizeTextEntrys());
    assertEquals(1, astNetwork.get().getUser(1).sizePhotoEntrys());
    DeleteAllEntriesFromUser matchingClass = new DeleteAllEntriesFromUser(astNetwork.get());
    assertTrue(matchingClass.doPatternMatching());
    matchingClass.doReplacement();
    assertEquals(0, astNetwork.get().getUser(0).sizeTextEntrys());
    assertEquals(0, astNetwork.get().getUser(0).sizePhotoEntrys());

    assertEquals(0,Log.getErrorCount());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEmptyUserNetwork() throws IOException {
    SocialNetworkParser parser = new SocialNetworkParser();
    Optional<ASTNetwork> astNetwork = parser.parse("src/test/resources/social/EmptyUserNetwork.net");
    assertTrue(astNetwork.isPresent());
    assertEquals(2, astNetwork.get().sizeUsers());
    assertEquals(0, astNetwork.get().getUser(0).sizeTextEntrys());
    assertEquals(0, astNetwork.get().getUser(0).sizePhotoEntrys());
    assertEquals(0, astNetwork.get().getUser(1).sizeTextEntrys());
    assertEquals(0, astNetwork.get().getUser(1).sizePhotoEntrys());
    DeleteAllEntriesFromUser matchingClass = new DeleteAllEntriesFromUser(astNetwork.get());
    // is still true but nothing is done
    assertTrue(matchingClass.doPatternMatching());
    matchingClass.doReplacement();
    assertEquals(0, astNetwork.get().getUser(0).sizeTextEntrys());
    assertEquals(0, astNetwork.get().getUser(0).sizePhotoEntrys());
    assertEquals(0, astNetwork.get().getUser(1).sizeTextEntrys());
    assertEquals(0, astNetwork.get().getUser(1).sizePhotoEntrys());

    assertEquals(0,Log.getErrorCount());
    assertTrue(Log.getFindings().isEmpty());
  }
}
