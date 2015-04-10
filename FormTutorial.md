The eXtremeTable is essentially a form component. By that I mean the table is wrapped in a form, and all the functionality is handled using form elements. If you want to include any custom form elements in the body of the table, or want to embed the eXtremeTable in another form then you will need to use the TableTag form attribute and reference an enclosing form.

To demonstrate the form feature the work will be split into a JSP, Cell, and Controller.
### JSP ###

This is the complete code listing for the checkbox example. The main thing to point out is how the TableTag form attribute called presForm is referencing the form element called presForm.

Also pay attention to the TableTag autoIncludeParameters. By default the eXtremeTable will keep track of all the parameters passed to the JSP while sorting, filtering and paging. As helpful as this feature is it effectively duplicates the form elements while sorting, filtering and paging when used inside another form. Setting the autoIncludeParameters to false fixes this.

The form id attribute is used here because it is the xhtml standard, but you could also use the form name attribute.

```
<form id="presForm" action="<c:url value="selectedPresidentsListedController.run"/>" method="post">

 Enter your name: 
 <input 
  type="text" 
  name="userName" 
  style="font-family:verdana,arial,helvetica,sans-serif;font-size:11px;"
  value="<c:out value="${param.userName}"/>"
  /> 

  <ec:table 
    items="presidents"
    action="${pageContext.request.contextPath}/selectedPresidentsController.run"  
    view="compact"
    imagePath="${pageContext.request.contextPath}/images/table/compact/*.gif"
    rowsDisplayed="8"
    autoIncludeParameters="false"
    form="presForm"
    >
    <ec:exportPdf 
        fileName="output.pdf" 
        tooltip="Export PDF" 
        headerColor="black" 
        headerBackgroundColor="#b6c2da" 
        headerTitle="Presidents"
        />
    <ec:row>
      <ec:column 
          alias="checkbox"
          title=" " 
          width="5px" 
          filterable="false" 
          sortable="false" 
          viewsAllowed="compact"
          cell="selectedPresident"
          />
      <ec:column property="fullName" title="Name"/>
      <ec:column property="nickName" />
      <ec:column property="term" />
    </ec:row>
  </ec:table>
 
  <input
      type="button"
      name="sel"
      class="button"
      value="List Selected Presidents"
      onclick="document.forms.presForm.submit();"
      />
 
    <script type="text/javascript">
    function setPresidentState(chkbx) {
     //make sure that always know the state of the checkbox
     if (chkbx.checked) {
      eval('document.forms.presForm.chkbx_' + chkbx.name).value='SELECTED';
     } else {
      eval('document.forms.presForm.chkbx_' + chkbx.name).value='UNSELECTED';
     }
    }
    </script> 

</form>
```

#### Technical Explanation of Form Feature ####

Knowing that the TableTag form attribute is referencing the enclosing form is really all you have to know to use the feature. However, it may be worth going into more detail as to what is technically happening under the covers to understand the feature more.

If you do not specify the form attribute the eXtremeTable will wrap a form around the table automatically. All the table actions such as sorting, filtering, and paging will set the values on some hidden input elements and then submit the form to the action defined by the TableTag action attribute. This works great until you want to put your own form elements into the body of the table, or want to place the table inside another form.

By specifiying the TableTag form attribute the eXtremeTable will now reference an enclosing form. The table actions such as sorting, filtering, and paging will still set the values on some hidden input elements, but now the action attribute of the enclosing form will be modifed to the TableTag action. This is very important because the eXtremeTable can get the Collection from one controller while sorting, filtering, and paging, but then submit the form to some other controller to process the form do something with the user input. However, all this happens transparently while you are coding the table. Simply set the TableTag action just how you currently do and then set the enclosing form to where you want the form to be submitted.

#### Checkbox ####

The first column of this example is a checkbox. Because the column does not reference a bean property (in the Collection of Beans) the alias attribute is used to uniquely identify the column. You could use the property attribute but the alias attribute makes the column more clear on how the column is to be used. The other use of the alias attribute is to uniquely define the column when the same property is used for more than one column.

#### Custom Cell ####

You might be wondering how the custom cell is referenced by the name selectedPresident (cell="selectedPresident"). This is a more powerful use of the preferences feature in the eXtremeTable. All that needs to be done is place a property in the extremecomponents.properties file. See the Preferences chapter of the documentation for more information.

```
column.cell.selectedPresident=org.extremesite.cell.SelectedPresidentCell
```

The selectedPresident part of the column.cell.selectedPresident is how you reference the cell by a named value.

Of course it is still perfectly legal to just put the fully qualified class name as the cell attribute. It is also nice for testing.

```
 <ec:column 
       alias="checkbox"
       title=" " 
       width="5px" 
       filterable="false" 
       sortable="false" 
       viewsAllowed="compact"
       cell="org.extremesite.cell.SelectedPresidentCell"
       />
```

What is nice about putting the reference in the properties file is it is possible to define the cell in one place and then reference it by name in the corresponding JSP files. If the class name or package changes you only have to modify one file. Plus you get a certain degree of abstraction from the cell by referring to it by name.

#### JavaScript ####

The javascript setPresidentState() is used by the custom cell to set a hidden input element on each checkbox element to update whether or not the checkbox is checked or not. The reason to set a hidden input element is to get around the browser behavior of not submitting checkbox elements that are not checked. By setting a hidden input element that represents the checkbox element the controller will always know whether a checkbox has been checked or unchecked from a previous post.

### Cell ###

The custom cell is used to create a checkbox. In addition it creates a hidden element that represents the checkbox element for reasons described in the JSP section. The Collection of presidents that are selected is placed into session while the user is sorting, filtering, and paging.

The getExportDisplay() method does not return a value because the Cell is only used for the html display as defined by the ColumnTag viewsAllowed attribute.

```
public class SelectedPresidentCell implements Cell {
    public String getExportDisplay(TableModel model, Column column) {
        return null;
    }

    public String getHtmlDisplay(TableModel model, Column column) {
        ColumnBuilder columnBuilder = new ColumnBuilder(column);
        
        columnBuilder.tdStart();
        
        try {
            Object bean = model.getCurrentRowBean();
            String presidentId = BeanUtils.getProperty(bean, "presidentId");
            
            Collection selectedPresidentsIds = (Collection)model.getContext().getSessionAttribute(SelectedPresidentsConstants.SELECTED_PRESIDENTS);
            if (selectedPresidentsIds != null && selectedPresidentsIds.contains(presidentId)) {
                columnBuilder.getHtmlBuilder().input("hidden").name("chkbx_" + presidentId).value(SelectedPresidentsConstants.SELECTED).xclose();
                columnBuilder.getHtmlBuilder().input("checkbox").name(BeanUtils.getProperty(bean, "presidentId"));
                columnBuilder.getHtmlBuilder().onclick("setPresidentState(this)");
                columnBuilder.getHtmlBuilder().checked();
                columnBuilder.getHtmlBuilder().xclose();
            } else {
                columnBuilder.getHtmlBuilder().input("hidden").name("chkbx_" + presidentId).value(SelectedPresidentsConstants.UNSELECTED).xclose();
                columnBuilder.getHtmlBuilder().input("checkbox").name(BeanUtils.getProperty(bean, "presidentId"));
                columnBuilder.getHtmlBuilder().onclick("setPresidentState(this)");
                columnBuilder.getHtmlBuilder().xclose();
            }
        } catch (Exception e) {}
        
        columnBuilder.tdEnd();
        
        return columnBuilder.toString();
    }
}
```

### Controller ###

Note: A Controller in the Spring Framework works very much like an Action in the Struts Framework.

When using the eXtremeTable embeded within another form you will have either one or two controllers. You need one controller to process the user input when the form is submitted and then redirect to a different JSP. You could also have another controller that gets the Collection of Beans that the table uses while sorting, filtering and paging and forwards to the same JSP. Or you could just use one controller that does both.

In the checkbox example I have one controller that cooresponds to the TableTag action attribute. I also have another controller that cooresponds to the enclosing form element action.

#### TableTag Action Controller ####

The controller is responsible for calling the SelectedPresidentsUtils to save the selected presidentIds in session and then forwards back to the same JSP.

```
SelectedPresidentsUtils.saveSelectedPresidentsIDs(request);
Collection presidents = presidentsService.getPresidents();
request.setAttribute("presidents", presidents);
```

#### Form Action Controller ####

The controller is responsible for taking the presidentIds to get the Collection of President Beans and then redirects to the next JSP. This tutorial does not show what that next JSP looks like as it is just an eXtremeTable that displays the selected presidents.

```
Collection selectedPresidentsIds = SelectedPresidentsUtils.saveSelectedPresidentsIDs(request);
Collection selectedPresidents = SelectedPresidentsUtils.getSelectedPresidents(presidentsService.getPresidents(), selectedPresidentsIds);
request.setAttribute("selected", selectedPresidents);
request.getSession().removeAttribute(SelectedPresidentsConstants.SELECTED_PRESIDENTS);
```

#### Retrieving Checkbox Values ####

Just to be complete I am also going to show the code that saves the presidentIds in session. I frequently get asked how to retrieve the form element values nested in the eXtremeTable. You might have to study the code for a little bit, but the trick is to set the form input element name attribute prepended with something that will uniquely identify the element.

In this example I know that it is an element that I care about because the parameter is prepended with chkbx. Right after the chkbx is the presidentId that corresponds to the checkbox and is what I parse off to get the unique presidentId. Because this is a hidden input element that is directly related to to checkbox element I know by the value whether or not this president was selected. If you remember from the JSP section of the tutorial that for each form checkbox element I have a hidden input element so that I always know whether or not the checkbox was checked.

```
public static Collection saveSelectedPresidentsIDs(HttpServletRequest request) {
    Collection presidents = (Collection) request.getSession().getAttribute(SelectedPresidentsConstants.SELECTED_PRESIDENTS);
    
    if (presidents == null) {
        presidents = new ArrayList();
        request.getSession().setAttribute(SelectedPresidentsConstants.SELECTED_PRESIDENTS, presidents);
    }

    Enumeration parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
        String parameterName = (String) parameterNames.nextElement();
        if (parameterName.startsWith("chkbx_")) {
            String presidentId = StringUtils.substringAfter(parameterName, "chkbx_");
            String parameterValue = request.getParameter(parameterName);
            if (parameterValue.equals(SelectedPresidentsConstants.SELECTED)) {
                if (!presidents.contains(presidentId)) {
                    presidents.add(presidentId);
                }
            } else {
                presidents.remove(presidentId);
            }
        }
    }

    return presidents;
}
```


