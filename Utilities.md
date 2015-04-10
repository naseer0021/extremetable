There are quite a few utility classes to come out of eXtremeTable. I will discuss some of them very briefly here, and then will ask you to look at the javadocs for more information.

### HtmlBuilder ###

A class that encapsulates all the html syntax. The power of this simple class is you can write much cleaner html code, and without having to worry about null or empty string values. For example a span tag would look as follows:

```
 HtmlBuilder html = new HtmlBuilder();
 html.span().styleClass(FORM_BUTTONS).close();
 html.append(formButtons);
 html.spanEnd();
 return html.toString();
```