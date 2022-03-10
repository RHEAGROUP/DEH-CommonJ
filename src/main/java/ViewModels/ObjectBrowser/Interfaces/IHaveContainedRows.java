/*
 * IHaveContainedRows.java
 *
 * Copyright (c) 2020-2021 RHEA System S.A.
 *
 * Author: Sam Geren�, Alex Vorobiev, Nathanael Smiechowski 
 *
 * This file is part of DEH-CommonJ
 *
 * The DEH-CommonJ is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * The DEH-CommonJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ViewModels.ObjectBrowser.Interfaces;

import java.util.ArrayList;

import Reactive.ObservableCollection;
import cdp4common.types.OrderedItemList;

/**
 * The {@linkplain IHaveContainedRows} is the interface definition for object browser row view models that have contained rows
 */
public interface IHaveContainedRows<TRowViewModel extends IRowViewModel>
{
    /**
     * Gets the contained row the implementing view model has
     * 
     * @return An {@linkplain ObservableCollection} of {@linkplain TRowViewModel}
     */
    ArrayList<TRowViewModel> GetContainedRows();
        
    /**
     * Computes the contained rows
     */
    void ComputeContainedRows();
}
