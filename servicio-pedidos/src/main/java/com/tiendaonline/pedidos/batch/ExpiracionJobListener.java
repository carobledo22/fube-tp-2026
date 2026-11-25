package com.tiendaonline.pedidos.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class ExpiracionJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("****** INICIANDO CORRIDA  DEL BATCH: {} (ID Ejecución: {}) ***",
                jobExecution.getJobInstance().getJobName(), jobExecution.getId());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long leidos = 0;
        long procesados = 0;
        long errores = 0;

        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            leidos += stepExecution.getReadCount();
            procesados += stepExecution.getWriteCount();
            errores += stepExecution.getSkipCount() + stepExecution.getRollbackCount();
        }

        Duration duracion = Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime());
        log.info("=============================================");
        log.info("RESUMEN DE CORRIDA - JOB EXPIRACIÓN DE PEDIDOS");
        log.info("Estado Final    : {}", jobExecution.getStatus());
        log.info("Pedidos leídos   :  {}", leidos);
        log.info("Pedidos expirados  : {}", procesados);
        log.info("Errores/Rollbacks  :  {}", errores);
        log.info("Duración total   : {} ms", duracion.toMillis());
        log.info("=============================================");
    }
}