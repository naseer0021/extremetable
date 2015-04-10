To globally set attributes and settings you will need to use the Preferences feature. This is currently done by using a properties file. The documentation does a good job of explaining how to set up the Preferences in the web.xml, and the common attributes that would be defined. What I would like to explain here is the more advanced uses of the Preferences feature.

All the tag attributes that represent a pluggable interface can be declared by giving the fully qualified path to the class that implements the interface. This gives a very convenient way to plug in the implementation. However, there are some design and maintenance considerations to this approach for the long term. The first is that you have now tied your implemenation to a very specific code base. The second is if you want to use that same implementation in another eXtremeTable you must copy and paste the fully qualified path in another JSP. A way to solve both problems is to declare everything in the Preferences.

### Preferences Chart ###

The following lists all the attributes tied to interfaces that can be declared in the Preferences. The Tag column shows the applicable eXtremeTable tag. The Attribute column shows the cooresponding tag attribute. The Interface column shows the Java interface that needs to be implemented. The Preference Key column shows the key to use in the Preferences.

| **Tag** | **Attribute** | **Interface** | **Preference Key** |
|:--------|:--------------|:--------------|:-------------------|
| TableTag | filterRowsCallback | org.extremecomponents.table.callback.FilterRowsCallback | table.filterRowsCallback |
| TableTag | interceptor | org.extremecomponents.table.interceptor.TableInterceptor | table.interceptor |
| TableTag  | retrieveRowsCallback  | org.extremecomponents.table.callback.RetrieveRowsCallback | table.retrieveRowsCallback |
| TableTag | sortRowsCallback | org.extremecomponents.table.callback.SortRowsCallback | table.sortRowsCallback |
| TableTag  | state | org.extremecomponents.table.state.State | table.state |
| TableTag | view | org.extremecomponents.table.view.View | table.view |
| RowTag | interceptor | org.extremecomponents.table.interceptor.RowInterceptor | row.interceptor |
| ColumnTag | calc | org.extremecomponents.table.calc.Calc | column.calc |
| ColumnTag | cell | org.extremecomponents.table.cell.Cell | column.cell |
| ColumnTag | filterCell | org.extremecomponents.table.cell.Cell | column.filterCell |
| ColumnTag  | headerCell | org.extremecomponents.table.cell.Cell | column.headerCell |
| ColumnTag | interceptor | org.extremecomponents.table.interceptor.ColumnInterceptor | column.interceptor |
| ExportTag | interceptor | org.extremecomponents.table.interceptor.ExportInterceptor | export.interceptor |
| ExportTag | view | org.extremecomponents.table.view.View | export.view |
| ExportTag | viewResolver | org.extremecomponents.table.filter.ViewResolver | export.viewResolver |
| ColumnsTag  | autoGenerateColumns  | org.extremecomponents.table.core.AutoGenerateColumns |  |

### Assign Preference Alias ###

The chart (above) shows what to declare for the preference key, but not how to give it a meaningful alias to use. If you notice the preference key gives a very consistent syntax of using the tag.attribute. To give the key an alias just append a name onto it. The syntax will be tag.attribute.alias.

The eXtremeTable comes with a custom cell called the RowCountCell that will display the current row count. I will demonstrate the RowCountCell as an example of using the ColumnTag cell attribute declared in the Preferences.

First make the custom cell by implementing the Cell interface or extending the AbstractCell.

```
 public class RowCountCell extends AbstractCell {
     protected String getCellValue(TableModel model, Column column) {
         int rowcount = ((model.getLimit().getPage() - 1) 
                 * model.getLimit().getCurrentRowsDisplayed()) 
                 + model.getRowHandler().getRow().getRowCount();
         return String.valueOf(rowcount);
     }
 }
```

Then declare the preference key in the Preferences (properties file) and give it an alias. ''The eXtremeTable keeps all of its configuration in a default Preferences. You can also override any of these attributes with your local Preferences.''

The default alias for the RowCountCell is rowCount and looks like this:

```
 column.cell.rowCount=org.extremecomponents.table.cell.!RowCountCell
```

To use the RowCountCell in the ColumnTag just reference the Cell by the alias like this:

```
 <ec:column alias="count" cell="rowCount"/>
```

That is all there is to it. Now you can refer to the cell as rowCount. If the package changes you just need to update the Preferences and it changes it for every custom row count cell.

''Note: In this example I used the ColumnTag alias attribute. The alias attribute is used when you have two columns that use the same property. It is also used when the column does not relate directly to a column bean property, which is the case here.''



