package eu.openminted.registry.service.tool;

import eu.openminted.registry.core.domain.Browsing;
import eu.openminted.registry.core.domain.FacetFilter;
import eu.openminted.registry.core.service.SearchService;
import eu.openminted.registry.core.service.ServiceException;
import eu.openminted.registry.domain.BaseMetadataRecord;
import eu.openminted.registry.service.RequestService;
import eu.openminted.registry.service.generate.LabelGenerate;
import eu.openminted.registry.service.hotfix.AbstractPublicUsersGenericService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("requestService")
public class RequestServiceImpl extends AbstractPublicUsersGenericService implements RequestService {

    final private static Logger logger = LogManager.getLogger(RequestServiceImpl.class);
    final private static String RESOURCE_ALIAS = "resourceTypes";
    @Autowired
    SearchService searchService;
    @Autowired
    LabelGenerate labelGenerate;

    public RequestServiceImpl() {
        super(BaseMetadataRecord.class);
    }

    @SuppressWarnings("unchecked")
    public Browsing getResponseByFiltersElastic(FacetFilter filter) {
        filter.getFilter().keySet().retainAll(getBrowseBy());
        filter.addFilter("public", true);
        filter.setBrowseBy(getBrowseBy());
        Browsing<BaseMetadataRecord> ret = getResults(filter);
        labelGenerate.createLabels(ret);
        return ret;
    }

    @SuppressWarnings("unchecked")
    public Map<String, List<?>> getResourceGroupedElastic(FacetFilter filter, String category) throws ServiceException {
        filter.addFilter("public", true);
        return getResultsGrouped(filter, category);
    }

    @Override
    public Browsing getResponseByFiltersAndUserElastic(FacetFilter filter, String user) {
        Browsing ret = super.getResponseByFiltersAndUserElastic(filter, user);
        labelGenerate.createLabels(ret);
        return ret;
    }

    @Override
    public String getResourceType() {
        return RESOURCE_ALIAS;
    }
}
