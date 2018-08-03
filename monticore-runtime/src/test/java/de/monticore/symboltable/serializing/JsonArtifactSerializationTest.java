/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.mocks.languages.automaton.AutSymbol;
import de.monticore.symboltable.mocks.languages.automaton.AutomatonScope;
import de.monticore.symboltable.mocks.languages.automaton.AutomatonSerializer;
import de.monticore.symboltable.mocks.languages.automaton.StateSymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartLanguage;
import de.se_rwth.commons.Directories;

public class JsonArtifactSerializationTest {
  
  @Before
  public void setup() {
    Path p = Paths.get("target/serialization");
    
    Directories.delete(p.toFile());
    assertFalse(Files.exists(p));
    
    try {
      Files.createDirectory(p);
    }
    catch (IOException e) {
      fail("Could not create directory " + p);
    }
    
  }
  
  protected ArtifactScope initPingPong() {
    ModelPath mp = new ModelPath(Paths.get("src/test/resources/modelloader/modelpath2"));
    GlobalScope gs = new GlobalScope(mp, new StateChartLanguage());
    ArtifactScope as = new ArtifactScope(Optional.of(gs), "pack", new ArrayList<>());
    as.add(new AutSymbol("p"));
    AutomatonScope scScope = new AutomatonScope();
    scScope.add(new StateSymbol("ping"));
    scScope.add(new StateSymbol("pong"));
    as.addSubScope(scScope);
    return as;
  }
  
  @Test
  public void testSimpleSerialization() {
    ArtifactScope as = initPingPong();
    Optional<String> serialized = new AutomatonSerializer().serialize(as);
    assertTrue(serialized.isPresent());
    System.out.println(serialized.get());
    assertTrue(serialized.get().contains("ping"));
    assertTrue(serialized.get().contains("pong"));

    AutomatonSerializer deserializer = new AutomatonSerializer();
    Optional<ArtifactScope> deserialized = deserializer.deserialize(serialized.get());
    assertTrue(deserialized.isPresent());
    assertEquals(1,deserialized.get().getSubScopes().size());
    assertEquals(1,deserialized.get().getSymbolsSize());
    MutableScope deserializedScope = deserialized.get().getSubScopes().get(0);
    assertEquals(0,deserializedScope.getSubScopes().size());
    assertEquals(2,deserializedScope.getSymbolsSize());
  }
  
  @Test
  public void testSimpleStoring() {
    ArtifactScope as = initPingPong();
    new AutomatonSerializer().store(as);
    assertTrue(new File(IArtifactScopeSerializer.SYMBOL_STORE_LOCATION).exists());
  }
  
  @Ignore
  @Test
  public void testSimpleLoading() {
    AutomatonSerializer deserializer = new AutomatonSerializer();
    ArtifactScope as = deserializer.load("Symbols.json");
    assertNotNull(as);
  }
  
}
