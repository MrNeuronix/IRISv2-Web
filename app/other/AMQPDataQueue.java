package other;

import other.common.messaging.JsonEnvelope;
import play.libs.F;

public class AMQPDataQueue
{
    private final static AMQPDataQueue instance = new AMQPDataQueue();
    private F.ArchivedEventStream<JsonEnvelope> data = new F.ArchivedEventStream<>(1);

    public static AMQPDataQueue getInstance()
    {
            return instance;
    }

    public F.ArchivedEventStream<JsonEnvelope> getQueue() {
        return data;
    }
}
