package other;

import play.libs.F;

public class DataQueue
{
    private final static DataQueue instance = new DataQueue();
    private F.ArchivedEventStream<Object> data = new F.ArchivedEventStream<Object>(30);

    public static DataQueue getInstance()
    {
            return instance;
    }

    public F.ArchivedEventStream<Object> getQueue() {
        return data;
    }
}
