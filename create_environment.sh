source cecho.sh

warn "Creating project"
gcloud projects create adengine-backend
gcloud config set project adengine-backend
warn "Project: $DEVSHELL_PROJECT_ID"

warn "Creating app engine for the current project"
gcloud app create --region "us-central"

warn "Creating bucket for the current project"
gsutil mb gs://$DEVSHELL_PROJECT_ID-media
warn "Bucket: gs://$DEVSHELL_PROJECT_ID-media"

