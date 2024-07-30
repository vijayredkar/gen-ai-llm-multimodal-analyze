package com.genai.llm.fraud.detect.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testcontainers.shaded.org.awaitility.Durations;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.Response;

@Service
public class LargeLanguageModelService 
{	
	@Autowired
	FileUtilsService fileUtilsSvc;
	
	/*
	 * Local LLM server : Ollama operations	
	 */
	public String generate(String text) 
	{	
		Map<String, String> severConfigMap  = gatherConfig();		
		String modelName       = severConfigMap.get("modelName");
		String llmResponse = null;
		
		Integer llmServerPort  = Integer.parseInt(severConfigMap.get("llmServerPort"));
		Double llmResponseTemp = Double.parseDouble(severConfigMap.get("llmResponseTemp"));
	
		ChatLanguageModel model = buildLLMResponseModelStandAloneServer("http://127.0.0.1:"+llmServerPort, modelName, llmResponseTemp);
		Response<AiMessage> response = model.generate(UserMessage.userMessage
				(
				TextContent.from(text),
				ImageContent.from("https://github.com/vijayredkar/vijayredkar.github.io/blob/main/memory-usage-1.png?raw=true")
				));	
	     
		llmResponse = response.content().text();	    
	    return llmResponse;	    
	 }
	
	/*
	 * get server config
	 */
	private Map<String, String> gatherConfig() 
	{
		Map<String, String> llmServerConfig = new HashMap<String, String>();
		
		String currentDir = System.getProperty("user.dir");
		String resoucePath = currentDir + "\\"+ "\\src\\main\\resources\\application.properties";
		
		String modelName       = fileUtilsSvc.extractFields("llm.model.name", resoucePath);		
		String llmServerPort   = fileUtilsSvc.extractFields("llm.server.port", resoucePath);
		String llmResponseTemp = fileUtilsSvc.extractFields("llm.response.temperature", resoucePath);		
		
		llmServerConfig.put("modelName", modelName);
		llmServerConfig.put("llmServerPort", llmServerPort);
		llmServerConfig.put("llmResponseTemp", llmResponseTemp);
		
		return llmServerConfig;
	}
		 
	 private ChatLanguageModel buildLLMResponseModelStandAloneServer(String llmServerUrl, String modelName, double llmResponseTemp) 
	 {
			ChatLanguageModel model = OllamaChatModel.builder()
								        			   //.baseUrl("http://127.0.0.1:11434") //server running on localhost
												   	   .baseUrl(llmServerUrl)
								        			   .modelName(modelName)
								        			   .temperature(llmResponseTemp)
								        			   .timeout(Durations.TEN_MINUTES) //best is to NOT change this
								        			   .maxRetries(100)
								        			   .build();
			return model;
	}	 
}