## 1.0.1-M4 Upgrade ##

### Overview of Changes ###

The following features have changed (in no particular order):
  * HtmlView based on old code base deprecated
  * Cell Interface has been simplified
  * RowTag is required
  * AutoGenerateColumns is a singleton and now easier to add column attributes
  * Extended Attributes has different method signature
  * TableTag collection attribute is removed
  * BaseModel is renamed to TableModel
  * Properties and ResourceBundle are now Preferences and Messages
  * The pageContext has been replaced by the Context interface
  * Limit and LimitFactory syntax changes and easier to use
  * TableTag saveFilterSort attribute replaced by the state attribute
  * ColumnTag showTotal attribute replaced by the calc attribute
  * Changed the search image name from search to filter
  * FormTag / InputTag deprecated
  * RetrieveRowsCallbacks, FilterRowsCallback, SortRowsCallback are singletons

### HtmlView ###

I placed the original view, cell, and related code based on the old view code into a package aptly named deprecated. The reason is because the new view code is such a success there is no reason for anyone to use the old view. For examples on how to build a custom view with the new code take a look at the HtmlView or CompactView in the view package.

### Cell ###

The Cell interface has changed. The reason for the change was to eliminate confusion and improve the flexibility. It was also not very obvious on how to deal with html versus export display values. Currently the Column value had to be set for html, but for an export the Column propertyValue had to be set. In addition because the Column value and propertyValue had to be overwritten they were inaccessible in the view.

A cell is now a singleton and is no longer thread safe so do not define any class variables. The reason for the change is that is how the Cell interface was being used and makes it simplier. On top of that the init() and destroy() methods pretty much simulated a singleton but in a confusing manner.

Here is the new Cell interface:

```
 public interface Cell {
 
     /**
      * The display that will be used for the exports.
      */
     public String getExportDisplay(TableModel model, Column column); 
 
     /**
      * The html that will be displayed in the table.
      */
     public String getHtmlDisplay(TableModel model, Column column);
 }
```

There is now a clear distinction between getting the export markup and html markup. More importantly, a String needs to be returned. The column value and property value should never be set. Another slight difference is that the BaseModel has been changed to use the TableModel. The change was a result of not having a base package anymore, which means there was no longer a need for a BaseModel.

The cell being a singleton should not be difficult to work with. If you had any class variable defined just pull them into the proper method and then pass them into any other methods that were using them.

The BaseCell has been removed as it no longer added any value. The replacement would be the AbstractCell. The abstract method is getCellValue and is used to return the value for the cell. This is an easy way to return the cell value without having to worry about the markup. It is also worth looking at the source code for the AbstractCell to see how simple the code is. However, it is a very simple class and also shows that in many cases just implementing the Cell interface directly is probably what you want to do.

The DisplayCell:

```
 public class DisplayCell extends AbstractCell {
     public String getExportDisplay(TableModel model, Column column) {
         return column.getPropertyValueAsString();
     }
 
     protected String getCellValue(TableModel model, Column column) {
         return column.getValueAsString();
     }
 }
```

The AbstractCell:

```
 public abstract class AbstractCell implements Cell {
     public String getExportDisplay(TableModel model, Column column) {
         return getCellValue(model, column);
     }
 
     public String getHtmlDisplay(TableModel model, Column column) {
         HtmlBuilder html = new HtmlBuilder();
         CellBuilder.tdStart(html, column);
         CellBuilder.tdBody(html, getCellValue(model, column));
         CellBuilder.tdEnd(html);
         return html.toString();
     }
 
     /**
      * A convenience method to get the display value.
      */
     protected abstract String getCellValue(TableModel model, Column column);
 }
```

### RowTag ###

The RowTag 

&lt;ec:row&gt;

 is required now. It should be placed around the columns. In hindsight this should have always been required. It does not make sense to have columns directly under just the table and with the latest refactoring I was able to incorporate a lot of flexibility into the code by having a more solid structure of Table -> Row -> Column. In the future I will be able to offer more features because I know every eXtremeTable has this clean organization.

A typical eXtremeTable will now look like this:

```
 <ec:table 
   items="presidents"
   var="pres" 
   action="${pageContext.request.contextPath}/presidents.run" 
   >
   <ec:row>
     <ec:column property="name"/>
     <ec:column property="term"/>
   </ec:row>
 </ec:table>
```

### AutoGenerateColumns ###

The AutoGenerateColumns is vastly improved so that you only have to set the attributes that you want. The defaults will automatically be called when you add the column to the ColumnHandler.

The AutoGenerateColumns is now a singleton and is no longer thread safe so do not define any class variables.

The example from the current documentation now looks like this:

```
 public class AutoGenerateColumnsImpl implements AutoGenerateColumns {
     public void addColumns(TableModel model) {
         Iterator iterator = columnsToAdd().iterator();
         while (iterator.hasNext()) { 
             Map columnToAdd = (Map) iterator.next();
             Column column = new Column(model);
             column.setProperty((String) columnToAdd.get(PROPERTY));
             column.setCell((String) columnToAdd.get(CELL));
             model.getColumnHandler().addAutoGenerateColumn(column);
         }
     }
 }
```

### Extended Attributes ###

The addExtendedAttributes method is renamed to make it more clear on how to use the feature. So the RowTag addExtendedAttributes is now addRowAttributes. The ColumnTag is addColumnAttributes. The TableTag is addTableAttributes. The ExportTag is addExportAttributes. In addtion you get a reference to the correct model bean which makes it more clear on how you should pull the attribute in your cell, view, etc...

An example using the ExportCsvTag looks like this:

```
 public void addExportAttributes(Export export) {
     String view = export.getView();
     if (StringUtils.isBlank(view)) {
         export.setView(TableConstants.CSV);
         export.setImageName(TableConstants.CSV);
     }
     export.addAttribute(CsvView.DELIMITER, getDelimiter());
 }
```

To retrieve the delimiter attribute value you just get it from the Export bean:

```
 Export export = model.getExportHandler().getCurrentExport();
 String delimiter = export.getAttributeAsString(DELIMITER);
```

Now when you need to override values in the ExportTag you call the setter method that is needed. If this is a new attribute then use the addAttribute() method just like in the previous releases. To retrieve the values first get the Export from the ExportHandler and then call the getter method that is needed. This same pattern is used for the other tags as well.

There are also two new callback methods in the RowTag and ColumnTag called modifyRowAttributes and modifyColumnAttributes so that you modify the attributes as the rows/columns are being processed.

### TableTag collection attribute ###

The TableTag collection attribute is removed, and now there are three new attributes. They are tableId, items, and var. Hopefully just by looking at them you can guess how they work, as I went with more standard JSTL naming. The tableId attribute is used to represent the unique table identifier, the items attribute represents the Collection you will pull from the various servlet scopes, and the var attribute represents the name that you will script against with EL.

How you work with the new attributes depends on what you need. The tableId is used to uniquely identify the table. If you only have one eXtremeTable on that page and are not using the Limit feature then you do not to define it at all. The default will be to uniquely name it 'ec'; If using the Limit feature you can also count on the name 'ec'. However, if you have two eXtremeTables on one JSP then you will need to give it a unique tableId. The var attribute is used to script against with EL. Lastly, you have the items attribute. This is used to pull the Collection from the various servlet scopes, but is now very robust. You can now nest your Collection within another object, or nest as deep as you want.

The syntax to pull the Collection will look like:

```
 <ec:table items="command.myObject.myCol" />
```

From this example you will be retrieving the myCol Collection from the command object.

```
 <ec:table items="myCol" />
```

This example show that you can just specify the Collection by name and it will be retrieved automatically, just like the previous collection attribute did.

### BaseModel ###

BaseModel has been renamed to TableModel. The change was a result of not having a base package anymore, which means there was no longer a BaseModel.

### Properties and ResourceBundle ###

The extremecomponentsPropertiesLocation is replaced by the extremecomponentsPreferencesLocation attribute in the web.xml file as the properties is governed by the Preferences interface now. In a later release I would like to offer an alternate xml file configuration option.

The eXtremeTable no longer looks for a default extremecomponents.properties file in the top level classpath. You should be using the web.xml context-param extremecomponentsPreferencesLocation attribute. The change was made to make things more generic.

The extremecomponentsResourceBundleLocation is replaced by the extremecomponentsMessagesLocation attribute in the web.xml file as the internationalization is governed by the Messages interface now.

The Properties and ResourceBundle attributes are split up by table, row, column, export and filter. In previous releases every attribute is prepended with 'table'. Now each attribute cooresponds with the tag in which it is used. In addition there is no longer that strange underline syntax for a property. Just replace the underline with a dot.

An example properties file would look like this:

```
 table.imagePath=/extremesite/images/*.gif
 table.rowsDisplayed=12
 column.parse.date=yyyy-MM-dd
 column.format.date=MM/dd/yyyy
 column.format.currency=$###,###,##0.00
```

As an example the (english) resource bundle looks like this:

```
 statusbar.resultsFound={0} results found, displaying {1} to {2} 
 statusbar.noResultsFound=There were no results found.
 
 toolbar.firstPageTooltip=First Page
 toolbar.lastPageTooltip=Last Page
 toolbar.prevPageTooltip=Previous Page
 toolbar.nextPageTooltip=Next Page
 toolbar.filterTooltip=Filter
 toolbar.clearTooltip=Clear
 
 toolbar.clearText=Clear
 toolbar.firstPageText=First 
 toolbar.lastPageText=Last
 toolbar.nextPageText=Next
 toolbar.prevPageText=Prev
 toolbar.filterText=Filter
 
 column.headercell.sortTooltip=Sort By
 
 column.calc.total=Total
 column.calc.average=Average
```

### pageContext ###

The TableModel (previously BaseModel) no longer has direct access to the pageContext. Instead use the Context interface, which by default, is backed by the pageContext. It was a bad implementation to give direct access to the pageContext. The Context has all the methods you need to pull the attributes from the different servlet scopes. However, if you need direct access to the backing object use the getContextObject() method.

### Limit and LimitFactory ###

The Limit and LimitFactory are now interfaces. The previous implementation was not as easy to work with as I wanted. However, the Limit object has the exact same method signatures as before so your existing code will work perfectly.

I was able to refactor the two Limit implementations into one, but still have two LimitFactory implementations. This is only interesting from a coding standpoint because as user you would only use the one implementation, but is important to the overall maintenance of the code. It does mean that there is only one class and so I renamed it to TableLimit. Also, because the Limit feature is tied to a Context and not a request I renamed the factory class (TableLimitFactory) to reflect that.

The Limit correctly displays the row information if doing an export. There is also a isExported() method on the Limit.

And example using the Limit and LimitFactory

```
 Context context = new HttpServletRequestContext(request);
 LimitFactory limitFactory = new TableLimitFactory(context, tableId);
 Limit limit = new TableLimit(limitFactory);
```

To set the row attributes just set the totalRows and default rows displayed.

```
 limit.setRowAttributes(totalRows, DEFAULT_ROWS_DISPLAYED);
```

The RequestLimitFactory has another constructor that will default the tableId to ec if one is not specified.

```
 Context context = new HttpServletRequestContext(request);
 LimitFactory limitFactory = new TableLimitFactory(context);
```

### TableTag saveFilterSort attribute ###

The saveFilterSort attribute is replaced by the state attribute. The state attribute references the State interface and can be used to plug in different implementations of how to save the table state.

The State interface looks like this:

```
 public interface State {
     public void saveParameters(TableModel model, Map parameters);
     public Map getParameters(TableModel model);
 }
```

There are two new attributes on the table. One is called state and the other is stateAttr. The state attribute is used to specify one of four states, with the ability to plug in your own solution. The precanned states are default, notifyToDefault, persist, and notifyToPersist. The default state does not maintain any state. The persist state will always persist the state of the table without having to pass any parameters around. The notifyToDefault state will persist the table state until you pass it a parameter telling it to go back to the default state. And lastly, the notifyToPersist state works like the current implementation in that you pass it a parameter telling it to keep the persisted state. The stateAttr is a way to specify what the parameter is. You can also use the properties file to specify it globally. The default parameter is still useSessionFilterSort to give it a little backward compatiblity.

If you would like the state to work differently then just implement the State interface and use the TableTag state attribute. You just need to give the fully qualified path to the class that implements the State interferface.

### ColumnTag showTotal attribute ###

There are two new attributes on the column. One is called calc and the other is calcTitle.

```
 <ec:column property="data" calc="total" calcTitle="Total:" />
```

The calc attribute corresponds to the Calc interface which has a single method:

```
 public interface Calc {
     public Number getCalcResult(TableModel model, Column column);
 }
```

You get passed the model and column and return the value as a Number. The default implementations are total and average.

To use your own custom Calc use the ColumnTag calc attribute and give the fully qualified path to the class that implements the Calc interface.

A Calc is a singleton and is not thread safe so do not define any class variables.

The existing showTotal is removed in the new version as it no longer fits. I also took off the totalTitle on the table as that did not make much sense.

### Image Names ###

Changed the search image name from search to filter. The change is made to reflect that nowhere is the feature referred to as search.

### FormTag / InputTag Deprecated ###

The FormTag and InputTag are deprecated. They should not be used with the new html view.

### RetrieveRowsCallbacks, FilterRowsCallback, SortRowsCallback ###

Callbacks are singletons now and are not thread safe so do not define any class variables.