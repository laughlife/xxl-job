package com.xxl.job.core.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xxl.job.core.openapi.model.LogResult;
import com.xxl.tool.core.DateTool;
import com.xxl.tool.core.StringTool;
import com.xxl.tool.io.FileTool;

/**
 * store trigger log in each log-file
 *
 * @author xuxueli 2016-3-12 19:25:12
 */
public class XxlJobFileAppender {
	private static final Logger logger = LoggerFactory.getLogger(XxlJobFileAppender.class);

	/**
	 * log base path
	 *
	 * strut like:
	 * 	---/
	 * 	---/gluesource/10_1514171108000.js
	 * 	---/callbacklogs/xxl-job-callback-1761412677119.log
	 * 	---/2017-12-25/639.log
	 * 	---/2017-12-25/821.log
	 *
	 */
	private static String logBasePath = "/data/applogs/xxl-job/jobhandler";
	private static String glueSrcPath = logBasePath.concat(File.separator).concat("gluesource");
	private static String callbackLogPath = logBasePath.concat(File.separator).concat("callbacklogs");
	public static void initLogPath(String logPath) throws IOException {
		// init
		if (StringTool.isNotBlank(logPath)) {
			logBasePath = logPath.trim();
		}
		// mk base dir
		File logPathDir = new File(logBasePath);
        FileTool.createDirectories(logPathDir);
		logBasePath = logPathDir.getPath();

		// mk glue dir
		File glueBaseDir = new File(logPathDir, "gluesource");
        FileTool.createDirectories(glueBaseDir);
		glueSrcPath = glueBaseDir.getPath();
	}
	public static String getLogPath() {
		return logBasePath;
	}
	public static String getGlueSrcPath() {
		return glueSrcPath;
	}
	public static String getCallbackLogPath() {
		return callbackLogPath;
	}

	/**
	 * log filename, like "logPath/yyyy-MM-dd/9999.log"
	 *
	 * @param logId log id
	 * @return      log file name
	 */
	public static String makeLogFileName(Date triggerDate, long logId) {

		// "filePath/yyyy-MM-dd"
		File logFilePath = new File(getLogPath(), DateTool.formatDate(triggerDate));
        try {
            FileTool.createDirectories(logFilePath);
        } catch (IOException e) {
            throw new RuntimeException("XxlJobFileAppender makeLogFileName error, logFilePath:"+ logFilePath.getPath(), e);
        }

        // filePath/yyyy-MM-dd/9999.log
        return logFilePath.getPath()
                .concat(File.separator)
                .concat(String.valueOf(logId))
                .concat(".log");
	}

	/**
	 * append log
	 *
	 * @param logFileName	log file name
	 * @param appendLog		append log
	 */
	public static void appendLog(String logFileName, String appendLog) {

		// valid
		if (StringTool.isBlank(logFileName) || appendLog == null) {
			return;
		}

		// append log
        try {
            FileTool.writeLines(logFileName, List.of(appendLog), true);
        } catch (IOException e) {
            throw new RuntimeException("XxlJobFileAppender appendLog error, logFileName:"+ logFileName, e);
        }
	}

	/**
	 * support read log-file
	 *
	 * @param logFileName	log file name
	 * @param fromLineNum	from line num
	 * @return log content
	 */
	public static LogResult readLog(String logFileName, final int fromLineNum){

		// valid
		if (StringTool.isBlank(logFileName)) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not found", true);
		}
		if (!FileTool.exists(logFileName)) {
            return new LogResult(fromLineNum, 0, "readLog fail, logFile not exists", true);
		}

		// read data
        StringBuilder logContentBuilder = new StringBuilder();
        // num: [from, to], start as 1
        int toLineNum = 0;
        int currentLineNum = 0;

        // do read with charset fallback to handle encoding issues
        try (BufferedReader reader = createRobustReader(logFileName)) {
            String line;
            while ((line = reader.readLine()) != null) {
                // refresh line num
                currentLineNum++;

                // valid
                if (currentLineNum < fromLineNum) {
                    continue;
                }

                // collect line data
                toLineNum = currentLineNum;
                logContentBuilder.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            logger.error("XxlJobFileAppender readLog error, logFileName:{}, fromLineNum:{}", logFileName, fromLineNum, e);
        }

        // result
        return new LogResult(fromLineNum, toLineNum, logContentBuilder.toString(), false);
	}

	/**
	 * Create a BufferedReader that handles encoding errors gracefully.
	 * Uses UTF-8 with REPLACE action for malformed input to avoid MalformedInputException.
	 */
	private static BufferedReader createRobustReader(String logFileName) throws IOException {
		FileInputStream fis = new FileInputStream(logFileName);
		// Use UTF-8 decoder with REPLACE action - replaces malformed bytes with replacement character
		InputStreamReader isr = new InputStreamReader(fis, 
			StandardCharsets.UTF_8.newDecoder()
				.onMalformedInput(CodingErrorAction.REPLACE)
				.onUnmappableCharacter(CodingErrorAction.REPLACE));
		return new BufferedReader(isr);
	}

}
