### Introduction ###

Instead of hard coding the default attribute values the eXtremeTable uses, I put everything into a properties file. If you need to override any of these default settings you can do so by creating your own extremecomponents.properties file and then change the values you want.

To set up the properties file specify a context-param in your /WEB-INF/web.xml file and place the properties wherever you want to like this:

```
 <context-param>
   <param-name>extremecomponentsPreferencesLocation</param-name>
   <param-value>/org/extremesite/resource/extremecomponents.properties</param-value>
 </context-param>
```

You can think of the properties file as a way to declare global settings for all your eXtremeTables. As you will see shortly, the biggest advantage of creating your own properties file is you can get away with the cutting and pasting of common attributes on your tags. This is what a typical extremecomponents.properties looks like:

```
 table.imagePath=/extremesite/images/*.gif
 table.rowsDisplayed=12
 column.parse.date=yyyy-MM-dd
 column.format.date=MM/dd/yyyy
 column.format.currency=$###,###,##0.00
```

### TableTag ###

The two most common TableTag attributes to default in the properties file are imagePath and rowsDisplayed. If you did not define these attributes in your properties file then you would need to put them on each eXtremeTable. For instance this is what a typical table looks like:

```
 <ec:table 
   items="presidents"
   action="${pageContext.request.contextPath}/presidents.run" 
   imagePath="${pageContext.request.contextPath}/images/*.gif"
   rowsDisplayed="12"
   title="Presidents"
   >
   ...
 </ec:table>
```

By putting the imagePath and rowsDisplayed in the properties file then the table looks like this:

```
 <ec:table 
   items="presidents"
   action="${pageContext.request.contextPath}/presidents.run" 
   title="Presidents"
   >
   ...
 </ec:table>
```

As you can see the properties file eliminates the duplicate code.

Note: if the context path is not specified in the Preferences for the table.imagePath then put one in. However it will only provide a context if the path starts with a slash to signal absolute and the contextPath is not already put in.

### ColumnTag ###

The two most common ColumnTag attributes to default in the properties file are the parse and format. For instance this is what a typical column looks like when using a date cell:

```
 <ec:column property="dateOfBirth" cell=”date” parse=”yyyy-MM-dd” format=”MM/dd/yyyy”/> 
```

By putting the parse and format attributes in the properties file the column looks like this:

```
 <ec:column property="dateOfBirth" cell=”date”/> 
```

Of course you could still define the parse and format attributes to override the global settings, but most projects have one consistent parse and format for dates. One thing to pay attention to is the special syntax of parse.date and format.date in the properties file.

For instance this is what a typical column looks like when using a currency cell:

```
 <ec:column property="salary" cell=”currency” format=”$###,###,##0.00”/> 
```

By putting the format attribute in the properties file then the column looks like this:

```
 <ec:column property="salary" cell=”currency”/> 
```

Also, kind of a hidden feature in regards to the format attribute is that you can specify a custom format and and then refer back to it on a column by column basis. I think of these as named attributes. So if your extremecomponents.properties looks like this:

```
 table.format.myCustomDate=yy-MM-dd
```

Then your column could look like this to use the custom format:

```
 <ec:column property="dateOfBirth" cell="date" format=”myCustomDate”>
```

### Advanced Techniques ###

Using named attributes is something that I do quite often for defaulting various other attributes. You might have wondered how I get away with specifying the date cell as cell="date", or the currency cell as cell="currency", or on the xls export as view="xls." The magic might be more clear if I show you a snippit of the extremetable.properties file. The extremetable.properties is the file eXtremeTable uses for its default settings and is what you override with the extremecomponents.properties file.

```
 column.cell.date=org.extremecomponents.table.cell.DateCell
 column.cell.currency=org.extremecomponents.table.cell.NumberCell
 column.filterCell.droplist=org.extremecomponents.table.cell.FilterDroplistCell
 table.view.xls=org.extremecomponents.table.view.XlsView
```

So when you define cell="date" on a column the eXtremeTable looks up the column.cell. property and appends the cell attribute onto it. In other words the cell="date" corresponds to the column.cell.date=org.extremecomponents.table.cell.DateCell property. To really power use the properties file you could define a custom cell in your extremecomponents.properties file and then just refer to it by name on the ColumnTag.

Lets try one more example to claify this. If you remember the discussion on the Cell section of the ColumnTag chapter you will recall a custom cell defined as MyCell.

```
 <ec:column property="firstName" cell="com.mycompany.cell.MyCell"/>
```

If this cell is used quite a bit a better way to define it would be in the properties file and then refer to it by name. To make the switch you first need to update the extremecomponents.properties file.

```
 table.imagePath=/extremesite/images/*.gif
 table.rowsDisplayed=12
 table.cellspacing=2
 column.parse.date=yyyy-MM-dd
 column.format.date=MM/dd/yyyy
 column.format.currency=$###,###,##0.00
 column.cell.myCell=com.mycompany.cell.MyCell
```

Now you can refer to MyCell in your column by name.

```
 <ec:column property="firstName" cell="myCell"/>
```

As you can see this helps clean up the code and the definition is in one place. If the custom cell definition ever changes you can just modify the properties file and your done.


