#!/bin/bash

# THe issue is that the Maven Release Plugin insists on creating a tag, and git-flow also wants to create a tag.
# Secondly, the Maven Release Plugin updates the version number to the next SNAPSHOT release before you can
# merge the changes into master, so you end with the SNAPSHOT version number in master, and this is highly undesired.
# This script solves this by doing changes locally, only pushing at the end.
# All git commands are fully automated, without requiring any user input.
# See the required configuration options for the Maven Release Plugin to avoid unwanted pushs.

# CHANGE THESE BEFORE RUNNING THE SCRIPT!
# The version to be released
releaseVersion=0.45.0
# The next development version
developmentVersion=0.46.0-SNAPSHOT
# Provide an optional comment prefix, e.g. for your bug tracking system
scmCommentPrefix='EAIZEEBE-0.44.0: '

# Start the release by creating a new release branch
git checkout -b release/$releaseVersion develop

# The Maven release
mvn --batch-mode release:prepare release:perform -DscmCommentPrefix="$scmCommentPrefix" -DreleaseVersion=$releaseVersion -DdevelopmentVersion=$developmentVersion -Darguments="-Dmaven.deploy.skip=true"

# Clean up and finish
# get back to the develop branch
git checkout develop

# merge the version back into develop
git merge --no-ff -m "$scmCommentPrefix Merge release/$releaseVersion into develop" release/$releaseVersion
# go to the master branch
git checkout master
# merge the version back into master but use the tagged version instead of the release/$releaseVersion HEAD
git merge --no-ff -m "$scmCommentPrefix Merge previous version into master to avoid the increased version number" release/$releaseVersion~1
# Removing the release branch
git branch -D release/$releaseVersion
# Get back on the develop branch
git checkout develop
# Finally push everything
git push --all && git push --tags