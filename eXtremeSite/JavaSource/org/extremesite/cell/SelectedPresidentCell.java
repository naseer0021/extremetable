/*
 * Copyright 2004 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.extremesite.cell;

import java.util.Collection;

import org.apache.commons.beanutils.BeanUtils;
import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.cell.Cell;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.table.view.html.ColumnBuilder;
import org.extremesite.controller.SelectedPresidentsConstants;

/**
 * Make up the checkboxes to select the presidents. The trick is to keep
 * a hidden field for each checkbox. The hidden field will keep track 
 * of what was selected and will be modified by using javascript. The
 * reason for the hidden field is to deal with the fact that a checkbox value
 * is not submitted in a form submit, but the hidden field will.
 * 
 * The javascript will look as follows:
 * 
 * <script>
 * function setPresidentState(chkbx) {
 *     if (chkbx.checked) {
 *         eval('pres.chkbx_' + chkbx.name).value='SELECTED';
 *     } else {
 *         eval('pres.chkbx_' + chkbx.name).value='UNSELECTED';
 *     }
 * }
 * </script>
 * 
 * @author Jeff Johnston
 */
public class SelectedPresidentCell implements Cell {
    public String getExportDisplay(TableModel model, Column column) {
        return null;
    }

    public String getHtmlDisplay(TableModel model, Column column) {
        ColumnBuilder columnBuilder = new ColumnBuilder(column);
        
        columnBuilder.tdStart();
        
        try {
            Object bean = model.getCurrentRowBean();
            String presidentId = BeanUtils.getProperty(bean, "presidentId");
            
            Collection selectedPresidentsIds = (Collection)model.getContext().getSessionAttribute(SelectedPresidentsConstants.SELECTED_PRESIDENTS);
            if (selectedPresidentsIds != null && selectedPresidentsIds.contains(presidentId)) {
                columnBuilder.getHtmlBuilder().input("hidden").name("chkbx_" + presidentId).value(SelectedPresidentsConstants.SELECTED).xclose();
                columnBuilder.getHtmlBuilder().input("checkbox").name(BeanUtils.getProperty(bean, "presidentId"));
                columnBuilder.getHtmlBuilder().onclick("setPresidentState(this)");
                columnBuilder.getHtmlBuilder().checked();
                columnBuilder.getHtmlBuilder().xclose();
            } else {
                columnBuilder.getHtmlBuilder().input("hidden").name("chkbx_" + presidentId).value(SelectedPresidentsConstants.UNSELECTED).xclose();
                columnBuilder.getHtmlBuilder().input("checkbox").name(BeanUtils.getProperty(bean, "presidentId"));
                columnBuilder.getHtmlBuilder().onclick("setPresidentState(this)");
                columnBuilder.getHtmlBuilder().xclose();
            }
        } catch (Exception e) {}
        
        columnBuilder.tdEnd();
        
        return columnBuilder.toString();
    }
}
