export ENDPOINT=https://cell-1.streaming.ap-tokyo-1.oci.oraclecloud.com
export STREAM_OCID=ocid1.stream.oc1.ap-tokyo-1.amaaaaaavsea7yialw2xz3tce5m7tv2noanwxlospe6jdzqiwnrsht6d7xuq 

oci streaming stream cursor create-cursor \
--stream-id $STREAM_OCID \
--partition 0 \
--type TRIM_HORIZON \
--endpoint $ENDPOINT 
