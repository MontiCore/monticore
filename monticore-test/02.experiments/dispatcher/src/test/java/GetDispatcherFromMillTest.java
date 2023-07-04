/* (c) https://github.com/MontiCore/monticore */

import org.junit.Test;
import simpleinterfaces.SimpleInterfacesMill;
import simpleinterfaces._util.SimpleInterfacesTypeDispatcher;

import static org.junit.Assert.assertSame;

public class GetDispatcherFromMillTest {

  @Test
  public void testGetterInMill() {
    SimpleInterfacesTypeDispatcher dispatcher1 = SimpleInterfacesMill.typeDispatcher();
    SimpleInterfacesTypeDispatcher dispatcher2 = SimpleInterfacesMill.typeDispatcher();
    assertSame(dispatcher1, dispatcher2);
  }

}
