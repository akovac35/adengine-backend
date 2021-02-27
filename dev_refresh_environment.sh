source cecho.sh

warn "Setting project to adengine-backend"
gcloud config set project adengine-backend

warn "Exporting GCLOUD_PROJECT and GCLOUD_BUCKET"
export GCLOUD_PROJECT=$DEVSHELL_PROJECT_ID
export GCLOUD_BUCKET=$DEVSHELL_PROJECT_ID-media

warn "Updating files on $GCLOUD_BUCKET"
gsutil cp data/ExcludedAdNetworks.csv gs://$GCLOUD_BUCKET/
gsutil cp data/AdNetworkScores.csv gs://$GCLOUD_BUCKET/

gitkeyfile=~/.ssh/id_github
if test -f $gitkeyfile; then
    warn "Updating ssh configuration for github updates"
    eval "$(ssh-agent -s)"
    ssh-add $gitkeyfile
fi