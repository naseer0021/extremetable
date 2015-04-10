### Introduction ###

The Column is used to define the table columns.

Lets start by looking at the example President bean again:

```
public class President implements Serializable {
  private String firstName;
  private String lastName;

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName; 
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
```

In the following example we are pulling the firstName and lastName.

```
 <ec:table 
   items="presidents" 
   var="pres" 
   action="${pageContext.request.contextPath}/presidents.run" 
   />
   <ec:row>
     <ec:column property="firstName"/>
     <ec:column property="lastName">
       ${pres.lastName}
     </ec:column>
   </ec:row>
 </ec:table>
```

During the discussion of the TableTag you learned that a column can pull its value either dynamically or explicitly. The column defined by the property firstName is pulling the value dynamically. The column looks at the current bean and finds a method called getFirstName() and pulls the value. The column defined by the property lastName is being pulled explicilty from the current bean, and requires you to pull the value yourself. Lets look at an example:

```
 <ec:table 
   items="presidents" 
   var="pres" 
   action="${pageContext.request.contextPath}/presidents.run" 
   >
   <ec:row>
    <ec:column property="lastName">
       ${pageScope.pres.lastName}
     </ec:column>
   </ec:row>
 </ec:table>
```

In plain english this is saying pull the bean named pres out of the page scope and then use the lastName attribute. If you are using a Collection of Beans then make sure your bean has a corresponding getter method. If you are using a Collection of Maps then you do not have to do anything, as the eXtremeTable will pull the value out of the Map via the property name.

The main reason for this alternative way to pull the value is to enable you to do some other kind of html markup, such as displaying an image or using the column as a link to another page using an href.

```
 <ec:table 
   items="presidents" 
   var="pres" 
   action="${pageContext.request.contextPath}/presidents.run" 
   >
   <ec:row>
     <ec:column property="lastName">
       <a href="http://goto.president.detail">${pageScope.pres.lastName}</a>
     </ec:column>
   </ec:row>
 </ec:table>
```

Keep in mind that everything in that bean is accessible, so you can even pass the firstName attribute to display on the next page. Notice how the firstName attribute is being passed as a URL String.

```
 <ec:table 
   items="presidents" 
   var="pres" 
   action="${pageContext.request.contextPath}/presidents.run" 
   />
   <ec:row>
     <ec:column property="lastName">
       <a href="http://goto.president.detail?firstName=${pageScope.pres.firstName}">
         ${pageScope.presidents.lastName}
       </a>
     </ec:column>
   </ec:row>
 </ec:table>
```

I am no longer going to be specifying the pageScope in any of the examples, as it just adds more typing and is not needed. A JSP tag will always look in the pageScope first for any objects so we will always safely return the correct bean.

### Cell ###

Every column is backed by an Object which implements the Cell interface. You can think of a Cell as an Object that returns the formatted value for displaying in html or exporting. The cells that are included with the distribution are the DisplayCell, DateCell, NumberCell, and the RowCountCell. The DisplayCell is the default cell and just displays the column value. The DateCell uses the parse (optional) and format attributes along with the property value. The NumberCell uses the format attribute along with the property value. The RowCountCell displays the current row.

Note: The Cell interface has changed. The reason for the change was to eliminate confusion and improve the flexibility. It was also not very obvious on how to deal with html versus export display values. Previously the Column value had to be set for html, but for an export the Column propertyValue had to be set. In addition because the Column value and propertyValue had to be overwritten they were inaccessible in the view.

A cell is now a singleton and is no longer thread safe. The reason for the change is that is how the Cell interface was being used and makes it simplier. On top of that the init() and destroy() methods pretty much simulated a singleton but in a confusing manner.

The Cell interface looks like this:

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

There is now a clear distinction between getting the export and html display. More importantly, a String needs to be returned. The column value and property value should never be set.

The DisplayCell is the simplest Cell and extends the AbstractCell. The abstract method defined for the AbstractCell is getCellValue and is used to return the value for the cell. This is an easy way to return the cell value without having to worry about the markup. Although the AbstractCell can be useful in some cases, many times just implementing the Cell interface directly is probably what you want to do.

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

Now you can see just how simple a Cell is. To make your own custom Cell all you have to do is implement the Cell interface, or extend AbstractCell, and then give the fully qualified path to the Cell. For example, if you have a custom cell called MyCell then you would do something like this:

```
 <ec:column property="firstName" cell="com.mycompany.cell.MyCell"/>
```

If you change the data of a column then filtering and sorting probably will not make much sense. Keep in mind I only mean if you actually change the data, not just wrap some styling around it or include it as an <a href>. For instance if your custom cell shows a tree view of data, or is an image, then nothing logically makes sense to try and filter or sort on.

### Filter Cell ###

The filterCell attribute controls how the filters render. It is similar to the cell attribute and also implements the Cell interface. The two filter cells defined right now are the default one and the droplist. The default filter cell will show up as an input field. You do not need to do anything except make sure that the column is filterable.

If you would like the filter to show up as a droplist then just do this:

```
 <ec:table 
   items="presidents" 
   action="${pageContext.request.contextPath}/presidents.run" 
   >
   <ec:row>
     <ec:column property="status" filterCell="droplist"/>
   </ec:row>
 </ec:table>
```

The filterCell also allows you to define custom filters. All you have to do is implement the Cell interface, or extend AbstractCell, and then give the fully qualified path to the Cell. For example, if you have a custom cell called MyFilterCell then you would do something like this:

```
 <ec:column property="firstName" filterCell="com.mycompany.cell.MyFilterCell"/>
```

See the Cell section for more information on how to create your own custom cells.

### Header Cell ###

The headerCell attribute controls how the headers render. It is similar to the cell attribute and also implements the Cell interface. The default header cell will show up as text and contains the logic to do the sorting.

The headerCell also allows you to define a custom cell. All you have to do is implement the Cell interface, or extend AbstractCell, and then give the fully qualified path to the Cell. For example, if you have a custom cell called MyHeaderCell then you would do something like this:

```
 <ec:column property="firstName" headerCell="com.mycompany.cell.MyHeaderCell"/>
```

See the Cell section for more information on how to create your own custom cells.

### Alias ###

The alias attribute is used to make the column unique if the property attribute is used more than once. The alias attribute is also more appropriate for columns that do not map to a specific bean property as well. A third case would be if you are using nested object attributes (using dot notation) for the property.

### Styling ###

There are a few styling attributes associated with the ColumnTag.

```
 <ec:column 
   width=""
   style=""
   styleClass=""
   headerStyle=""
   headerClass=""
   filterStyle=""
   filterClass=""
   />
```

All of these are optional. The style attribute puts an inline style attribute on the column. The styleClass allows you to define a css class on the column. The headerClass allows you to change the css class of the header column. The filterClass allows you to change the css class of the filter column.

### Parse and Format ###

The parse and format attributes are used to display dates and currency as your users would expect.

The way you work with a date depends on whether your bean property is a String or a Date Object. If it is a String then you need to specify the parse attribute. The parse attribute is the pattern for the date as it looks as a String so it can be converted to a Date Object. If the bean property is a Date then the parse is not neccessary as it is already a Date. In either case you do need to specify the format attribute. The format attribute wraps a format around the value, and is what the users will see.

The example here says to show the date using month/day/year (4 digit). Because the born property is represented in the Bean as a String we also had to include the parse attribute so it can be converted to a Date.

```
 <ec:column property="born" cell="date" parse="yyyy-MM-dd" format="MM/dd/yyyy"/>
```

With currency you only need to give the format attribute.

```
 <ec:column property="payroll" cell="currency" format="###,###,##0.00"/>
```

Most of the time the dates and currency are parsed and formatted the same for all your eXtremeTables. As a convenience you could define the parse and format once in your extremecomponents.properties file and then exclude those attributes from your ColumnTag. See the Preferences chapter for more detail.

### Filtering and Sorting ###

You probably remember seeing the attributes filterable and sortable on the TableTag. These same attributes are also on the ColumnTag. The column filterable and sortable attributes override the table filterable and sortable attribute settings. This is very convenient when you need to sort or filter the whole table, except for one or two columns.

```
 <ec:table 
   items="presidents"
   action="${pageContext.request.contextPath}/presidents.run" 
   >
   <ec:row>
     <ec:column property="firstName" filterable="false"/>
     <ec:column property="lastName" sortable="false"/>
   </ec:row>
 </ec:table>
```

### Interceptor ###
The Interceptor feature is used to change the attribute values at runtime. This makes it possible to change the behavior of the column based on the data. You can read more about it at the [interceptor tutorial](InterceptorTutorial.md).

### Calc ###

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

### Views Allowed and Views Denied ###

The viewsAllowed attribute specifies whether the column should be used for the view. The views include html, compact, pdf, xls, csv, plus any custom views. If you specify a given view (or views), then only that view (or views) will use the column. For example you can specify viewsAllowed="pdf" which will use the column for the PDF export, but not any other export or html view.

```
 <ec:table 
   items="presidents"
   action="${pageContext.request.contextPath}/presidents.run" 
   >
   <ec:row>
     <ec:column property="firstName"/>
     <ec:column property="lastName" viewsAllowed="pdf"/>
   </ec:row>
 </ec:table>
```

The viewsDenied attribute specifies whether the column should not be used for the current view. The views include html, pdf, xls, csv, plus any custom views. If you specify a given view (or views), then only that view (or views) will not use the column. For example you can specify viewsDenied="html" which will not use the column for the html view, but will for all the exports.

```
 <ec:table 
   items="presidents"
   action="${pageContext.request.contextPath}/presidents.run" 
   >
   <ec:row>
     <ec:column property="firstName"/>
     <ec:column property="lastName" viewsDenied="html"/>
   </ec:row>
 </ec:table>
```

### Title ###

The title attribute is used to give the header a descriptive name. If you do not define a title then the column will convert the camelcase property name to a real word. If you do not want anything to show up in the header then just add a single whitespace for the title.

```
 <ec:table 
   items="presidents"
   action="${pageContext.request.contextPath}/presidents.run" 
   title="Presidents"
   >
   <ec:row>
     <ec:column property="firstName"/> //title shows as First Name
     <ec:column property="firstName" title="First Name"/> //title shows as First Name
     <ec:column property="firstName" title=" "/> //no title shows up
   </ec:row>
 </ec:table>
```

### Filter Options ###

The filterOptions attribute accepts a Collection of filter options where each bean in the Collection implements the FilterOption interface. It is used in conjunction with the filterCell="droplist". Very useful when using the Limit to use a custom droplist.

This is also enhanced to use the same logic as the table items. This means you can use EL(if using tags) or just specify the String name and it will be searched for automatically. In addition the filterOptions Collection can be nested inside another object. Just specify the dot (.) notation to give the path.

### Sorting Nested Property ###

To use a nested property (or composition to be more formal) all you have to do is give the path down to the attribute. For example if you had a bean (from your Collection of Beans) that contained a Name object called name and wanted the firstName attribute inside Name you could do this:

Bean:
```
public class President {
  private Name name;

  public Name getName() {
    return name;
  }
}

public class Name {
  private String firstName;

  public String getFirstName() {
    return firstName;
  }
}
```

Use:
```
<ec:table items="presidents" var="president" >
...
<ec:column alias="firstName" property="president.name.firstName" />
```

Of course this is pretty quick and dirty, but you should see the point. In many cases it makes sense to compose your objects instead of just having a flat structure.

Note: in the default sorting I check to see if the property is a nested property, and, if so, I use the NullSafeBeanComparator. However, this feature does require the BeanUtils 1.7.

### Extended Attributes ###

The way most tags are developed is you have a fixed set of attributes on the tag. Those attributes are then used so that a fixed set of functionality can be invoked. The eXtremeTable, however, has a much more flexible approach. The eXtremeTable gives you a way to add your own tag attributes so that you can do more customized work. Furthermore, there are clear hooks in the eXtremeTable that allow you to retrieve those custom tag attributes and then do something with them. Typically this is going to be a custom Cell, or maybe a custom View.

The hook to add and modify the attributes is via the following two methods.

```
public void addColumnAttributes(TableModel model, Column column) {
}

public void modifyColumnAttributes(TableModel model, Column column) {
}
```

The ColumnTag calls both of these methods in case they are overridden. The addColumnAttributes is called when the attributes are first added to the Column. This method is only called once. However, the modifyColumnAttributes is called each time the column is called for each iteration over the rows.

For example, to add column attributes to the Column that means all you need to do is extend the ColumnTag and then add your attributes to the Column object. Here is what a CustomTag would look like:

```
public class MyCustomTag extends ColumnTag {
  private String customAttributeOne;
  
  public String getCustomAttributeOne() {
    return customAttributeOne;
  }

  public void setCustomAttributeOne(String customAttributeOne) {
    this.customAttributeOne = customAttributeOne;
  }
  
  public void addColumnAttributes(TableModel model, Column column) {
    column.addAttribute("customAttributeOne", customAttributeOne);
  }
}
```

Now that you added the attributes needed you can retrieve them right from the Column. For example here is what your custom cell would look like:

```
public class MyCustomCell implements Cell {
  public String getHtmlDisplay(TableModel model, Column column) {
    Object customAttributeOne = column.getAttribute("customAttributeOne")
    String customAttributeOne = column.getAttributeAsString("customAttributeOne")
  }
}
```

Another thing to understand is you can make your own tag, with your own TLD file. You do not need to modify the extremecomponents.tld file. Furthermore you use your custom tag just like you would any tag in eXtremeTable, except that you reference your tag. For instance if your tag reference is mycompany and the tag is called customColumn you could do something like this:

```
 <ec:table 
     items="presidents" 
     action="${pageContext.request.contextPath}/public/demo/presidents.jsp" 
     title="Presidents"
     >
   <ec:row>
     <mycompany:customColumn 
               property="hello" 
               cell="com.mycompany.cell.MyCustomCell" 
               customAttributeOne="Hello World"/>
   </ec:row>
 </ec:table>
```








