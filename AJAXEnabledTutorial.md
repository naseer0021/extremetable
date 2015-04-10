Using AJAX with the eXtremeTable is a very simple and natural extension to the existing functionality. Probably the best thing about the AJAX integration is there is no integration at all. You are free to use whatever AJAX toolkit you want. All you need to do is tell the eXtremeTable what javascript to use when a table action is invoked. The table actions meaning the filtering, sorting, paging, rows displayed and exporting.

For my example I will be using the ultra cool [DWR](http://getahead.ltd.uk/dwr/) toolkit. As will be demonstrated, the glue code required by DWR is very minimal, which leaves us to just worry about constructing the table.

### Assembler Example ###
For this example you will need to setup the DWR toolkit, create the POJO to build the table, and make up the JSP that contains the eXtremeTable.

### Setup DWR ###
The first thing to do is [download](http://getahead.ltd.uk/dwr/download) the DWR toolkit. You should read the instructions on the site, but the following is what I needed to do to get things working:

**Place the dwr-1.1.jar into the WEB-INF/lib directory** Create a dwr.xml file and place it in the WEB-INF directory
```
<!DOCTYPE dwr PUBLIC
    "-//GetAhead Limited//DTD Direct Web Remoting 1.0//EN"
    "http://www.getahead.ltd.uk/dwr/dwr10.dtd">

<dwr>
  <allow>
    <create creator="new" javascript="Assembler">
      <param name="class" value="org.extremesite.controller.Assembler"/>
      <include method="getTable"/>
    </create>
  </allow>
  <signatures>
    <![CDATA[
       import java.util.Map;
       import java.util.List;
       import org.extremesite.controller.Assembler;
       import javax.servlet.http.HttpServletRequest;
       Assembler.getTable(Map<String, List> parameterMap, HttpServletRequest request);
       ]]>
  </signatures>
</dwr>
```

In a nutshell the create tag allows you to reference the POJO that builds the table, along with the method to invoke. The signature tag declares what the actual types are for the method that is invoked. In this example the Assembler class getTable method will be called by passing in a Map, with the form parameters, along with the HttpServletRequest.

  * Reference the DWR servlet in the WEB-INF/web.xml file
```
  <servlet>
    <servlet-name>dwr-invoker</servlet-name>
    <display-name>DWR Servlet</display-name>
    <servlet-class>uk.ltd.getahead.dwr.DWRServlet</servlet-class>
    <init-param>
       <param-name>debug</param-name>
       <param-value>true</param-value>
    </init-param>
  </servlet>
  <servlet-mapping>
    <servlet-name>dwr-invoker</servlet-name>
      <url-pattern>/dwr/*</url-pattern>
  </servlet-mapping>
```

This is setting up the DWR servlet that is used to call your POJO. There is nothing more you should have to know about the servlet other than how to set it up here.

### Create POJO ###
DWR works with POJOs. This works great for our needs because the eXtremeTable has a full API that feels very much like building a table with the JSP tags. In fact the JSP tags are merely a front end for the eXtremeTable Java API. First I will show the method that builds the table.

```
public class Assembler {
  private Object build(TableModel model, Collection presidents) throws Exception {
    Table table = model.getTableInstance();
    table.setTableId("assembler");
    table.setItems(presidents);
    table.setAction(model.getContext().getContextPath() + "/assembler.run");
    table.setTitle("Presidents");
    table.setOnInvokeAction("buildTable('assembler')");
    model.addTable(table);
    
    Export export = model.getExportInstance();
    export.setView(TableConstants.VIEW_XLS);
    export.setViewResolver(TableConstants.VIEW_XLS);
    export.setImageName(TableConstants.VIEW_XLS);
    export.setText(TableConstants.VIEW_XLS);
    export.setFileName("output.xls");
    model.addExport(export);

    Row row = model.getRowInstance();
    row.setHighlightRow(Boolean.FALSE);
    model.addRow(row);

    Column columnName = model.getColumnInstance();
    columnName.setProperty("fullName");
    columnName.setIntercept((AssemblerIntercept.class).getName());
    model.addColumn(columnName);

    Column columnNickName = model.getColumnInstance();
    columnNickName.setProperty("nickName");
    model.addColumn(columnNickName);

    Column columnTerm = model.getColumnInstance();
    columnTerm.setProperty("term");
    model.addColumn(columnTerm);

    Column columnBorn = model.getColumnInstance();
    columnBorn.setProperty("born");
    columnBorn.setCell(TableConstants.DATE);
    model.addColumn(columnBorn);

    Column columnDied = model.getColumnInstance();
    columnDied.setProperty("died");
    columnDied.setCell(TableConstants.DATE);
    model.addColumn(columnDied);

    Column columnCareer = model.getColumnInstance();
    columnCareer.setProperty("career");
    model.addColumn(columnCareer);

    return model.assemble();
  }
}
```

Most of this code should be very self explanatory. You will see how to build a TableModel in a second, but first you will notice that it is the only object you will have to interact with to build the table. The first step to building a table is use the TableModel to create the Table, Row, Column and Export. Once you create one of the model objects it needs to be added to the model. Until you actually add it the TableModel, the model will not use it. Once you have everything constructed then call model.assemble(); to build the table.

Probably the most interesting call is table.setOnInvokeAction("buildTable('assembler')");. This is the javascript method that will be called when you invoke any of the table actions (paging, filtering, sorting, etc..). If the table onInvokeAction is left blank the default javascript method is to submit the form that wraps the eXtremeTable, just like you would expect.

The other method of the Assembler class is getTable().

```
public class Assembler {
  public String getTable(Map parameterMap, HttpServletRequest request) {
    WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());

    PresidentsDao presidentsDao = (PresidentsDao) webApplicationContext.getBean("presidentsDao");
    Collection presidents = presidentsDao.getPresidents();

    Context context = null;
    if (parameterMap == null) {
      context = new HttpServletRequestContext(request);
    } else {
      context = new HttpServletRequestContext(request, parameterMap);
    }

    TableModel model = new TableModelImpl(context);
    try {
      return build(model, presidents).toString();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "";
  }
}
```

This method is a little busy, but it is doing double duty. It needs to be called the first time by the Controller (if using Spring). ''If you use Struts this is the equivalent of the Action class.'' Then, when using AJAX, this method also needs to be called, but this time passing in a custom parameterMap. The parameterMap will contain all the parameters for the eXtremeTable using AJAX. This should become more clear once you see the Controller and the JSP.

### Controller ###

```
public class AssemblerController extends AbstractController {
  public String successView;

  public void setSuccessView(String successView) {
    this.successView = successView;
  }

  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
    ModelAndView mv = new ModelAndView(successView);
    Assembler assembler = new Assembler();
    Object viewData = assembler.getTable(null, request);
    request.setAttribute("assembler", viewData);
    return mv;
  }
}
```

The controller is called the first time to initially build the table. It calls the POJO and then passes the table to the request. Notice how null is sent in for the parameterMap. This is because the first time the table is built there are no table actions the table needs to be aware of.

### JSP ###
Start constructing the JSP by importing the proper javascript files. The only javascript file you need to include in your project is the extremecomponents.js. The other declarations are used by DWR.

```
  <script type="text/javascript" src="<c:url value="/dwr/interface/Assembler.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/dwr/util.js"/>"></script>
  <script type="text/javascript" src="<c:url value="/js/extremecomponents.js"/>"></script>
```

Then put in the hook to display the table. The div is used to call the innerHtml and stream the new markup.

```
  <div id="tableDiv">
    <c:out value="${assembler}" escapeXml="false"/> 
  </div>
```

Lastly, declare the javascript to call the DWR hooks.

```
<script type="text/javascript">
  function buildTable(form) {
    var parameterMap = getParameterMap(form);   
    Assembler.getTable(parameterMap, showTable);
  }

  function showTable(table) {
    document.getElementById('tableDiv').innerHTML=table;
  }
</script>
```

The getParameterMap() method is included in the extremecomponents.js file and will grab all the form parameters. You need to pass in a reference to the form id that wraps the eXtremeTable. Remember that the eXtremeTable is essentially a form component and the tableid is used as the form id. By default the tableId is ec, but you usually want to set a tableId so that code is easier to follow for people reading your code. With the parameterMap you will then call the POJO getTable method. The showTable is a callback that tells DWR what to call when it returns from the getTable() method.

### Conclusion ###
This tutorial described the steps needed to get the eXtremeTable working with AJAX. One of the main things to keep in mind is there is no actual integration with AJAX. You simply tell the eXtremeTable what javascript to call when one of the table actions is invoked. The other thing to get from this example is that the DWR toolkit works with POJOs and can be easily used with any framework.



