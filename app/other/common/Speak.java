package other.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import other.common.messaging.JsonMessaging;
import ru.iris.common.messaging.model.speak.SpeakAdvertisement;

import javax.jms.JMSException;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: nikolay.viguro
 * Date: 10.09.13
 * Time: 10:49
 * To change this template use File | Settings | File Templates.
 */
public class Speak {

    private static Logger log = LogManager.getLogger(Speak.class);

    public void say(String text) throws JMSException, URISyntaxException {

        try {
            JsonMessaging messaging = new JsonMessaging(UUID.randomUUID());
            messaging.broadcast("event.speak", new SpeakAdvertisement(text, 100.0));
            messaging.close();
        } catch (Exception e) {
            log.info("Error! Failed to speak: "+ text);
        }
    }
}
