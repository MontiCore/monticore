/* (c) https://github.com/MontiCore/monticore */
statechart PedestrianLight {

  state PedestrianLightOff <<initial>><<final>> {
    entry : { switch(NONE); }
  }

  state PedestrianLightOn {
    
    state Red <<initial>> {
      [ signal == STOP ]
      entry : { switch(STOP); }
      do : { tick(); }
    }
      
    state Green {
      [ signal == GO ]
      entry : { switch(GO); }
      do : { beep(); }
    }
      
    Red -> Green : buttonPressed()
      / { startTimer(); }
    ;
    
    Green -> Red : timerEvent() ; 
  }

  PedestrianLightOff -> PedestrianLightOn : switchedOn();      
  PedestrianLightOn -> PedestrianLightOff : switchedOff();
      
}
