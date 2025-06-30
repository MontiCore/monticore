/* (c) https://github.com/MontiCore/monticore */

import automata7.Automata7Mill;
import automata7._symboltable.IAutomata7ArtifactScope;
import automata7._symboltable.StimulusSymbol;
import de.monticore.io.paths.MCPath;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import javaandaut.Class2StimulusAdapter;
import javaandaut.JavaAndAutTool;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(Automata7Mill.class)
public class BasicJavaToolTest {

  @Test
  public void testPingPong(){
    MCPath symbolPath = new MCPath(Paths.get("src/test/resources/example"));
    IAutomata7ArtifactScope symTab = JavaAndAutTool
        .createJavaAndAutSymTab("src/test/resources/example/PingPong.aut", symbolPath);
    symTab.setName("PingPong");
    Optional<StimulusSymbol> hit = symTab.resolveStimulus("Hit");
    assertTrue(hit.isPresent());
    assertEquals("Hit", hit.get().getName());
    assertInstanceOf(Class2StimulusAdapter.class, hit.get()); //assure that an adapter was found
    MCAssertions.assertNoFindings();
  }


}
