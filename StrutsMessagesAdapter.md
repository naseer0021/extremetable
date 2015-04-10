Here is a simple interface between extremecomponents table resources and Strut's native resources manager.
This implementation works with JDK 1.5. It has not been tested with 1.4, but there is no 1.5 specific usage.

You just need to specify the Messages in your Preferences (extremecomponents.properties) to plug this in:

```
 messages=org.sample.struts.util.MessageResourcesAdapter
```

```
package org.sample.struts.util;

import java.util.Locale;

import javax.servlet.jsp.PageContext;

import org.apache.struts.Globals;
import org.apache.struts.util.MessageResources;
import org.extremecomponents.table.context.Context;
import org.extremecomponents.table.core.Messages;
import org.extremecomponents.table.resource.TableResourceBundle;

public class MessageResourcesAdapter implements Messages
{
    private Messages defaultMessages;
    private MessageResources messageResources;

    public MessageResourcesAdapter()
    {
        super();
        this.defaultMessages = new TableResourceBundle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.extremecomponents.table.core.Messages#getMessage(java.lang.String)
     */
    /**
     * Get a message from the resources.
     * 
     * @param <code>messageKey</code> the key in resources of the message to get.
     * @return returns the message if found, null otherwise.
     */
    public String getMessage(String messageKey)
    {
        return getMessage(messageKey, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.extremecomponents.table.core.Messages#getMessage(java.lang.String,
     *      java.lang.Object[])
     */
    /**
     * Get a message from the resources.
     * 
     * @param messageKey
     *            The key in resources of the message to get.
     * @param args
     *            Arguments to use to format message.
     * @return returns the message if found, null otherwise.
     */
    public String getMessage(String messageKey, Object[] args)
    {
        String message = null;
        if (this.messageResources != null)
        {
            message = this.messageResources.getMessage(messageKey, args);
        }
        if (message == null)
        {
            message = defaultMessages.getMessage(messageKey, args);
        }

        return message;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.extremecomponents.table.core.Messages#init(org.extremecomponents.table.context.Context,
     *      java.util.Locale)
     */
    /**
     * Initialize the underlying Struts'resources manager.
     * 
     * @param context
     *            The <code>Context</code> of the caller request.
     * @param locale
     *            The<code>Locale</code> to use.
     */
    public void init(Context context, Locale locale)
    {
        this.defaultMessages.init(context, locale);
        if (context.getContextObject() instanceof PageContext)
        {
            Object oResources = context.getRequestAttribute(Globals.MESSAGES_KEY);
            if (oResources instanceof MessageResources)
            {
                this.messageResources = (MessageResources) oResources;
            }
        }
    }

}
```