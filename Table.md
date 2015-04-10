### Introduction ###

The Table is used to define what to render and how to render it. By default the eXtremeTable looks for a Collection of Beans, or a Collection of Maps in a servlet scope (page, request, session, or applicaton) by the name of the items attribute. As the table iterates over the columns, it uses the var attribute to pass the current row bean from the Collection into the page scope so that you can retrieve the data from it and script against it. The tableId is used to uniquely identify the table, which becomes neccessary if you have more than one table on a JSP. Here is an example to clarify.

This is the President bean:

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

A Collection of Beans must be populated and passed into a servlet scope. I like to use the Spring framework, so this example shows what it would look like using a Spring Controller. If you are using Struts then this equates to the Action class. If you are using something else, such as straight servlets, then just know all I am doing is populating a Collection of Beans and passing it into the request scope.

```
public class Presidents extends AbstractController {
  protected ModelAndView handleRequestInternal(HttpServletRequest request, 
                                                 HttpServletResponse response) 
    throws Exception {
    List presidents = new ArrayList();

    President president = new President();

    president.setFirstName("George");
    president.setLastName("Washington");
    presidents.add(president);

    president = new President();
    president.setFirstName("John");
    president.setLastName("Adams");
    presidents.add(president);

    request.setAttribute("presidents", presidents);

    return new ModelAndView("/demo/presidents.jsp");
}
```

Now you are ready to construct the table.

```
<%@ taglib uri="/tld/extremecomponents" prefix="ec" %>

<ec:table 
  items="presidents" 
  var="pres"
  imagePath="${pageContext.request.contextPath}/images/*.gif"
  action="${pageContext.request.contextPath}/presidents.run"
  >
  <ec:row>
    <ec:column property="firstName"/>
    <ec:column property="lastName"/>
  </ec:row>
</ec:table>
```

What you should understand from this example is that we put a Collection of Beans called presidents into the request scope with the name presidents. We then set the items attribute on the Table to presidents so that the table knows how to find the Collection of Beans. We also defined two columns. One for the firstName and one for the lastName. The column called firstName is the more common case in which we just let the column find the firstName value in the current bean. The column called lastName is the other way to explicitly pull the value.

Explicitly pulling the value out of a column can be very powerful, but you need to understand how the table builds the rows. To build the rows the table iterates over the columns the number of times defined by the rowsDisplayed attribute. With each iteration it takes the next bean out of the Collection of Beans and sets that bean in the page scope using the name of the var attribute. Another way to say it is with each iteration over the columns you always have access to the current row bean in the Collection.

The Table can now use the Map values for the items, so it is possible to retrieve a Collection fast and easy using some key. This feature and code was requested by one of the eXtremeComponents users. I thought it was a interesting feature and wanted to put it in for the final release!

### Displaying Images ###

To display images use the imagePath attribute.

```
<ec:table 
  items="presidents" 
  var="pres"
  imagePath="${pageContext.request.contextPath}/images/*.gif"
  >
  ...
</ec:table>
```

The eXtremeTable expects to find all the images in one directory and uses a special syntax to define what kind of images they are. In this example the images are expected to be in an images directory directly under the web context. The .gif is eXtremeTable's way of saying that all the images will be GIF images. Later on when we start talking about the preferences you will discover that you can hide this attribute in the extremecomponents.properties file and will not have to include it on each eXtremeTable in your application.

### Filtering, Sorting and Action ###

The eXtremeTable has filtering and sorting built right in. The only thing you need to decide is whether or not to use it. The attributes you will use are filterable and sortable. Both are booleans and the default value is true. All features are turned on by default, and you could turn things off as needed. If you choose not to use the sorting or filtering, then set the attributes to false.

```
<ec:table 
  items="presidents" 
  var="pres"
  imagePath="${pageContext.request.contextPath}/images/*.gif"
  action="${pageContext.request.contextPath}/presidents.run" 
  filterable="false"
  sortable="false"
  >
  ...
</ec:table>
```

If you are still unsure then experiment by setting the filterable and sortable attributes to true. Then see how the eXtremeTable allows you to enter criteria in the filter to limit the results, it also allows you to roll your mouse over the headers, and sort. Next, set the filterable and sortable attributes to false and you will notice that those features are no longer accessible.

What may be more interesting to point out in this example is the use of the action attribute. The action is used to tell the eXtremeTable how to navigate back to the current page while filtering or sorting. In this example I need to run through the Spring controller to get my Collection of Beans, which is where the presidents.run syntax comes from. Also, by default, you do not need to worry about passing parameters. The eXtremeTable will keep track of all your parameters and will pass them along as you filter, sort, and page through the Collection of Beans. See the Parameter chapter for more information on that.

### Automatically Including Parameters ###
The autoIncludeParameters attribute is used to specify whether or not to automatically include parameters passed to it. The default is true and makes working with the table very transparent. However, you need to be aware of how this works so that you can evaluate if that is really the effect you want. In my experience the only time I need to set the autoIncludeParameters to false is when embedding the table in a custom form. As a rule you should always set this to false when the table is embedded in a form, otherwise just be aware of how this works. You can read more about it in the [Parameter](Parameter.md) chapter.

### Adjusting Rows Displayed ###

The eXtremeTable will display 15 rows by default. To change this set the rowsDisplayed attribute to the number of rows you want displayed. The rowsDisplayed is another good candidate to default in the [Preferences](Preferences.md) (extremecomponents.properties) file.

```
table.rowsDisplayed=12
```

You can also adjust the median and max rows displayed in the dropdown. The default values are 50 and 100, and can be declared in your extremecomponents.properties file.

```
table.medianRowsDisplayed=24
table.maxRowsDisplayed=36
```

Note: If you want to display all the rows at once just set the showPagination to false.

### Styling ###

There are quite a few styling attributes associated with the TableTag.

```
 <ec:table 
   cellspacing="0"
   cellpadding="0"
   border="0"
   width="80%"
   style=""
   styleClass=""
   />
```

All of these are optional. The width attribute is used to prevent the table from stretching all the way. The cellpadding, cellspacing and border default to 0 (zero). The style attribute puts an inline style attribute on the table. The styleClass allows you to define a css class attribute on the table.

### Saving Table State ###

There are two new attributes on the table. One is called state and the other is stateAttr. The state attribute references the State interface and can be used to plug in different implementations of how to save the table state.

The State interface looks like this:

```
 public interface State {
     public void saveParameters(TableModel model, Map parameters);
     public Map getParameters(TableModel model);
 }
```

The state attribute is used to specify one of four states, with the ability to plug in your own solution. The precanned states are default, notifyToDefault, persist, and notifyToPersist. The default state does not maintain any state. The persist state will always persist the state of the table without having to pass any parameters around. The notifyToDefault state will persist the table state until you pass it a parameter telling it to go back to the default state. And lastly, the notifyToPersist state works like the current implementation in that you pass it a parameter telling it to keep the persisted state.

The stateAttr is a way to specify what the parameter is. You can also use the properties file to specify it globally. The default parameter is notifyState.

If you would like the state to work differently then just implement the State interface, then use the Table state attribute to give the fully qualified path to the class that implements the State interface.

As a rule of thumb the tableId should be specified when using the state attribute. This is because the state is stored in session by the tableId. If the tableId is not uniquely specified then one eXtremeTable will override another one with the same name. If the tableId is not specifed the default value is ec.

Note:When using state along with limit, you should use TableLimitFactory(Context context, String tableId, String state, String stateAttr) instead of TableLimitFactory(Context context) or TableLimitFactory(Context context, String tableId).

### Locale ###
To specify a locale on the table use the Table locale attribute. You can read more about it in the [Messages](Messages.md) chapter.

### Theme ###
The theme attribute is used to change the look and feel of the table. However, it is really quite simple. The theme changes the class attribute of the HTML div that surrounds the table. The default theme is called eXtremeTable and looks like this in the source code in the browser:

```
 <div class="eXtremeTable" >
  ...
 </div>
```

The real trick then is to change your CSS stylesheet to include the theme:

```
.eXtremeTable {
   margin: 0;
   padding: 0;
}

.eXtremeTable .tableHeader {
   background-color: #308dbb;
   color: white;
   font-family: verdana, arial, helvetica, sans-serif;
   ...
}
```

### Callbacks ###
Callbacks are used to retrieve, filter and sort the rows. You can specify a custom implementation of each if needed. You can read more about it in the [Callbacks chapter](Callbacks.md).

### Custom Form Handling ###
The eXtremeTable is a form component. When you do a view source you can see that there is a form element that wraps the table and there are hidden input fields that the eXtremeTable uses to control how the table is used. By using the Table form attribute you can reference a different form instead of creating one. You can see an example by reading the [[Form\_Tutorial|custom form]] tutorial.

Note: if you have other buttons on the form that get invoked after exporting you need to call the setFormAction(form, action, method), which is included in the extremecomponents.js file. The reason the form action, method and export table id need to be reset is because the exports are invoked using javascript and there is no way to reset them automatically after the export does the form post (or get). What the setFormAction does is set the export table id to null and then sets the form action to whatever it was originally. The first method parameter is the form, the second is the form action and the last is the form method.

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

### OnInvokeAction ###
The onInvokeAction attribute is used to give the javascript method that should be called when one of the table actions are invoked. The table actions include filtering, sorting, paging and the current rows displayed. By default this will just submit the form that wraps the eXtremeTable. One reason for specifying the onInvokeAction is when using the table along with [[AJAX\_enabled\_eXtremeTable\_Tutorial|AJAX technology]].

### View ###
The view in the eXtremeTable is plugable, which means that the html can be easily modified to specify a default view. You can read more information in the [[| Views](Views.md)] chapter.

### Interceptor ###
The Interceptor feature is used to change the attribute values at runtime. This makes it possible to change the behavior of the table based on the data. You can read more about it at the [[Interceptor\_Tutorial|interceptor tutorial]].

### Buffering the View ###
The bufferView attribute is used to improve the performance of the tag rendering in that the html view is streamed right out to the response writer if the bufferView is set to false. This does in theory improve the performance but I only included the feature because I thought it would be a nice feature. In general the eXtremeTable is very streamlined and this is just an additional performance kick.

The default is true, which means that the HTML String is kept in the local buffer. The reason the default is true is because when generating the eXtremeTable in [[AJAX\_enabled\_eXtremeTable\_Tutorial|Java code]] there is no way to not buffer the view.

### Other attributes ###

For consistency I kept all the display features that can be toggled on or off as showXXXX. These include showPagination, showStatusBar, showTitle, showTooltips, and showExports.

The title attribute will display a title above the table. The location of the title depends on which view is being used. The default view has the title appear above the table and to the left of the toolbar. See the View chapter for more information.

You will notice that there are still a few attributes that I have not talked about, because they are directly tied to other chapters. The autoIncludeParameters attribute is discussed in the [[Parameter](Parameter.md)] chapter. The retrieveRowsCallback, sortRowsCallback and filterRowsCallback is discussed in the [Callbacks](Callbacks.md) chapter.

### Extending TableTag ###

The way most tags are developed is you have a fixed set of attributes on the tag. Those attributes are then used so that a fixed set of functionality can be invoked. The eXtremeTable, however, has a much more flexible approach. The eXtremeTable gives you a way to add your own tag attributes so that you can do more customized work. Furthermore, there are clear hooks in the eXtremeTable that allow you to retrieve those custom tag attributes and then do something with them.

The hook to add attributes on the TableTag is via the addTableAttributes() method.

> public void addTableAttributes(TableModel model, Table table);

The TableTag calls this method in case the method is overridden. That means all you need to do is extend the TableTag, override the addTableAttributes() method, and then add your attributes to the Table object.

Here is what a custom TreeTag looks like:

```
public class TreeTag extends TableTag {
    private String parentAttribute;
    private String identifier;

    public void setParentAttribute(String parentAttribute) {
        this.parentAttribute = parentAttribute;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void addTableAttributes(TableModel model, Table table) {
        table.addAttribute(TreeConstants.PARENT_ATTRIBUTE, TagUtils.evaluateExpressionAsString("parentAttribute", parentAttribute, this, pageContext));
        table.addAttribute(TreeConstants.IDENTIFIER, TagUtils.evaluateExpressionAsString("identifier", identifier, this, pageContext));
        
        table.setShowPagination(Boolean.FALSE);
        table.setFilterRowsCallback("org.extremecomponents.tree.ProcessTreeRowsCallback");
        table.setSortRowsCallback("org.extremecomponents.tree.ProcessTreeRowsCallback");
    }
}
```

Now that you added the attributes needed you can retrieve them right from the Table elsewhere in your code.

Another thing to understand is you can make your own tag, with your own TLD file. You do not need to modify the extremecomponents.tld file. Furthermore you use your custom tag just like you would any tag in eXtremeTable, except that you reference your tag. For instance if your tag reference is mycompany and the tag is called customTable you could do something like this:

```
 <mycompany:customTable 
     items="presidents" 
     action="${pageContext.request.contextPath}/public/demo/presidents.jsp" 
     title="Presidents"
     >
   <ec:row>
     <ec:column property="nickName"/>
   </ec:row>
 </mycompany:customTable>
```


