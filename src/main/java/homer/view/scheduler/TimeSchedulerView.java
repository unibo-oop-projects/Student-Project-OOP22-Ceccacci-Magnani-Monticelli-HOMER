package homer.view.scheduler;

import java.util.Map;
import java.util.UUID;

import homer.controller.scheduler.TimeSchedulerController;
import homer.model.scheduler.TimeSchedule;

/**
 * View for the time scheduler.
 * 
 * @param <T> the type of parameter to target for the schedule.
 */
public interface TimeSchedulerView<T extends Comparable<T>> {

    /**
     * Sends an updated collection of schedules currently active to the view which
     * redraws them all.
     * 
     * @param schedules the map containing the schedules and their ids.
     */
    void updateSchedules(Map<UUID, TimeSchedule<T>> schedules);

    void removeSchedule(UUID scheduleId);

    /**
     * Sets the scheduler controller.
     * 
     * @param scheduler the scheduler controller.
     */
    void setScheduler(TimeSchedulerController<T> scheduler);

}
