package mc.emf.serialization;

public class ECoreSerializationTest {
  
//  String testFolder = "testOutput/";
//  
//  @Before
//  public void setUp() throws Exception {
//    ActionAutomatonResourceController.getInstance().createECoreFile(testFolder);
//  }
//  
//  @Test
//  public void testECoreFileOFSuperGrammar() {
//    ResourceSet resourceSet = new ResourceSetImpl();
//    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
//            Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
//    
//    URI fileURI = URI.createFileURI(new File(testFolder + "FlatAutomaton" + ".ecore").getAbsolutePath());
//    Resource resource = resourceSet.getResource(fileURI, true);
//    EPackage serializedEPackage = (EPackage) resource.getContents().get(0);
//    
//    EClass serializedState = (EClass) serializedEPackage.getEClassifier("State");
//    
//    int expectedFeatureCountAutomaton = FlatAutomatonPackage.eINSTANCE.getAutomaton().getFeatureCount();
//    String expectedNameOfInitial = "initial";
//    
//    assertEquals("FlatAutomaton", serializedEPackage.getName());
//    assertEquals(expectedFeatureCountAutomaton,
//            ((EClass) serializedEPackage.getEClassifier("Automaton")).getFeatureCount());
//    assertEquals(expectedNameOfInitial,
//            serializedState.getEAllStructuralFeatures().get(FlatAutomatonPackage.STATE__INITIAL).getName());
//    
//  }
//  
//  @Test
//  public void testECoreFileOFGrammar() {
//    ResourceSet resourceSet = new ResourceSetImpl();
//    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
//            Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
//    
//    URI fileURI = URI.createFileURI(new File(testFolder + "ActionAutomaton" + ".ecore").getAbsolutePath());
//    Resource resource = resourceSet.getResource(fileURI, true);
//    EPackage serializedEPackage = (EPackage) resource.getContents().get(0);
//    
//    EClass serializedTransitionWithAction = (EClass) serializedEPackage.getEClassifier("TransitionWithAction");
//    
//    int expectedFeatureCountAutomaton = ActionAutomatonPackage.eINSTANCE.getAutomaton().getFeatureCount();
//    String expectedNameOfAction = "action";
//    String expectedFirstSuperType = "Transition";
//    
//    assertEquals("ActionAutomaton", serializedEPackage.getName());
//    assertEquals(expectedFeatureCountAutomaton, ((EClass) serializedEPackage.getEClassifier("Automaton")).getFeatureCount());
//    assertEquals(expectedFirstSuperType,
//            serializedTransitionWithAction.getESuperTypes().get(0).getName());
//    assertEquals(expectedNameOfAction,
//            serializedTransitionWithAction.getEAllStructuralFeatures().get(ActionAutomatonPackage.TRANSITIONWITHACTION__ACTION).getName());
//  }
//  
//  @Test
//  public void testECoreFileOFASTENode() {
//    ResourceSet resourceSet = new ResourceSetImpl();
//    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
//            Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
//    
//    URI fileURI = URI.createFileURI(new File(testFolder + "ASTENode" + ".ecore").getAbsolutePath());
//    Resource resource = resourceSet.getResource(fileURI, true);
//    EPackage serializedEPackage = (EPackage) resource.getContents().get(0);
//    
//    EClass serializedENode = (EClass) serializedEPackage.getEClassifier("ENode");
//    
//    assertEquals("ASTENode", serializedEPackage.getName());
//    assertTrue(serializedENode.isInterface());
//    
//  }
}
