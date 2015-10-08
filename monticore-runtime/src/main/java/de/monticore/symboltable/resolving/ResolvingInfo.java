/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.symboltable.resolving;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class ResolvingInfo {

  private boolean areSymbolsFound = false;

  private Collection<ResolvingFilter<? extends Symbol>> resolvingFilters = new ArrayList<>();

  /**
   * A list of scopes that where involved in the resolving process until now.
   */
  private final List<MutableScope> involvedScopes = new ArrayList<>();
  private final List<SymbolKind> handledTargetKinds = new ArrayList<>();

  public ResolvingInfo(Collection<ResolvingFilter<? extends Symbol>> resolvingFilters) {
    this.resolvingFilters.addAll(Log.errorIfNull(resolvingFilters));
  }

  // TODO PN should the found symbols also be stored here? If yes, areSymbolsFound == !found.isEmpty()
  public boolean areSymbolsFound() {
    return areSymbolsFound;
  }

  public void addInvolvedScope(final MutableScope scope) {
    involvedScopes.add(Log.errorIfNull(scope));
  }

  public List<MutableScope> getInvolvedScopes() {
    return ImmutableList.copyOf(involvedScopes);
  }

  public void addHandledTargetKind(final SymbolKind targetKind) {
    checkArgument(!isTargetKindHandled(targetKind));

    this.handledTargetKinds.add(targetKind);
  }

  public void removeTargetKind(SymbolKind targetKind) {
    checkArgument(isTargetKindHandled(targetKind));

    this.handledTargetKinds.remove(targetKind);
  }

  public boolean isTargetKindHandled(SymbolKind targetKind) {
    return handledTargetKinds.contains(targetKind);
  }

  public Collection<ResolvingFilter<? extends Symbol>> getResolvingFilters() {
    return ImmutableList.copyOf(resolvingFilters);
  }

}
