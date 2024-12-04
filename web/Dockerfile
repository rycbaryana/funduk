FROM amazoncorretto:20 AS runtime
EXPOSE 8081:8081
RUN mkdir /funduk
COPY  ./web/build/libs/web-all.jar /funduk/
ENTRYPOINT ["java","-jar","/funduk/web-all.jar"]