package yyc.open.component.report;

import static yyc.open.component.report.commons.ReportConstants.ALARM_LISTENER;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yyc.open.component.report.commons.Processor;
import yyc.open.component.report.listener.Listener;

/**
 * {@link ReportStatus}
 *
 * @author <a href="mailto:siran0611@gmail.com">Elias.Yao</a>
 * @version ${project.version} - 2021/8/2
 */
public class ReportStatus {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportStatus.class);

	private Map<String, Listener> listeners = Maps.newHashMap();

	public ReportStatus() {
		// Construct listeners through processor annotation.
		ServiceLoader<Listener> listeners = ServiceLoader.load(Listener.class);
		for (Listener listener : listeners) {
			Processor spi = listener.getClass().getAnnotation(Processor.class);
			if (spi != null) {
				String name = spi.name();
				if (this.listeners.containsKey(name)) {
					throw new RuntimeException(
							"The @Processor value(" + name + ") repeat, for class(" + listener.getClass() + ") and class(" + this.listeners
									.get(name).getClass() + ").");
				}
				this.listeners.put(name, listener);
			}
		}
	}

	/**
	 * Publish the event that will be consumed by {@link Listener}
	 *
	 * @param id   Maybe ReportId or TaskId.
	 * @param type
	 */

	public void publishEvent(String id, ReportEvent.EventType type) {
		this.publishEvent(id, "", type);
	}

	public void publishEvent(String id, String msg, ReportEvent.EventType type) {
		ReportEvent event = ReportEvent.builder()
				.taskId(id)
				.type(type)
				.message(msg)
				.build();

		switch (type) {
			case CREATION:
				LOGGER.info("[ReportStatus] report-{} all subTask are all creation.", id);
				break;
			case PARTIALLY_COMPLETED:
				LOGGER.info("[ReportStatus] task-{} is completed.", id);
				break;
			case REPORT:
				LOGGER.info("[ReportStatus] report-{} subTask are all finished.", id);

				break;
			case COMPLETED:
				LOGGER.info("[ReportStatus] report-{} is generated completely", id);
				break;
			case FAIL:
				LOGGER.info("[ReportStatus] task-{} execute fail, add to fail-queue and wait to retry", event.getTaskId());
				this.listeners.get(ALARM_LISTENER).onFailure(event);
				break;
		}
	}

	public enum ReportStatusEnum {
		INSTANCE;

		ReportStatus reportStatus;

		public ReportStatus getReportStatus() {
			if (reportStatus == null) {
				reportStatus = new ReportStatus();
			}
			return reportStatus;
		}
	}
}
