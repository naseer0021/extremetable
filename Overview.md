## Overview ##

### Introduction ###

The eXtremeTable takes a Collection of Beans or a Collection of Maps out of a given servlet scope and renders the content on the JSP. The order the servlet scopes will be searched is page, request, session, and application. The eXtremeTable knows what to pull out of the servlet scope because you specify the items attribute on the TableTag.

The Beans in the Collection are plain old java objects (POJO) where each attribute has a corresponding getter and setter method. If using maps the attributes would be name-value pairs. You can think of each bean as being one row in the table display. For the remainder of the documentation I may refer to the Collection of Beans and Collection of Maps as just a Collection of Beans to keep down on the amount of typing.

The minimum syntax for an actual table that users will interact with will look like this:

```
 <ec:table 
    items="presidents"
    action="${pageContext.request.contextPath}/presidents.run"
    imagePath="${pageContext.request.contextPath}/images/*.gif">
   <ec:row>
     <ec:column property="firstName"/>
     <ec:column property="lastName"/> 
     <ec:column property="termDate"/> 
   </ec:row>
 </ec:table>
```

This is how you will typically define an eXtremeTable. With this you have nicely formated columns and headers and all the features on the toolbar will work perfectly.
