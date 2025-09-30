# Usa uma imagem base com Java
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o JAR gerado para dentro do container
COPY target/pedido-service.jar app.jar

# Expõe a porta que sua aplicação usa
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
