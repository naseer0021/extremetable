### Introduction ###

The default way that the eXtremeTable deals with a Collection of Beans is to retrieve the whole result set and then process it. The nice thing about this is you can get the sorting, filtering, and pagination for free. You only have to make up the Collection of Beans or Collection of Maps and let the eXtremeTable know how to reference it. That works great for small to medium result sets, but starts to fail pretty miserably when the results are large. It really is a judgement call, but I prefer to let profiling make my technical decisions. If you are concerned that you have a performance problem then the best thing to do is stick a profiler on it and see for yourself. There are many open source and commercial profilers available to help you make the best decision. So, lets assume that we have discovered a performance problem and need to deal with the pagination ourselves.

To handle the pagination manually means that you only pull back enough results to fit on one page. At the same time you will need to deal with the sorting, filtering and pagination yourself. For the following discussion I am assuming that you are getting the Collection from a database and not some other mechanism. Of course the same principles apply no matter what you do so you should easily be able to adapt your code.

Now here is the important part. To get a smaller result set you would create a query pulling data like normal, but limit the results that you get back. In Sybase and SQLServer you would use the rowcount command and in MySql you would use the limit command. I am not sure what the other databases use, but I am pretty sure every database has similar functionality. If you are starting to follow my line of thinking what I am saying is when the user first goes to the table have the query only bring back the first page of result. If the user goes to the next page then return the next page of results.

The savy Sybase developer may be saying, but the rowcount command always starts at the beginning, so when I go to page two I have to start at the beginning. Well, yes, but now you have only returned two pages of results, as opposed to the whole result set. When you go to page three then you will only have pulled back three pages. Other databases, such as MySQL, allow you to ask for a given section of rows so then you consistently only pull back the page that you need.

To find out how the user is trying to sort and filter, what page they are going to, and how many results they need to see the eXtremeTable has a convenient interface called Limit and is created using the LimitFactory:

### Creating the Limit ###

First you need a new instance of the Limit and LimitFactory to work with:

```
 Context context = new HttpServletRequestContext(request);
 LimitFactory limitFactory = new TableLimitFactory(context, tableId);
 Limit limit = new TableLimit(limitFactory);
```

What is so nice about the Limit object is it abstracts out everything need to limit your table results.

The TableLimitFactory has another constructor that will default the tableId to ec if one is not specified.

```
 Context context = new HttpServletRequestContext(request);
 LimitFactory limitFactory = new TableLimitFactory(context);
 Limit limit = new TableLimit(limitFactory);
```

### The Filter and Sort Attributes ###

When you instantiate the Limit there two objects populated, FilterSet and Sort.

```
 private FilterSet filterSet;
 private Sort sort;
```

The FilterSet contains the filter action and an array of Filter objects. The action will be TableConstants.FILTER\_ACTION or TableConstants.CLEAR\_ACTION. Each Filter contains the property and value.

```
 private final String action;
 private final Filter[] filters;
```

The Sort contains the property and sortOrder. The sortOrder will be TableConstants.SORT\_ASC or TableConstants.SORT\_DESC.

```
 private Sort sort;
```

The Filter and Sort objects both have an alias and property attribute. This makes it consistant with the Column alias and property attributes. The thing to be careful about now though is that the property is the property and never the alias. Prior to the final release the property could be the alias. Of course that was always confusing so this is a nice improvement.

### Setting the Page and Row Attributes ###

To set the row attributes use the following:

```
 limit.setRowAttributes(totalRows, DEFAULT_ROWS_DISPLAYED);
```

This will populate the row attributes that provides all the additional information you need:

```
 private int rowStart;
 private int rowEnd;
 private int currentRowsDisplayed;
 private int page;
 private int totalRows;
```

Each variable has a getter method so you can retrieve the data. The attributes should be self explanitory so I will not go into any more detail.

### Setup ###

More than likely you will be doing all this custom sorting, filtering, etc... in a custom Controller (if using Spring), or Action (if using Struts), or something similar. In addition you will still need to set up a callback. The eXtremeTable does ship with a callback implementation that you can use called the LimitCallback. To use it you need to declare the retrieveRowsCallback, filterRowsCallback and sortRowsCallback table attributes.

```
 <ec:table 
   items="presidents" 
   retrieveRowsCallback="limit"
   filterRowsCallback="limit"
   sortRowsCallback="limit"
   view="limit"
   action="${pageContext.request.contextPath}/limit.run" 
   title="Presidents"
   >
   <ec:row>
     <ec:column property="fullName" title="Name"/>
     <ec:column property="nickName" />
     <ec:column property="term" />
     <ec:column property="born" cell="date"/>
     <ec:column property="died" cell="date"/>
     <ec:column property="career" />
   </ec:row>
 </ec:table>
```

The only thing you need to do to use the callback is to pass the Collection on the request like normal, but also pass an attribute in the request called totalRows. The totalRows should be passed as an Integer and represents the count of total rows that could be retrieved. Be sure to use the LimitCallback.TOTAL\_ROWS static variable to make things easier to maintain. If you have more than one eXtremeTable on a JSP you could pass the totalRows distinctly by appending the tableId with an underscore. For instance if your tableId is named pres you could do this:

```
 request.setAttribute("pres", presidents);
 request.setAttribute("pres_totalRows", new Integer(""+totalRows));
```

### Limit with State feature ###

To use the [Table|State]] feature with the Limit feature you need to use the TableLimitFactory constructor that accepts the state. When using the state feature you should always give a unique tableId (in this example called presidents) so the constructor with the state will require the tableId also.

```
  Context context = new HttpServletRequestContext(request);
  LimitFactory limitFactory = new TableLimitFactory(context, presidents, TableConstants.STATE_PERSIST, null);
  Limit limit = new TableLimit(limitFactory);
```

### Bullet proof Limit feature ###

The Limit is now forgiving and will only log a warning and then just display what it can if incorrect data is passed to the LimitCallback. Also, if you need to debug the Limit just do a Limit.toString() to get the paging information. This same information is available as the table is being rendered.

