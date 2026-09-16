package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.model.Project;
import com.sih.landacquisitionsystem.model.Project.Status;
import com.sih.landacquisitionsystem.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private ProjectRepository projectRepository;

    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        List<Project> allProjects = projectRepository.findAll();

        summary.put("totalProjects", allProjects.size());
        summary.put("notifiedCount", count(allProjects, Status.NOTIFIED));
        summary.put("awardedCount", count(allProjects, Status.AWARDED));
        summary.put("compensationPaidCount", count(allProjects, Status.COMPENSATION_PAID));
        summary.put("possessionTakenCount", count(allProjects, Status.POSSESSION_TAKEN));
        summary.put("rrCompleteCount", count(allProjects, Status.RR_COMPLETE));

        Map<String, Long> projectsByState = allProjects.stream()
                .filter(p -> p.getState() != null)
                .collect(Collectors.groupingBy(Project::getState, Collectors.counting()));
        summary.put("projectsByState", projectsByState);

        Map<String, Map<String, Long>> projectsByStateAndStatus = allProjects.stream()
                .filter(p -> p.getState() != null && p.getStatus() != null)
                .collect(Collectors.groupingBy(
                        Project::getState,
                        Collectors.groupingBy(
                                p -> p.getStatus().name(),
                                Collectors.counting()
                        )
                ));
        summary.put("projectsByStateAndStatus", projectsByStateAndStatus);

        return summary;
    }

    private long count(List<Project> projects, Status status) {
        return projects.stream().filter(p -> p.getStatus() == status).count();
    }
}
