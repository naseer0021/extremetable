The FilterRowsCallback is used to filter the Collection of Beans passed to the eXtremeTable. The default implemenation of the FilterRowsCallback is to take the Collection of Beans and then filter it by implementing the jakarta Predicate interface. However, if you need to do something custom you can plug in your own implementation.

First the disclaimer. This example contains quite a bit of cut and paste from the original code base (although not much code). After the initial final release the filtering of the values will be improved to be much more reusable and easier to implement, probably along the same lines as a custom cell. However, I have been asked to demonstrate how to implement custom filtering with the current code base and would like to demonstrate the current capabiltiy. There are very clear hooks to do this and it is easy to do, it just would be nice to be able to get to a more fine grained level.

The example will demonstrate how to tweak the code to only include an exact match for the filters. The current implemenation is to do a fuzzy match by using the StringUtils.contains() method. The example will change that to use the StringUtils.equals() method. If you need to anything more custom just tweak the code to your liking.

### Custom FilterRowsCallback Example ###

The first thing you need to do is create a custom class that implements the Predicate interface. The Predicate requires us to implement the evaluate() method and determines whether or not to include the current bean in the Collection of Beans. Because you only need to tweak the existing functionality start by grabbing the source code for the FilterPredicate, in the callback package, included in the distribution and copy it into your project. Then change the StringUtils.contains() method to the StringUtils.equals() method as shown below:

```
public final class ExactMatchFilterPredicate implements Predicate {
    private boolean isSearchMatch(String value, String search) {
        
        ...

        else if (StringUtils.equals(value, search)) {
            return true;
        }

        ...

    }
}
```

Then we will need to implement the FilterRowsCallback interface to work with the Predicate. Again, go to the distribution and grab the source code for the ProcessRowsCallback in the callback package and copy it into your project. Be sure to only implement the FilterRowsCallback and change the Predicate to reference the custom ExactMatchFilterPredicate we just created (above).

```
public class ExactMatchFilterRows implements FilterRowsCallback {
    public Collection filterRows(TableModel model, Collection rows) throws Exception {
         
        ...
        
        if (filtered) {
            Collection collection = new ArrayList();
            Predicate filterPredicate = new ExactMatchFilterPredicate(model);
            CollectionUtils.select(rows, filterPredicate, collection);

            return collection;
        }

        ...

    }
}
```

To use the FilterRowsCallback you should declare an alias In the Preferences. ''You could skip this and give the fully qualified class name to the FilterRowsCallback in the JSP, but the Preferences is much cleaner.''

```
 table.filterRowsCallback.exactMatch=org.extremesite.callback.ExactMatchFilterRows
```

The TableTag will then use the filterRowsCallback attribute to use the ExactMatchFilterRows.

```
 <ec:table filterRowsCallback="exactMatch"/>
```

_See the Preferences tutorial if the syntax for the Preferences and TableTag (above) does not make sense._