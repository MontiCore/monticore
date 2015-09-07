package mc.featureemf.serialization;

import static org.junit.Assert.*;
import java.io.File;
import mc.featureemf.fautomaton.automatonwithaction._ast.ActionAutomatonPackage;
import mc.featureemf.fautomaton.automatonwithaction._ast.util.ActionAutomatonResourceController;
import mc.featureemf.fautomaton.automaton._ast.FlatAutomatonPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.Before;
import org.junit.Test;

public class ECoreSerializationTest {
  
  String testFolder = "testOutput/";
  
  @Before
  public void setUp() throws Exception {
    ActionAutomatonResourceController.getInstance().createECoreFile(testFolder);
  }
  
  @Test
  public void testECoreFileOFSuperGrammar() {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
            Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    
    URI fileURI = URI.createFileURI(new File(testFolder + "FlatAutomaton" + ".ecore").getAbsolutePath());
    Resource resource = resourceSet.getResource(fileURI, true);
    EPackage serializedEPackage = (EPackage) resource.getContents().get(0);
    
    EClass serializedState = (EClass) serializedEPackage.getEClassifier("State");
    
    int expectedFeatureCountAutomaton = FlatAutomatonPackage.eINSTANCE.getAutomaton().getFeatureCount();
    String expectedNameOfInitial = "initial";
    
    assertEquals("FlatAutomaton", serializedEPackage.getName());
    assertEquals(expectedFeatureCountAutomaton,
            ((EClass) serializedEPackage.getEClassifier("Automaton")).getFeatureCount());
    assertEquals(expectedNameOfInitial,
            serializedState.getEAllStructuralFeatures().get(FlatAutomatonPackage.STATE__INITIAL).getName());
    
  }
  
  @Test
  public void testECoreFileOFGrammar() {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
            Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    
    URI fileURI = URI.createFileURI(new File(testFolder + "ActionAutomaton" + ".ecore").getAbsolutePath());
    Resource resource = resourceSet.getResource(fileURI, true);
    EPackage serializedEPackage = (EPackage) resource.getContents().get(0);
    
    EClass serializedTransitionWithAction = (EClass) serializedEPackage.getEClassifier("TransitionWithAction");
    
    int expectedFeatureCountAutomaton = ActionAutomatonPackage.eINSTANCE.getAutomaton().getFeatureCount();
    String expectedNameOfAction = "action";
    String expectedFirstSuperType = "Transition";
    
    assertEquals("ActionAutomaton", serializedEPackage.getName());
    assertEquals(expectedFeatureCountAutomaton, ((EClass) serializedEPackage.getEClassifier("Automaton")).getFeatureCount());
    assertEquals(expectedFirstSuperType,
            serializedTransitionWithAction.getESuperTypes().get(0).getName());
    assertEquals(expectedNameOfAction,
            serializedTransitionWithAction.getEAllStructuralFeatures().get(ActionAutomatonPackage.TRANSITIONWITHACTION__ACTION).getName());
  }
  
  @Test
  public void testECoreFileOFASTENode() {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
            Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    
    URI fileURI = URI.createFileURI(new File(testFolder + "ASTENode" + ".ecore").getAbsolutePath());
    Resource resource = resourceSet.getResource(fileURI, true);
    EPackage serializedEPackage = (EPackage) resource.getContents().get(0);
    
    EClass serializedENode = (EClass) serializedEPackage.getEClassifier("ENode");
    
    assertEquals("ASTENode", serializedEPackage.getName());
    assertTrue(serializedENode.isInterface());
    
  }
}
