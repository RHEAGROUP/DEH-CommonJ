/*
 * ElementDefinitionRowViewModel.java
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
package ViewModels.ObjectBrowser.ElementDefinitionTree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netbeans.swing.outline.RowModel;

import ViewModels.Interfaces.IElementDefinitionRowViewModel;
import ViewModels.ObjectBrowser.ElementDefinitionTree.Rows.IterationRowViewModel;
import ViewModels.ObjectBrowser.ElementDefinitionTree.Rows.Parameters.ParameterValueBaseRowViewModel;
import ViewModels.ObjectBrowser.Rows.OwnedDefinedThingRowViewModel;
import cdp4common.commondata.ClassKind;

public class ElementDefinitionBrowserTreeRowViewModel implements RowModel, IElementDefinitionRowViewModel
{
    /**
     * The current class logger
     */
    private final Logger logger = LogManager.getLogger();
    
    @Override
    public int getColumnCount()
    {
        return 11;
    }

    @Override
    public Object getValueFor(Object rowViewModel, int column)
    {
        if(rowViewModel instanceof IterationRowViewModel)
        {
            return "";
        }
        
        if(rowViewModel instanceof ParameterValueBaseRowViewModel<?>)
        {
            ParameterValueBaseRowViewModel<?> element = (ParameterValueBaseRowViewModel<?>) rowViewModel;
            
            switch (column)
            {
                case 0 : return "";
                case 1 : return element.GetOwnerShortName();
                case 2 : return element.GetPublishedValue();
                case 3 : return element.GetScale();
                case 4 : return element.GetValueSwitch();
                case 5 : return element.GetComputedValue();
                case 6 : return element.GetManualValue();
                case 7 : return element.GetReferenceValue();
                case 8 : return element.GetFormula();
                case 9 : return element.GetModelCode();
                case 10 : return element.GetThing().getClassKind();
                
                default : assert false;
            }
        }
        
        else if(rowViewModel instanceof OwnedDefinedThingRowViewModel<?>)
        {
            OwnedDefinedThingRowViewModel<?> element = (OwnedDefinedThingRowViewModel<?>) rowViewModel;
            
            switch (column)
            {
                case 0 : return "";
                case 1 : return element.GetOwnerShortName();
                case 2 : return "";
                case 3 : return "";
                case 4 : return "";
                case 5 : return "";
                case 6 : return "";
                case 7 : return "";
                case 8 : return "";
                case 9 : return element.GetModelCode();
                case 10 : return element.GetThing().getClassKind();
                
                default : assert false;
            }
        }
        
        return "-";
    }

    @Override
    public Class<?> getColumnClass(int column)
    {
        switch (column)
        {
            case 0 : return String.class;
            case 1 : return String.class;
            case 2 : return String.class;
            case 3 : return String.class;
            case 4 : return String.class;
            case 5 : return String.class;
            case 6 : return String.class;
            case 7 : return String.class;
            case 8 : return String.class;
            case 9 : return String.class;
            case 10 : return ClassKind.class;
            default : assert false;
        }
        
        return null;
    }

    @Override
    public boolean isCellEditable(Object node, int column)
    {
        return false;
    }

    @Override
    public void setValueFor(Object node, int column, Object value) { }

    @Override
    public String getColumnName(int column)
    {
        switch (column) 
        {
            case 0 : return "Option";
            case 1 : return "Owner";
            case 2 : return "Published Value";
            case 3 : return "Scale";
            case 4 : return "Switch";
            case 5 : return "Computed";
            case 6 : return "Manual";
            case 7 : return "Reference";
            case 8 : return "Formula";
            case 9 : return "Model Code";
            case 10 : return "Row Type";
            default : assert false;
        }
        
        return null;
    }
}
