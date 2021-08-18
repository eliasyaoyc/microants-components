package yyc.open.framework.microants.components.kit.report;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Encoder;
import yyc.open.framework.microants.components.kit.common.file.FileKit;
import yyc.open.framework.microants.components.kit.common.uuid.UUIDsKit;
import yyc.open.framework.microants.components.kit.common.validate.Asserts;
import yyc.open.framework.microants.components.kit.common.validate.NonNull;
import yyc.open.framework.microants.components.kit.report.commons.ReportEnums;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.CRC32;

/**
 * {@link ReportTaskRegistry}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2021/7/30
 */
public class ReportTaskRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportTaskRegistry.class);

    private static final String METADATA_FILE = ".metadata";
    private Gson gson;

    private ReportStatus reportStatus;
    private Map<String, ReportResource /* reportId*/> reports;
    private Map<String, ReportTask> tasks;
    private Map<String, ReportTask> failTasks;
    private Map<Long, String /*checksum, image file*/> paths;
    private Map<Long, String /*checksum, image base64*/> base64Maps;
    private final String output;

    {
        gson = new GsonBuilder().create();
    }

    private ReportTaskRegistry(ReportStatus reportStatus, final String output) {
        this.reportStatus = reportStatus;
        this.tasks = Maps.newConcurrentMap();
        this.failTasks = Maps.newConcurrentMap();
        this.paths = Maps.newConcurrentMap();
        this.base64Maps = Maps.newConcurrentMap();
        this.reports = Maps.newConcurrentMap();
        this.output = output;
        load(output);
    }

    /**
     * load the report dir and generation relevant mapping.
     */
    private void load(String path) {
        Asserts.hasText(path);
        this.reports = gson.fromJson(readJsonFile(this.output + METADATA_FILE + "/report.json"), Map.class);
        this.tasks = gson.fromJson(readJsonFile(this.output + METADATA_FILE + "/tasks.json"), Map.class);
        this.failTasks = gson.fromJson(readJsonFile(this.output + METADATA_FILE + "/failTasks.json"), Map.class);
        this.paths = gson.fromJson(readJsonFile(this.output + METADATA_FILE + "/paths.json"), Map.class);
        this.base64Maps = gson.fromJson(readJsonFile(this.output + METADATA_FILE + "/base64Maps.json"), Map.class);
    }

    String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            if (!jsonFile.exists()) {
                return gson.toJson(Maps.newHashMap());
            }
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Store all metadata in .metadata file.
     */
    void close() {
        Asserts.hasText(this.output);
        File f = new File(this.output + METADATA_FILE);
        if (!f.exists()) {
            f.mkdirs();
        }

        String reports = gson.toJson(this.reports);
        String tasks = gson.toJson(this.tasks);
        String failTasks = gson.toJson(this.failTasks);
        String paths = gson.toJson(this.paths);
        String base64Maps = gson.toJson(this.base64Maps);

        BufferedWriter writer = null;
        try {
            String file = f.getAbsolutePath().endsWith(File.separator) ? f.getAbsolutePath() : f.getAbsolutePath() + File.separator;
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file + "report.json"), StandardCharsets.UTF_8));
            writer.write(reports);

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file + "tasks.json"), StandardCharsets.UTF_8));
            writer.write(tasks);

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file + "failTasks.json"), StandardCharsets.UTF_8));
            writer.write(failTasks);

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file + "paths.json"), StandardCharsets.UTF_8));
            writer.write(paths);

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file + "base64Maps.json"), StandardCharsets.UTF_8));
            writer.write(base64Maps);

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * Delete report all resource(image,report self and mapping in memory).
     *
     * @param reportId represent report identity id.
     */
    void deleteReport(String reportId) throws IOException {
        if (this.reports.containsKey(reportId)) {
            // start delete related resource.
            ReportResource resource = this.reports.remove(reportId);
            FileKit.deleteFile(resource.getReportRootPath());
            List<String> taskId = resource.getTaskId();
            List<Long> checksums = resource.getChecksums();
            taskId.stream().forEach(id -> {
                this.tasks.remove(id);
                this.failTasks.remove(id);
            });
            checksums.stream().forEach(cs -> {
                this.paths.remove(cs);
                this.base64Maps.remove(cs);
            });
        }
    }

    void addReportResource(ReportResource reportResource) {
        this.reports.putIfAbsent(reportResource.getReportId(), reportResource);
    }

    public void updateChecksum(String reportId, String taskId, String path) {
        // Notice：the partially completed event only scoped to echarts generation.
        ReportTask task = this.tasks.remove(taskId);
        if (task == null) {
            return;
        }
        Long checksum = checksum(task);
        this.paths.putIfAbsent(checksum, path);
        this.base64Maps.putIfAbsent(checksum, imageToBase64ByLocal(path));

        ReportResource reportResource = this.reports.get(reportId);
        if (reportResource != null) {
            reportResource.getChecksums().add(checksum);
        }
    }

    public enum ReportRegistryEnum {
        INSTANCE;

        ReportTaskRegistry reportRegistry;

        public ReportTaskRegistry getReportRegistry(ReportStatus reportStatus, @Nonnull String output) {
            if (reportRegistry == null) {
                reportRegistry = new ReportTaskRegistry(reportStatus, output);
            }
            return reportRegistry;
        }
    }

    /**
     * Returns the task collections to support parallel execution.
     *
     * @param config need to generate report.
     * @param entity data need to generate report.
     * @return
     */
    public List<ReportTask> createTask(ReportConfig config, ReportMetadata entity) {
        List<ReportTask> rest = new ArrayList<>();
        // Record report resources.
        ReportResource.ReportResourceBuilder resourceBuilder = ReportResource.builder()
                .reportId(entity.getReportId())
                .checksums(new ArrayList<>())
                .reportRootPath(entity.getPath());

        List<String> taskIds = new ArrayList<>();

        entity.getContent().getData().stream().forEach(data -> {
            data.stream().forEach(item -> {
                if (ReportEnums.isCharts(item.getType())) {
                    String taskId = UUIDsKit.base64UUID();
                    ReportTask task = ReportTask.builder()
                            .reportId(entity.getReportId())
                            .reportName(entity.getReportName())
                            .outputPath(entity.getPath())
                            .templatePath(item.getTemplateUrl())
                            .taskId(taskId)
                            .data(ReportTask.parseReportData(item))
                            .build();
                    task.setReportType(item.getType());

                    item.setTaskId(taskId);

                    // Calculate task checksum.
                    Long checksum = checksum(task);
                    if (this.paths.containsKey(checksum)) { // Must be exists.
                        task.setChecksum(checksum); // Used to clear resource.
                        String value = ""; // base64 if report type is pdf or html, otherwise url.
                        if (entity.getReportType() == ReportEnums.WORD) {
                            // Find the duplicate task so used result directly.
                            value = this.paths.get(checksum);
                        } else if (entity.getReportType() == ReportEnums.HTML || entity.getReportType() == ReportEnums.PDF) {
                            value = this.base64Maps.get(checksum);
                        }
                        entity.setSubTaskExecutionResult(taskId, value);
                    } else {
                        taskIds.add(taskId);
                        rest.add(task);
                        this.tasks.put(taskId, task);
                    }
                }
            });
            addReportResource(resourceBuilder.taskId(taskIds).build());
        });
        this.reportStatus.publishEvent(entity.getReportId(), ReportEvent.EventType.CREATION);
        return rest;
    }


    /**
     * Calculate the task checksum.
     *
     * @param task
     * @return checksum.
     */
    private Long checksum(ReportTask task) {
        CRC32 crc32 = new CRC32();
        crc32.update(task.getData().getBytes(StandardCharsets.UTF_8), 0, task.getData().length());
        return crc32.getValue();
    }

    /**
     * Add the task that
     *
     * @param taskId
     */
    public void addToFailQueue(@NonNull String taskId) {
        if (!this.tasks.containsKey(taskId)) {
            return;
        }

        ReportTask failTask = this.tasks.remove(taskId);
        if (!Objects.isNull(failTask)) {
            this.failTasks.put(taskId, failTask);
        }
    }

    /**
     * Generation base64 through input file path.
     *
     * @param imgFile input file path.
     * @return base64.
     */
    static String imageToBase64ByLocal(String imgFile) {
        if (!new File(imgFile).exists()) {
            return null;
        }
        InputStream in = null;
        byte[] data = null;

        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return "data:img/jpg;base64," + encoder.encode(data);
    }
}
