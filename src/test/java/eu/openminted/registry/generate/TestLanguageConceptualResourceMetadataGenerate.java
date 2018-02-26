package eu.openminted.registry.generate;

import eu.openminted.registry.beans.security.UserInfoAAIConfig;
import eu.openminted.registry.core.service.ResourceCRUDService;
import eu.openminted.registry.domain.Component;
import eu.openminted.registry.domain.Corpus;
import eu.openminted.registry.service.aai.UserInfoAAIRetrieve;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
	

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=UserInfoAAIConfig.class, loader=AnnotationConfigContextLoader.class)
public class  TestLanguageConceptualResourceMetadataGenerate {

		static final Logger logger = Logger.getLogger(TestLanguageConceptualResourceMetadataGenerate.class.getName());
		
	    @InjectMocks
		private LanguageConceptualResourceMetadataGenerate lcrMetadataGenerator;
		
		@Mock
		private ResourceCRUDService<Corpus> corpusService;

		@Mock
		private ResourceCRUDService<Component> componentService;

		@Autowired
		private UserInfoAAIRetrieve aaiUserService;
		
		
		private Corpus generateCorpus(String fileToCorpus) throws FileNotFoundException {
			
			Corpus inputCorpus = null;
			try {

					logger.info("File :: " + this.getClass().getResource(fileToCorpus));
					File file = ResourceUtils.getFile(this.getClass().getResource(fileToCorpus));
					JAXBContext jaxbContext = JAXBContext.newInstance(Corpus.class);

					Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					inputCorpus = (Corpus) jaxbUnmarshaller.unmarshal(file);
			

			} catch (JAXBException e) {
				e.printStackTrace();
			}
	        return inputCorpus;
	    }
		
		private Component generateComponent(String fileToComponent) throws FileNotFoundException {
			Component component = null;
			try {

					logger.info("File :: " + this.getClass().getResource(fileToComponent));
					File file = ResourceUtils.getFile(this.getClass().getResource(fileToComponent));
					JAXBContext jaxbContext = JAXBContext.newInstance(Corpus.class);

					Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					component = (Component) jaxbUnmarshaller.unmarshal(file);
			

			} catch (JAXBException e) {
				e.printStackTrace();
			}
	        return component;
	    }
		
		@Before
		public void setup() {		        
		    MockitoAnnotations.initMocks(this);
		    ReflectionTestUtils.setField(lcrMetadataGenerator, "aaiUserInfoRetriever", aaiUserService);
		}
		
		
		@Test
		public void testBasic() throws IOException {
			
		
			logger.info("Running Corpus Metadata Generate");
			String inputCorpusId =  "corpus_maximum.xml"; // omtdid	
			String componentId = "component_minimal.xml";  //omtdid
			String userId = "0931731143127784@openminted.eu"; 
			String outputCorpusArchiveId = "outputArchiveId";
				
			
			Mockito.when(corpusService.get(inputCorpusId)).thenReturn(this.generateCorpus("/metadata_resources_v302/" + inputCorpusId));
			Mockito.when(componentService.get(componentId)).thenReturn(this.generateComponent("/metadata_resources_v302/" + componentId));
			
		//	Corpus outputCorpus = lcr.generateAnnotatedCorpusMetadata(inputCorpusId, componentId, userId, outputCorpusArchiveId);

		}

		
	}

