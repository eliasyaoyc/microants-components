package yyc.open.framework.microants.components.kit.report.entity;

import lombok.Getter;
import lombok.Setter;
import yyc.open.framework.microants.components.kit.report.commons.ReportEnums;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ReportEntity}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2021/8/1
 */
@Getter
@Setter
public class ReportEntity {
    /**
     * The type of report. e.g. pdf, word, html etc.
     */
    private ReportEnums reportType;

    /**
     * Report title.
     */
    private ReportTitle title;

    /**
     * Report info.
     */
    private ReportInfo info;

    /**
     * Report catalogue.
     */
    private ReportCatalogue catalogue;

    /**
     * Report content.
     */
    private ReportContent content;

    public ReportEntity(ReportEnums reportType, ReportTitle title, ReportInfo info, ReportCatalogue catalogue, ReportContent content) {
        this.reportType = reportType;
        this.title = title;
        this.info = info;
        this.catalogue = catalogue;
        this.content = content;
    }

    /**
     * Inner class about report title part.
     */
    @Getter
    @Setter
    public static class ReportTitle {
        private String background;
        private String title;
        private String description;
    }

    /**
     * Inner class about report info part (e.g. generate-time, report range time etc.)
     */
    @Getter
    @Setter
    public static class ReportInfo {
        private List<String> labels = new ArrayList<>();
        private List<String> values = new ArrayList<>();
    }

    /**
     * Inner class about report catalogue.
     */
    @Getter
    @Setter
    public static class ReportCatalogue {
        private List<String> chapter = new ArrayList<>();
        private List<List<String>> indices = new ArrayList<>();
    }

    /**
     * Inner class about report content.
     */
    @Getter
    @Setter
    public static class ReportContent {
        private List<String> chapter;
        private List<List<String>> indices = new ArrayList<>();
        private List<List<String>> description = new ArrayList<>();
        private List<List<Object>> data = new ArrayList<>();
    }

    /**
     * Construct `reportEntity` used builder.
     */
    public static ReportEntityBuilder builder() {
        return new ReportEntityBuilder();
    }
}

//    /**
//     * Chart title.
//     */
//    private String title;
//
//    /**
//     * The legend name that only to Bar and Pie.
//     */
//    private String legendName;
//
//
//    private List<Object[]> xdatas;
//
//    private Object[] yDatas;