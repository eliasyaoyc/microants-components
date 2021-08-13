package yyc.open.framework.microants.components.kit.report;

import lombok.Getter;
import yyc.open.framework.microants.components.kit.common.file.FileKit;
import yyc.open.framework.microants.components.kit.report.exceptions.ReportException;

import java.io.IOException;

/**
 * {@link ReportPlatforms}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2021/8/6
 */
@Getter
public enum ReportPlatforms {
    WINDOWS("Windows", "phantomjs-windows.exe"),
    MACOS("Mac OS X", "phantomjs-macosx"),
    UNIX("Unix", "phantomjs-linux");

    String name;
    String path;

    ReportPlatforms(String name, String path) {
        this.name = name;
        this.path = path;
    }

    /**
     * Returns the corresponding platform through input parma.
     *
     * @param name
     * @return
     */
    public static ReportPlatforms getPlatforms(String name) {
        for (ReportPlatforms pf : ReportPlatforms.values()) {
            if (name.equals(pf.getName())) {
                return pf;
            }
        }
        throw new IllegalArgumentException("[Report Runtime] input parameter is not support.");
    }

    public static void runPhantomStartCommand(ReportConfig.GlobalConfig config) {
        String property = System.getProperty("os.name");
        ReportPlatforms platforms = ReportPlatforms.getPlatforms(property);
        String path = FileKit.getResourcePath(config.getExecPath() + platforms);

        String p = System.getProperty("os.name").contains(ReportPlatforms.WINDOWS.getName()) ? path.substring(1) : path;

        String command = new StringBuilder(p)
                .append(" ")
                .append(FileKit.getResourcePath(config.getJsPath()))
                .append(" -s -p ")
                .append(config.getPort()).toString();

        try {
            Runtime.getRuntime().exec("chmod 777 " + p);
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new ReportException(String.format("[Report Runtime] exec command: %s fail", command), e);
        }
    }

    public static void runPhantomStartCommand(int port, String execPath, String jsPath) {
        String command = new StringBuilder(execPath)
                .append(" ")
                .append(jsPath)
                .append(" -s -p ")
                .append(port).toString();

        try {
            Runtime.getRuntime().exec("chmod 777 " + execPath);
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new ReportException(String.format("[Report Runtime] exec command: %s fail", command), e);
        }
    }

    public static void pdfConvertCommand(String htmlPath, String pdfName, String outputPath, String execPath, String jsPdfPath) {
        String property = System.getProperty("os.name");
        ReportPlatforms platforms = ReportPlatforms.getPlatforms(property);
        String path = FileKit.getResourcePath(execPath + platforms);

        String p = System.getProperty("os.name").contains(ReportPlatforms.WINDOWS.getName()) ? path.substring(1) : path;

        String command = new StringBuilder(p)
                .append(" ")
                .append(FileKit.getResourcePath(jsPdfPath))
                .append(" " + htmlPath)
                .append(" " + pdfName)
                .append(" " + outputPath)
                .toString();
        try {
            Runtime.getRuntime().exec("chmod 777 " + p);
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new ReportException(String.format("[Report Runtime] exec command: %s fail", command), e);
        }
    }
}
