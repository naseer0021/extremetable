The Interceptor feature is used to change the attribute values at runtime. This makes it possible to change the behavior of the eXtremeTable based on the data. If you read about the extended tag attributes it is the same concept and method signature. As a rule of thumb you should use the extended tag attributes when you need to add attributes to the tag that are defined in the TLD and accessible in the JSP. Use the Interceptor when you only need to modify the attributes that are already defined.

You might need a little more technical background into how the eXtremeTable works to fully understand the feature. The first thing the eXtremeTable does is go through all the tags and creates the model beans (pojos). The beans are objects that have the same attributes as the tags but use real types instead of just strings. The beans are what the model uses and are what you modify with the Interceptor feature. All the interceptor interfaces have an add method. The add method is used to manipulate the attributes the first time the model bean is created. The row and column interceptor also have a modify method. The modify method will manipulate the attributes while the rows and columns are being processed.

### Interceptor Chart ###

The following lists the tags that contain an interceptor attribute and then the interface that needs to be implemented. The Bean column shows the bean that is created by the model.


| **Tag** | **Interface** | **Bean** |
|:--------|:--------------|:---------|
| TableTag | org.extremecomponents.table.interceptor.TableInterceptor | org.extremecomponents.table.bean.Table |
| RowTag | org.extremecomponents.table.interceptor.RowInterceptor | org.extremecomponents.table.bean.Row |
| ColumnTag | org.extremecomponents.table.interceptor.ColumnInterceptor | org.extremecomponents.table.bean.Column |
| ExportTag | org.extremecomponents.table.interceptor.ExportInterceptor | org.extremecomponents.table.bean.Export |


### Row Interceptor Example ###

A perfect example to demonstrate the Interceptor feature would be to highlight a row based on certain criteria, and will be the example that we will work through. This will be short and simple, as it should be. The concepts used in this example apply to every interceptor interface.

The first thing we need to do is implement the RowInterceptor interface. What you will notice is that there are two methods on the interface. They are addRowAttributes() and modifyRowAttributes(). The addRowAttributes is called while the Row bean is created. The modifyRowAttributes is called for every pass over the rows while loading the current page of the table.

```
 public class MarkerInterceptor implements RowInterceptor {
     public void addRowAttributes(TableModel tableModel, Row row) {
     } 
 
     public void modifyRowAttributes(TableModel model, Row row) {
         President president = (President) model.getCurrentRowBean();
         String career = president.getCareer();
         if (StringUtils.contains(career, "Soldier")) {
             row.setStyle("background-color:#fdffc0;");
         } else {
             row.setStyle("");
         }
     }
 }
```

In the Preferences you need to declare the alias for the row interceptor.

```
 row.interceptor.marker=org.extremesite.interceptor.MarkerInterceptor
```

The RowTag will then use the interceptor attribute to use the MarkerInterceptor.

```
 <ec:row interceptor="marker">
```

_See the Preferences tutorial if the syntax for the Preferences and RowTag (above) does not make sense._







