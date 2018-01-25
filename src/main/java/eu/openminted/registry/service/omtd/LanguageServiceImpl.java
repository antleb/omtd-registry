package eu.openminted.registry.service.omtd;

import eu.openminted.registry.domain.LanguageDescription;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Created by stefanos on 13/1/2017.
 */
@Service("languageService")
@Primary
public class LanguageServiceImpl extends OmtdGenericService<LanguageDescription>{

    public LanguageServiceImpl() {
        super(LanguageDescription.class);
    }

    @Override
    public String getResourceType() {
        return "language";
    }
}
