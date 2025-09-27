FROM maven:3.9.5-eclipse-temurin-17 AS build

# mvn files
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk

COPY entrypoint.sh /entrypoint.sh

COPY src/main/resources/application.properties /config/

COPY --from=build /target/RentalCar-0.0.1-SNAPSHOT.jar /RentalCar-0.0.1-SNAPSHOT.jar

CMD ["/entrypoint.sh"]