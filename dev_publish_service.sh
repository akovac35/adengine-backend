source cecho.sh

warn "Generating temporary app.yaml"
envsubst < src/main/appengine/app.yaml > tmp-app.yaml

warn "Deploying service"
gcloud app deploy --appyaml=tmp-app.yaml

warn "Basic service information"
gcloud app describe

url=$(gcloud app describe | grep defaultHostname)
warn "Url is: "${url/#defaultHostname: }"/swagger-ui.html"

