### Resource Bundle ###

To set up the resource bundle specify a context-param in your /WEB-INF/web.xml file and place the bundle wherever you want to like this:

```
 <context-param>
   <param-name>extremecomponentsMessagesLocation</param-name>
   <param-value>org/extremesite/resource/extremecomponentsResourceBundle</param-value>
 </context-param>
```

In this example the resource bundle is called extremecomponentsResourceBundle, but you could call it anything, or just use an existing bundle that you have.

If you do not specify a locale, then it will be determined based on the servlet request. If you would like to specify a locale on the eXtremeTable then use the Table locale attribute.

```
 <ec:table 
   items="presidents" 
   action="${pageContext.request.contextPath}/public/demo/locale.jsp" 
   title="table.title.president"
   locale="de_DE"
   >
   ...
 </ec:table>
```

Here the eXtremeTable will look for a German resource bundle.

### Global Resources ###

There are some global keys that the eXtremeTable needs to communicate to the user what is happening. This includes the statusbar text informing the user what specific rows are being displayed, the Rows Displayed droplist, and the various tooltips. If you are lucky enough to have a language supported by the eXtremeTable then you do not need to worry about this. If not then you will need to define the following keys:

```
statusbar.resultsFound={0} results found, displaying {1} to {2} 
statusbar.noResultsFound=There were no results found.

toolbar.tooltip.firstPage=First Page
toolbar.tooltip.lastPage=Last Page
toolbar.tooltip.prevPage=Previous Page
toolbar.tooltip.nextPage=Next Page
toolbar.tooltip.filter=Filter
toolbar.tooltip.clear=Clear
toolbar.tooltip.xls=XLS Export
toolbar.tooltip.pdf=PDF Export
toolbar.tooltip.csv=CSV Export
toolbar.tooltip.xml=XML Export

toolbar.text.firstPage=First
toolbar.text.lastPage=Last
toolbar.text.nextPage=Next
toolbar.text.prevPage=Prev
toolbar.text.filter=Filter
toolbar.text.clear=Clear
toolbar.text.xls=XLS
toolbar.text.pdf=PDF
toolbar.text.csv=CSV
toolbar.text.xml=XML

column.headercell.tooltip.sort=Sort By

column.calc.total=Total
column.calc.average=Average
```

Note: There is  something with the classloader that makes loading resource bundles in a different jar file, but the same package as the eXtremeTable Messages (org\extremecomponents\table\resource\extremetableResourceBundle\_language\_country.properties), not work correctly. Just something to keep in mind if you try putting your messages in the exact same package with the same resource bundle name as the defaults. I would recommend just referencing your own project structure for custom messages.

### Table ###

The Table attributes that can be used in a locale specific manner are the imagePath and title.

The imagePath attribute has a specific key that the eXtremeTable expects to use called table.imagePath. You can put this key in your resource bundle in a language specific folder structure. For instance the German images could be placed in a de folder if you specifed this in your resource bundle:

```
 table.imagePath=/extremesite/images/table/de/*.gif
```

The title attribute works a little different. If you specify a title attribute with a dot (.) in the name and you defined a resource bundle then the eXtremeTable will look for a matching key. For example if you specified an attribute of title="table.title.president" on your table like this:

```
 <ec:table 
   items="presidents" 
   action="${pageContext.request.contextPath}/public/demo/locale.jsp" 
   title="table.title.president"
   >
   ...
 </ec:table>
```

Then the eXtremeTable will look in the resource bundle for matching key like this:

```
 table.title.president=US Präsidenten
```

### Column ###

The Column attributes that can be used in a locale specific manner are the format and title.

The format attribute has a specific key that the eXtremeTable expects to use called table.fomat.type. See the discussion on the Properties File for more details, as it is the same concept. For instance the date and currency format types could be defined like this:

```
 column.format.date=MM/dd/yyyy
 column.format.currency=$###,###,##0.00
```

The title attribute works a little different. If you specify a title attribute with a dot (.) in the name and you defined a resource bundle then the eXtremeTable will look for a matching key. For example if you specified an attribute of title="table.column.nickName" on your column like this:

```
 <ec:table 
   items="presidents" 
   action="${pageContext.request.contextPath}/public/demo/locale.jsp" 
   title="table.title.president"
   >
   <ec:row>
     <ec:column property="nickName" title="table.column.nickName" />
   </ec:row>
 </ec:table>
```

Then the eXtremeTable will look in the resource bundle for matching key like this:

```
 table.column.nickName=Spitzname
```


