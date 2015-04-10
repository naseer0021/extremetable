## Frequently Asked Questions ##

##### Custom Form #####
  * Question: How can I customize the eXtremeTable to include other form components, or embed in a different form?
  * Answer: Just remember that the eXtremeTable is a form component and at the heart is quite straightforward and simple in that respect.

##### Create custom View #####
  * Question: How can I modify the View?
  * Answer: You need to create a custom View.

##### Create custom filter #####
  * Question: How can I create a custom filter?
  * Answer: The filters are meant to be pluggable. What you need to do is plug in your own Table filterRowsCallback and make up your own FilterPredicate. You can see the default implementation in the ProcessRowsCallback class in the callback package. You can read more about it in the docs at [Callbacks](Callbacks.md). You are going to have to deal with the filters not associated with columns in a custom manner. Remember on the TableModel you get access to the Context so you can pull those other fields.

##### ViewsAllowed and filter conflict #####
  * If you set a column to not allow a view and then filter on that column then nothing shows up in the export. This is a known issue in the eXtremeTable and not considered a bug. The idea was always that you would export at least as much as you can see on the page, but then could display additional information if needed.

##### Javascript error with nested column property #####
  * Question: Some of my columns come from one of the nested beans inside my current row bean.  It works fine to get those values for display purposes, but when I try to sort on one of those columns, such as I get a javascript error.
  * Answer: You should be using the ColumnTag alias attribute. The alias attribute is used to make the column unique if the property attribute is used more than once. The alias attribute is also more appropriate for columns that do not map to a specific bean property as well. A third case would be if you are using nested object attributes (using dot notation) for the property.

##### Problems with the export downloads #####
  * Question: I am having problems exporting anything (this is if you cannnot export at all)
  * Answer: What I usually ask people to do is take the test.jsp from the eXtremeComponents distribution (that you downloaded) and try hitting that from your application. You will need to add the ExportXlsTag to the table and adjust the TableTag action attribute. Because this is just using scriplets to create the Collection of Beans you do not have to go through any framework code and can test it in isolation. If you cannot export with that test then it might be a problem with the filter. Do you have any other filters defined in your web.xml? If so could you trying removing them (either all or one by one) and see if any of them are causing the conflict. If you can export with the test.jsp then it has something to do with your environment. Post on the forum including your servlet container (tomcat,jboss,etc...), the JDK version and the eXtremeComponents version.

##### Problems filtering with locale support #####
  * Question: When I input Chinese character to the filter input box, filters cannot normally work.
  * Answer: For Tomcat the fix is to change the config in server.xml(Tomcat):
```
 <Connector port="80" URIEncoding="UTF-8" maxThreads="150" minSpareThreads="25" maxSpareThreads="75" enableLookups="false"     
            redirectPort="8443" acceptCount="100" debug="0" connectionTimeout="20000" disableUploadTimeout="true" />
```

##### Tree Code #####
  * Question: Can I see a Tree code example?
  * Answer: The tree code does not work very good. It was started by a developer but the code did not keep up with the refactorings about the time the eXtremeTable became a form component. At one time it worked pretty good, but its been neglected for awhile now. I just refactored it into a separate tree package in case someone ever is interested in picking it up.

##### Images directory #####
  * Question: What is the difference between the /images/table and the /images/table/compact directory?  When do we use either?  Are both needed in production use?
  * Answer: Somebody please answer.

##### How can I use two table in same page #####
  * Question: How can I use two table in same page?
  * Answer: use different table-ids
```
<ec:table 
   tableId="id-one"
   ...
 >
 <ec:table
   tableId="id-two"
   ...
 >
```
