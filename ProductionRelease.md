### Introduction ###

Everything has exceeded my expectations about what I thought the final version would look like. The interfaces are very clear and the code is very clean. I am also now very happy with the view code as that got tweaked quite a bit and is now very easy to modify. In addition along the way the eXtremeTable became an API and I no longer even think of it as a tag library...tags are just one way to generate a table.

### Release 1.0.4 ###
This is a minor release, but there are modifications to the DateCell and NumberCell functionality.  The changes are basically to enable DateCell and NumberCell (which now extend FormattedCell) to support user-defined contents between the begin/end column tag elements.  Before, they ignored any content between the begin/end column tags.  This enables you to now sort on dates and numbers, while formatting them differently or including them in a link in the table output.

Also, debug line numbers are now in the distro as well as a source jar to enable debugging within your own projects.

### Release 1.0.3 ###
This is a simple bug fix release addressing an issue when using the eXtremeTable filter feature in AJAX mode by setting the onInvokeAction. Previously, clicking the 'filter' button or pressing enter caused a page refresh.

Also incorporated the 1.0.2 patch.

### Html Builder classes are concrete now. ###
All the view builder classes are concrete and need to be instantiated. This only effects developers that have created custom views or custom cells. This was a much needed change to make the view code as flexible as it needs to be. Having the builder classes be static worked Ok, but there would be no opportunities to do anything interesting in the future, and made creating custom views more difficult. However, the methods are all the same yet and just requires a builder class to be instantiated with an HtmlBuilder.

To make the transition easier the CellBuilder is still static, but is now deprecated. The new (non-static) builder is called the ColumnBuilder, which actually makes more sense as that is what is being built.

```
   public String getHtmlDisplay(TableModel model, Column column) {
       ColumnBuilder columnBuilder = new ColumnBuilder(column);
       columnBuilder.tdStart();
       columnBuilder.tdBody(getCellValue(model, column));
       columnBuilder.tdEnd();
       return columnBuilder.toString();
   }
```

As another example, now that the ColumnBuilder is not static I would write custom cells more like this:

```
 public String getHtmlDisplay(TableModel model, Column column) {
        InputBuilder inputBuilder = new InputBuilder(column);
        inputBuilder.tdStart();

        try {
            Object bean = model.getCurrentRowBean();
            Integer id = new Integer(BeanUtils.getProperty(bean, "id"));
            inputBuilder.tdBody(id);

        } catch (Exception e) {}

        inputBuilder.tdEnd();

        return inputBuilder.toString();
    }

    private static class InputBuilder extends ColumnBuilder {
        public InputBuilder(Column column) {
            super(column);
        }

        public void tdBody(Integer id) {
            getHtmlBuilder().input("radio").name("location.id").id("location.id").value(id.toString()).onclick("populateLocationFields(this.value)");
            getHtmlBuilder().xclose();
        }
    }
 } 
```

It is much cleaner to just extend the builder you care about and then build your custom implementation.

### AbstractHtmlView template improvements ###
The AbstractHtmlView is now completely worked out. Also, the class to extend now is the HtmlView or CompactView class if you are not already doing so. The methods to override are the same but now I opened things up more and you can also extend the beforeBodyInternal and afterBodyInternal as well as the body() method from the AbstractHtmlView. In addition the TableBuilder, RowBuilder and CalcBuilder are accessible and can be custom instantiated in the init() method.

I abstracted out the things you should never care to implement, such as the bufferView feature, the FormBuilder, etc... This should suit everyone's needs now.

```
public abstract class AbstractHtmlView implements View {
    public void body(TableModel model, Column column) {
      ...
    }

    protected void init(HtmlBuilder html, TableModel model) {
        setTableBuilder(new TableBuilder(html, model));
        setRowBuilder(new RowBuilder(html, model));
        setCalcBuilder(new CalcBuilder(html, model));
    }
    
    protected abstract void beforeBodyInternal(TableModel model);
    
    protected abstract void afterBodyInternal(TableModel model);
}
```

Merely extend the View that you are interested in and override any method accessible.

```
public class HtmlView extends AbstractHtmlView {
    protected void beforeBodyInternal(TableModel model) {
      ...
    }

    protected void afterBodyInternal(TableModel model) {
      ...
    }

    protected void toolbar(HtmlBuilder html, TableModel model) {
        new DefaultToolbar(html, model).layout();
    }

    protected void statusBar(HtmlBuilder html, TableModel model) {
        new DefaultStatusBar(html, model).layout();
    }
}
```

```
public class CompactView extends AbstractHtmlView {
    protected void beforeBodyInternal(TableModel model) {
      ...
    }

    protected void afterBodyInternal(TableModel model) {
      ...
    }

    protected void toolbar(HtmlBuilder html, TableModel model) {
        new CompactToolbar(html, model).layout();
    }
}
```

In addition the TwoColumnRowLayout and the TwoColumnTableLayout now use a constructor. This is a feature because not only can you subclass the two classes, but also define and extra constructor attributes. This also makes it very consistant with the builders.

```
   public TwoColumnTableLayout(HtmlBuilder html, TableModel model) {
       this.html = html;
       this.model = model;
   }
```

### Intercept is now named Interceptor ###
The intercept feature is renamed to interceptor. So now there is a TableInterceptor, RowInterceptor, ExportInterceptor and ColumnInterceptor. They are in the interceptor package. The reason is because intercept was never intuitive as everyone just (correctly) called it the interceptor. I did not want to go to production with a name I really disliked. However, the method names are all the same so it is just a matter of pointing to the right package. Be sure to grab the latest TLD.

### TableAssembler is integrated into the TableModel. ###
When building an eXtremeTable in Java code in the last release you would use the TableAssembler class. That code was refactored and completely integrated into the TableModel. This makes the feature even easier to use than before.

```
    TableModel model = new TableModelImpl(context);

    Table table = model.getTableInstance();
    table.setItems(presidents);
    table.setAction("assembler.run");
    table.setTitle("Presidents");
    table.setShowTooltips(Boolean.FALSE);
    model.addTable(table);
        
    Row row = model.getRowInstance();
    row.setHighlightRow(Boolean.FALSE);
    model.addRow(row);

    Column columnName = model.getColumnInstance();
    columnName.setProperty("fullName");
    columnName.setIntercept((AssemblerIntercept.class).getName());
    model.addColumn(columnName);

    Column columnNickName = model.getColumnInstance();
    columnNickName.setProperty("nickName");
    model.addColumn(columnNickName);
        
    Object view = model.assemble();
```

### Limit Filter and Sort alias attribute ###

The Limit Filter and Sort objects now have an alias attribute. This is a very big improvement because now the Column alias and property are consistant with the Filter/Sort property. The thing to be careful about now though is that the property is the property and never the alias. Prior to this the property could be the alias. Of course that was always confusing so this is a nice improvement.

### New Table showTitle attribute. ###

New TableTag showTitle attribute is used to specify whether or not to show the title. This is a boolean value with the default being true.

### Limit can now use the State feature. ###

To use the State feature with the Limit feature you need to use the TableLimitFactory constructor that accepts the state. When using the state feature you should always give a unique tableId (in this example called presidents) so the constructor with the state will require the tableId also.

```
   Context context = new HttpServletRequestContext(request);
   LimitFactory limitFactory = new TableLimitFactory(context, presidents, TableConstants.STATE_PERSIST, null);
   Limit limit = new TableLimit(limitFactory);
```

### New Column filterOptions attribute / FilterOption interface. ###

New ColumnTag filterOptions attribute. Accepts a Collection of filter options where each bean in the Collection implements the FilterOption interface. It is used in conjunction with the filterCell=droplist. Very useful when using the Limit to use a custom droplist.

This is also now enhanced to use the same logic as the table items. This means you can use EL or just specify the String name and it will be searched for automatically. In addition the filter options Collection can be nested inside another object. Just specify the dot (.) notation to give the path.

### New Export encoding attribute / XlsView has Locale support. ###

By default the XlsView uses UTF-16 character encoding. If you need to use unicode then use the new ExportTag encoding attribute. The acceptable values are UTF and UNICODE.

### Removed the style attribute from the CompactView title. ###

This was kind of bug in the view code as I hard coded the title of the table when using the compact view. Just style (or move) the title through the CSS titleRow attribute.

### Renamed FilterSet.getValue() method. ###

Deprecated the poorly named FilterSet.getValue() method and renamed it to FilterSet.getFilterValue().

### Remove Table onsubmit. ###

The onsubmit was removed as it was never being called because javascript is used for all the table actions.

### Export totaling ###

The PDF and XLS exports now include totaling capability. There is nothing you need to do except use the Calc feature as normal.

### Export improvement - response headers change ###

The filter response headers should handle exporting in different environments better. Add the following to the response headers:

```
response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
```

### Columns autoGenerateColumns Preferences ###

The tag attributes that are backed by an interface have the ability to alias out the attribute to avoid having to give the fully qualified package name. The ColumnsTag autoGenerateColumns attribute now includes this feature.

### Table bufferView attribute ###

TableTag bufferView attribute. The default is true to buffer the view by default. This will improve the performance of the tag rendering in that the html view is streamed right out to the response writer if the bufferView is set to false. This does in theory improve the performance but I only included the feature because I thought it would be a nice feature. In general the eXtremeTable is very streamlined and this is just an additional performance kick.

### AJAX enable the eXtremeTable ###

When creating a table with Java code using the eXtremeTable API it is now possible to combine AJAX technology to render the view. For those new to AJAX this means that the web page does not have to refresh to navigate the eXtremeTable! The only real hook to use this feature is to use the new Table onInvokeAction attribute to give the javascript method that should be invoked. One of the most powerful aspects of the AJAX integration is there is no integration. The eXtremeTable has a very clear and easy API to use and so this was the logical next step. This also means you can use your favorite AJAX technology as the eXtremeTable does not actually integrate with any specific one. See the [tutorial](AJAXEnabledEXtremeTableTutorial.md) for more information.

### Use Map values for Table items ###

The Table can now use a Map (values) for the items, so it is possible to retreat a Collection fast and easy using some key. This feature and code was requested by one of the eXtremeComponents users. I thought it was a interesting feature and wanted to put it in for this next release!

### Can define more than one rows displayed ###

You can now define more than one rows displayed. This makes it possible to duplicate the the toolbar on the top and bottom of the eXtremeTable now.

### More conversions for Parameter parameters ###

The Parameter feature is even better as it supports Arrays, Lists, String and any other java object with a toString() that can be converted to a String array, such as a Number.

### Bug fix with Export viewResolver attribute not being referenced ###

Bug fixed with the Export viewResolver attribute as it was not actually being used. This made using custom exports not possible without naming both the view and viewResolver the same. This is now fixed and works as you would expect by giving the fully qualified path to the ViewResolver or aliasing the attribute in the preferences.

### Bug fix on exporting with two tables in one form ###

There was a bug in the exports in that you could not include two eXtremeTables in the same form and export them separately. This is now fixed and works perfectly. I just needed to recognize that the table instance parameter (now named ec\_eti for export table id) is a parameter that needs to be shared between tables in the same form and then reset after each request.

If you have other buttons on the form that get invoked after exporting you need to call the setFormAction(form, action, method), which is included in the extremecomponents.js file. The reason the form action, method and export table id need to be reset is because the exports are invoked using javascript and there is no way to reset them automatically after the export does the form post (or get). What the setFormAction does is set the export table id to null and then sets the form action to whatever it was originally. The first method parameter is the form, the second is the form action and the last is the form method.

This simple script looks like:

```
function setFormAction(form, action, method) {
  if (action) {
    document.forms[form].setAttribute('action', action);
  }
	
  if (method) {
    document.forms[form].setAttribute('method', method);
  }
	
  document.forms[form].ec_eti.value='';
}
```

Notes: this method was renamed from RC3 to the final. This is a very simple script so name it to whatever makes sense and how it is being used in your applicaton. This also fixed a bug related to State feature. When the State was set to persist the export caused problems.

### Add contextPath to table.imagePath preference ###

If the context path is not specified in the Preferences for the table.imagePath then put one in. However it will only provide a context if the path starts with a slash to signal absolute and the contextPath is not already put in.

### Set form method with javascript ###

Set the form method attribute with javascript when paging, filtering, and sorting. Previously only the form action was set, but to be complete the form method is set now as well.

### Bullet proof Limit feature ###

The Limit is now forgiving and will only log a warning and then just display what it can if incorrect data is passed to the LimitCallback. Also, if you need to debug the Limit just do a Limit.toString() to get the paging information. This same information is available as the table is being rendered.