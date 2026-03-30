## ETAPA 1 (construcción)
# Uso la imagen oficial de Gradle que ya inlcuye el JDK17
FROM gradle:8.14-jdk17-alpine AS builder

# Creo el directorio de trabajo en la imagen temporal
WORKDIR /app

# Copio el código fuente de mi máquina al contenedor temporal
COPY build.gradle settings.gradle ./

COPY src ./src

# Ejecuto el comando exacto para compilar e ignorar los tests
RUN gradle build -x test --no-daemon

# ETAPA 2: Ejecución
# Imagen ligera  que solo tiene el JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copio SOLO el archivo .jar desde la etapa anterior (desde el 'builder')
# Todo el código fuente, Gradle y el JDK de la etapa 1 se descartan y se destruyen
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]