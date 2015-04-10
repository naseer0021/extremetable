#### Requirements ####
  * Servlet 2.3 or higher
  * JDK 1.3.1 or higher

#### Minimum Jars Needed: ####
  * commons-beanutils 1.6 (1.7 if sorting with nested properties)
  * commons-collections 3.0
  * commons-lang 2.0
  * commons-logging 1.0.4
  * standard 1.0.2

#### Jars Needed for the PDF export: ####
  * minimum jars (above)
  * avalon-framework 4.0
  * batik 1.5-fop-0.20-5
  * fop 0.20.5
  * xalan 2.5.1
  * xercesImpl 2.6.1
  * xml-apis 2.0.2

#### Jars Needed for the XLS export: ####
  * minimum jars (above)
  * poi-2.5.1.jar

### Installation ###

Download the distribution.

In the zip file you will find everything you need to get started:
  * extremecomponents.jar
  * extremecomponents.tld
  * extremecomponents.css
  * a set of default images
  * source code
  * test.jsp (to verify installation)

The extremecomponents.jar file and the necessary JAR files listed above need to go into your projects /WEB-INF/lib directory.

There are two ways to handle the TLD file. The most cross servlet container way to use it is to put the extremecomponents.tld file somewhere in your WEB-INF directory. I like to put my TLD files in a /WEB-INF/tld directory just to keep things organized. Then, with your extremecomponents.tld file in place you will need to update the /WEB-INF/web.xml file to include the taglib mapping.

```
 <taglib>
    <taglib-uri>/tld/extremecomponents</taglib-uri>
    <taglib-location>/WEB-INF/tld/extremecomponents.tld</taglib-location>
 </taglib>
```

Then in your JSP pages that include the eXtremeTable you will need to put in a taglib like this:

```
 <%@ taglib uri="/tld/extremecomponents" prefix="ec" %>
```

If you have a JSP 1.2 (or higher) servlet container that supports the auto-discovery of TLD files then you do not have to do anything. The extremecomponents.tld file is packaged in the META-INF directory of the extremecomponents.jar and will be found by the container when it loads up. Then in your JSP that includes the eXtremeTable you will need to put in a taglib like this:

```
 <%@ taglib uri="http://www.extremecomponents.org" prefix="ec" %>
```

To style the eXtremeTable, copy the extremecomponents.css from the the styles folder to wherever you keep your .css scripts. Of course in the JSP you will need to put a link to the CSS. For example I put my stylesheet in a /styles directory.

```
 <%@ taglib uri="/tld/c" prefix="c" %>
 <link rel="stylesheet" type="text/css" href="<c:url value="/styles/extremecomponents.css"/>">
```

### Export Filter (optional) ###

For the exports to work you will need to set up the ExportFilter. This is optional and is only used for the exports.

To set up the filter put a entry in your /WEB-INF/web.xml file like this:

```
 <filter>
   <filter-name>eXtremeExport</filter-name>
   <filter-class>org.extremecomponents.table.filter.ExportFilter</filter-class>
 </filter>
 <filter-mapping>
   <filter-name>eXtremeExport</filter-name>
   <url-pattern>/*</url-pattern>
 </filter-mapping>
```

There is also an optional init-param that you can set on the Filter to determine when the response headers are set. I have found that in most servlet containers it is more desirable to set the response headers after calling the filter's doFilter() method. However, the filter will only work correctly in some containers if the response headers are set before the filter's doFilter() method. The default is to set the response headers after the filters doFilter method. To set it before use the responseHeadersSetBeforeDoFilter init-param.

```
 <filter>
   <filter-name>eXtremeExport</filter-name>
   <filter-class>org.extremecomponents.table.filter.ExportFilter</filter-class>
     <init-param>
       <param-name>responseHeadersSetBeforeDoFilter</param-name>
       <param-value>true</param-value>
     </init-param>
 </filter>
```

If you are using Sitemesh for your layout, you will also need to include the SitemeshPageFilter. The SitemeshPageFilter extends the regular sitemesh PageFilter and will cause the JSP to not be decorated if there is an export being done.

To set up the filter put a entry in the /WEB-INF/web.xml file like this:

```
 <filter>
   <filter-name>Sitemesh</filter-name>
   <filter-class>org.extremecomponents.table.filter.SitemeshPageFilter</filter-class>
 </filter>
 <filter-mapping>
   <filter-name>Sitemesh</filter-name>
   <url-pattern>/*</url-pattern>
 </filter-mapping>
```

### Test Installation ###

To test the installation included is a quick and dirty test.jsp page with the distribution files. Simply take the test.jsp page and stick it at the top level of your web application. By default the images are expected to be in a sub-directory called /images/table/. For this test also make that directory and put the images in there. Once that is done just point your browser at the test.jsp page.

Note: I do not like to promote using scriplets in your JSP but for the test.jsp it is the only way to demonstrate a quick test without having to go through a regular framework.

