package de.monticore.javalight._symboltable;

public class JavaLightPhasedSymbolTableCreatorDelegator extends JavaLightPhasedSymbolTableCreatorDelegatorTOP {

  public JavaLightPhasedSymbolTableCreatorDelegator(IJavaLightGlobalScope globalScope){
    super(globalScope);
    this.priorityList.add(new JavaLightSTCompleteTypesDelegator());
  }

  public JavaLightPhasedSymbolTableCreatorDelegator(){
    super();
    this.priorityList.add(new JavaLightSTCompleteTypesDelegator());
  }
}
