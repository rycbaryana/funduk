FROM amazoncorretto:20 AS runtime
EXPOSE 8080:8080
RUN mkdir /funduk
COPY  ./server/build/libs/server-all.jar /funduk/
ENTRYPOINT ["java","-jar","/funduk/server-all.jar"]