/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serializing;

import de.monticore.symboltable.serializing.cycle.CycleA;
import de.monticore.symboltable.serializing.cycle.CycleB;
import de.monticore.symboltable.serializing.cycle.CycleC;
import de.monticore.symboltable.serializing.inheritance.A;
import de.monticore.symboltable.serializing.samereferences.RefA;
import de.monticore.symboltable.serializing.samereferences.RefB;
import de.monticore.symboltable.serializing.samereferences.RefC;
import de.se_rwth.commons.Directories;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * @author  Pedram Mir Seyed Nazari
 *
 */
public class SerializationTest {

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
  
  @Test
  public void testSerializeInheritance() {
    A a = new A("Hans", false);
    a.setCity("Aachen");
    a.setAge(30);
    
    final String FILE_NAME = "target/serialization/" + a.getName() + ".st";

    try {
      Serialization.serialize(a, FILE_NAME);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    A desA = null;
    try {
      desA = (A) Serialization.deserialize(FILE_NAME);
    }
    catch (ClassNotFoundException | IOException e) {
      fail(e.getMessage());
    }
    
    assertNotSame(a, desA);
    assertEquals("Hans", desA.getName());
    // default values are ignored
    assertNull(desA.getCity());
    assertEquals(30, desA.getAge());
  }
  
  @Test
  public void testSerializeCycle() {
    CycleA a = new CycleA();
    CycleB b = new CycleB();
    CycleC c = new CycleC();
    
    a.setB(b);
    b.setC(c);
    c.setA(a);
    
    
    final String FILE_NAME = "target/serialization/Cycle.st";
    
    try {
      Serialization.serialize(a, FILE_NAME);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    CycleA desA = null;
    try {
      desA = (CycleA) Serialization.deserialize(FILE_NAME);
    }
    catch (ClassNotFoundException | IOException e) {
      fail(e.getMessage());
    }
    
    assertSame(desA, desA.getB().getC().getA());
  }
  
  @Test
  public void testSameReferences() {
    RefA a = new RefA();
    RefB b = new RefB();
    RefC c = new RefC();
    
    a.setB(b);
    a.setC(c);
    b.setA(a);
    b.setC(c);
    
    final String FILE_NAME = "target/serialization/SameReferences.st";
    
    
    try {
      Serialization.serialize(a, FILE_NAME);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    RefA desA = null;
    try {
      desA = (RefA) Serialization.deserialize(FILE_NAME);
    }
    catch (ClassNotFoundException | IOException e) {
      fail(e.getMessage());
    }
    
    assertSame(desA, desA.getB().getA());
    assertSame(desA.getC(), desA.getB().getC());
  }
  
}
