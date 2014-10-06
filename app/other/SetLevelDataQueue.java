package other;

import play.libs.F;

public class SetLevelDataQueue
{
    private final static SetLevelDataQueue instance = new SetLevelDataQueue();
    private F.EventStream<Object> data = new F.EventStream<>();

    public static SetLevelDataQueue getInstance()
    {
            return instance;
    }

    public F.EventStream<Object> getQueue() {
        return data;
    }
}
