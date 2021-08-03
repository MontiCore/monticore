/* (c) https://github.com/MontiCore/monticore */
statechart PedestrianLight {

  state PedestrianLightOn {
    [ status == ON ]
    
    state Red {
      [ signal == STOP ]
    }
      
    state Green {
      [ signal == GO ]
    }
      
  }

}
