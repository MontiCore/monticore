package de.monticore.rete;

public interface ReteTransformationWrapper extends RetePatternWrapper {

  boolean applyTransformation(Match match);

  boolean applyTransformation(MatchWrapper match);
}