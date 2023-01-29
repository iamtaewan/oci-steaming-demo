package kr.oracle.devmeetup.ocistreaming;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.streaming.StreamClient;
import com.oracle.bmc.streaming.model.PutMessagesDetails;
import com.oracle.bmc.streaming.model.PutMessagesDetailsEntry;
import com.oracle.bmc.streaming.model.PutMessagesResultEntry;
import com.oracle.bmc.streaming.requests.PutMessagesRequest;
import com.oracle.bmc.streaming.responses.PutMessagesResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Producer {
    public static void main(String[] args) throws Exception {
        final String configurationFilePath = "~/.oci/config";
        final String profile = "default";
        final String ociStreamOcid = "ocid1.stream.oc1.ap-tokyo-1.amaaaaaavsea7yialql3at5tpipc556r4xiquv5abdtiuz3fobu5qe7xe4gq";
        final String ociMessageEndpoint = "https://cell-1.streaming.ap-tokyo-1.oci.oraclecloud.com";


        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
        final AuthenticationDetailsProvider provider =
                new ConfigFileAuthenticationDetailsProvider(configFile);

        StreamClient streamClient = StreamClient.builder().endpoint(ociMessageEndpoint).build(provider);
        publishExampleMessages(streamClient, ociStreamOcid);

    }

    private static void publishExampleMessages(StreamClient streamClient, String streamId) {
        // build up a putRequest and publish some messages to the stream
        List<PutMessagesDetailsEntry> messages = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            messages.add(
                    PutMessagesDetailsEntry.builder()
                            .key(String.format("messageKey%s", i).getBytes(UTF_8))
                            .value(String.format("오라클 디벨로퍼 밋업 - 스트림 메시지 -%s (OCI Java SDK)", i).getBytes(UTF_8))
                            .build());
        }

        System.out.println(
                String.format("Publishing %s messages to stream %s.", messages.size(), streamId));
        PutMessagesDetails messagesDetails =
                PutMessagesDetails.builder().messages(messages).build();

        PutMessagesRequest putRequest =
                PutMessagesRequest.builder()
                        .streamId(streamId)
                        .putMessagesDetails(messagesDetails)
                        .build();

        PutMessagesResponse putResponse = streamClient.putMessages(putRequest);

        for (PutMessagesResultEntry entry : putResponse.getPutMessagesResult().getEntries()) {
            if (StringUtils.isNotBlank(entry.getError())) {
                System.out.println(
                        String.format("Error(%s): %s", entry.getError(), entry.getErrorMessage()));
            } else {
                System.out.println(
                        String.format(
                                "Published message to partition %s, offset %s.",
                                entry.getPartition(),
                                entry.getOffset()));
            }
        }
    }
}