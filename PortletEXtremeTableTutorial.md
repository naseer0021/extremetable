Using eXtremeTable in a JSR-168 compliant portlet works well, but does require a little more work.  Here are two examples to get you started; one utilizes the regular Portlet API, and the other is an adaptation of the AJAX enabled table example.

### Create the Model ###
First, we have to have a data model.  Here is a simple DAO:

```
package portlet.extremetable;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

public class TableDAO {

    private final Collection items;

    private static final String[] firstNames = new String[] {
            "Ben", "John", "Jeff", "Tim", "Jane", "Linda", "Patty", "Brenda"
    };
    
    private static final String[] lastNames = new String[] {
            "Smith", "Johnson", "Lee", "Jones", "Miller", "Carpenter", "Baker"
    };
    
    public TableDAO() {
        items = new LinkedList();
        for (int i = 0; i < 200; i++) {
            String first = firstNames[random(0, firstNames.length)];
            String last = lastNames[random(0, lastNames.length)];
            StringBuffer phoneBuffer = new StringBuffer();
            for (int j = 0; j < 10; j++) {
                phoneBuffer.append(random(1, 9));
                if (j == 2 || j == 5)
                    phoneBuffer.append('-');
            }
            int age = random(20, 60);
            Calendar cal = new GregorianCalendar(random(2000, 2006), random(0, 11), random(1, 27));
            Date startDate = cal.getTime();
            TableBean bean = new TableBean(first, last, phoneBuffer.toString(), age, startDate);
            items.add(bean);
        }
    }

    public Collection getItems() {
        return items;
    }
    
    private static int random(int lower, int upper) {
        return ((int) (Math.random() * (upper - lower))) + lower;
    }
}
```

Here is the bean class that we will use:

```
package portlet.extremetable;

import java.util.Date;

public class TableBean {

    private String firstName;

    private String LastName;

    private String phoneNumber;

    private int age;

    private Date startOfEmployment;

    public TableBean() {
        super();
    }

    public TableBean(String firstName, String lastName, String phoneNumber,
            int age, Date startOfEmployment) {
        super();
        this.firstName = firstName;
        this.LastName = lastName;
        this.phoneNumber = phoneNumber;
        this.age = age;
        this.startOfEmployment = startOfEmployment;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.LastName;
    }

    public void setLastName(String lastName) {
        this.LastName = lastName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getStartOfEmployment() {
        return this.startOfEmployment;
    }

    public void setStartOfEmployment(Date startOfEmployment) {
        this.startOfEmployment = startOfEmployment;
    }
}
```

### Implement Context to Wrap PortletRequest ###
In order to properly use the Java API for building the eXtremeTable, we will need an implementation of org.extremecomponents.table.context.Context that wraps PortletRequest:

```
package portlet.extremetable;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import org.extremecomponents.table.context.Context;

public class PortletRequestContext implements Context {

    private final PortletRequest req;
    
    private final PortletContext ctx;
    
    private final int sessionScopeToUse;
    
    private final Map parameters;

    /**
     * Creates a PortletRequestContext based on the given PortletRequest.
     * This method is equivalent to calling PortletRequestContext(req, PortletSession.PORTLET_SCOPE)
     * @param req
     */
    public PortletRequestContext(PortletRequest req) {
        this(req, PortletSession.PORTLET_SCOPE, req.getParameterMap());
    }
    
    public PortletRequestContext(PortletRequest req, Map parameters) {
        this(req, PortletSession.PORTLET_SCOPE, parameters);
    }
    
    /**
     * Creates a PortletRequestContext based on the given PortletRequest.
     * <p>Since a portlet session defines two scopes (PORTLET_SCOPE and APPLICATION_SCOPE),
     * this method allows you to specify in which scope session attributes should be saved.</p>
     * @param req
     * @param sessionScopeToUse This should be one of the two values of PortletSession.APPLICATION_SCOPE or PortletSession.PORTLET_SCOPE
     */
    public PortletRequestContext(PortletRequest req, int sessionScopeToUse) {
        this (req, sessionScopeToUse, req.getParameterMap());
    }
    
    public PortletRequestContext(PortletRequest req, int sessionScopeToUse, Map parameters) {
        super();
        this.req = req;
        this.ctx = req.getPortletSession().getPortletContext();
        this.sessionScopeToUse = sessionScopeToUse;
        this.parameters = Collections.unmodifiableMap(parameters);
    }

    public Object getApplicationInitParameter(String arg0) {
        return ctx.getInitParameter(arg0);
    }

    public Object getApplicationAttribute(String arg0) {
        return ctx.getAttribute(arg0);
    }

    public void setApplicationAttribute(String arg0, Object arg1) {
        ctx.setAttribute(arg0, arg1);
    }

    public void removeApplicationAttribute(String arg0) {
        ctx.removeAttribute(arg0);
    }

    public Object getPageAttribute(String arg0) {
        return req.getAttribute(arg0);
    }

    public void setPageAttribute(String arg0, Object arg1) {
        req.setAttribute(arg0, arg1);
    }

    public void removePageAttribute(String arg0) {
        req.removeAttribute(arg0);
    }

    public String getParameter(String arg0) {
        Object values = parameters.get(arg0);
        if (values == null) {
            return null;
        } else if (values instanceof String[]) {
            if (((String[]) values).length == 0) {
                return null;
            } else {
                return ((String[]) values)[0];
            }
        } else if (values instanceof List) {
            if (((List) values).size() == 0) {
                return null;
            } else {
                return (String) ((List) values).get(0);
            }
        } else {
            throw new IllegalStateException();
        }
    }

    public Map getParameterMap() {
        return parameters;
    }

    public Object getRequestAttribute(String arg0) {
        return req.getAttribute(arg0);
    }

    public void setRequestAttribute(String arg0, Object arg1) {
        req.setAttribute(arg0, arg1);
    }

    public void removeRequestAttribute(String arg0) {
        req.removeAttribute(arg0);
    }

    public Object getSessionAttribute(String arg0) {
        return req.getPortletSession().getAttribute(arg0, sessionScopeToUse);
    }

    public void setSessionAttribute(String arg0, Object arg1) {
        req.getPortletSession().setAttribute(arg0, arg1, sessionScopeToUse);
    }

    public void removeSessionAttribute(String arg0) {
        req.getPortletSession().removeAttribute(arg0, sessionScopeToUse);
    }

    public Writer getWriter() {
        return new StringWriter();
    }

    public Locale getLocale() {
        return req.getLocale();
    }

    public String getContextPath() {
        return req.getContextPath();
    }

    public Object getContextObject() {
        return ctx;
    }

}
```

### Create The Table ###
Next we will need to create the table.  You can do this with either the JSP tags or using the Java API.  If you are planning on using exports, it important that you make sure that the code used to build the table can be accessed from either a servlet or a portlet.  This means creating a separate class if you are using the Java API, or a separate JSP and including it with 

&lt;jsp:include&gt;

 if you are using the tags.  If you are using the 

&lt;jsp:include&gt;

 method, be sure not to use any of the custom portlet tags in it.  It is somewhat simpler to modularize this step by using a Java class, so we will use that for our demonstration (also, this is the method used in the AJAX tutorial).

**Because we are going to use the same class for building both the non-AJAX and AJAX versions of the table, we should encapsulate the differences in an object:**

```
package portlet.extremetable;

public class TableConfig {

    private final String tableId;
    
    private final String tableAction;
    
    private final String invocationAction;

    public TableConfig(String tableId, String tableAction, String invocationAction) {
        super();
        this.tableAction = tableAction;
        this.tableId = tableId;
        this.invocationAction = invocationAction;
    }
    
    public String getInvocationAction() {
        return invocationAction;
    }
    
    public String getTableId() {
        return tableId;
    }
    
    public String getTableAction() {
        return tableAction;
    }
}
```

**Next, the class to actually build the table:
```
package portlet.extremetable;

import java.util.Collection;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.extremecomponents.table.bean.Column;
import org.extremecomponents.table.bean.Export;
import org.extremecomponents.table.bean.Row;
import org.extremecomponents.table.bean.Table;
import org.extremecomponents.table.context.Context;
import org.extremecomponents.table.context.HttpServletRequestContext;
import org.extremecomponents.table.core.TableConstants;
import org.extremecomponents.table.core.TableModel;
import org.extremecomponents.table.core.TableModelImpl;

public class Assembler {
        
    public String getPortletTable(PortletRequest request, TableConfig config) {
        Context ctx = new PortletRequestContext((PortletRequest) request);
        return getTableString(ctx, config);
    }
    
    private String getTableString(Context ctx, TableConfig config) {
        TableDAO dao = (TableDAO) ctx.getApplicationAttribute("tableDAO");
        Collection items = dao.getItems();
        TableModel model = new TableModelImpl(ctx);
        try {
            String result = build(model, config, items).toString();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private Object build(TableModel model, TableConfig config, Collection items) throws Exception {

        Table table = model.getTableInstance();
        table.setTableId(config.getTableId());
        table.setItems(items);
        table.setAction(config.getTableAction());
        table.setTitle("Employees");
        table.setOnInvokeAction(config.getInvocationAction());
        table.setImagePath(model.getContext().getContextPath() + "/images/*.gif");
        model.addTable(table);

        Export export = model.getExportInstance();
        export.setView(TableConstants.VIEW_XLS);
        export.setViewResolver(TableConstants.VIEW_XLS);
        export.setImageName(TableConstants.VIEW_XLS);
        export.setText(TableConstants.VIEW_XLS);
        export.setFileName("output.xls");
        model.addExport(export);
        
        export = model.getExportInstance();
        export.setView(TableConstants.VIEW_PDF);
        export.setViewResolver(TableConstants.VIEW_PDF);
        export.setImageName(TableConstants.VIEW_PDF);
        export.setText(TableConstants.VIEW_PDF);
        export.setFileName("output.pdf");
        model.addExport(export);
        
        export = model.getExportInstance();
        export.setView(TableConstants.VIEW_CSV);
        export.setViewResolver(TableConstants.VIEW_CSV);
        export.setImageName(TableConstants.VIEW_CSV);
        export.setText(TableConstants.VIEW_CSV);
        export.setFileName("output.csv");
        model.addExport(export);

        Row row = model.getRowInstance();
        row.setHighlightRow(Boolean.FALSE);
        model.addRow(row);

        Column columnFirstName = model.getColumnInstance();
        columnFirstName.setProperty("firstName");
        model.addColumn(columnFirstName);

        Column columnLastName = model.getColumnInstance();
        columnLastName.setProperty("lastName");
        model.addColumn(columnLastName);

        Column columnAge = model.getColumnInstance();
        columnAge.setProperty("age");
        model.addColumn(columnAge);

        Column columnPhoneNumber = model.getColumnInstance();
        columnPhoneNumber.setProperty("phoneNumber");
        model.addColumn(columnPhoneNumber);
        
        Column columnStartOfEmployment = model.getColumnInstance();
        columnStartOfEmployment.setProperty("startOfEmployment");
        columnStartOfEmployment.setCell(TableConstants.DATE);
        columnStartOfEmployment.setFormat("MM/dd/yyyy");
        model.addColumn(columnStartOfEmployment);

        model.addParameter(getClass().getName() + "_tableId", config.getTableId());
        
        return model.assemble();
    }
}
```**

  * Notice that the getTableString() method retrieves the DAO from a context attribute.  Because the Portlet specification states specifically that the ServletContext and the PortletContext share attributes, this is an acceptable way for accessing the data model.  Here is ServletContextListener for adding the DAO as a context attribute (sorry about all the classes; I tried to do this using good MVC and it is really two tutorials in one):

```
package portlet.extremetable;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextListener implements ServletContextListener {
    public ContextListener() {
        super();
    }

    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("tableDAO", new TableDAO());
    }

    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("tableDAO");
    }
}
```

### Create the Portlet Controller ###
This step is a little trickier than using servlets, because the portlet API splits each request into two phases.  The eXtremeTable is really just a form, so any operations such as sorting, filtering, etc. all get sent to the server as a form request.  In terms of portlets, this should trigger the action phase.