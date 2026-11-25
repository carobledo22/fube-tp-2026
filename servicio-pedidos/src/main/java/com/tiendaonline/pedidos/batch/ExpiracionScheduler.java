package com.tiendaonline.pedidos.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiracionScheduler {

    private final JobLauncher jobLauncher;
    private final Job expirarPedidosJob;

    @Scheduled(cron = "0 0 3 * * *")
    public void ejecutarJobNocturno() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("ejecucionTimestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(expirarPedidosJob, params);
        } catch (Exception e) {
            log.error("Error al lanzar el Job de Expiración: {}", e.getMessage(), e);
        }
    }
}