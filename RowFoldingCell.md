A custom cell that will open and colapse a row.

```
public class CollectionCell extends AbstractCell
{

   public final static String LINEBREAK = "<br/>";
   
    public String getExportDisplay(TableModel model, Column column) {
        return getStringValue((Collection) column.getPropertyValue());
    }
   
   protected String getCellValue(TableModel model, Column column)
   {
      Object propertyValue = column.getPropertyValue();
      if(!(propertyValue instanceof Collection))
         throw new IllegalArgumentException("Column property must be of type Collection");
      
      String result = "";
      Collection items = (Collection) propertyValue;
      String tableId = ExtendedTableModelUtils.getTableId(model);
      int hashCode = model.getCurrentRowBean().hashCode();
      String colAction = model.getContext().getParameter(tableId+"_colaction"+hashCode);
      if(colAction != null && "open".equalsIgnoreCase(colAction))
      {
         result = getColumnValue(items, model, "close", ViewConstants.IMG_PATH_LIST_REMOVE);
         result += "<br/>"+getStringValue(items);
      }
      else if(!items.isEmpty())
      {
         result = getColumnValue(items, model, "open", ViewConstants.IMG_PATH_LIST_ADD);
      }
      
      return result;
   }

   private String getColumnValue(Collection items, TableModel model, String action, String img)
   {
      String formName = ExtendedTableModelUtils.getTableFormName(model);
      String tableId = ExtendedTableModelUtils.getTableId(model);
      String script =
            "document.forms."+formName+".ec_eti.value='';"+
            "document.getElementsByName('"+tableId+"_colaction"+model.getCurrentRowBean().hashCode()+"')[0].value='"+action+"';"+
            "buildTable('"+formName+"');";
      
      String link = "<input type=\"hidden\" name=\""+tableId+"_colaction"+model.getCurrentRowBean().hashCode()
      +"\"/><a href=\"javascript:"+script+"\"><img src=\""+img+"\" border=\"0\"></a>";
      return link;
   }

   private String getStringValue(Collection items)
   {
      StringBuffer sb = new StringBuffer();
      for (Iterator iter = items.iterator(); iter.hasNext();)
      {
         Object element = (Object) iter.next();
         if(element != null)
            sb.append(element).append(LINEBREAK);
      }
      if(sb.length() > 0)
         sb = sb.delete(sb.length()-LINEBREAK.length(),sb.length());
      return sb.toString();
   }

} 
```