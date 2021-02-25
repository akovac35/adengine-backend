echo "Creating adengine-backend"
gcloud app create --region "us-central"

echo "Making bucket: gs://$DEVSHELL_PROJECT_ID-media"
gsutil mb gs://$DEVSHELL_PROJECT_ID-media

echo "Exporting GCLOUD_PROJECT and GCLOUD_BUCKET"
export GCLOUD_PROJECT=$DEVSHELL_PROJECT_ID
export GCLOUD_BUCKET=$DEVSHELL_PROJECT_ID-media

echo "Updating files on"
gsutil cp data/ExcludedAdNetworks.csv gs://$GCLOUD_BUCKET/

echo "Installing dependencies"
mvn clean install

#echo "Creating Datastore entities"
#mvn exec:java@create-entities

echo "Project ID: $DEVSHELL_PROJECT_ID"