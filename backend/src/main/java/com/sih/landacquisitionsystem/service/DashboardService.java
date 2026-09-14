package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.model.Project;
import com.sih.landacquisitionsystem.model.Project.Status;
import com.sih.landacquisitionsystem.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private ProjectRepository projectRepository;

    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Get all projects
        List<Project> allProjects = projectRepository.findAll();
        summary.put("totalProjects", allProjects.size());

        // Count by status
        long notifiedCount = allProjects.stream()
                .filter(p -> p.getStatus() == Status.NOTIFIED)
                .count();
        summary.put("notifiedCount", notifiedCount);

        long awardedCount = allProjects.stream()
                .filter(p -> p.getStatus() == Status.AWARDED)
                .count();
        summary.put("awardedCount", awardedCount);

        long compensationPaidCount = allProjects.stream()
                .filter(p -> p.getStatus() == Status.COMPENSATION_PAID)
                .count();
        summary.put("compensationPaidCount", compensationPaidCount);

        long possessionTakenCount = allProjects.stream()
                .filter(p -> p.getStatus() == Status.POSSESSION_TAKEN)
                .count();
        summary.put("possessionTakenCount", possessionTakenCount);

        long rrCompleteCount = allProjects.stream()
                .filter(p -> p.getStatus() == Status.RR_COMPLETE)
                .count();
        summary.put("rrCompleteCount", rrCompleteCount);

        // Group by state
        Map<String, Long> projectsByState = allProjects.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Project::getState,
                        java.util.stream.Collectors.counting()
                ));
        summary.put("projectsByState", projectsByState);

        // Group by state and status for more detailed breakdown
        Map<String, Map<String, Long>> projectsByStateAndStatus = allProjects.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Project::getState,
                        java.util.stream.Collectors.collectingAndThen(
                                java.util.stream.Collectors.groupingBy(
                                        p -> p.getStatus().toString(),
                                        java.util.stream.Collectors.counting
                                ),
                                map -> map
                        )
                ));
        summary.put("projectsByStateAndStatus", projectsByStateAndStatus);

        return summary;
    }
}