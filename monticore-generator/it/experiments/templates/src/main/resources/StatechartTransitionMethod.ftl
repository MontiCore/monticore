${tc.signature("transitionName")}
    public void ${transitionName?uncap_first}(){
        currentState.handle${transitionName?cap_first}(this);
    }