package com.tiendaonline.pedidos.batch;


import com.tiendaonline.pedidos.client.InventarioClient;
import com.tiendaonline.pedidos.entity.EstadoPedido;
import com.tiendaonline.pedidos.entity.Pedido;
import com.tiendaonline.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExpiracionPedidosJobConfig {
    private final PedidoRepository pedidoRepository;
    private final InventarioClient inventarioClient;

    @Bean
    @StepScope
    public RepositoryItemReader<Pedido> pedidoExpiradoReader() {
        LocalDateTime hace1Dia = LocalDateTime.now().minusHours(24);

        return new RepositoryItemReaderBuilder<Pedido>()
                .name("pedidoExpiradoReader")
                .repository(pedidoRepository)
                .methodName("findByEstadoAndFechaCreacionBefore")
                .arguments(List.of(EstadoPedido.CREADO, hace1Dia))
                .pageSize(10)
                .sorts(Map.of("fechaCreacion", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<Pedido, Pedido> pedidoExpiradoProcessor() {
        return pedido -> {
            log.info("Marcando como EXPIRADO el pedido ID: {} (fecha creación: {})",
                    pedido.getId(), pedido.getFechaCreacion());
            pedido.setEstado(EstadoPedido.EXPIRADO);
            pedido.setFechaActualizacion(LocalDateTime.now());
            return pedido;
        };
    }

    @Bean
    public ItemWriter<Pedido> pedidoExpiradoWriter() {
        return chunk -> {
            for (Pedido pedido : chunk.getItems()) {
                inventarioClient.liberarReservas(pedido.getId());
                pedidoRepository.save(pedido);
            }
        };
    }
    @Bean
    public Step expirarPedidosStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("expirarPedidosStep", jobRepository)
                .<Pedido, Pedido>chunk(10, transactionManager)
                .reader(pedidoExpiradoReader())
                .processor(pedidoExpiradoProcessor())
                .writer(pedidoExpiradoWriter())
                .build();
    }
    @Bean
    public Job expirarPedidosJob(JobRepository jobRepository,
                                 Step expirarPedidosStep,
                                 ExpiracionJobListener listener) {
        return new JobBuilder("expirarPedidosJob", jobRepository)
                .listener(listener)
                .start(expirarPedidosStep)
                .build();
    }
}
