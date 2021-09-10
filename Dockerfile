FROM adoptopenjdk/openjdk11

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN mvn clean install

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]

EXPOSE 8080