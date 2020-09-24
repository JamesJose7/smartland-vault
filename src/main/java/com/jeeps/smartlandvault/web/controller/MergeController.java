package com.jeeps.smartlandvault.web.controller;

import com.jeeps.smartlandvault.merging.JoinCandidate;
import com.jeeps.smartlandvault.merging.MergeService;
import com.jeeps.smartlandvault.merging.UnionForm;
import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import com.jeeps.smartlandvault.nosql.merged_container.MergedContainer;
import com.jeeps.smartlandvault.nosql.merged_container.MergedContainerRepository;
import com.jeeps.smartlandvault.web.FlashMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class MergeController {
    private static Logger logger = LoggerFactory.getLogger(ContainerController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private MergeService mergeService;

    @Autowired
    private MergedContainerRepository mergedContainerRepository;
    @Autowired
    private DataContainerRepository dataContainerRepository;


    @GetMapping("/merge")
    public String mergeMenu(Model model) {

        model.addAttribute("mergedContainers", mergedContainerRepository.findAll());
        model.addAttribute("newUnionLink", "/merge/union/new");
        model.addAttribute("newJoinLink", "/merge/join/new");
        return "merge/merge_home";
    }

    /****** UNION *******/

    @GetMapping("/merge/union/new")
    public String newUnion(Model model) {
        model.addAttribute("containers", dataContainerRepository.findAll());
        model.addAttribute("formUrl", contextPath + "/merge/union");
        return "merge/new_union";
    }

    @GetMapping("/merge/union")
    public String displayUnionCandidates(@RequestParam("containerId") String containerId, Model model) {
        List<DataContainer> unionCandidates = mergeService.findUnionCandidates(containerId);
        DataContainer originalContainer = dataContainerRepository.findById(containerId).orElse(new DataContainer());
        UnionForm unionForm = new UnionForm(originalContainer, unionCandidates);

        model.addAttribute("candidates", unionCandidates);
        model.addAttribute("formUrl", contextPath + "/merge/union/create");
        model.addAttribute("unionForm", unionForm);
        return "merge/candidates";
    }

    @PostMapping("/merge/union/create")
    public String unionContainers(RedirectAttributes redirectAttributes, UnionForm unionForm) {
        // Check if at least one container was selected
        unionForm.getNewContainers().removeIf(Objects::isNull);
        if (unionForm.getNewContainers().isEmpty()) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("Seleccione al menos un dataset", FlashMessage.Status.FAILURE));
            return "redirect:/merge/union?containerId=" + unionForm.getOriginalContainer().getId();
        }
        // Create new merged container
        DataContainer dataContainer = mergeService.performUnion(unionForm.getOriginalContainer(), unionForm.getNewContainers());
        dataContainer.setName(unionForm.getName());
        dataContainer.setNewUnionId();
        dataContainer.setMerge(true);
        dataContainer.setFileType("Unión");
        dataContainerRepository.save(dataContainer);
        // Save merge container data
        List<String> containersIds = unionForm.getNewContainers().stream()
                .map(DataContainer::getId)
                .collect(Collectors.toList());
        MergedContainer mergedContainer = new MergedContainer(dataContainer.getId(), dataContainer.getName(), containersIds, dataContainer);
        mergedContainerRepository.save(mergedContainer);

        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Unión creada exitosamente", FlashMessage.Status.SUCCESS));
        return "redirect:/merge";
    }

    /****** JOIN *******/

    @GetMapping("/merge/join/new")
    public String newJoin(Model model) {
        model.addAttribute("containers", dataContainerRepository.findAll());
        model.addAttribute("formUrl", contextPath + "/merge/join");
        return "merge/new_join";
    }

    @GetMapping("/merge/join")
    public String displayJoinCandidates(@RequestParam("containerId") String containerId, Model model) {
        List<JoinCandidate> joinCandidates = mergeService.findJoinCandidates(containerId);
        DataContainer originalContainer = dataContainerRepository.findById(containerId).orElse(new DataContainer());

        model.addAttribute("candidates", joinCandidates);
        model.addAttribute("formUrl", contextPath + "/merge/join/create");
        model.addAttribute("originalContainer", originalContainer);
        return "merge/join_candidates";
    }

    @PostMapping("/merge/join/create")
    public String unionContainers(RedirectAttributes redirectAttributes,
                                  @RequestParam("containerProperty") String containerProperty,
                                  @RequestParam("name") String name,
                                  @RequestParam("originalContainerId") String originalContainerId) {
        String joinContainerId = containerProperty.split("///")[0];
        String joinProperty = containerProperty.split("///")[1];
        DataContainer originalContainer = dataContainerRepository.findById(originalContainerId).orElse(null);
        DataContainer joinContainer = dataContainerRepository.findById(joinContainerId).orElse(null);
        // Create new merged container
        DataContainer newContainer = mergeService.performJoin(originalContainer, joinContainer, joinProperty);
        newContainer.setName(name);
        newContainer.setNewJoinId();
        newContainer.setMerge(true);
        newContainer.setFileType("Join");
        dataContainerRepository.save(newContainer);
        // Save merge container data
        List<String> containersIds = Arrays.asList(originalContainerId, joinContainerId);
        MergedContainer mergedContainer = new MergedContainer(newContainer.getId(), newContainer.getName(), containersIds, newContainer);
        mergedContainerRepository.save(mergedContainer);

        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Unión creada exitosamente", FlashMessage.Status.SUCCESS));
        return "redirect:/merge";
    }
}
