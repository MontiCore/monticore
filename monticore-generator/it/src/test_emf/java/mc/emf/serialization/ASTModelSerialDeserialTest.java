/* (c) https://github.com/MontiCore/monticore */

package mc.emf.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.monticore.emf.util.AST2ModelFiles;
import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonNodeFactory;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;
import mc.feature.fautomaton.automatonwithaction.actionautomaton._ast.ActionAutomatonPackage;

public class ASTModelSerialDeserialTest extends GeneratorIntegrationsTest {
  
  public void testECoreFileOFSuperGrammar() {
    
    ASTAutomaton aut = FlatAutomatonNodeFactory.createASTAutomaton();
    try {
      AST2ModelFiles.get().serializeASTInstance(aut, "Aut1");
    
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
            Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    
    URI fileURI = URI.createFileURI(new File("FlatAutomaton.ecore").getAbsolutePath());
    Resource resource = resourceSet.getResource(fileURI, true);
    EPackage serializedEPackage = (EPackage) resource.getContents().get(0);
    
    EClass serializedState = (EClass) serializedEPackage.getEClassifier("State");
    
    int expectedFeatureCountAutomaton = FlatAutomatonPackage.eINSTANCE.getAutomaton().getFeatureCount();
    String expectedNameOfInitial = "initial";
    
    assertEquals("FlatAutomaton", serializedEPackage.getName());
    assertEquals(expectedFeatureCountAutomaton,
            ((EClass) serializedEPackage.getEClassifier("Automaton")).getFeatureCount());
    assertEquals(expectedNameOfInitial,
            serializedState.getEAllStructuralFeatures().get(FlatAutomatonPackage.ASTState_Initial).getName());
    }
    catch (IOException e) {
      fail("Should not reach this, but: " + e);
    }
  }
  
  public void testECoreFileOFGrammar() {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
            Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    
    URI fileURI = URI.createFileURI(new File("ActionAutomaton.ecore").getAbsolutePath());
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
            serializedTransitionWithAction.getEAllStructuralFeatures().get(ActionAutomatonPackage.ASTCounter_Names).getName());
  }
  
  public void testECoreFileOFASTENode() {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
            Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
    
    URI fileURI = URI.createFileURI(new File("ASTENode.ecore").getAbsolutePath());
    Resource resource = resourceSet.getResource(fileURI, true);
    EPackage serializedEPackage = (EPackage) resource.getContents().get(0);
    
    EClass serializedENode = (EClass) serializedEPackage.getEClassifier("ENode");
    
    assertEquals("ASTENode", serializedEPackage.getName());
    assertTrue(serializedENode.isInterface());
    
  }
}
