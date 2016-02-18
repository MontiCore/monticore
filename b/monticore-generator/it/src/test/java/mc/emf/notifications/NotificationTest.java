package mc.emf.notifications;

public class NotificationTest {
  
//  ASTAutomaton aut;
//  ASTState state1;
//  ASTState state2;
//  ASTTransition transition;
//  // notifications for corresponding object
//  ArrayList<Notification> autNotList = new ArrayList<Notification>();
//  ArrayList<Notification> state1NotList = new ArrayList<Notification>();
//  ArrayList<Notification> state2NotList = new ArrayList<Notification>();
//  ArrayList<Notification> transitionNotList = new ArrayList<Notification>();
//  
//  @Before
//  public void setUp() throws Exception {
//    aut = FlatAutomatonFactory.eINSTANCE.createAutomaton();
//    aut.setName("aut1");
//    state1 = FlatAutomatonFactory.eINSTANCE.createState();
//    state2 = FlatAutomatonFactory.eINSTANCE.createState();
//    
//    transition = FlatAutomatonFactory.eINSTANCE.createTransition();
//    
//    aut.getState().add(state1);
//    
//    state1.setName("state1");
//    state2.setName("state2");
//    
//    // add Adapters to EMF compatible AST-Objects
//    Adapter adapterAut = new AdapterImpl() {
//      public void notifyChanged(Notification notification) {
//        autNotList.add(notification);
//      }
//    };
//    
//    aut.eAdapters().add(adapterAut);
//    
//    Adapter adapterState1 = new AdapterImpl() {
//      public void notifyChanged(Notification notification) {
//        state1NotList.add(notification);
//      }
//    };
//    
//    state1.eAdapters().add(adapterState1);
//    
//    Adapter adapterState2 = new AdapterImpl() {
//      public void notifyChanged(Notification notification) {
//        state2NotList.add(notification);
//      }
//    };
//    
//    state2.eAdapters().add(adapterState2);
//    
//    Adapter adapterTransition = new AdapterImpl() {
//      public void notifyChanged(Notification notification) {
//        transitionNotList.add(notification);
//      }
//    };
//    
//    transition.eAdapters().add(adapterTransition);
//  }
//  
//  @Test
//  public void testAttribute() {
//    aut.setName("aut2");
//    aut.getNames().add("name1");
//    
//    Notification not = autNotList.get(0);
//    Notification not2 = autNotList.get(1);
//    
//    String expectedOldValue = "aut1";
//    String expectedNewValue = "aut2";
//    String expectedNewValueList = "name1";
//    
//    assertFalse(autNotList.isEmpty());
//    assertEquals(aut, not.getNotifier());
//    assertEquals(Notification.SET, not.getEventType());
//    assertEquals(FlatAutomatonPackage.Literals.AUTOMATON__NAME, not
//        .getFeature());
//    assertEquals(expectedOldValue, not.getOldValue());
//    assertEquals(expectedNewValue, not.getNewValue());
//    
//    assertEquals(aut, not2.getNotifier());
//    assertEquals(Notification.ADD, not2.getEventType());
//    assertEquals(FlatAutomatonPackage.Literals.AUTOMATON__NAMES, not2
//        .getFeature());
//    assertEquals(expectedNewValueList, not2.getNewValue());
//    
//  }
//  
//  @Test
//  public void testComposition() {
//    aut.getTransition().add(transition);
//    aut.setImpState(state2);
//    
//    Notification not = autNotList.get(0);
//    Notification not2 = autNotList.get(1);
//    
//    ASTTransition expectedNewTransition = transition;
//    ASTState expectedOldImpState = null;
//    ASTState expectedNewImpState = state2;
//    
//    assertFalse(autNotList.isEmpty());
//    assertEquals(aut, not.getNotifier());
//    assertEquals(Notification.ADD, not.getEventType());
//    assertEquals(FlatAutomatonPackage.Literals.AUTOMATON__TRANSITION, not
//        .getFeature());
//    assertEquals(expectedNewTransition, not.getNewValue());
//    
//    assertEquals(aut, not2.getNotifier());
//    assertEquals(Notification.SET, not2.getEventType());
//    assertEquals(FlatAutomatonPackage.Literals.AUTOMATON__IMPSTATE, not2
//        .getFeature());
//    assertEquals(expectedOldImpState, not2.getOldValue());
//    assertEquals(expectedNewImpState, not2.getNewValue());
//  }
//  
//  @Test
//  public void testAssoc() {
//    transition.setFromState(state1);
//    state2.getIngoingTransitions().add(transition);
//    transition.setFromState(state2);
//    
//    Notification notState11 = state1NotList.get(0);
//    Notification notState12 = state1NotList.get(1);
//    Notification notState21 = state2NotList.get(0);
//    Notification notTransition1 = transitionNotList.get(0);
//    Notification notTransition2 = transitionNotList.get(1);
//    
//    ASTState expectedNewFromState = state1;
//    ASTTransition expectedNewOutgoingTransition = transition;
//    ASTTransition expectedNewIngoingTransition = transition;
//    ASTState expectedNewToState = state2;
//    
//    assertFalse(state1NotList.isEmpty());
//    assertFalse(state2NotList.isEmpty());
//    assertFalse(transitionNotList.isEmpty());
//    
//    assertEquals(transition, notTransition1.getNotifier());
//    assertEquals(Notification.SET, notTransition1.getEventType());
//    assertEquals(FlatAutomatonPackage.Literals.TRANSITION__FROMSTATE,
//        notTransition1.getFeature());
//    assertEquals(expectedNewFromState, notTransition1.getNewValue());
//    
//    assertEquals(state1, notState11.getNotifier());
//    assertEquals(Notification.ADD, notState11.getEventType());
//    assertEquals(FlatAutomatonPackage.Literals.STATE__OUTGOINGTRANSITIONS,
//        notState11.getFeature());
//    assertEquals(expectedNewOutgoingTransition, notState11.getNewValue());
//    
//    assertEquals(state2, notState21.getNotifier());
//    assertEquals(Notification.ADD, notState21.getEventType());
//    assertEquals(FlatAutomatonPackage.Literals.STATE__INGOINGTRANSITIONS,
//        notState21.getFeature());
//    assertEquals(expectedNewIngoingTransition, notState21.getNewValue());
//    
//    assertEquals(transition, notTransition2.getNotifier());
//    assertEquals(Notification.SET, notTransition2.getEventType());
//    assertEquals(FlatAutomatonPackage.Literals.TRANSITION__TOSTATE,
//        notTransition2.getFeature());
//    assertEquals(expectedNewToState, notTransition2.getNewValue());
//    
//    assertEquals(state1, notState12.getNotifier());
//    assertEquals(Notification.REMOVE, notState12.getEventType());
//    assertEquals(FlatAutomatonPackage.Literals.STATE__OUTGOINGTRANSITIONS,
//        notState12.getFeature());
//    
//  }
//  
}
