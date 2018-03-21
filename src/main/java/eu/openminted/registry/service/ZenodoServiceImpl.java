package eu.openminted.registry.service;


import eu.openminted.registry.core.service.ParserService;
import eu.openminted.registry.core.service.ResourceCRUDService;
import eu.openminted.registry.core.service.ServiceException;
import eu.openminted.registry.domain.Corpus;
import eu.openminted.store.restclient.StoreRESTClient;
import net.sf.saxon.s9api.*;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by spyroukostas on 16-Mar-2018.
 */

@Service("zenodoService")
public class ZenodoServiceImpl implements ZenodoService {

    private Logger logger = Logger.getLogger(ZenodoServiceImpl.class);
    private String server = "https://sandbox.zenodo.org/api/deposit/depositions"; // sandbox
//    private String server = "https://zenodo.org/api/deposit/depositions"; // Zenodo
    private String token = "K0LLM8l9BoOjcAwhCtXpCN34bobz1sDWDJMlvj0o9gPckqYRlbQTvvJFchGF"; // sandbox
//    private String token = "bYgBRgbwWE9YFY1t2nvErhBILtA7dzOqdun1qGOle4vhNPdAqQewCkmbJy7H"; // Zenodo

    private final String xsl_file = "eu/openminted/registry/service/corpusMetadataToJson.xsl";


    @Autowired
    StoreRESTClient storeClient;

    @Autowired
    ParserService parserPool;

    @Autowired
    @Qualifier("corpusService")
    ResourceCRUDService<Corpus> corpusService;


    @Override
    public String createDeposition() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>("{}", headers);
        logger.info("request: " + request.toString());

        ResponseEntity<String> response = restTemplate.exchange( server, HttpMethod.POST, request , String.class );
        if (response.getStatusCode().value() == 201) {
            JSONObject res = new JSONObject(response.getBody());
            return res.get("id").toString();
        }
        return null;
    }

    @Override
    public String createDeposition(String metadata) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>(metadata, headers);
        logger.info("request: " + request.toString());

        ResponseEntity<String> response = restTemplate.exchange( server, HttpMethod.POST, request , String.class );
        if (response.getStatusCode().value() == 201) {
            JSONObject res = new JSONObject(response.getBody());
            return res.get("id").toString();
        }
        return null;
    }

    @Override
    public String retrieveDeposition(String zenodo_id) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>("", headers);
        logger.info("GET request: " + request.toString());

        ResponseEntity<String> response = restTemplate.exchange( server+"/"+zenodo_id, HttpMethod.GET, request, String.class );
        if (response.getStatusCode().value() == 200) {
            return response.getBody();
        }
        return null;
    }

    @Override
    public String updateDeposition(String zenodo_id, String metadata) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>(metadata, headers);
        logger.info("PUT request: " + request.toString());

        ResponseEntity<String> response = restTemplate.exchange( server+"/"+zenodo_id, HttpMethod.PUT, request, String.class );
        if (response.getStatusCode().value() == 200) {
            JSONObject res = new JSONObject(response.getBody());
            return res.get("id").toString();
        }
        return null;
    }

    @Override
    public boolean deleteDeposition(String zenodo_id) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> request = new HttpEntity<>("", headers);
        logger.info("DELETE request: " + request.toString());

        ResponseEntity<String> response = restTemplate.exchange( server+"/"+zenodo_id, HttpMethod.DELETE, request, String.class );
        if (response.getStatusCode().value() == 201) {
            return true;
        }
        return false;
    }

    @Override
    public String publishCorpus(String corpusId) {
        String zenodo_metadata = createZenodoMetadata(corpusId);
        logger.info(zenodo_metadata);
        String ret = createDeposition();
        updateDeposition(ret, zenodo_metadata);
        if (deleteDeposition(ret) == true) {
            logger.info("deleted entry: " + ret);
        }
        ret = createDeposition(zenodo_metadata);
        ret = retrieveDeposition(ret);
        return ret;
    }

    /**
     * Resolves archiveId from corpusId.
     *
     * @param corpusId
     * @return archiveId
     */
    private String resolveCorpusArchive(String corpusId) {
        final Pattern pattern = Pattern.compile(".*?\\?archiveId=(?<archive>[\\d\\w-]+)$");
        Corpus corpus = corpusService.get(corpusId);
        if (corpus == null) {
            logger.error("Corpus with id " + corpusId + " not found");
            throw new ServiceException("Corpus with id " + corpusId + " not found");
        }
        String distributionLocation = corpus.getCorpusInfo().getDatasetDistributionInfo().getDistributionLocation();
        Matcher archiveMatcher = pattern.matcher(distributionLocation);
        if (!archiveMatcher.find()) {
            throw new ServiceException("No archive Id was present");
        }
        String archiveId = archiveMatcher.group("archive");
        logger.debug(archiveId);
        return archiveId;
    }


    /**
     *
     * @param corpusId
     * @return
     */
    private String createZenodoMetadata(String corpusId) {
        String corpusXML = "";
        Corpus corpus = corpusService.get(corpusId);
        try {
            corpusXML = parserPool.serialize(corpus, ParserService.ParserServiceTypes.XML).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        logger.info("corpus metadata: " + corpusXML);

        Processor processor = new Processor(false);
        XsltCompiler xsltCompiler = processor.newXsltCompiler();
        StreamSource stylesource =
                new StreamSource(new File(this.getClass().getClassLoader()
                        .getResource(xsl_file).getFile()));

        XsltExecutable xsltExecutable;
        String result = null;
        XdmNode source = null;
        try {
            xsltExecutable = xsltCompiler.compile(stylesource);
            XsltTransformer xsltTransformer = xsltExecutable.load();

            Source src = new StreamSource(IOUtils.toInputStream(corpusXML));
            source = processor.newDocumentBuilder().build(src);

            Serializer out = new Serializer();
            out.setOutputProperty(Serializer.Property.METHOD, "text");
            out.setOutputProperty(Serializer.Property.INDENT, "yes");

            StringWriter sw = new StringWriter();
            out.setOutputWriter(sw);

            xsltTransformer.setInitialContextNode(source);
            xsltTransformer.setDestination(out);
            xsltTransformer.transform();

            result = sw.toString();

        } catch (SaxonApiException e) {
            e.printStackTrace();
        }
        if (result == null) {
            logger.warn("createZenodoMetadata returned " + result);
            return null;
        }
        return new JSONObject(result).toString();
    }


    private String zenodoPublish(String corpusId) {

        String zenodo_metadata = createZenodoMetadata(corpusId);
        logger.info(zenodo_metadata);

        return null;
    }
}
