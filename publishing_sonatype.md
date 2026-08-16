# Publishing to Sonatype

1. Open in IntelliJ IDEA as separate projects forge-server and forge-server-jetty


2. For forge-server
- 2.1. in build.gradle uncomment plugin gradle-nexus.publish-plugin and nexusPublishing
- 2.2. clean + build
- 2.3. publishToSonatype
- 2.4. manually run Gradle task with (with changing repo ID) closeSonatypeStagingRepository --staging-repository-id=com.bolyartech--58ba03b9-a6da-41c8-87f7-bc96bc642921
- 2.5. in https://central.sonatype.com/publishing/deployments manually publish
- 2.6. wait to get published
- 2.7. git tag -a vVersion -m "vVersion release"
- 2.8. git push origin --tags
- 2.9. in build.gradle comment back plugin gradle-nexus.publish-plugin and nexusPublishing


3. for forge-server-jetty
- 3.1. In settings.gradle comment the includes()
- 3.2. In gradle.build/dependencies comment api project(':forge-server')
- 3.3. refresh Gradle
- 3.4. clean + build
- 3.5. publishToSonatype
- 3.6. manually run Gradle task with (with changing repo ID) closeSonatypeStagingRepository --staging-repository-id=com.bolyartech--58ba03b9-a6da-41c8-87f7-bc96bc642921
- 3.7. in https://central.sonatype.com/publishing/deployments manually publish
- 3.8. git tag -a vVersion -m "vVersion release"
- 3.9. git push origin --tags
- 3.10. in build.gradle comment back plugin gradle-nexus.publish-plugin and nexusPublishing

# Prerequisites
## 1. gradle.properties

`~/.gradle/gradle.properties` with:
```
signing.keyId=DB2E9D2E
signing.password=
signing.secretKeyRingFile=/home/ogre/.gnupg/secring.gpg

ossrhUsername=token
ossrhPassword=password
```

Token is from: https://central.sonatype.com/usertoken


## 2. `.gnupg` dir

from `~/Documents/ogre/backup_sensitive`


