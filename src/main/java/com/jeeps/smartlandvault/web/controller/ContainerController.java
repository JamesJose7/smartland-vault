package com.jeeps.smartlandvault.web.controller;

import com.jeeps.smartlandvault.container_filter.ContainerFilterService;
import com.jeeps.smartlandvault.container_tools.ColumnChangesForm;
import com.jeeps.smartlandvault.exceptions.IncorrectExcelFormatException;
import com.jeeps.smartlandvault.fileupload.ExcelTransformerService;
import com.jeeps.smartlandvault.nosql.data_container.DataContainer;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerForm;
import com.jeeps.smartlandvault.nosql.data_container.DataContainerRepository;
import com.jeeps.smartlandvault.nosql.license_type.LicenseType;
import com.jeeps.smartlandvault.nosql.license_type.LicenseTypeRepository;
import com.jeeps.smartlandvault.nosql.table_file.TableFileService;
import com.jeeps.smartlandvault.observatories.ObservatoriesService;
import com.jeeps.smartlandvault.observatories.Observatory;
import com.jeeps.smartlandvault.observatories.SharedObservatoriesForm;
import com.jeeps.smartlandvault.observatories.UserObservatory;
import com.jeeps.smartlandvault.rest_extraction.RestExtractorService;
import com.jeeps.smartlandvault.sql.inventory.ContainerInventory;
import com.jeeps.smartlandvault.sql.inventory.ContainerInventoryRepository;
import com.jeeps.smartlandvault.sql.item.Item;
import com.jeeps.smartlandvault.sql.item.ItemRepository;
import com.jeeps.smartlandvault.util.*;
import com.jeeps.smartlandvault.web.FlashMessage;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.jeeps.smartlandvault.util.GenericJsonMapper.getPropertiesFromTree;
import static com.jeeps.smartlandvault.util.GenericJsonMapper.getSelectedTree;
import static com.jeeps.smartlandvault.util.InventoryHelper.createItemsFromProperties;

@Controller
public class ContainerController {
    private static Logger logger = LoggerFactory.getLogger(ContainerController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private ExcelTransformerService excelTransformerService;
    @Autowired
    private RestExtractorService restExtractorService;

    @Autowired
    private DataContainerRepository dataContainerRepository;
    @Autowired
    private ContainerInventoryRepository containerInventoryRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private LicenseTypeRepository licenseTypeRepository;

    @Autowired
    private ObservatoriesService observatoriesService;
    @Autowired
    private ContainerFilterService containerFilterService;

    @Autowired
    private TableFileService tableFileService;

    private final String EXPORT_EXCEL_URL = "/files/export/excel/%s";
    private final String EXPORT_CSV_URL = "/files/export/csv/%s";

    @GetMapping("/{userToken}")
    public String containersBrowser(Model model, @PathVariable("userToken") String userToken) {
        // Filter resources available for this user
        List<DataContainer> dataContainers = containerFilterService.filterDataContainersByUser(userToken);

        model.addAttribute("dataContainers", dataContainers);
        model.addAttribute("addNewContainerLink", "/container/selectType/" + userToken);
        model.addAttribute("contextPath", contextPath);
        model.addAttribute("userToken", userToken);
        return "containers/containers_browser";
    }

    @GetMapping("/container/selectType/{userToken}")
    public String selectContainerType(Model model, @PathVariable("userToken") String userToken) {
        model.addAttribute("excelTypeUrl", "/container/add/excel/" + userToken);
        model.addAttribute("restTypeUrl", "/container/add/rest/" + userToken);
        return "containers/select_container_type";
    }

    // Upload excel container
    @GetMapping("/container/add/excel/{userToken}")
    public String addExcelContainer(Model model, @PathVariable("userToken") String userToken) {
        model.addAttribute("uploadExcelUrl", String.format("%s/container/add/excel/fileUpload/%s",
                contextPath, userToken));
        model.addAttribute("dataContainerForm", new DataContainerForm());
        model.addAttribute("licenses", licenseTypeRepository.findAll());
        model.addAttribute("userToken", userToken);
        // Get list of observatories based on user
        try {
            model.addAttribute("observatories", observatoriesService.getObservatoriesByUserToken(userToken));
        } catch (IOException e) {
            model.addAttribute("observatories", new ArrayList<Observatory>());
            logger.error("Could not retrieve observatories from API", e);
        }
        return "containers/excel_upload_form";
    }

    @PostMapping("container/add/excel/fileUpload/{userToken}")
    public String uploadExcelTableWeb(
            DataContainerForm dataContainerForm, RedirectAttributes redirectAttributes,
            @PathVariable("userToken") String userToken) throws Exception {
        String failureRedirect = "redirect:/container/add/excel/" + userToken;
        String successRedirect = "redirect:/" + userToken;

        MultipartFile file = dataContainerForm.getFile();
        DataContainer dataContainer = dataContainerForm.getDataContainer();
        // Process keywords
        dataContainer.setKeywords(KeywordsHelper.processKeywords(dataContainerForm.getKeywordsRaw()));

        // Check MIME type to match the accepted ones
        if (file.getContentType() == null) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("No se pudo determinar el tipo de archivo, por favor intente de nuevo", FlashMessage.Status.FAILURE));
            return failureRedirect;
        }
        if (!ExcelSheetReader.isExcelMimeType(file.getContentType())) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("Only Excel spreadsheets are supported, please try again", FlashMessage.Status.FAILURE));
            return failureRedirect;
        }
        // Hand input stream to excel service
        try {
            // Upload file to mongodb
            String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
            String fileId = tableFileService.addTableFile(dataContainer.getName(), fileExtension, file);
            String fileUrl = String.format("%s/files/download/%s", contextPath, fileId);
            // Additional data
            dataContainer.setFileUrl(fileUrl);
            dataContainer.setFileType(fileExtension);
            dataContainer.setDateCreated(new Date());
            dataContainer.setUserToken(userToken);
            // Transform excel
            excelTransformerService.transform(file.getInputStream(), dataContainer, fileExtension);
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("Recurso agregado correctamente", FlashMessage.Status.SUCCESS));
            return successRedirect;
        } catch (IncorrectExcelFormatException | IOException e) {
            logger.error(e.getMessage());
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage(e.getMessage(), FlashMessage.Status.FAILURE));
            return failureRedirect;
        }
    }

    // Edit excel container
    @GetMapping("/container/edit/{id}/{userToken}")
    public String editContainer(@PathVariable("id") String id,
                                @PathVariable("userToken") String userToken,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request,
                                Model model) {
        DataContainer dataContainer = dataContainerRepository.findById(id).orElse(null);
        if (dataContainer == null) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("El recurso seleccionado no existe", FlashMessage.Status.FAILURE));
            return "redirect:/" + userToken;
        }

        DataContainerForm dataContainerForm = new DataContainerForm();
        // Get previous keywords
        dataContainerForm.setKeywordsRaw(KeywordsHelper.unprocessKeywords(dataContainer.getKeywords()));
        dataContainerForm.setDataContainer(dataContainer);

        model.addAttribute("userToken", userToken);
        model.addAttribute("backUrl", request.getHeader("Referer"));
        model.addAttribute("dataContainerForm", dataContainerForm);
        model.addAttribute("uploadExcelUrl", String.format("%s/container/edit/excel/%s",
                contextPath, userToken));
        model.addAttribute("licenses", licenseTypeRepository.findAll());
        // Get list of observatories
        try {
            model.addAttribute("observatories", observatoriesService.getObservatoriesByUserToken(userToken));
        } catch (IOException e) {
            model.addAttribute("observatories", new ArrayList<Observatory>());
            logger.error("Could not retrieve observatories from API", e);
        }
        return "containers/excel_edit_form";
    }

    @PostMapping("/container/edit/excel/{userToken}")
    public String updateContainer(
            DataContainerForm dataContainerForm, RedirectAttributes redirectAttributes,
            @PathVariable("userToken") String userToken) {
        // New container data received from form
        DataContainer newDataContainer = dataContainerForm.getDataContainer();
        // Get previous data and transfer the form data
        DataContainer dataContainer = dataContainerRepository.findById(newDataContainer.getId()).orElse(new DataContainer());
        FormHelper.transferContainerFormData(newDataContainer, dataContainer);
        // Parse keywords
        dataContainer.setKeywords(KeywordsHelper.processKeywords(dataContainerForm.getKeywordsRaw()));
        // New update date
        dataContainer.setDateUpdated(new Date());
        dataContainerRepository.save(dataContainer);

        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Recurso actualizado correctamente", FlashMessage.Status.SUCCESS));
        return "redirect:/" + userToken;
    }

    // Upload rest container
    @GetMapping("/container/add/rest/{userToken}")
    public String addRestContainer(Model model, @PathVariable("userToken") String userToken) {
        model.addAttribute("uploadRestUrl", String.format("%s/container/add/rest/upload/%s",
                contextPath, userToken));
        return "containers/rest_upload_form";
    }

    @PostMapping("container/add/rest/upload/{userToken}")
    public String uploadRestEndpointWeb(
            @RequestParam(name = "restUrl") String restUrl,
            @RequestParam(name = "id", required = false) String id,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "publisher", required = false) String publisher,
            @RequestParam(name = "sourceUrl", required = false) String sourceUrl,
            @PathVariable("userToken") String userToken,
            RedirectAttributes redirectAttributes) {
        String failureRedirect = "redirect:/container/add/rest/" + userToken;
        String successRedirect = "redirect:/" + userToken;

        if (!UrlUtils.isValidUrl(restUrl)) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("Por favor ingrese una URL válida", FlashMessage.Status.FAILURE));
            return failureRedirect;
        }

        // TODO: Add a service to extract and store the contents of the rest Endpoint
        restExtractorService.extractRestData(restUrl, id, name, publisher, sourceUrl);
        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Recurso REST agregado correctamente", FlashMessage.Status.SUCCESS));
        return successRedirect;
    }

    // View container details
    @GetMapping("/container/{id}/{userToken}")
    public String viewContainer(@PathVariable("id") String id,
                                @PathVariable("userToken") String userToken,
                                Model model) {
        Optional<DataContainer> dataContainerOptional = dataContainerRepository.findById(id);
        if (!dataContainerOptional.isPresent())
            throw new ResourceNotFoundException();
        // Container and it's data
        DataContainer dataContainer = dataContainerOptional.get();
        // Show inventory when it has one
        ContainerInventory containerInventory = containerInventoryRepository.findByContainerId(dataContainer.getId()).orElse(null);
        // Get observatory data
        try {
            Observatory observatory = observatoriesService.getObservatory(dataContainer.getObservatory());
            model.addAttribute("observatoryData", observatory);
        } catch (IOException e) {
            logger.error("Error retrieving observatory's details", e);
        }

        // Get license name
        LicenseType license = licenseTypeRepository.findById(dataContainer.getLicenseType()).orElse(new LicenseType("", ""));
        model.addAttribute("license", license.getName());

        // Get download URL
        String fileRelativeUrl = dataContainer.getFileUrl().replace(contextPath, "");
        model.addAttribute("fileDownloadUrl", fileRelativeUrl);

        model.addAttribute("userToken", userToken);
        model.addAttribute("exportExcelUrl", String.format(EXPORT_EXCEL_URL, dataContainer.getId()));
        model.addAttribute("exportCsvUrl", String.format(EXPORT_CSV_URL, dataContainer.getId()));
        model.addAttribute("dataContainer", dataContainer);
        model.addAttribute("inventory", containerInventory);
        model.addAttribute("browseDataLink", String.format("/container/%s/browseData/%s",
                dataContainer.getId(), userToken));
        model.addAttribute("rawDataLink", String.format("/api/v1/dataContainers/%s/data", dataContainer.getId()));
        model.addAttribute("dataTablesEndPoint", String.format("%s/api/v1/dataContainers/%s/data", contextPath, dataContainer.getId()));
        return "containers/container_home";
    }


    @GetMapping("/container/{id}/browseData/{userToken}")
    public String viewContainerData(@PathVariable("id") String id,
                        @PathVariable("userToken") String userToken,
                        @RequestParam(name = "tree", defaultValue = "/", required = false) String currentTree,
                        @RequestParam(name = "navigating", defaultValue = "false", required = false) String isNavigating,
                        Model model) {
        Optional<DataContainer> dataContainerOptional = dataContainerRepository.findById(id);
        if (!dataContainerOptional.isPresent())
            throw new ResourceNotFoundException();
        // Container and it's data
        DataContainer dataContainer = dataContainerOptional.get();
        // Check if it has an inventory and change the default tree accordingly
        ContainerInventory inventory = containerInventoryRepository.findByContainerId(id)
                .orElse(null);
        if (isNavigating.equals("false") && inventory != null)
                currentTree = inventory.getMainDataProperty();

        // Get attribute names and types
        List<Object> data = dataContainer.getData();
        Map<String, String> properties = new HashMap<>();
        if (!data.isEmpty())
           properties = getPropertiesFromTree(getSelectedTree(data, currentTree));
        // Append '/' to the tree if it does not have one at the end
        if (currentTree.charAt(currentTree.length()-1) != '/')
            currentTree += "/";
        // Get previous tree for navigation
        if (currentTree.split("/").length > 0) {
            List<String> treeElements = new ArrayList<>(Arrays.asList(currentTree.split("/")));
            treeElements.remove(treeElements.size()-1);
            String previousTreeUrl = String.join("/", treeElements);
            model.addAttribute("previousTreeUrl", previousTreeUrl.trim().isEmpty() ? "/" : previousTreeUrl);
        }
        // Check if the current tree is the main data
        boolean isMainDataSelected = false;
        if (inventory != null)
            if (inventory.getMainDataProperty().equals(currentTree))
                isMainDataSelected = true;

        model.addAttribute("dataContainer", dataContainer);
        model.addAttribute("currentTree", currentTree);
        model.addAttribute("isMainDataSelected", isMainDataSelected);
        model.addAttribute("dataProperties", properties);
        model.addAttribute("containerUrl", String.format("/container/%s/browseData/%s",
                dataContainer.getId(), userToken));
        model.addAttribute("selectMainDataUrl", String.format("%s/container/%s/main-data/%s",
                contextPath, dataContainer.getId(), userToken));
        return "containers/container_data_browser";
    }

    @PostMapping(value = "/container/{id}/main-data/{userToken}")
    public String selectMainDataTree(RedirectAttributes redirectAttributes,
                                     @PathVariable(name = "id") String containerId,
                                     @PathVariable("userToken") String userToken,
                                     @RequestParam(name = "tree") String selectedTree) {
        Optional<DataContainer> dataContainerOptional = dataContainerRepository.findById(containerId);
        if (!dataContainerOptional.isPresent())
            throw new ResourceNotFoundException();
        DataContainer dataContainer = dataContainerOptional.get();
        List<Object> data = dataContainer.getData();
        // Get the inventory or create it if it does not exist
        ContainerInventory containerInventory = containerInventoryRepository.findByContainerId(containerId)
                .orElse(new ContainerInventory(containerId, "unnamed", selectedTree));
        containerInventory.setMainDataProperty(selectedTree);
        // Create items from selected tree
        Map<String, String> properties = getPropertiesFromTree(getSelectedTree(data, selectedTree));
        List<Item> items = createItemsFromProperties(properties, containerInventory);
        // Delete previous items before saving
        if (containerInventory.getItems() != null)
            itemRepository.deleteAllByContainerInventory(containerInventory);
        containerInventory.setItems(items);
        // Save or update inventory
        containerInventoryRepository.save(containerInventory);

        dataContainer.setMetadata(InventoryHelper.getMetadataFromInventory(containerInventory));
        dataContainerRepository.save(dataContainer);
        logger.info(String.format("Request :%s | from id: %s", selectedTree, containerId));
        return String.format("redirect:/container/%s/browseData/%s", containerId, userToken);
    }

    // Remove container
    @GetMapping("/container/{id}/remove/{userToken}")
    public String logicalDelete(@PathVariable(name = "id") String containerId,
                                @PathVariable("userToken") String userToken,
                                @RequestParam(name = "redirect", required = false, defaultValue = "") String redirect,
                                RedirectAttributes redirectAttributes) {
        // Get container
        DataContainer dataContainer = dataContainerRepository.findById(containerId).orElse(null);
        if (dataContainer == null) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("El recurso seleccionado no existe", FlashMessage.Status.FAILURE));
        } else {
            // Logical delete
            dataContainer.setDeleted(true);
            dataContainerRepository.save(dataContainer);

            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("Recurso borrado correctamente", FlashMessage.Status.SUCCESS));
        }

        if (redirect.isEmpty())
            return String.format("redirect:/%s", userToken);
        else
            return String.format("redirect:/%s/%s", redirect, userToken);
    }

    /* Tools */
    @GetMapping("/container/duplicate/{id}/{userToken}")
    public String duplicateContainer(@PathVariable(name = "id") String containerId,
                                     @PathVariable("userToken") String userToken,
                                     @RequestParam(name = "redirect", required = false, defaultValue = "") String redirect,
                                     RedirectAttributes redirectAttributes) {
        // Get container
        DataContainer dataContainer = dataContainerRepository.findById(containerId).orElse(null);
        if (dataContainer == null) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("El recurso seleccionado no existe", FlashMessage.Status.FAILURE));
        } else {
            ContainerInventory containerInventory = containerInventoryRepository.findByContainerId(dataContainer.getId()).orElse(null);
            // Duplicate container
            dataContainer.setNewId();
            dataContainer.setName(String.format("%s - copia", dataContainer.getName()));
            dataContainer.setDuplicate(true);
            dataContainer.setUserToken(userToken);
            // Reset shared observatories
            dataContainer.setSharedObservatories(new ArrayList<>());
            // Change the observatory to the current user
            try {
                List<UserObservatory.ObservatoryDetails> userObs = observatoriesService.getObservatoriesByUserToken(userToken);
                if (userObs.get(0) != null)
                    dataContainer.setObservatory(userObs.get(0).getId());
            } catch (IOException e) {
                e.printStackTrace();
            }

            DataContainer newContainer = dataContainerRepository.save(dataContainer);
            // Duplicate inventory
            if (containerInventory != null) {
                containerInventory.setContainerId(newContainer.getId());
                ContainerInventory inventoryCopy = containerInventory.createCopy();
                try {
                    containerInventoryRepository.save(inventoryCopy);
                } catch (Exception e) {}
            }

            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("Recurso duplicado correctamente", FlashMessage.Status.SUCCESS));
        }

        if (redirect.isEmpty())
            return String.format("redirect:/%s", userToken);
        else
            return String.format("redirect:/%s/%s", redirect, userToken);
    }

    @GetMapping("/container/publicToggle/{id}/{userToken}")
    public String publicToggle(@PathVariable(name = "id") String containerId,
                                     @PathVariable("userToken") String userToken,
                                     RedirectAttributes redirectAttributes) {
        // Get container
        DataContainer dataContainer = dataContainerRepository.findById(containerId).orElse(null);
        if (dataContainer == null) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("El recurso seleccionado no existe", FlashMessage.Status.FAILURE));
        } else {
            // Toggle public value and save it
            dataContainer.setPublicContainer(!dataContainer.getPublicContainer());
            dataContainerRepository.save(dataContainer);
        }

        return String.format("redirect:/container/%s/%s", containerId, userToken);
    }

    @GetMapping("/container/share/{id}/{userToken}")
    public String shareContainer(@PathVariable(name = "id") String containerId,
                              @PathVariable("userToken") String userToken,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        // Get container
        DataContainer dataContainer = dataContainerRepository.findById(containerId).orElse(null);
        if (dataContainer == null) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("El recurso seleccionado no existe", FlashMessage.Status.FAILURE));
            return "redirect:/" + userToken;
        }

        try {
            // Get all containers
            List<Observatory> observatories = observatoriesService.getObservatories();
            // Remove container's observatory
            observatories.removeIf(obs -> obs.getId() == dataContainer.getObservatory());
            // Get shared containers
            List<Integer> sharedObservatories = dataContainer.getSharedObservatories();
            // Merge shared and unshared observatories
            SharedObservatoriesForm sharedObservatoriesForm =
                    new SharedObservatoriesForm(observatories, sharedObservatories, dataContainer.getId());

            model.addAttribute("formUrl", contextPath + "/container/share/save/" + userToken);
            model.addAttribute("sharedObservatoriesForm", sharedObservatoriesForm);
            model.addAttribute("backUrl", String.format("/container/%s/%s", dataContainer.getId(), userToken));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return "containers/share_container";
    }

    @PostMapping("/container/share/save/{userToken}")
    public String saveSharedObservatories(@PathVariable("userToken") String userToken,
                                     SharedObservatoriesForm sharedObservatoriesForm,
                                     RedirectAttributes redirectAttributes) {
        // Get ids from the shared containers
        List<Integer> sharedObservatories = sharedObservatoriesForm.getObservatories().stream()
                .filter(Observatory::isShared)
                .map(Observatory::getId)
                .collect(Collectors.toList());
        // Get container and save the shared observatories
        DataContainer dataContainer = dataContainerRepository.findById(sharedObservatoriesForm.getContainerId()).orElse(null);
        if (dataContainer == null) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("El recurso seleccionado no existe", FlashMessage.Status.FAILURE));
            return String.format("redirect:/%s", userToken);
        }
        dataContainer.setSharedObservatories(sharedObservatories);
        dataContainerRepository.save(dataContainer);

        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Se ha compartido el recurso exitosamente", FlashMessage.Status.SUCCESS));

        return String.format("redirect:/container/%s/%s", sharedObservatoriesForm.getContainerId(), userToken);
    }

    @GetMapping("/container/edit/columns/{id}/{userToken}")
    public String editColumns(@PathVariable(name = "id") String containerId,
                              @PathVariable("userToken") String userToken,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        // Get container
        DataContainer dataContainer = dataContainerRepository.findById(containerId).orElse(null);
        if (dataContainer == null) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("El recurso seleccionado no existe", FlashMessage.Status.FAILURE));
            return "redirect:/" + userToken;
        }

        model.addAttribute("formUrl", contextPath + "/container/edit/columns/save/" + userToken);
        model.addAttribute("columnChangesForm", new ColumnChangesForm());
        model.addAttribute("dataContainer", dataContainer);

        return "containers/container_column_editor";
    }

    @PostMapping("/container/edit/columns/save/{userToken}")
    public String saveColumnsChanges(@PathVariable("userToken") String userToken,
                                     ColumnChangesForm columnChangesForm,
                                     RedirectAttributes redirectAttributes) {
        String containerId = columnChangesForm.getContainerId();
        // Check if at least one column was selected
        List<String> selectedColumns = columnChangesForm.getSelectedColumns();
        selectedColumns.removeIf(Objects::isNull);
        if (selectedColumns.isEmpty()) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("Por favor seleccionar al menos una columna", FlashMessage.Status.FAILURE));
            return String.format("redirect:/container/edit/columns/%s/%s", containerId, userToken);
        }
        // Get container
        DataContainer dataContainer = dataContainerRepository.findById(containerId).orElse(null);
        if (dataContainer == null) {
            redirectAttributes.addFlashAttribute("flash",
                    new FlashMessage("El recurso seleccionado no existe", FlashMessage.Status.FAILURE));
            return "redirect:/" + userToken;
        }

        // Delete columns from metadata
        dataContainer.getMetadata().removeIf(column -> selectedColumns.contains(column.getPropertyName()));
        // Delete data column
        for (Object rowDataRaw : dataContainer.getData()) {
            LinkedHashMap<String, Object> dataRow = (LinkedHashMap) rowDataRaw;
            selectedColumns.forEach(dataRow::remove);
        }
        // Update number of columns
        dataContainer.setPropertyCount(dataContainer.getMetadata().size());

        // Delete columns from inventory
        ContainerInventory inventory = containerInventoryRepository.findByContainerId(containerId).orElse(null);
        if (inventory != null) {
            Map<String, String> properties = getPropertiesFromTree(getSelectedTree(dataContainer.getData(), inventory.getMainDataProperty()));
            List<Item> items = createItemsFromProperties(properties, inventory);
            // Delete previous items before saving
            if (inventory.getItems() != null)
                itemRepository.deleteAllByContainerInventory(inventory);
            inventory.setItems(items);
            // Save or update inventory
            containerInventoryRepository.save(inventory);
        }

        dataContainerRepository.save(dataContainer);

        redirectAttributes.addFlashAttribute("flash",
                new FlashMessage("Columnas borradas correctamente", FlashMessage.Status.SUCCESS));

        return String.format("redirect:/container/%s/%s", containerId, userToken);
    }
}
