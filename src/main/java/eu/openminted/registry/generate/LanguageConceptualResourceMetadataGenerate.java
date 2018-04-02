package eu.openminted.registry.generate;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import eu.openminted.registry.core.service.ResourceCRUDService;
import eu.openminted.registry.domain.*;
import eu.openminted.registry.domain.Date;
import eu.openminted.registry.service.CorpusServiceImpl;
import eu.openminted.registry.service.aai.UserInfoAAIRetrieve;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


@org.springframework.stereotype.Component
public class LanguageConceptualResourceMetadataGenerate extends WorkflowOutputMetadataGenerate {
	
    static final Logger logger = LogManager.getLogger(LanguageConceptualResourceMetadataGenerate.class);
    
    public Lexical generateLanguageConceptualResourceMetadata(String inputCorpusId, String componentId, String userId, String outputResourceArchiveId) 
    		throws IOException, NullPointerException  {
    	//eu.openminted.registry.domain.Lexical
    	Lexical lcr = new Lexical();
    	lcr.setMetadataHeaderInfo(generateMetadataHeaderInfo(userId));
    	String lcrOmtdId = lcr.getMetadataHeaderInfo().getMetadataRecordIdentifier().getValue();    
    	lcr.setLexicalConceptualResourceInfo(generateLanguageConceptualResourceInfo(lcrOmtdId, inputCorpusId, componentId, userId, outputResourceArchiveId));
    	logger.debug("Output language description metadata::\n " + mapper.writeValueAsString(lcr)+"\n");
    	return lcr;
    }

    public LexicalConceptualResourceInfo generateLanguageConceptualResourceInfo(String lcrOmtdId, String inputCorpusId, String componentId, String userId, String outputCorpusArchiveId) 
    		throws IOException, NullPointerException {
    	 // Get input corpus information       		
        Corpus inputCorpus = getInputCorpusMetadata(inputCorpusId);
        // Get component information
        Component component = getComponentMetadata(componentId);    	
        
        LexicalConceptualResourceInfo lcrInfo = new LexicalConceptualResourceInfo();
               
        ////////////////////////
        // IdentificationInfo      
        lcrInfo.setIdentificationInfo(generateIdentificationInfo(inputCorpus, component));
        logger.debug("Identification Info:\n" + mapper.writeValueAsString(lcrInfo.getIdentificationInfo()) +"\n");
        
        /////////////////////////
        // VersionInfo     
        lcrInfo.setVersionInfo(generateVersionInfo());
        //logger.info("Version info:\n" + mapper.writeValueAsString(lcrInfo.getVersionInfo())+"\n");
        
        //////////////////////////
        // ContactInfo       
        lcrInfo.setContactInfo(generateContactInfo(userId, lcrOmtdId));
        //logger.info("Contact info::\n" + mapper.writeValueAsString(lcrInfo.getContactInfo()) + "\n");
		        
		//////////////////////////
		// datasetDistributionInfo       
        List<DatasetDistributionInfo> distributionInfos = new ArrayList<>() ;
        distributionInfos.add(generateDatasetDistributionInfo(inputCorpus, component, outputCorpusArchiveId));
		lcrInfo.setDistributionInfos(distributionInfos);
		//logger.info("Distribution info:\n" + mapper.writeValueAsString(lcrInfo.getDistributionInfos())+"\n");
		
		//////////////////////////
		// rightsInfo
        RightsInfo rightsInfo = generateRightsInfo(inputCorpus, component);
        lcrInfo.setRightsInfo(rightsInfo);
        //logger.info("Rights info:\n" + mapper.writeValueAsString(rightsInfo) + "\n");    
        
        //////////////////////////
        // resourceCreationInfo
        ResourceCreationInfo resourceCreationInfo = generateResourceCreationInfo(userId);
        lcrInfo.setResourceCreationInfo(resourceCreationInfo);
        //logger.info("Resource Creation info::\n" + mapper.writeValueAsString(resourceCreationInfo) + "\n");

        //////////////////////////////
        // lexicalConceptualResourceType
        OperationType componentOperation = component.getComponentInfo().getFunctionInfo().getFunction();                
        // OntologyAcquisition, OntologyEnhancement --> Ontology         
        if (componentOperation.equals(OperationType.HTTP___W3ID_ORG_META_SHARE_OMTD_SHARE_ONTOLOGY_ACQUISITION) ||
        	componentOperation.equals(OperationType.HTTP___W3ID_ORG_META_SHARE_OMTD_SHARE_ONTOLOGY_ENHANCEMENT)) {
        	//logger.info("Into ontology functionality");
        	lcrInfo.setLexicalConceptualResourceType(LexicalConceptualResourceTypeEnum.ONTOLOGY);
        }
        // LexiconAquisitionFromCorpora, LexiconEnhancement, BilignualLexiconInduction --> Lexicon
        else if (componentOperation.equals(OperationType.HTTP___W3ID_ORG_META_SHARE_OMTD_SHARE_LEXICON_ACQUISITION_FROM_CORPORA) || 
        		 componentOperation.equals(OperationType.HTTP___W3ID_ORG_META_SHARE_OMTD_SHARE_LEXICON_EXTRACTION_FROM_LEXICA) ||
        		 componentOperation.equals(OperationType.HTTP___W3ID_ORG_META_SHARE_OMTD_SHARE_LEXICON_EXTRACTOR_FROM_CORPORA) ||        
        		 componentOperation.equals(OperationType.HTTP___W3ID_ORG_META_SHARE_OMTD_SHARE_LEXICON_ENHANCEMENT) ||
        		 componentOperation.equals(OperationType.HTTP___W3ID_ORG_META_SHARE_OMTD_SHARE_BILINGUAL_LEXICON_INDUCTION) ) {
        	//logger.info("Into lexicon functionality");
        	lcrInfo.setLexicalConceptualResourceType(LexicalConceptualResourceTypeEnum.LEXICON);
        }       
        // rest --> other 
        else {
        	//logger.info("Into other functionality");
        	lcrInfo.setLexicalConceptualResourceType(LexicalConceptualResourceTypeEnum.OTHER);
        }
        
		//////////////////////////
		// relations.relationInfo
		List<RelationInfo> relations = new ArrayList<>();        		
		relations.add(generateRelationInfo(component));
		lcrInfo.setRelations(relations);
		//logger.info("Resource Relation info::\n" + mapper.writeValueAsString(relations) + "\n");   
		
		/////////////////////////////////////////////////
		// lexicalConceptualResourceTextInfo
		LexicalConceptualResourceTextInfo lcrTextInfo =  generateLexicalConceptualResourceTextInfo(inputCorpus, component);
		lcrInfo.setLexicalConceptualResourceTextInfo(lcrTextInfo);
		//logger.info("Text info::\n" + mapper.writeValueAsString(lcrTextInfo) + "\n");
		        
        return(lcrInfo);
        		

    }

	@Override
	protected IdentificationInfo generateIdentificationInfo(Corpus inputCorpus, Component component) {

		String descriptionDescription  =  "The lexical/conceptual resource generated by the processing of " +  
		        "[input_corpus_name] with [component_name] version [component_version].";
								    		
		IdentificationInfo identificationInfo = new IdentificationInfo();
		
		// identificationInfo.resourceNames.resourceName		
		identificationInfo.setResourceNames(generateResourceNameList(inputCorpus, component));
		
		// identificationInfo.descriptions.description
		identificationInfo.setDescriptions(generateDescriptionList(inputCorpus, component, descriptionDescription));
	
		// identificationInfo.resourceIdentifiers.resourceIdentifier
		identificationInfo.setResourceIdentifiers(generateResourceIdentifierList());
		
		return identificationInfo;
	}

	@Override
	protected String getWorkingResourceName(Corpus inputCorpus, Component component) {
		String lexicalName = "The lexical/conceptual resource generated by the processing of " + 
				"[input_corpus_name] with [component_name]";
		lexicalName = lexicalName.replaceAll("\\[input_corpus_name\\]", 
				getInputCorpusName(inputCorpus));			
		lexicalName = lexicalName .replaceAll("\\[component_name\\]", 
				getComponentName(component));
									
		return lexicalName;
	}

	@Override
	protected String generateWorkingResourceLandingPage(String resourceOmtdId) {
		return landingPageDomain + "/landingPage/lexical/" + resourceOmtdId;
	}

	@Override
	protected String generateDistributionLocation(String outputArchiveId) {
		return registryHost + "/request/lexical/download?archiveId=" + outputArchiveId;
	}

	@Override
	protected List<SizeInfo> generateDistributionSizeInfo(Corpus inputCorpus) {
		List<SizeInfo> sizeInfoList = new ArrayList<>();
		SizeInfo sizeInfo = new SizeInfo();
		sizeInfo.setSize("1");
		sizeInfo.setSizeUnit(SizeUnitEnum.FILES);
		sizeInfoList.add(sizeInfo);
		return sizeInfoList;
	}
	
	@Override
	protected String generateAttributionTextStart(){
		return "The language/conceptual resource generated by the ";
	}
	
	@Override    
	protected RelationInfo generateRelationInfo(Corpus inputCorpus) {
		return null;
	}
	 	
	
	protected LexicalConceptualResourceTextInfo generateLexicalConceptualResourceTextInfo(Corpus inputCorpus, Component component) {

		LexicalConceptualResourceTextInfo lcrTextInfo = new LexicalConceptualResourceTextInfo();

		// lexicalConceptualResourceTextInfo.lingualityInfo
		LingualityInfo lingualityInfo = inputCorpus.getCorpusInfo().getCorpusSubtypeSpecificInfo().getRawCorpusInfo().getLingualityInfo();
		lcrTextInfo.setLingualityInfo(lingualityInfo);

		// lexicalConceptualResourceTextInfo.languages
		List<LanguageInfo> languages = inputCorpus.getCorpusInfo().getCorpusSubtypeSpecificInfo().getRawCorpusInfo().getLanguages();
		lcrTextInfo.setLanguages(languages);
	       								
			
		// lexicalConceptualResourceTextInfo.timeCoverage
		List<TimeCoverageInfo> timeClassifications = inputCorpus.getCorpusInfo().getCorpusSubtypeSpecificInfo().getRawCorpusInfo().getTimeClassifications();
		String timeCoverage = null;
		Iterator<TimeCoverageInfo> timeIter = timeClassifications.iterator();
		while(timeIter.hasNext()) {
			String sep = ", ";
			if (timeCoverage == null) {
				timeCoverage = "";
				sep = "";
			}
			timeCoverage += sep + timeIter.next().getTimeCoverage();
			
		}
		if (timeCoverage != null) {
			lcrTextInfo.setTimeCoverage(timeCoverage);
		}

		// lexicalConceptualResourceTextInfo.geographicClassifications
		List<GeographicCoverageInfo> geographicClassifications = inputCorpus.getCorpusInfo().getCorpusSubtypeSpecificInfo().getRawCorpusInfo().getGeographicClassifications();
		String geoCoverage = null;
		Iterator<GeographicCoverageInfo> geoIter = geographicClassifications.iterator();
		while(geoIter.hasNext()) {
			String sep = ", ";
			if (geoCoverage == null) {
				geoCoverage = "";
				sep = "";
			}
			geoCoverage += sep + geoIter.next().getGeographicCoverage();
			
		}
		if (geoCoverage != null) {
			lcrTextInfo.setGeographicCoverage(geoCoverage);
		}
	
			                
		CreationInfo creationInfo = generateCreationInfo(inputCorpus, component);
		lcrTextInfo.setCreationInfo(creationInfo);
		return lcrTextInfo;
	}
	

		
}