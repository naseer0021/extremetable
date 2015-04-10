### Introduction ###

The Row is used to define the table rows.

You define the RowTag to wrap around the columns like this:

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

### Highlight Rows ###

To do row level highlighting use the highlightRow attribute. Acceptable values are true and false, with the default being false.

```
 <ec:row highlightRow="true">  
   <ec:column property="name"/>
   <ec:column property="term"/>
 </ec:row>
```

The highlightRow attribute will insert javascript that will set the row css class. The default css class will be called highlight. To define a custom css class attribute then use the highlightClass attribute.

### Styling ###

There are a few styling attributes associated with the RowTag.

```
<ec:row 
  style="" 
  styleClass=""
  highlightClass=""
  />
```

All of these are optional. The highlightClass effects the row highlighting css class. The style attribute puts an inline style attribute on the row. The styleClass allows you to define a css class attribute on the row.

### Javascript Attributes ###

To do more dynamic processing you can use javascript on a row by row basis. The supported attributes are onclick, onmouseover and onmouseout.

```
 <ec:row 
   onclick="" 
   onmouseover=""
   onmouseout=""
   />
```

### Interceptor ###
The Interceptor feature is used to change the attribute values at runtime. This makes it possible to change the behavior of the row based on the data. You can read more about it at the [interceptor tutorial](InterceptorTutorial.md).

### Extended Attributes ###

The way most tags are developed is you have a fixed set of attributes on the tag. Those attributes are then used so that a fixed set of functionality can be invoked. The eXtremeTable, however, has a much more flexible approach. The eXtremeTable gives you a way to add and modify your own tag attributes so that you can do more customized work. Furthermore, there are clear hooks in the eXtremeTable that allow you to retrieve those custom tag attributes and then do something with them.

The hook to include (or modify) the attributes include one or both of the following methods:

```
  public void addRowAttributes(TableModel model, Row row) {}
  public void modifyRowAttributes(TableModel model, Row row) {}
```