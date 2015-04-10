This is a way to export the current page being displayed without using the [Limit](Limit.md) feature (the feature, not the object). It implements a custom FilterRowsCallback that wraps the default functionality.

Ok, yes this is kind of a hack but I thought this was a good idea. Plus this can get us thinking about how to make this more generic, or get this incorporated into the table.


First implement the FilterRowsCallback by wrapping the ProcessRowsCallback and overriding the filterRows() method:

```
public class CustomExportCallback extends ProcessRowsCallback {
   public Collection filterRows(TableModel model, Collection rows) throws Exception {
     Collection data = super.filterRows(model, rows);
        
     Limit limit = model.getLimit();
     if (limit.isExported()) {
        int rowsDisplayed = model.getTableHandler().getTable().getRowsDisplayed();
            
        String currentRowsDisplayed =  
                             model.getRegistry().getParameter(model.getTableHandler().prefixWithTableId() 
                             + TableConstants.CURRENT_ROWS_DISPLAYED);
        if (StringUtils.isNotBlank(currentRowsDisplayed)) {
            rowsDisplayed = Integer.parseInt(currentRowsDisplayed);
        }
            
        if (rowsDisplayed > data.size()) {
            rowsDisplayed = data.size();
        }

        Collection results = new ArrayList();
        for (int i = 0; i < rowsDisplayed; i++) {
           Object bean = ((List) data).get(i);
           results.add(bean);
        }
        
        return results;
     }
        
     return data;
   }
}
```

Then just reference the callback in your Table:
```
filterRowsCallback="org.mycompany.callback.CustomExportCallback"
```

