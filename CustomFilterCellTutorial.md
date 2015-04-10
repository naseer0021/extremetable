The column filterCell attribute controls how the filters render. It is similar to the cell attribute and also implements the Cell interface. The two filter cells defined right now are the default and the droplist. The default is an input element and the droplist is a select element. However, if you need to do something custom you can plug in your own implementation.

Recently, I was asked if it was possible to have a filter cell show a subset of what was selected in a different filter. The answer, of course, is yes and is what I will demonstrate here. Most of the time a custom cell will be pretty easy to create, and this example will reflect that. In the example that follows the last name column will be a subset of what was selected in the first name column. If the first name was not selected then all the values will be shown.

### Custom Droplist FilterCell Example ###

Usually you would just implement the Cell interface for a filter cell. However, because the filter cell we will be creating is a droplist there is quite a bit of functionality to be gained by extending the FilterDroplistCell, which is one of the cells that is included in the distribution.

The only method from the FilterDroplistCell to override is getFilterDropList(). This is the class in its entirety:

```
public class FilteredDroplistCell extends FilterDroplistCell {
    private static Log logger = LogFactory.getLog(FilterDroplistCell.class);
    
    protected List getFilterDropList(TableModel model, Column column) {
        List droplist = new ArrayList();
        
        String firstNameFilter = model.getLimit().getFilterSet().getValue("firstName");

        Collection beans = model.getCollectionOfBeans();
        for (Iterator iter = beans.iterator(); iter.hasNext();) {
            Object bean = iter.next();
            try {
                String firstName = BeanUtils.getProperty(bean, "firstName");
                if (StringUtils.isNotBlank(firstNameFilter) && !firstName.equals(firstNameFilter)) {
                    continue;
                }

                String lastName = BeanUtils.getProperty(bean, column.getProperty());
                if (lastName != null && !droplist.contains(lastName)) {
                    droplist.add(lastName);
                }
            } catch (Exception e) {
                logger.debug("Problems getting the droplist.", e);
            }
        }

        Collections.sort(droplist);

        return droplist;
    }
}
```

If you were to compare the source of this class with the parent class the difference would be pretty minor.

The first thing you would notice is we need to find out if the first name was filtered.

```
String firstNameFilter = model.getLimit().getFilterSet().getValue("firstName");
```

Then we need to figure out if the first name value of the current bean is the same as the first name filter value. If it is not then do not include the current last name value in the last name droplist.

```
 String firstName = BeanUtils.getProperty(bean, "firstName");
 if (StringUtils.isNotBlank(firstNameFilter) && !firstName.equals(firstNameFilter)) {
     continue;
 }
```

If the last name should be included in the droplist then just make sure it has not already been included. If not then add it.

```
 String lastName = BeanUtils.getProperty(bean, column.getProperty());
 if (lastName != null && !droplist.contains(lastName)) {
     droplist.add(lastName);
 }
```

To use the Cell you should declare an alias In the Preferences. ''You could skip this and give the fully qualified class name to the Cell in the JSP, but the Preferences is much cleaner.''

```
 column.filterCell.filteredDroplist=org.extremesite.cell.FilteredDroplistCell
```

The ColumnTag will then use the filterCell attribute to use the FilteredDroplistCell.

```
 <ec:column property="lastName" filterCell="filteredDroplist"/>
```

_See the Preferences tutorial if the syntax for the Preferences and ColumnTag (above) does not make sense._


