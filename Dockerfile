FROM eclipse-temurin:17-jdk

MAINTAINER jcodinera.work

COPY target/libraryPortal.jar libraryPortal.jar

ENTRYPOINT ["java","-jar","/libraryPortal.jar"]