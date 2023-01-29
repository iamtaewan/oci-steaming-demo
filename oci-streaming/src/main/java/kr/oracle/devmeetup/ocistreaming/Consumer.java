package kr.oracle.devmeetup.ocistreaming;

import com.google.common.util.concurrent.Uninterruptibles;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.streaming.StreamClient;
import com.oracle.bmc.streaming.model.CreateGroupCursorDetails;
import com.oracle.bmc.streaming.model.Message;
import com.oracle.bmc.streaming.requests.CreateGroupCursorRequest;
import com.oracle.bmc.streaming.requests.GetMessagesRequest;
import com.oracle.bmc.streaming.responses.CreateGroupCursorResponse;
import com.oracle.bmc.streaming.responses.GetMessagesResponse;

import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;


public class Consumer {
    public static void main(String[] args) throws Exception {
        final String configurationFilePath = "~/.oci/config";
        final String profile = "default";
        final String ociStreamOcid = "ocid1.stream.oc1.ap-tokyo-1.amaaaaaavsea7yialql3at5tpipc556r4xiquv5abdtiuz3fobu5qe7xe4gq";
        final String ociMessageEndpoint = "https://cell-1.streaming.ap-tokyo-1.oci.oraclecloud.com";

        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parseDefault();
        final AuthenticationDetailsProvider provider =
                new ConfigFileAuthenticationDetailsProvider(configFile);

        StreamClient streamClient = StreamClient.builder().endpoint(ociMessageEndpoint).build(provider);

        System.out.println("Starting a simple message loop with a group cursor");
        String groupCursor =
                getCursorByGroup(streamClient, ociStreamOcid, "exampleGroup", "exampleInstance-1");
        simpleMessageLoop(streamClient, ociStreamOcid, groupCursor);

    }

    private static void simpleMessageLoop(
            StreamClient streamClient, String streamId, String initialCursor) {
        String cursor = initialCursor;
        for (int i = 0; i < 10; i++) {

            GetMessagesRequest getRequest =
                    GetMessagesRequest.builder()
                            .streamId(streamId)
                            .cursor(cursor)
                            .limit(25)
                            .build();

            GetMessagesResponse getResponse = streamClient.getMessages(getRequest);

            // process the messages
            System.out.println(String.format("Read %s messages.", getResponse.getItems().size()));
            for (Message message : ((GetMessagesResponse) getResponse).getItems()) {
                System.out.println(
                        String.format(
                                "%s: %s",
                                message.getKey() == null ? "Null" :new String(message.getKey(), UTF_8),
                                new String(message.getValue(), UTF_8)));
            }

            // getMessages is a throttled method; clients should retrieve sufficiently large message
            // batches, as to avoid too many http requests.
            Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);

            // use the next-cursor for iteration
            cursor = getResponse.getOpcNextCursor();
        }
    }

    private static String getCursorByGroup(
            StreamClient streamClient, String streamId, String groupName, String instanceName) {
        System.out.println(
                String.format(
                        "Creating a cursor for group %s, instance %s.", groupName, instanceName));

        CreateGroupCursorDetails cursorDetails =
                CreateGroupCursorDetails.builder()
                        .groupName(groupName)
                        .instanceName(instanceName)
                        .type(CreateGroupCursorDetails.Type.TrimHorizon)
                        .commitOnGet(true)
                        .build();

        CreateGroupCursorRequest createCursorRequest =
                CreateGroupCursorRequest.builder()
                        .streamId(streamId)
                        .createGroupCursorDetails(cursorDetails)
                        .build();

        CreateGroupCursorResponse groupCursorResponse =
                streamClient.createGroupCursor(createCursorRequest);
        return groupCursorResponse.getCursor().getValue();
    }

}