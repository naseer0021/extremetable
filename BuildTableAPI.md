The eXtremeTable has a full API that feels very much like building a table with the JSP tags. In fact the JSP tags are merely a front end for the eXtremeTable Java API. This allows you to create tables however you want.

```
  TableModel model = new TableModelImpl(context);

  Table table = model.getTableInstance();
  table.setItems(presidents);
  table.setAction("assembler.run");
  table.setTitle("Presidents");
  table.setShowTooltips(Boolean.FALSE);
  model.addTable(table);
      
  Row row = model.getRowInstance();
  row.setHighlightRow(Boolean.FALSE);
  model.addRow(row);

  Column columnName = model.getColumnInstance();
  columnName.setProperty("fullName");
  columnName.setIntercept((AssemblerInterceptor.class).getName());
  model.addColumn(columnName);

  Column columnNickName = model.getColumnInstance();
  columnNickName.setProperty("nickName");
  model.addColumn(columnNickName);
        
  Object view = model.assemble();
```

The only disadvantage of creating a table in Java code is you lose the nice EL expression language of the JSP page. So, what you need to do is create an Interceptor. You can read the [tutorial](InterceptorTutorial.md) for more information. However, this is a pretty minor disadvantage and the power and flexibility of creating a table with just Java code is very powerful.

