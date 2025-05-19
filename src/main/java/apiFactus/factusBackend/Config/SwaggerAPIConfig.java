package apiFactus.factusBackend.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerAPIConfig {

    @Value("${factus.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server()
                .url(devUrl)
                .description("Servidor de desarrollo para Factus Backend");


        Contact contact = new Contact()
                .name("Soporte Factus")
                .email("soporte@factus.com")
                .url("https://www.factus.com");


        License mitLicense = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");


        Info apiInfo = new Info()
                .title("Factus API - Sistema de Facturación Electrónica")
                .version("1.0")
                .description("API REST para la gestión de facturación electrónica, compras, productos y reportes.")
                .termsOfService("https://www.factus.com/terms")
                .contact(contact)
                .license(mitLicense);

        return new OpenAPI()
                .info(apiInfo)
                .addServersItem(devServer);
    }
}
