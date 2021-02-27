source cecho.sh

info "Creating project"
gcloud projects create adengine-backend
gcloud config set project adengine-backend
warn "Project ID: $DEVSHELL_PROJECT_ID"

info "Creating adengine-backend"
gcloud app create --region "us-central"

info "Creating bucket: gs://$DEVSHELL_PROJECT_ID-media"
gsutil mb gs://$DEVSHELL_PROJECT_ID-media

