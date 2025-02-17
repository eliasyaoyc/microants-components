package yyc.open.component.report.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import yyc.open.component.report.commons.ReportEnums;

/**
 * {@link ReportData}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2021/8/2
 */
@Setter
@Getter
public class ReportData {

	/**
	 * Internally generated.
	 */
	private String taskId;

	/**
	 * The absolute template path for generation.
	 */
	private String templateUrl;

	/**
	 * Chart title.
	 */
	private String title;

	private ReportEnums type;

	/**
	 * The legend name that only to Bar and Pie.
	 */
	private String legendName;

	private List<Object[]> xdatas;

	private Object[] yDatas;

	/**
	 * Only scope for multi pie.
	 */
	private Object[] subDatas;

	private List<String> texts;

	private List<List<String>> tables;

	private Map<String, Object> statistics = new HashMap<>();

	private String statDescription;

	private String base64;

	/**
	 * Create text data.
	 *
	 * @param texts
	 * @return ReportData instance.
	 */
	public static ReportData text(List<String> texts) {
		ReportData reportData = new ReportData();
		reportData.setTexts(texts);
		reportData.setType(ReportEnums.TABLE);
		return reportData;
	}

	public static ReportData text(String texts, String sperator) {
		List<String> data = Arrays.stream(texts.split(sperator)).collect(Collectors.toList());
		return text(data);
	}

	/**
	 * Create table data.
	 *
	 * @param tables
	 * @return ReportData instance.
	 */
	public static ReportData table(List<List<String>> tables) {
		ReportData reportData = new ReportData();
		reportData.setTables(tables);
		reportData.setType(ReportEnums.TABLE);
		return reportData;
	}

	/**
	 * Create statistics data.
	 *
	 * @param statistics
	 * @return ReportData instance.
	 */
	public static ReportData statistics(Map<String, Object> statistics) {
		ReportData reportData = new ReportData();
		reportData.setStatistics(statistics);
		reportData.setType(ReportEnums.TABLE);
		return reportData;
	}

	/**
	 * Create statistics data.
	 *
	 * @param statistics
	 * @return
	 */
	public static ReportData statistics(Map<String, Object> statistics, String description) {
		ReportData reportData = new ReportData();
		reportData.setStatistics(statistics);
		reportData.setStatDescription(description);
		reportData.setType(ReportEnums.TABLE);
		return reportData;
	}


	public static ReportData image(String image) {
		ReportData reportData = new ReportData();
		reportData.setBase64(image);
		reportData.setType(ReportEnums.TABLE);
		return reportData;
	}

	/**
	 * Create echarts data.
	 *
	 * @param title
	 * @param type
	 * @param xdatas
	 * @param yDatas
	 * @return ReportData instance.
	 */
	public static ReportData echarts(String title,
			ReportEnums type,
			List<Object[]> xdatas,
			Object[] yDatas) {
		return echarts(title, "", type, "", xdatas, yDatas);
	}

	public static ReportData echarts(String title,
			ReportEnums type,
			String legendName,
			List<Object[]> xdatas,
			Object[] yDatas) {
		return echarts(title, "", type, legendName, xdatas, yDatas);
	}

	public static ReportData echarts(String title,
			String templateUrl,
			ReportEnums type,
			String legendName,
			List<Object[]> xdatas,
			Object[] yDatas) {
		ReportData reportData = new ReportData();
		reportData.setTitle(title);
		reportData.setType(type);
		reportData.setTemplateUrl(templateUrl);
		reportData.setLegendName(legendName);
		reportData.setXdatas(xdatas);
		reportData.setYDatas(yDatas);
		return reportData;
	}

	public static ReportData echarts(String title, Map<String, Object> message, ReportEnums type) {
		List<String> keys = new ArrayList<>(message.keySet());
		List<String> values = message.values().stream().map(val -> val.toString()).collect(Collectors.toList());

		List<Object[]> k = new ArrayList<>();

		if (type == ReportEnums.LINE) {
			k.add(values.toArray());
			return echarts(title, type, title, k, keys.toArray());
		}

		k.add(keys.toArray());
		return echarts(title, type, title, k, values.toArray());
	}

	public static ReportData echarts(String title, String legendName, Map<String, Object> message, ReportEnums type) {
		List<String> keys = new ArrayList<>(message.keySet());
		List<String> values = message.values().stream().map(val -> val.toString()).collect(Collectors.toList());

		List<Object[]> k = new ArrayList<>();

		if (type == ReportEnums.LINE) {
			k.add(values.toArray());
			return echarts(title, type, legendName, k, keys.toArray());
		}

		k.add(keys.toArray());
		return echarts(title, type, legendName, k, values.toArray());
	}
}
