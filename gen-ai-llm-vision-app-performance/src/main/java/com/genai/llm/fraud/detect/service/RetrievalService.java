package com.genai.llm.fraud.detect.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RetrievalService 
{
	@Value("${llm.system.message:You are a helpful assistant}")
	private String systemMessage;
	
	@Value("${cloud.resource.token}")
	private String cloudResourceToken;
		
	@Autowired
	private LargeLanguageModelService largeLangModelSvc;
	
	public String orchestrateVision(String systemMsg, String text, String imgSrc, Integer maxResultsToRetrieveDynamic, 
									Double minScoreRelevanceScoreDynamic, Float temperature, String executeOnLocalMachine) 
									throws IOException, InterruptedException 
	{	
		String result = null;
		if("Y".equals(executeOnLocalMachine.trim()))
		{
			//if you are sure that your local machine is powerful enough to run the workload
			result = executeOnLocalResource(text, maxResultsToRetrieveDynamic, minScoreRelevanceScoreDynamic);
		}
		else 
		{   //when your local machine is not powerful enough to run the workload
			result = executeOnCloudResource(text, imgSrc, temperature);
		}
		
		return result;
	}

	/*
     fully local machine based execution
     */
	public String executeOnLocalResource(String text, Integer maxResultsToRetrieveDynamic, Double minScoreRelevanceScoreDynamic) 
	{	
		System.out.println("\n---- invoking LLM on local machine. Ensure that this machine has very high config");
		String userPrompt = text;
		
		//--step -1  : enhance the user prompt with the context information from the DB		
		String promptWithFullContext = systemMessage + "\n"+ " Here is the question: " + "\n" + userPrompt ;		
		System.out.println("---- constructed RAG promptWithFullContext \n"+promptWithFullContext);
				
		//--step -2 : invoke the LLM inferencing engine with the fully constructed prompt
		String response = largeLangModelSvc.generate(promptWithFullContext);
		
		System.out.println("---- completed LLM - RAG orchestrations with response : \n"+ response);
		return response;
	}
	
	/*
	 * execute on the cloud provided resource 
	 * instill.tech provides resources and models to run heavy workloads. 
	 * Thanks Instill AI 
	*/
	private String executeOnCloudResource(String text, String imgSrc, Float temperature)
										throws IOException, InterruptedException 
	{
		String result;
		System.out.println("\n---- invokig LLM on cloud");
		int outputTokens = 5000; //to ensure a reasonably elaborate response without getting truncated
		
		/*
		cloudResourceToken
		login/signup   https://auth.instill.tech/
		create/use tokens https://instill.tech/settings/api-tokens
		update application.properties cloud.resource.token=<YOUR_TOKEN>	
		*/
		String llmModelProvidedByCloud = "llava-1-6-13b";
		HttpRequest request = HttpRequest.newBuilder()
			    .uri(URI.create("https://api.instill.tech/v1alpha/organizations%2Finstill-ai%2Fmodels%2F"+ llmModelProvidedByCloud +"/trigger"))
			    .header("Authorization", "Bearer "+ cloudResourceToken)
			    .header("accept", "application/json")
			    .header("content-type", "application/json").method("POST", HttpRequest.BodyPublishers.ofString("{  \"taskInputs\": [    {      \"visualQuestionAnswering\": {  \"temperature\":\"" + temperature + "\",  \"maxNewTokens\":\"" + outputTokens + "\",   \"prompt\":\"" + text + "\", \"promptImages\": [          {            \"promptImageUrl\":\"" +imgSrc+ "\"          }        ]      }    }  ]}"))
			    .build();
		
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println(response.body());
			result =  response.body().toString();
		return result;
	}
		
}