### Introduction ###

The eXtremeTable comes with the ability to export the table in various formats. When exporting you will get the full set of results after filtering and sorting. The pagination will not effect the results returned. In other words, if the table content displays on multiple pages all the results from all the pages will be exported. The exports included are Microsoft Excel (OpenOffice Calc), PDF and CSV.

To export to Microsoft Excel (OpenOffice Calc) use the ExportXlsTag.

```
 <ec:table
   items="presidents" 
   action="${pageContext.request.contextPath}/presidents.run" 
   />
   <ec:exportXls
     fileName="presidents.xls" 
     tooltip="Export Excel"/>
   ...
 </ec:table>
```

By default the XlsView uses UTF-16 character encoding. If you need to use unicode then use the encoding attribute. The acceptable values are UTF and UNICODE.

To export to PDF use the ExportPdfTag. All you need to define is the fileName attribute and a few other styling attributes.

```
 <ec:table
   items="presidents"
   action="${pageContext.request.contextPath}/presidents.run" 
   />
   <ec:exportPdf 
     fileName="presidents.pdf" 
     tooltip="Export PDF" 
     headerColor="blue" 
     headerBackgroundColor="red" 
     headerTitle="Presidents"/>
   ...
 </ec:table>
```

To export to CSV use the ExportCsvTag. When using the CSV export the default delimiter will be a comma. If you want to delimit by something else then use the delimiter attribute. In this example I used the pipe delimiter.

```
 <ec:table
   items="presidents" 
   action="${pageContext.request.contextPath}/presidents.run"    />
   <ec:exportCsv 
     fileName="presidents.txt" 
     tooltip="Export CSV" 
     delimiter="|"/>
   ...
 </ec:table>
```

If you would like to make up some other export you can do so by using the view attribute. In the eXtremeTable views implement the View interface and are plugable. See the [[Views|View]] chapter for more information.

### Interceptor ###
The Interceptor feature is used to change the attribute values at runtime. This makes it possible to change the behavior of the export based on the data. You can read more about it at the [interceptor tutorial](InterceptorTutorial.md).

### Extended Attributes ###

The way most tags are developed is you have a fixed set of attributes on the tag. Those attributes are then used so that a fixed set of functionality can be invoked. The eXtremeTable, however, has a much more flexible approach. The eXtremeTable gives you a way to add your own tag attributes so that you can do more customized work. Furthermore, there are clear hooks in the eXtremeTable that allow you to retrieve those custom tag attributes and then do something with them. Typically this is going to be a custom View.

The hook to include the attributes into the eXtremeTable is via the addExportAttributes() method.

```
public void addExportAttributes(TableModel model, Export export) {
}
```

The ExportTag calls this method in case the method is overridden. That means all you need to do is extend the ExportTag, override the addExportAttributes() method, and then add your attributes to the Export object.

Here is what the ExportCsvTag looks like:

```
public class ExportCsvTag extends ExportTag {
  private String delimiter;
  
  public String getDelimiter() {
        return delimiter;
  }

  public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
  }
  
  public void addExportAttributes(TableModel model, Export export) {
        export.addAttribute(CsvView.DELIMITER, TagUtils.evaluateExpressionAsString("delimiter", delimiter, this, pageContext));
    }
}
```

Now that you added the attributes needed you can retrieve them right from the Export. Here is part of the CsvView implementation:

```
 public class CsvView implements View {
   public void body(TableModel model, Column column) {
     Export export = model.getExportHandler().getCurrentExport();
     String delimiter = export.getAttributeAsString(DELIMITER);
   }
 }
```

Another thing I wanted to make sure you understand is you can make your own tag, with your own TLD file. You do not need to modify the extremecomponents.tld file. Furthermore you use your custom tag just like you would any tag in eXtremeTable, except that you reference your tag. For instance if your tag reference is mycompany and the tag is called customExport you could do something like this:

```
 <ec:table 
     items="presidents" 
     action="${pageContext.request.contextPath}/public/demo/presidents.jsp" 
     title="Presidents"
     >
   <mycompany:customExport fileName="presidents.txt" delimiter="|"/>
   ...
 </ec:table>
```



