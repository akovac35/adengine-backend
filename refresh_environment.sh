echo "Setting project"
gcloud config set project adengine-backend

echo "Exporting GCLOUD_PROJECT and GCLOUD_BUCKET"
export GCLOUD_PROJECT=$DEVSHELL_PROJECT_ID
export GCLOUD_BUCKET=$DEVSHELL_PROJECT_ID-media

echo "Updating files on $GCLOUD_BUCKET"
gsutil cp data/ExcludedAdNetworks.csv gs://$GCLOUD_BUCKET/
gsutil cp data/AdNetworkScores.csv gs://$GCLOUD_BUCKET/

echo "Updating ssh configuration"
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_github

#echo "Creating Datastore entities"
#mvn exec:java@create-entities

echo "Project ID: $DEVSHELL_PROJECT_ID"