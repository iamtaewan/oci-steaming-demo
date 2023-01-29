export KEY1=`echo "key1" | base64`
export VALUE1=`echo "Developer Meetup" | base64`
export STREAM_OCID=ocid1.stream.oc1.ap-tokyo-1.amaaaaaavsea7yialw2xz3tce5m7tv2noanwxlospe6jdzqiwnrsht6d7xuq
export ENDPOINT=https://cell-1.streaming.ap-tokyo-1.oci.oraclecloud.com

oci streaming stream message put \
  --stream-id $STREAM_OCID \
  --endpoint $ENDPOINT \
  --messages '[{"key":"a2V5MQo=", "value":"b3JhY2xlIG1lZXR1cDEK"}]'

