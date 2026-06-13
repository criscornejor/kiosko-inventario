# Etapa 1: compilación
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar primero el wrapper y el pom para cachear la descarga de dependencias
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copiar el código fuente y compilar (los tests se ejecutan en CI, no en la imagen)
COPY src src
RUN ./mvnw package -DskipTests -B

# Etapa 2: imagen de ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
