This is a custom implementation for displaying a dropdown list of page numbers:

```
public class CustomToolbarBuilder extends ToolbarBuilder {
    public CustomToolbarBuilder(HtmlBuilder html, TableModel model) {
        super(html, model);
    }

    public void pagesDisplayedDroplist() {
        HtmlBuilder html = getHtmlBuilder();
        TableModel model = getTableModel();
       
        int totalPages = BuilderUtils.getTotalPages(model);
        int currentPage = model.getLimit().getPage();
       
        StringBuffer onchange = new StringBuffer();
        onchange.append(new CustomTableActions(model).getPageAction());
        html.select().name("pages");
        html.onchange(onchange.toString());
        html.close();
        html.newline();
        html.tabs(4);
        for (int i = 1; i <= totalPages; i++) {
            html.option().value(String.valueOf(i));
            if (currentPage == i) {
                html.selected();
            }
            html.close();
            html.append(String.valueOf(i));
            html.optionEnd();
        }
        html.newline();
        html.tabs(4);
        html.selectEnd();
    }
} 
```

The page action code:

```
public class CustomTableActions extends TableActions {
    public CustomTableActions(TableModel model) {
        super(model);
    }
   
    public String getPageAction() {
        TableModel model = getTableModel();

        StringBuffer action = new StringBuffer("javascript:");
       
        action.append(getClearedExportTableIdParameters());

        String form = BuilderUtils.getForm(model);
        action.append("document.forms.").append(form).append(".");
        action.append(model.getTableHandler().prefixWithTableId()).append(TableConstants.PAGE);
        String page = "this.options[this.selectedIndex].value";
        action.append(".value=").append(page).append(";");

        action.append(getOnInvokeAction());
       
        return action.toString();
    }
} 
```