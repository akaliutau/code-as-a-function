package functions.messaging;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
public class PubSubMessage implements Serializable {
    private String data;
    private Map<String, String> attributes;
    private String messageId;
    private String publishTime;
}
