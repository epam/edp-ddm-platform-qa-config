package platform.qa.entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisConfiguration {
    private String podLabel;
    private String secret;
    private String url;
    private boolean portForwarding;
    private int defaultPort;
}
