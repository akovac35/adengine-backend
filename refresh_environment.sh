echo "Exporting GCLOUD_PROJECT and GCLOUD_BUCKET"
export GCLOUD_PROJECT=$DEVSHELL_PROJECT_ID
export GCLOUD_BUCKET=$DEVSHELL_PROJECT_ID-media

echo "Updating files on"
gsutil cp data/ExcludedAdNetworks.csv gs://$GCLOUD_BUCKET/