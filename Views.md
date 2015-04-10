The views in eXtremeTable are plugable. This means that the html can be easily modified, or a new kind of export can be implemented. All you need to do is implement the View interface and set the view attribute on the TableTag or ExportTag. First lets look at the View interface:

```
 public interface View {
   public void beforeBody(TableModel model);
   public void body(TableModel model, Column column);
   public Object afterBody(TableModel model);
 }
```

Any class that implements the View has three opportunities to insert content. The beforeBody() method gets called right away. The body() method gets called as each column in each row is processed. The afterBody() method is the last thing to be called by the eXtremeTable and it returns an Object that will represent the View. Usually this is a String, such as the html markup in the HtmlView class, but it could be anything. The most common reason you may return some other Object is with a custom export.

### Table View ###

All the markup in the eXtremeTable is generated in two places, either a View or a Cell. Combined, both give you a very plugable solution to rendering content. To use the custom View simply define the view attribute on the TableTag and give the fully qualified path to the class which implements the View interface, or use the [Preferences](PreferencesTutorial.md) to alias it out:

```
 <ec:table 
   items="presidents" 
   action="${pageContext.request.contextPath}/presidents.run" 
   view="com.mycompany.view.MyCustomView"
   >
   ...
 </ec:table>
```

You can read more about creating a custom view with the [messages tutorial](HtmlViewTutorial.md).

### Export View ###

In contrast to a View on the table, all the markup for an export is generated in the View. To use the custom View just define the view attribute on the ExportTag and give the fully qualified path to the class which implements the View interface, or use the [Preferences](PreferencesTutorial.md) to alias it out.

```
 <ec:table
   items="presidents" 
   action="${pageContext.request.contextPath}/presidents.run" 
   >
   <ec:export 
     fileName="custom.file" 
     tooltip="Export Custom" 
     view="com.mycompany.view.MyCustomExportView"/>
   ...
 </ec:table>
```
