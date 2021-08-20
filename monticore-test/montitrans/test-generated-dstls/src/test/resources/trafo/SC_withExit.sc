/* (c) https://github.com/MontiCore/monticore */
statechart $sc {
  state Green {
    exit: {
      timer.stop(this); 
    }
  }
  state Red;
  
  Green->Red : buttonPressed() / { } ;
}
