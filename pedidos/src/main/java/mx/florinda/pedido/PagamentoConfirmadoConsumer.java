package mx.florinda.pedido;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.time.LocalDateTime;

@ApplicationScoped
public class PagamentoConfirmadoConsumer {

    @Incoming("pagamentosConfirmados")
    public Uni<Void> consome(PagamentoConfirmadoEvent evento) {
        System.out.println("confirmando pagamento " + evento.pagamentoId + " pedidoId " + evento.pedidoId);
        return Panache.withTransaction(() -> {
            return Pedido.<Pedido>findById(evento.pedidoId)
                    .onItem().ifNotNull().invoke(pedido -> {
                        pedido.status = StatusPedido.PAGO;
                        pedido.dataHora = LocalDateTime.now();
                    });
        }).replaceWithVoid();
    }
}