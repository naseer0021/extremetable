In situations where you need to process large results of data you should think about using the Limit feature of the eXtremeTable. The name Limit comes from the MySQL limit command, and the the purpose of the Limit interface is to know how to limit the table results. The implemenation of the Limit knows how the user interacted with the table with regards to sorting, filtering, paging, and exporting. With this information you will be able to display the requested page filtered and sorted correctly in the most efficient manner possible.

To demonstrate the Limit feature I will split the work into a JSP, Controller, Service and DAO. This demonstrates a layered approach to dealing with the Limit. You may choose to add more layers, or less depending on your needs. The example also uses the Spring framework to retrieve the data using Spring's JDBC abstraction, so your code may look slightly different. One of strengths of the eXtremeTable is it is not dependant on any specific framework or container.

Also be sure to read the [Limit](Limit.md) documentation as that explains better at a high level what the Limit is and how it works. The following example is a way that would work in any environment and that was the motivation for showing this route. However, if you use tools such as Hibernate or iBatis they can make this process a lot easier. Just rememember that what the Limit buys you is an easy way to see how the user interacted with the table. How you get the actual page of relevant data depends on what you use for tools and methodology.

### JSP ###

To use the Limit feature the eXtremeTable needs to use the limit specific RetrieveRowsCallback, FilterRowsCallback, and SortRowsCallback interfaces. The eXtremeComponents does provide an implemenation of each that can be easily referenced by using the name limit for each attribute.

```
 <ec:table 
    items="presidents"
    retrieveRowsCallback="limit"
    filterRowsCallback="limit"
    sortRowsCallback="limit"
    view="limit"
    >
 ...
```

In addtion the view attribute references a custom view named limit. This is a simple modification to the default eXtremeTable look and feel to not include the last page toobar item. This is only relevant if you cannot get the exact rows needed. Some databases such as Oracle and !MySQL do have a way to get the specific rows, however, other databases such as Sybase do not. For my example I will assume the worst and that your database does not have such a feature.

Even if your database does not have a way to grab specific rows the Limit still makes perfect sense when you think about how users will interact with the table. The user will usually sort, filter and page through a few results. In the case where there are 15 rows to a page, the first page requires 15 results. The second page requires 30 results. The third requires 45, and so on. After paging through a few times they will usually refine the filter. Even if they do not they have to page through a ton of results before this ever impacts performance. Of course if the user was allowed to click the last page then all the data would have to be pulled and would severly effect performance.

### Controller ###

Note: A Controller in the Spring Framework works very much like an Action in the Struts Framework.

The controller first needs to create a new Limit. To do this you will need a little help from a Context and LimitFactory.

```
 Context context = new HttpServletRequestContext(request);
 LimitFactory limitFactory = new TableLimitFactory(context);
 Limit limit = new TableLimit(limitFactory);
```

The Context is an Interface that handles retrieving the attributes. The LimitFactory uses the Context to find out how the user interacted with the eXtremeTable. The Limit then uses the LimitFactory to populate itself.

Just by instantiating the Limit it will contain all kinds of useful information. The information includes how the table should be sorted and filtered, which page to display, and whether or not the data is being exported.

However, the Limit still needs to figure out the row information so the proper page of information can be displayed to the user. The row information includes the row start, row end, and the current rows displayed. To find out this information the controller will have to ask our service, which in turn will ask the dao. For now I will just show the controller side of the story.

```
 int totalRows = presidentsService.getTotalPresidents(limit.getFilterSet(),  limit.isExported());
 limit.setRowAttributes(totalRows, defaultRowsDisplayed);
```

The limit needs the total rows to find the row information. The service needs to know what was filtered and whether or not the data is being exported. To set the row information the default rows displayed needs to be set. This should be set to the exact same number that was specified for the TableTag rowsDisplayed attribute.

Now we just need to ask the service for the Collection of data.

```
 Collection presidents = presidentsService.getPresidents(limit.getFilterSet(), limit.getSort(), limit.getRowEnd());
```

This is pretty easy because the limit has all the information. All that needs to be done is pass in the filter, sort and row end information. With the Collection and totalRows in hand the last thing that needs to be done is send that information back to the JSP so the eXtremeTable knows what and how to display the information.

```
 request.setAttribute("presidents", presidents);
 request.setAttribute("totalRows", new Integer(totalRows));
```

### Service ###

The service needs to interact with the dao to retrieve the total rows and Collection.

#### Retrieve Total Rows ####

Thinking back to the controller the first piece of information requested was the total rows.

```
 public int getTotalPresidents(FilterSet filterSet, boolean isExported) {
     String totalQuery = presidentsDao.getTotalPresidentsQuery();
     String modTotalQuery = filterQuery(filterSet, totalQuery);
     int totalRows = presidentsDao.getTotalPresidents(modTotalQuery);
     if (isExported && totalRows > maxExportRows) {  
         totalRows = maxExportRows;
     }
     return totalRows;
 }
```

The service works with the dao to filter the results. What this really means is you need to modify the query string to append more AND clauses to the WHERE section of the query. To do this you need to work with the Limit FilterSet.

The FilterSet is an Array of Filter objects. A Filter contains a bean property and the filter value. Or, in other words it is simply the column that the user is trying to filter and the value that they entered. This makes it very easy to work with. In my example the work of appending the various filters to the AND clause of the query string is done in the dao. The service just needs to iterate over the FilterSet and call the dao.

```
 private String filterQuery(FilterSet filterSet, String query) {
     if (!filterSet.isFiltered() || filterSet.isCleared()) {
         return query;
     }
     
     Filter filters[] = filterSet.getFilters();
     for (int i = 0; i < filters.length; i++) {
         Filter filter = filters[i];
         String property = filter.getProperty();
         String value = filter.getValue();
         query = presidentsDao.filterQuery(query, property, value);
     }  
 
     return query;
 }
```

With the query modified to include the filter information the dao returns the total rows. In some cases this may be enough, but there is still the potential problem of the user exporting to much information. With trying to keep efficiency in mind the service will not allow more than a maximum amount of rows when exporting.

#### Retrieve Collection ####

The second piece of information the controller asked for was the Collection.

```
 public Collection getPresidents(FilterSet filterSet, Sort sort, int rowEnd) {
     String patientsQuery = presidentsDao.getPresidentsQuery();
     String modPatientsQuery = filterQuery(filterSet, patientsQuery);
     modPatientsQuery = sortQuery(sort, modPatientsQuery);
     modPatientsQuery = presidentsDao.limitQuery(rowEnd, modPatientsQuery);
     return presidentsDao.getPresidents(modPatientsQuery);
 }
```

The service works with the dao to modify the query string to filter, as has been shown previously.

In addtion the query string needs to append the ORDER BY clause so that the results are sorted correctly. The Sort contains a bean property and the sortOrder value. The service just needs to use the Sort and call the dao.

```
 private String sortQuery(Sort sort, String query) {
     if (!sort.isSorted()) {
         String defaultSortOrder = presidentsDao.getDefaultSortOrder();
         if (StringUtils.isNotBlank(defaultSortOrder)) {
             return query + defaultSortOrder;
         }
         
         return query;
     } 
 
     String property = sort.getProperty();
     String sortOrder = sort.getSortOrder();
     
     return presidentsDao.sortQuery(query, property, sortOrder);
 }
```

The last modification to the query string is to append the database specific command to limit the results that will be returned. This is the work of the limitQuery() method.

### DAO ###

The dao is in charge of doing the low level data work for the service.

#### Define Query String ####

To really understand the dao the query string(s) need to be shown.

This is the presidents query to get the data:

```
 private final static String presidentsQuery = 
         " SELECT " +
         "    president_id presidentId, " + 
         "    first_name firstName, " +
         "    last_name lastName, " +
         "    nick_name nickName,  " +
         "    concat(first_name, ' ',last_name) fullName, " +
         "    term,  " +
         "    born,  " +
         "    died,  " +
         "    education, " + 
         "    career,  " +
         "    political_party politicalParty " +
         " FROM presidents "; 
```

This is the total presidents query to get the total rows:

```
 private final static String totalPresidentsQuery = 
         " SELECT count(*) FROM presidents ";
```

#### Filter And Sort Query String ####

Probably the two most interesting methods are the ones that handle the filtering and sorting.

The filter looks like this:

```
 public String filterQuery(String query, String property, String value) {
     StringBuffer result = new StringBuffer(query); 
 
     if (query.indexOf("WHERE") == -1) {
         result.append(" WHERE 1 = 1 "); //stub WHERE clause so can just append AND clause
     }
          
     if (property.equals("fullName")) {
         result.append(" AND concat(first_name, ' ',last_name) like '%" + value + "%'");
     } else if (property.equals("nickName")) {
         result.append(" AND nick_name like '%" + value + "%'");
     } else {
         result.append(" AND " + property + " like '%" + value + "%'");
     }
         
     return result.toString();
 }
```

The filterQuery() method needs to append the proper AND clause to the query string.

The sort looks pretty similar:

```
 public String sortQuery(String query, String property, String sortOrder) {
     StringBuffer result = new StringBuffer(query + " ORDER BY ");
         
     if (property.equals("fullName")) {
         result.append("concat(first_name, ' ',last_name) " + sortOrder);
     } else {
         result.append(property + " " + sortOrder);
     } 
 
     return result.toString();
 }
```

The sortQuery() method needs to append the proper ORDER BY clause to the query string.

#### Limit Query String ####

Now that the query string is modified to filter and sort the data correctly it needs to be modified to get only the data relevant to the page to display. In !MySQL this is the limit command.

```
public String limitQuery(int rowEnd, String query) {
    return query + " limit " + rowEnd;
}
```

#### Retrieve Total Rows And Collection. ####

At this point the only thing the service needs is the tws and the Collection.

```
 public Collection getPresidents(final String query) {
     return jdbcTemplate.query(query, new ResultReader() {
         List results = new ArrayList();
          public List getResults() {
             return results;
         } 
 
         public void processRow(ResultSet rs)
                 throws SQLException {
             President president = new President();
             president.setPresidentId(new Integer(rs.getInt("presidentId")));
             president.setFirstName(rs.getString("firstName"));
             president.setLastName(rs.getString("lastName"));
             president.setNickName(rs.getString("nickName"));
             president.setFullName(rs.getString("fullName"));
             president.setTerm(rs.getString("term"));
             president.setBorn(rs.getDate("born"));
             president.setDied(rs.getDate("died"));
             president.setEducation(rs.getString("education"));
             president.setCareer(rs.getString("career"));
             president.setPoliticalParty(rs.getString("politicalParty"));
             results.add(president);
         }
     });        
 }
 
 public int getTotalPresidents(final String query) {
     return jdbcTemplate.queryForInt(query);
 }
```

The ResultReader is a Spring specific class to help with processing JDBC queries, and is used as a callback to process the JDBC ResultSet. The jdbcTemplate abstracts out the JDBC connection.

#### Default Sort Order ####

And, just to be complete, this is default sort order that the service needs:

```
 public String getDefaultSortOrder() {
     return " ORDER BY concat(first_name, ' ', last_name) ";        
 }
```


