package mc.featureemf.serialization;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import mc.featureemf.fautomaton.automaton._ast.*;
import mc.featureemf.fautomaton.automaton._ast.util.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.Before;
import org.junit.Test;

public class ASTObjectSerializationTest {
  
  String testFolder = "testOutput/";
  ASTAutomaton aut = FlatAutomatonFactory.eINSTANCE.createAutomaton();
  ASTTransition trans = FlatAutomatonFactory.eINSTANCE.createTransition();
  ASTState state1 = FlatAutomatonFactory.eINSTANCE.createState();
  ASTState state2 = FlatAutomatonFactory.eINSTANCE.createState();
  
  @Before
  public void setUp() throws Exception {
    
    aut.setName("aut1");
    aut.getTransition().add(trans);
    aut.getState().add(state1);
    aut.getState().add(state2);
    
    trans.setFromState(state1);
    trans.setToState(state2);
    trans.setActivate("trans1");
    
    state1.setInitial(true);
    state2.setFinal(true);
  }
  
  @Test
  public void testSerialization() {
    // serialize aut with generated method
    FlatAutomatonResourceController.getInstance().serializeASTClassInstance(aut, testFolder + "aut");
    
    // Deserialize serialized resource
    FlatAutomatonPackage.eINSTANCE.eClass();
    
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
            Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    URI fileURI = URI.createFileURI(new File(testFolder + "aut.xmi").getAbsolutePath());
    Resource resource = resourceSet.getResource(fileURI, true);
    
    ASTAutomaton serializedAut = (ASTAutomaton) resource.getContents().get(0);
    
    assertTrue(EcoreUtil.equals(aut, serializedAut));
    assertTrue(aut.deepEquals(serializedAut));
    assertEquals(trans.getActivate(),
        serializedAut.getState().get(0).getOutgoingTransitions().get(0).getActivate());
  }
  
  @Test
  public void testDeserialization() {
    
    // serialize aut
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
            Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    URI fileURI = URI.createFileURI(new File(testFolder + "autDe.xmi").getAbsolutePath());
    Resource resource = resourceSet.createResource(fileURI);
    resource.getContents().add(aut);
    try {
      resource.save(Collections.EMPTY_MAP);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    // deserialize autDe.xmi with generated method
    ASTAutomaton deserializedAut = FlatAutomatonResourceController.getInstance().deserializeASTAutomaton(testFolder + "autDe");
    
    assertTrue(EcoreUtil.equals(aut, deserializedAut));
    assertTrue(aut.deepEquals(deserializedAut));
    assertEquals(state2.isFinal(), ((ASTTransition) aut.getTransition().get(0)).getToState().isFinal());
    
  }
}
