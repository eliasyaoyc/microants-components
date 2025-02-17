package yyc.open.component.report.autoconfigure;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yyc.open.component.report.Report;
import yyc.open.component.report.ReportBuilder;
import yyc.open.component.report.ReportConfig;
import yyc.open.component.report.ReportRuntime;
import yyc.open.component.report.commons.beans.BeansKit;

/**
 * {@link ReportAutoConfiguration}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2021/7/28
 */
@Configuration
@EnableConfigurationProperties(ReportProperties.class)
public class ReportAutoConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportAutoConfiguration.class);
	private static final String[] TMPS = new String[]{"barOption.ftl", "crossBarOption.ftl", "lineOption.ftl", "pieOption.ftl",
			"echarts-util.js", "html2pdf.js", "html.ftl","sw-pdf.js","sw.js","sw_barOption.ftl","sw_crossBarOption.ftl",
			"sw_lineOption.ftl","sw_pieOption.ftl"};

	@Bean
	@ConditionalOnClass(ReportProperties.class)
	public Report report(ReportProperties properties) {
		ReportConfig reportConfig = new ReportConfig();
		BeansKit.copyProperties(properties, reportConfig);

		if (!new File(reportConfig.getOutputPath()).isAbsolute()) {
			String parentPath = System.getProperty("user.dir");
			File file;
			if (parentPath.endsWith(File.separator) || reportConfig.getOutputPath().startsWith(File.separator)) {
				file = new File(parentPath + reportConfig.getOutputPath());
			} else {
				file = new File(parentPath + File.separator + reportConfig.getOutputPath());
			}
			if (!file.exists()) {
				file.mkdirs();
			}
			reportConfig.setOutputPath(file.getAbsolutePath());
		}

		// remove tmp files.
		for (String tmp : TMPS) {
			String tempPath = System.getProperty("java.io.tmpdir").endsWith(File.separator) ?
					System.getProperty("java.io.tmpdir") + tmp :
					System.getProperty("java.io.tmpdir") + File.separator + tmp;
			File file = new File(tempPath);
			if (file.exists()) {
				file.delete();
			}
		}

		ReportRuntime reportRuntime = new ReportRuntime();
		reportRuntime.start(properties.getEchart(),reportConfig);
		LOGGER.info("[ReportRuntime] start success.");

		return ReportBuilder.builder().config(reportConfig).build();
	}


}
