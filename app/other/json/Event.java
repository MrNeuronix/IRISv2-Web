package other.json;

/**
 * Created by nikolay.viguro on 13.10.2014.
 */
public class Event
{
	private int id;
	private String title;
	private boolean allDay = false;
	private String start;
	private String end;
	private String url;
	private String className;
	private boolean editable = true;

	public Event()
	{
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public boolean isAllDay()
	{
		return allDay;
	}

	public void setAllDay(boolean allDay)
	{
		this.allDay = allDay;
	}

	public String getStart()
	{
		return start;
	}

	public void setStart(String start)
	{
		this.start = start;
	}

	public String getEnd()
	{
		return end;
	}

	public void setEnd(String end)
	{
		this.end = end;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getClassName()
	{
		return className;
	}

	public void setClassName(String className)
	{
		this.className = className;
	}

	public boolean isEditable()
	{
		return editable;
	}

	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}
}
