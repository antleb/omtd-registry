package eu.openminted.registry.service;

import eu.openminted.registry.domain.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request/component")
public class ComponentController extends OmtdRestController<Component>{

    private ValidateInterface<Component> validateInterface;

    @Autowired
    ComponentController(@Qualifier("componentService") ValidateInterface<Component> service) {
        super(service);
        validateInterface = service;
    }
}
