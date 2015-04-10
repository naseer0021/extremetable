A JDK1.5 implementation for using the Messages with the !Webwork Framework!

You just need to specify the Messages in your Preferences (extremecomponents.properties) to plug this in:

```
 messages=com.messages.MessagesWebworkAdapter 
```

```
public class MessagesWebworkAdapter implements Messages {
	private TextProvider textProvider;
	private Messages defaultMessages;

	public MessagesWebworkAdapter() {
		super();
		this.textProvider = null;
		this.defaultMessages = new TableResourceBundle();
	}

	public void init(Context context, String messagesLocation, Locale locale) {
		this.defaultMessages.init(context, messagesLocation, locale);
		if (context.getContextObject() instanceof PageContext) {
			PageContext pageContext = (PageContext)context.getContextObject();
			OgnlValueStack stack = TagUtils.getStack(pageContext);
			
			for (Object o : stack.getRoot()) {
				if (o instanceof TextProvider) {
					this.textProvider = (TextProvider)o;
					break;
				}
			}
		}
	}

	public String getMessage(String code) {
		return getMessage(code, null);
	}

	public String getMessage(String code, Object[] args) {
		List<Object> theArgs = null;
		if (args != null) {
			theArgs = new ArrayList<Object>();
			for(int i = 0; i < args.length; i++) {
				theArgs.add(args[i]);
			}
		}
		
		String message = null;
		if (this.textProvider != null) {
			message = this.textProvider.getText(code, null, theArgs);
		}
        if (message == null)
        {
            message = defaultMessages.getMessage(code, args);
        }

        return message;
	}

}
```

If you need a JDK1.4 compliant code, you can use this class:

```
package amadeus.hdp.inventoryrules.extremecomponents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.jsp.PageContext;

import org.extremecomponents.table.context.Context;
import org.extremecomponents.table.core.Messages;
import org.extremecomponents.table.resource.TableResourceBundle;

import com.opensymphony.webwork.views.jsp.TagUtils;
import com.opensymphony.xwork.TextProvider;
import com.opensymphony.xwork.util.OgnlValueStack;

public class MessagesWebworkAdapter implements Messages {
	private TextProvider textProvider;
	private Messages defaultMessages;

	public MessagesWebworkAdapter() {
		super();
		this.textProvider = null;
		this.defaultMessages = new TableResourceBundle();
	}

	public void init(Context context, Locale locale) {
		this.defaultMessages.init(context, locale);
		if (context.getContextObject() instanceof PageContext) {
			PageContext pageContext = (PageContext)context.getContextObject();
			OgnlValueStack stack = TagUtils.getStack(pageContext);
			Iterator iter = stack.getRoot().iterator();
			while (iter.hasNext()) {
				Object o = iter.next();
				if (o instanceof TextProvider) {
					this.textProvider = (TextProvider)o;
					break;
				}
			}
		}
	}

	public String getMessage(String code) {
		return getMessage(code, null);
	}

	public String getMessage(String code, Object[] args) {
		List theArgs = null;
		if (args != null) {
			theArgs = new ArrayList();
			for(int i = 0; i < args.length; i++) {
				theArgs.add(args[i]);
			}
		}
		
		String message = null;
		if (this.textProvider != null) {
			message = this.textProvider.getText(code, null, theArgs);
		}
        if (message == null)
        {
            message = defaultMessages.getMessage(code, args);
        }

        return message;
	}

}
```