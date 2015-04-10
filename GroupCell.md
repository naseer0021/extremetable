Until the eXtremeTable gets grouping capability this can serve as a simple way to group one column.

```
public class GroupCell extends DisplayCell {
    private static final String LAST_VALUE = "lastValue";

    protected String getCellValue(TableModel model, Column column) {
        String value = column.getValueAsString();
        String lastValue = column.getAttributeAsString(LAST_VALUE);

        if (value.equals(lastValue)) {
            value = "";
        }

        column.addAttribute(LAST_VALUE, column.getValueAsString());
       
        return value;
    }
}
```