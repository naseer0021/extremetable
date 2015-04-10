### Introduction ###

Most of the time when you design a table you will know exactly what columns you will need. However, there are times when it is nice to just generate the columns at runtime. To do this with eXtremeTable you will need to use the ColumnsTag and set the autoGenerateColumns attribute.

The AutoGenerateColumns is now a singleton and is no longer thread safe so do not define any class variables.

### ColumnsTag ###

The ColumnsTag only has one attribute, autoGenerateColumns. The autoGenerateColumns attribute is the fully qualified path to the class that Implements the AutoGenerateColumns Interface, which is where all the custom work is done.

```
 <ec:table 
   items="presidents" 
   action="${pageContext.request.contextPath}/autoGenerateColumns.run" 
   title="Presidents"
   >
   <ec:columns autoGenerateColumns="org.extremesite.controller.AutoGenerateColumnsImpl"/>
 </ec:table>
```

The AutoGenerateColumns Interface only has one method:

```
 public void addColumns(TableModel model);
```

What you are required to do is add the columns to the model. The easiest thing to do is show you an example:

```
 public class AutoGenerateColumnsImpl implements AutoGenerateColumns {
     public void addColumns(TableModel model) {
         Iterator iterator = columnsToAdd().iterator();
         while (iterator.hasNext()) {
             Map columnToAdd = (Map) iterator.next();
             Column column = model.getColumnInstance();
             column.setProperty((String) columnToAdd.get(PROPERTY));
             column.setCell((String) columnToAdd.get(CELL));
             model.addColumn(column);
         }
     }
 }
```

The columnsToAdd() method (in this example) simply returns a Collection that contains all the information to build the columns. I will leave it up to you on what that Collection looks like. As a reference here is what I used for the example on the eXtremeComponents site:

```
 private List columnsToAdd() {
   List columns = new ArrayList();
   columns.add(columnToAdd("fullName", "display"));
   columns.add(columnToAdd("nickName", "display"));
   columns.add(columnToAdd("term", "display"));
   columns.add(columnToAdd("born", "date"));
   columns.add(columnToAdd("died", "date"));
   columns.add(columnToAdd("career", "display"));
   return columns;
 }
    
 private Map columnToAdd(String property, String cell) {
   Map column = new HashMap();
   column.put(Column.PROPERTY, property);
   column.put(Column.CELL, cell);
   return column;
 }
```

Keep in mind that this is just the way I choose to use the feature, as there are many ways this example could have been implemented!

Anther thing I wanted to add is that you are only creating a Column once. The eXtremeTable is very efficient and does not create a new Column for each row. Instead it just keeps inserting the new column value into the existing Column with each iteration over the tag. Also remember that the TableModel has access to the Context so you could build what the columns look like in a Controller (if using Spring) or an Action (if using Struts) and pass the Collection through the request. Then your AutoGenerateColumns implementation just needs to build the Columns and add them to the model.
