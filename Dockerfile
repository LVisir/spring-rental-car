FROM eclipse-temurin:17-jdk

COPY entrypoint.sh /entrypoint.sh

COPY src/main/resources/application.properties /config/

COPY target/RentalCar-0.0.1-SNAPSHOT.jar /RentalCar-0.0.1-SNAPSHOT.jar

CMD ["/entrypoint.sh"]