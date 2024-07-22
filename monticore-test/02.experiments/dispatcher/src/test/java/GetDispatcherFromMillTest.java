/* (c) https://github.com/MontiCore/monticore */

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import simpleinterfaces.SimpleInterfacesMill;
import simpleinterfaces._util.ISimpleInterfacesTypeDispatcher;

import static org.junit.Assert.assertSame;

public class GetDispatcherFromMillTest {

  @Test
  public void testGetterInMill() {
    ISimpleInterfacesTypeDispatcher dispatcher1 = SimpleInterfacesMill.typeDispatcher();
    ISimpleInterfacesTypeDispatcher dispatcher2 = SimpleInterfacesMill.typeDispatcher();
    Assertions.assertSame(dispatcher1, dispatcher2);
  }

}
