First an abstract base class

Code:
```
public abstract class EnumCellFilter extends FilterDroplistCell {

    private EnumSet enums;

    protected EnumCellFilter(EnumSet enums) {
        this.enums = enums;
    }

    protected List getFilterDropList(TableModel model, Column column) {

        List<Option> droplist = new ArrayList<Option>();

        for (Object theEnum : enums) {
            droplist.add(new Option(theEnum.toString()));
        }

        return droplist;
    }

}
```

an enum

Code:
```
public enum StitchState {

    Waiting,
    Running,
    Failed,
    Success;

}
```

Now a class extending the base class. You don't have to use allOf here, you can pick and choose which enums to use.

Code:
```
public class StitchStateFilter extends EnumCellFilter {

    public StitchStateFilter() {
        super(EnumSet.allOf(StitchState.class));
    }
}
```

To set up the column

Code:
```
        Column columnState = model.getColumnInstance();
        columnState.setProperty("stitchState");
        columnState.setFilterCell("org.brainatlas.web.util.StitchStateFilter");
        model.addColumn(columnState); 
```