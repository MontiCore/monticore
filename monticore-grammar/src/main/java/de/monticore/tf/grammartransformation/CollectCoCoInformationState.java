/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.grammartransformation;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;


public class CollectCoCoInformationState {

    private int parentNest = 0;
    private boolean rhs = false;
    private Set<String> varsOnLHS = new HashSet();
    private Set<String> varsOnRHS = new HashSet();
    private int repElements = 0;
    private int negElements = 0;

    public Set<String> getRHSOnlyVars() {
        Set<String> result = Sets.newHashSet(varsOnRHS);
        result.removeAll(varsOnLHS);
        return result;
    }

    public int getParentNest() {
        return parentNest;
    }

    public void incrementParentNest() {
        this.parentNest += 1;
    }
    public void decrementParentNest() {
        this.parentNest -= 1;
    }

    public Set<String> getVarsOnLHS() {
        return varsOnLHS;
    }

    public void addVarOnLHS(String var) {
        varsOnLHS.add(var);
    }

    public Set<String> getVarsOnRHS() {
        return varsOnRHS;
    }

    public void addVarOnRHS(String var) {
        varsOnRHS.add(var);
    }

    public boolean getRHS() {
        return rhs;
    }

    public void setRHS(boolean rhs) {
        this.rhs = rhs;
    }

    public void incrementRepElements() {
        repElements += 1;
    }

    public int getRepElements() {
        return repElements;
    }

    public void incrementNegElements() {
        negElements += 1;
    }

    public int getNegElements() {
        return negElements;
    }
}
