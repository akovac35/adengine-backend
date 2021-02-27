


echo "Creating project"
gcloud projects create adengine-backend
gcloud config set project adengine-backend

echo "Creating adengine-backend"
gcloud app create --region "us-central"

echo "Making bucket: gs://$DEVSHELL_PROJECT_ID-media"
gsutil mb gs://$DEVSHELL_PROJECT_ID-media

echo "Project ID: $DEVSHELL_PROJECT_ID"