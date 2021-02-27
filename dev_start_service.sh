source cecho.sh

info "Starting service"
warn "Once the service is started: use the cloud shell web preview function to open the preview service url."
error "The web preview function starts with a default path which does not have a binding - change the path to: /swagger-ui.html"
read -p "Press enter key to continue"
mvn spring-boot:run -Dspring-boot.run.arguments="logging.level.root=info, --logging.level.com.github.akovac35=trace, --com.github.akovac35.cacheServiceTimerIntervalSeconds=10"