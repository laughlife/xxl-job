package com.xxl.job.core.openapi.model;


/**
 * Created by xuxueli on 16/7/22.
 */
import lombok.Data;

@Data
public class TriggerRequest{

    // job base info
    private int jobId;

    // job execute info
    private String executorHandler;
    private String executorParams;
    private String executorBlockStrategy;
    private int executorTimeout;

    // log info
    private long logId;
    private long logDateTime;

    // glue info
    private String glueType;
    private String glueSource;
    private long glueUpdatetime;

    private int broadcastIndex;
    private int broadcastTotal;

    private String pythonExecPath;

}
