package cl.kiosko.ms_inventario.Client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class NotificacionesClient {

    private static final Logger log = LoggerFactory.getLogger(NotificacionesClient.class);

    private final RestClient restClient;
    private final String baseUrl;

    public NotificacionesClient(RestClient.Builder restClientBuilder,
                                @Value("${app.clients.notificaciones.url:http://localhost:8089}") String baseUrl) {
        this.restClient = restClientBuilder.build();
        this.baseUrl = baseUrl;
    }

    public void enviarAlertaStock(String destinatario, String producto, Integer stockActual, String authorizationHeader) {
        if (!StringUtils.hasText(destinatario)) {
            log.debug("No se envio alerta de stock porque no hay destinatario configurado");
            return;
        }

        Map<String, Object> request = Map.of(
                "destinatario", destinatario,
                "nombrePlantilla", "ALERTA_STOCK",
                "variables", Map.of(
                        "producto", producto,
                        "stockActual", String.valueOf(stockActual)));

        try {
            restClient.post()
                    .uri(baseUrl + "/api/notificaciones/enviar")
                    .headers(headers -> addAuthorization(headers, authorizationHeader))
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.warn("No fue posible notificar alerta de stock para producto {}: {}", producto, ex.getMessage());
        }
    }

    private void addAuthorization(HttpHeaders headers, String authorizationHeader) {
        if (StringUtils.hasText(authorizationHeader)) {
            headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        }
    }
}
