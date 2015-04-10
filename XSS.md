It was brought to my attention that there is a potential security hole in the FilterCell. The solution is pretty simple and instead of doing a full deployment the fix will be posted here.

The only change that needs to be done is the FilterCell needs to have the value escaped for the hidden input field.

```
 html.value(StringEscapeUtils.escapeHtml(value));
```

