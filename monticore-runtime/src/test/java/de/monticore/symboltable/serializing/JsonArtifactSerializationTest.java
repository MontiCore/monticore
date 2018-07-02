/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.mocks.languages.statechart.StateChartLanguage;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateSymbol;
import de.se_rwth.commons.Directories;

/**
 * @author Pedram Mir Seyed Nazari
 */
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
    ArtifactScope as = new ArtifactScope(Optional.of(gs), "p", new ArrayList<>());
    as.add(new StateChartSymbol("p"));
    CommonScope scScope = new CommonScope();
    scScope.add(new StateSymbol("ping"));
    scScope.add(new StateSymbol("pong"));
    as.addSubScope(scScope);
    return as;
  }
  
  @Test
  public void testSimpleSerialization() {
    ArtifactScope as = initPingPong();
    Optional<String> asString = new JsonArtifactScopeSerializer().serialize(as);
    assertTrue(asString.isPresent());
    System.out.println(asString.get());
    assertTrue(asString.get().contains("ping"));
    assertTrue(asString.get().contains("pong"));
  }
  
  @Test
  public void testSimpleDeserialization() {
    JsonArtifactScopeSerializer deserializer = new JsonArtifactScopeSerializer();
    deserializer.registerDeserializer(Symbol.class, new ISymbolDeserializer<Symbol>() {
      @Override
      public Symbol deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
          throws JsonParseException {
        return new StateSymbol(json.toString());
      }});
    deserializer.registerDeserializer(MutableScope.class, new IScopeDeserializer<MutableScope>() {
      @Override
      public MutableScope deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
          throws JsonParseException {
        return new CommonScope();
      }});
    String input = "{\"symbols\":{\"p\":[{}]},\"subScopes\":[{\"symbols\":{},\"subScopes\":[]},{\"symbols\":{\"ping\":[{}],\"pong\":[{}]},\"subScopes\":[]}]}";
    Optional<ArtifactScope> as =deserializer.deserialize(input);
    assertTrue(as.isPresent());
    assertTrue(as.get().getSubScopes().size() == 2);
  }
  
}
