# adengine-backend

A Google Cloud scalable Java REST microservice used by mobile clients for ad network metadata retrieval.

The service returns a list of ad network names and their metadata, sorted by a descending score, based on request context properties:

* platform
* osVersion
* appName
* appVersion
* countryCodeIso2

The service implements basic business rules to cover the requirements such as the following:

* Ad network X does not support Android 9 and it should not be in the list for such a device. It supports other os versions.
* Ad network Y should not be served in China.
* Ad network Y-OptOut should be present in list only if there is no ad network Y in list.

The service retrieves the data from a storage bucket and caches it in memory - cache in refreshed once per hour by a timer. The service is designed in such a way to enable uninterrupted operation without increasing response times while the cache is being refreshed. 

## Usage

Open gcloud console and:

* clone this git repository and cd to the cloned repository
* ```chmod 700 .``` for script execution
* FIRST TIME ONLY: ```create_environment.sh``` to create project and bucket
* ```source dev_refresh_environment.sh``` to deploy file changes to the bucket and **to set required environment variables**, e.g., when resuming development work
* ```dev_run_tests.sh``` to run unit tests
* ```dev_start_service.sh``` to start the service in preview (development) mode in the current console
* ```dev_publish_service.sh``` to publish the service from the current console to production - yikes! Next time setup the devops workflow ...

The service is available on a url similar to the following sample URL: http://adengine-backend.uc.r.appspot.com/swagger-ui.html

Note that requests in Swagger are slower because of result parsing and highlighting.

### Sample requests

The URLs provided here are intended for format reference only - consider yourself charged if they work.

* A request which filters out facebook ads because it is not allowed in China: http://adengine-backend.uc.r.appspot.com/api/adnetworkscores?countryCodeIso2=cn
* A request which includes facebook ads: http://adengine-backend.uc.r.appspot.com/api/adnetworkscores?countryCodeIso2=*
* A request which contains admob-optout because the list for France does not contain admob: http://adengine-backend.uc.r.appspot.com/api/adnetworkscores?countryCodeIso2=fr
* A request which does not contain admob-optout because the list contains admob: http://adengine-backend.uc.r.appspot.com/api/adnetworkscores?countryCodeIso2=*
* A request which does not contain admob because it does not support android 9: http://adengine-backend.uc.r.appspot.com/api/adnetworkscores?countryCodeIso2=br&platform=android&osVersion=9
* A request which contains admob: http://adengine-backend.uc.r.appspot.com/api/adnetworkscores?countryCodeIso2=br&platform=android&osVersion=*

## Reliability

The service is designed with reliability in mind:

* missing request context properties have defaults,
* problems with cache update do not impact service functionality - last valid cache continues to be used,
* if the number of result items per ad network type is too low, unfiltered items will be appended - something is better than nothing,
* duplicates are removed,
* ...

## Configuration

The service is configured with the following two files located in the project's storage bucket:

ExcludedAdNetworks.csv

```csv
adName,countryCodeIso2,appName,platform,osVersion,appVersion,excludeIfThisAdNamePresent
facebook ads,cn,*,*,*,*,-
admob-optout,*,*,*,*,*,admob
admob,*,*,android,9,*,-
...
```

AdNetworkScores.csv

```csv
adName,adScore,adType,countryCodeIso2
unruly media,4.94,3,cm
rubicon project,3.95,2,no
tradablebits media inc.,8.63,1,pt
receptiv,5.75,1,fr
tribal fusion media,1.3,3,cn
social game media,5.5,3,cn
aleksander,10,1,*
aleksander,10,2,*
aleksander,10,3,*
...
```

## Design considerations

The service is designed for about 10 000 entries in the AdNetworkScores.csv file and about 1000 entries in the ExcludedAdNetworks.csv file. This should not be a limitation because parameterizations are possible, see file examples.

On the hosting side, the service uses the standard variant of Java 11 Google App Engine, which supports rapid and high scalability. Automatic scaling is configured with criteria for cpu usage and req/s/instance. This enables Google Cloud to start and stop instances as needed. **The default configuration for this service permits Google Cloud to stop all service instances** if there are no requests in a given time frame, for cost cutting. This also means that the first request in such a state will be slower.
## TODO

* add authentication with tokens
* consider rate limiters
* add health checks
* add scalability tests
* add devops workflows
* add alert notifications
* add cache refresh on demand

## See also

[Notes](NOTES.md)

## Author

Aleksander Kovač
