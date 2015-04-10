### Introduction ###

Callbacks are used to retrieve, filter and sort the rows. The eXtremeTable comes with a custom implementation for each callback. Internally this is how things happen. First, all the meta data is loaded up, the meta data being all the attributes on all the eXtremeTable. Then the execute method is called on the eXtremeTable model. Inside the execute method is where the eXtremeTable figures out how to retrieve, filter and sort the rows by calling each of the callback interfaces. The three callback interfaces are the RetrieveRowsCallback, FilterRowsCallback and SortRowsCallback.

The callbacks are loaded up as a singleton and are not thread safe so do not define any class variables.

### RetrieveRowsCallback ###

The default implementation of the RetrieveRowsCallback is to look through the various servlet scopes for a Collection of Beans or a Collection of Maps defined by the items attribute on the Table. To use a custom callback to define the retrieveRowsCallback attribute and give the fully qualified path to the class which implements the RetrieveRowsCallback.

```
 <ec:table 
   var="pres" 
   action="${pageContext.request.contextPath}/presidents.run" 
   retrieveRowsCallback="com.mycompany.callback.MyCustomCallback"
   />
```

The interface looks as follows:

```
 public interface RetrieveRowsCallback {
   public Collection retrieveRows(TableModel model) throws Exception;
 }
```

There is only one method to implement. You get passed the TableModel and return a Collection. The Collection being a Collection of Beans or a Collection of Maps. By getting the TableModel you have all the meta data of the Table, plus access to the Context. This is very important because having access to the Context also gives access to just about everything in the web container.

### FilterRowsCallback ###

The default implemenation of the FilterRowsCallback is to take the Collection of Beans or Collection of Maps and then filter it by implementing the jakarta Predicate interface. The filter values are retrieved from eXtremeTable's filter input fields when a user does a search. To use a custom callback to define the filterRowsCallback attribute and give the fully qualified path to the class which implements the FilterRowsCallback.

```
 <ec:table 
   var="pres" 
   action="${pageContext.request.contextPath}/presidents.run" 
   filterRowsCallback="com.mycompany.callback.MyCustomCallback"
   />
```

The interface looks as follows:

```
 public interface FilterRowsCallback {
   public Collection filterRows(TableModel model, Collection rows) throws Exception;
 }
```

There is only one method to implement. You get passed the TableModel and return a Collection. You also get passed the Collection of rows retrieved by the RetrieveRowsCallback. You only have to implement the callback that you want, as the eXtremeTable has a default implementation for each callback.

### SortRowsCallback ###

The default implemenation of the SortRowsCallback is to take the Collection of Beans or Collection of Maps and then sort it using the jakarta BeanComparator. The sort value is retrieved using the header column that the user clicks on. To use a custom callback to define the sortRowsCallback attribute and give the fully qualified path to the class which implements the SortRowsCallback.

```
 <ec:table 
   var="pres" 
   action="${pageContext.request.contextPath}/presidents.run" 
   sortRowsCallback="com.mycompany.callback.MyCustomCallback"
   />
```

The interface looks as follows:

```
 public interface SortRowsCallback {
   public Collection sortRows(TableModel model, Collection rows) throws Exception;
 }
```

There is only one method to implement. You get passed the TableModel and return a Collection. You also get passed the Collection of rows retrieved by the RetrieveRowsCallback and filtered by the FilterRowsCallback. Again, you only have to implement the callback that you want, as the eXtremeTable has a default implementation for each one.


