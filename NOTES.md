# NOTES

This file contains information which may or may not be relevant years from when it was prepared :)

## Enabling git on gcloud

Sure gcloud can work with repositories, but to access git at will and without any extra costs, run the following commands:

```sh
eval "$(ssh-agent -s)"
mkdir ~/.ssh
chmod 700 ~/.ssh
ssh-keyscan -t rsa github.com > ~/.ssh/known_hosts.github
cd ~.ssh
ssh-keygen -t rsa -b 4096 -N '' -f id_github -C aleksander.kovac@gmail.com
chmod 600 ~/.ssh/*
ssh-add ~/.ssh/id_github

git clone git@github.com:akovac35/tmp.git
git config --global user.email "aleksander.kovac@gmail.com"
git config --global user.name "Aleksander Kovač"
```
## Running custom script files

You may need chmod 700 for those.

## Useful references

* https://github.com/GoogleCloudPlatform/java-docs-samples
* https://github.com/GoogleCloudPlatform/endpoints-quickstart
* https://google.qwiklabs.com/focuses/1060?parent=catalog
* https://github.com/GoogleCloudPlatform/training-data-analyst