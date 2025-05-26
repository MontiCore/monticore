/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.CompKindExpression;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ComponentTypeSymbol extends ComponentTypeSymbolTOP {

  protected ComponentTypeSymbol outerComponent;

  public ComponentTypeSymbol(String name) {
    super(name);
  }

  public Optional<VariableSymbol> getParameter(@NonNull String name) {
    Preconditions.checkNotNull(name);
    for (VariableSymbol parameter : this.getParameterList()) {
      if (parameter.getName().equals(name)) return Optional.of(parameter);
    }
    return Optional.empty();
  }

  @Override
  public boolean addParameter(@NonNull VariableSymbol parameter) {
    Preconditions.checkNotNull(parameter);
    Preconditions.checkArgument(this.getSpannedScope().getLocalVariableSymbols().contains(parameter));
    return super.addParameter(parameter);
  }

  public boolean hasParameters() {
    return !this.getParameterList().isEmpty();
  }

  public boolean hasTypeParameter() {
    return !this.getTypeParameters().isEmpty();
  }

  /**
   * Returns the port of this component that matches the given name, if it
   * exists. Does not consider inherited ports.
   *
   * @param name the name of the port
   * @return the port with the given name wrapped in an {@code Optional} or
   * an empty {@code Optional} if no such port exists
   */
  public Optional<PortSymbol> getPort(@NonNull String name) {
    return this.getPort(name, false);
  }

  /**
   * Returns the port of this component that matches the given name, if it
   * exists. Does consider inherited ports if {@code searchSuper} is set
   * to true.
   *
   * @param name the name of the port
   * @param searchSuper whether to consider ports of super components
   * @return the port with the given name wrapped in an {@code Optional} or
   * an empty {@code Optional} if no such port exists.
   */
  public Optional<PortSymbol> getPort(@NonNull String name, boolean searchSuper) {
    Preconditions.checkNotNull(name);
    for (PortSymbol port : searchSuper ? this.getAllPorts() : this.getPorts()) {
      if (port.getName().equals(name)) return Optional.of(port);
    }
    return Optional.empty();
  }

  /**
   * Returns the incoming ports of this component. Does not include inherited ports.
   *
   * @return a {@code List} of incoming ports of this component
   */
  public List<PortSymbol> getIncomingPorts() {
    List<PortSymbol> result = new ArrayList<>();
    for (PortSymbol port : this.getPorts()) {
      if (port.isIncoming()) {
        result.add(port);
      }
    }
    return result;
  }

  /**
   * Returns the incoming port of this component that matches the given name,
   * if it exists. Does not consider inherited ports.
   *
   * @param name the name of the port
   * @return the incoming port with the given name wrapped in an
   * {@code Optional} or an empty {@code Optional} if no such port exists.
   */
  public Optional<PortSymbol> getIncomingPort(@NonNull String name) {
    Preconditions.checkNotNull(name);
    return this.getIncomingPort(name, false);
  }

  /**
   * Returns the incoming port with matching name of this component, if it
   * exists. Does consider inherited ports if {@code searchSuper} is set
   * to true.
   *
   * @param name the name of the port
   * @param searchSuper whether to consider ports of super components
   * @return the incoming port with the given name wrapped in an
   * {@code Optional} or an empty {@code Optional} if no such port exists
   */
  public Optional<PortSymbol> getIncomingPort(@NonNull String name, boolean searchSuper) {
    Preconditions.checkNotNull(name);
    for (PortSymbol port : searchSuper ? this.getAllIncomingPorts() : this.getIncomingPorts()) {
      if (port.getName().equals(name)) return Optional.of(port);
    }
    return Optional.empty();
  }

  /**
   * Returns the outgoing ports of this component. Does not include inherited ports.
   *
   * @return a {@code List} of the outgoing ports of this component
   */
  public List<PortSymbol> getOutgoingPorts() {
    List<PortSymbol> result = new ArrayList<>();
    for (PortSymbol port : this.getPorts()) {
      if (port.isOutgoing()) {
        result.add(port);
      }
    }
    return result;
  }

  /**
   * Returns the outgoing port of this component that matches the given name,
   * if it exists. Does not consider inherited ports.
   *
   * @param name the name of the port
   * @return the outgoing port with the given name wrapped in an
   * {@code Optional} or an empty {@code Optional} if no such port exists.
   */
  public Optional<PortSymbol> getOutgoingPort(@NonNull String name) {
    Preconditions.checkNotNull(name);
    return this.getOutgoingPort(name, false);
  }

  /**
   * Returns the outgoing port of this component that matches the given name,
   * if it exists. Does consider inherited ports if {@code searchSuper} is set
   * to true.
   *
   * @param name the name of the port
   * @param searchSuper whether to consider ports of super components
   * @return the outgoing port with the given name wrapped in an
   * {@code Optional} or an empty {@code Optional} if no such port exists
   */
  public Optional<PortSymbol> getOutgoingPort(@NonNull String name, boolean searchSuper) {
    Preconditions.checkNotNull(name);
    for (PortSymbol port : searchSuper ? this.getAllOutgoingPorts() : this.getOutgoingPorts()) {
      if (port.getName().equals(name)) return Optional.of(port);
    }
    return Optional.empty();
  }

  /**
   * Returns the ports of this component with matching direction. Does not
   * included inherited ports.
   *
   * @param incoming whether to included incoming ports
   * @param outgoing whether to included outgoing ports
   * @return a {@code List} of all ports of this component the given direction
   */
  public List<PortSymbol> getPorts(boolean incoming, boolean outgoing) {
    List<PortSymbol> result = new ArrayList<>();
    for (PortSymbol port : this.getPorts()) {
      if (port.isIncoming() == incoming && port.isOutgoing() == outgoing) {
        result.add(port);
      }
    }
    return result;
  }

  /**
   * Return all ports of this component, including inherited ports.
   *
   * @return a {@code Set} of all ports of this component
   */
  public Set<PortSymbol> getAllPorts() {
    return this.getAllPorts(new LinkedHashSet<>());
  }

  protected Set<PortSymbol> getAllPorts(Collection<ComponentTypeSymbol> visited) {
    visited.add(this);
    Set<PortSymbol> result = new LinkedHashSet<>(this.getPorts());
    for (CompKindExpression superComponent : this.getSuperComponentsList()) {
      if (visited.contains(superComponent.getTypeInfo())) continue;
      Set<PortSymbol> inheritedPorts = new LinkedHashSet<>();
      for (PortSymbol port : superComponent.getTypeInfo().getAllPorts(visited)) {
        // Shadow super ports
        if (result.stream().noneMatch(e -> e.getName().equals(port.getName()))) {
          inheritedPorts.add(port);
        }
      }
      result.addAll(inheritedPorts);
    }
    return result;
  }

  /**
   * Returns all incoming ports of this component, including inherited ports.
   *
   * @return a {@code Set} of all incoming ports of this component
   */
  public Set<PortSymbol> getAllIncomingPorts() {
    Set<PortSymbol> result = new LinkedHashSet<>();
    for (PortSymbol port : this.getAllPorts()) {
      if (port.isIncoming()) {
        result.add(port);
      }
    }
    return result;
  }

  /**
   * Returns all outgoing ports of this component, including inherited ports.
   *
   * @return a {@code Set} of all outgoing ports of this component
   */
  public Set<PortSymbol> getAllOutgoingPorts() {
    Set<PortSymbol> result = new LinkedHashSet<>();
    for (PortSymbol port : this.getAllPorts()) {
      if (port.isOutgoing()) {
        result.add(port);
      }
    }
    return result;
  }

  /**
   * Returns the ports of this component with matching direction. Does included
   * inherited ports.
   *
   * @param incoming whether to included incoming ports
   * @param outgoing whether to included outgoing ports
   * @return a {@code Set} of all ports of this component with the given direction
   */
  public Set<PortSymbol> getAllPorts(boolean incoming, boolean outgoing) {
    Set<PortSymbol> result = new LinkedHashSet<>();
    for (PortSymbol port : this.getAllPorts()) {
      if (port.isIncoming() == incoming && port.isOutgoing() == outgoing) {
        result.add(port);
      }
    }
    return result;
  }

  public boolean hasPorts() {
    return !this.getPorts().isEmpty();
  }

  /**
   * Returns the subcomponent with matching name of this component, if it
   * exists.
   *
   * @param name the name of the subcomponent
   * @return the subcomponent with the given name wrapped in an
   * {@code Optional} or an empty {@code Optional} if no such subcomponent
   * exists.
   */
  public Optional<SubcomponentSymbol> getSubcomponents(@NonNull String name) {
    Preconditions.checkNotNull(name);
    for (SubcomponentSymbol subcomponent : this.getSubcomponents()) {
      if (subcomponent.getName().equals(name)) return Optional.of(subcomponent);
    }
    return Optional.empty();
  }

  public boolean isDecomposed() {
    return !this.getSubcomponents().isEmpty();
  }

  public boolean isAtomic() {
    return this.getSubcomponents().isEmpty();
  }

  /**
   * Helper function that transitively determines the start of the refinement chain.<br>
   *
   * Example: A refines B, C; B refines D; C refines D;
   *           The unique start is D.<br>
   *
   * A component without explicit refinements is itself the start on the chain. If there does not exist an unique
   * start (A refines B, C and B, C are unrefined) we throw an error.
   */
  public Optional<ComponentTypeSymbol> getRefinementStart() {
    if(getRefinementsList() == null || getRefinementsList().isEmpty()) {
      return Optional.of(this);
    }
    else {
      var candidates = getRefinementsList().stream()
          .map(CompKindExpression::getTypeInfo)
          .map(ComponentTypeSymbol::getRefinementStart) // Recursion
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toSet());
      if(candidates.size() == 1) {
        return candidates.stream().findFirst();
      }
      else {
        Log.warn("Could not determine a single root component in the refinement chain.");
        return Optional.empty();
      }
    }
  }

  /**
   * @return a {@code List} of the fields of this component type.
   */
  public List<VariableSymbol> getFields() {
    return this.getSpannedScope().getLocalVariableSymbols().stream()
      .filter(f -> !(f instanceof Port2VariableAdapter))
      .filter(f -> !(f instanceof Subcomponent2VariableAdapter))
      .filter(f -> !(getParameterList().contains(f)))
      .collect(Collectors.toList());
  }

  /**
   * Searches the fields of this component type for a field with the given name. Returns an {@code
   * Optional} of a field of this component type with the given name, or an empty {@code Optional}
   * if no such field exists. Throws an {@link IllegalArgumentException} if the given name is
   * {@code null}.
   *
   * @param name the name of the field.
   * @return an {@code Optional} of a field of this component type with the given name, or an
   * empty {@code Optional} if no such field exists.
   */
  public Optional<VariableSymbol> getField(@NonNull String name) {
    Preconditions.checkNotNull(name);
    return this.getFields().stream().filter(field -> field.getName().equals(name)).findFirst();
  }

  /**
   * @return {@code true}, if this is an inner component, else {@code false}.
   */
  public boolean isInnerComponent() {
    return outerComponent != null;
  }

  /**
   * @return an {@code Optional} of this component type's outer component, or an empty {@code
   * Optional} if this is not an inner component type.
   */
  public Optional<ComponentTypeSymbol> getOuterComponent() {
    return Optional.ofNullable(outerComponent);
  }

  /**
   * Sets the outer component type that contains this component type and subsequently states
   * whether this is an inner component type or not.
   *
   * @param outerComponent the component type that contains this component type.
   */
  public void setOuterComponent(@Nullable ComponentTypeSymbol outerComponent) {
    Preconditions.checkArgument(!(outerComponent instanceof ComponentTypeSymbolSurrogate));
    this.outerComponent = outerComponent;
  }
}
